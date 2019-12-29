Synthi100 {
	var <server; // Servidor por defecto

	// Módulos que incluye:
	var <modulOscillators;
	var <modulInputAmplifiers;
	var <modulOutputChannels;
	var <modulPatchbayAudio;
	var <connectionOut = nil;

	// Puertos externos de entrada y salida
	var <stereoOutBuses;
	var <panOutputs1to4Busses;
	var <panOutputs5to8Busses;
	var <individualChannelOutputsBusses;
	var <sendToDeviceBusses;

	var <returnFromDeviceBusses;
	var <inputAmplifiersBusses;
	var <micAmpBusses;

	// Array que almacena los dispositivos OSC con los que se comunica Synthi100
	var <netAddr;
	// Función que se utiliza para escuchar todos los puertos OSC. Es variable de clase para poder añadirla y suprimirla desde cualquier instancia.
	classvar functionOSC = nil;
	// Puerto por defecto de envío de mensajes OSC (por defecto en TouchOSC)
	var devicePort;

	var <generalVol;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(S100_Settings);
		Class.initClassTree(S100_Oscillator);
		Class.initClassTree(S100_OutputChannel);
		Class.initClassTree(S100_InputAmplifier);
		Class.initClassTree(S100_PatchbayAudio);
	}

	*new {
		arg server = Server.local; // por defecto los canales izquierdo y derecho del sistema
		^super.new.init(server);
	}


	*addSynthDef {
		SynthDef(\connection, {|outBusL, outBusR, inBusL, inBusR, vol|
			Out.ar(outBusL, In.ar(inBusL, 1) * vol);
			Out.ar(outBusR, In.ar(inBusR, 1) * vol);
		}).add;
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init {|serv|

		// Carga la configuración
		var settings = S100_Settings.get;

		generalVol = settings[\generalVol];
		devicePort = settings[\OSCDevicePort];

		server = serv;

		stereoOutBuses = [0,1]; // mezcla stereo de los 8 canales de salida.

		// Módulos
		modulOscillators = 9.collect({S100_Oscillator(serv)}); // 9 osciladores generadores de señal de audio
		modulInputAmplifiers = 8.collect({S100_InputAmplifier(serv)});
		modulOutputChannels = 8.collect({|i| S100_OutputChannel(serv, modulInputAmplifiers[i].outputBus)});
		modulPatchbayAudio = S100_PatchbayAudio(server);

		// Se añaden al servidor las declaracines SynthDefs
		Synthi100.addSynthDef;
		S100_Oscillator.addSynthDef;
		S100_InputAmplifier.addSynthDef;
		S100_OutputChannel.addSynthDef;
		S100_PatchbayAudio.addSynthDef;
	}


	// Métodos de instancia /////////////////////////////////////////////////////////////////////////

	run { arg standalone = false;
		var waitTime = 0.001;
		2.do({"".postln}); // líneas en blanco para mostrar después todos los mensajes.
		if (connectionOut != nil, {"Synthi100 en ejecución".error; ^this});

		Routine({
			while({server == nil}, {wait(waitTime)});
			// Nos aseguramos de que el número de canales de entrada y salida son los correctos
			if (standalone == true, {
				if (or (server.options.numOutputBusChannels != 18,
					server.options.numInputBusChannels != 16), {
					"Número de canales de entrada y salida incorrectos".postln;
					if (server.serverRunning, {
						"Apagando servidor...".post;
						server.quit;
						wait(waitTime);
						while({server.serverRunning}, {wait(waitTime)});
						"OK".post;
						"".postln;
					});
					"Estableciendo número correcto de canales de entrada y salida...".post;
					server.options.numOutputBusChannels = 18;
					while({server.options.numOutputBusChannels != 18}, {wait(waitTime)});
					server.options.numInputBusChannels = 16;
					while({server.options.numInputBusChannels != 16}, {wait(waitTime)});
					"OK".post;
					"".postln;
				});
			});

			if(server.serverRunning == false, {"Arrancando servidor...".postln});
			server.boot;
			while({server.serverRunning == false}, {wait(waitTime)});

			wait(0.3);
			// Se conectan las salidas de los canales de salida a los dos primeros buses de salida especificados en stereoOutBuses
			2.do({"".postln}); // líneas en blanco para mostrar después todos los mensajes.
			"Conexión de salida stereo...".post;
			connectionOut = modulOutputChannels.collect({|i|
				var result = nil;
				result = Synth(\connection, [
					\inBusL, i.outBusL,
					\inBusR, i.outBusR,
					\outBusL, stereoOutBuses[0],
					\outBusR, stereoOutBuses[1],
					\vol, generalVol,
				], server).register;
				while({result.isPlaying == false}, {wait(waitTime)});
			});
			"OK".post;
			"".postln;


			// Se arrancan todos los Synths de todos los módulos //////////////////////////////////

			// Output Channels
			"Output Channels...".post;
			modulOutputChannels.do({|i|
				i.createSynth;
				while({i.synth.isPlaying == false}, {wait(waitTime)});
			});
			"OK".post;
			"".postln;

			// Input Amplifier
			"Input Amplifiers...".post;
			modulInputAmplifiers.do({|i|
				i.createSynth;
				while({i.synth.isPlaying == false}, {wait(waitTime)});
			});
			"OK".post;
			"".postln;

			// Oscillators
			"Oscillators...".post;
			modulOscillators.do({|i|
				i.createSynth;
				while({i.synth.isPlaying == false}, {wait(waitTime)});
			});
			"OK".post;
			"".postln;

			// conecta cada entrada y salida de cada módulo en el patchbay de audio
			"Conexiones en Patchbay de audio...".post;
			modulPatchbayAudio.connect(modulOscillators, modulInputAmplifiers, modulOutputChannels);
			"OK".post;
			"".postln;

			"Synthi100 en ejecución".postln;
		}).play;
	}

	// Habilita el envío y recepción de mensajes OSC desde otros dispositivos.
	pairDevice {
		var oscDevices = Dictionary.new;
		NetAddr.broadcastFlag = true;
		Routine({
			var functionOSC = {|msg, time, addr, recvPort|
				if(
					"/S100/sync".matchRegexp(msg[0].asString).or( // si se pulsa el botón de sincronización
						"/ping".matchRegexp(msg[0].asString) // ...o si está activado el /ping en TouchOSC en menos de 4s
					), {
						if(oscDevices.trueAt(addr.ip) == false, {
							oscDevices.put(addr.ip, addr.ip);
							("Found device at " ++ addr.ip).postln;
						})
				})
			};
			"Searching OSC devices...".postln;
			thisProcess.addOSCRecvFunc(functionOSC);
			wait(5);
			thisProcess.removeOSCRecvFunc(functionOSC);
			("Found " ++ oscDevices.size ++ " devices:").postln;
			oscDevices.do({|i| i.postln});
			if (oscDevices.size > 0, {
				this.prepareOSC;
				netAddr = List.new;
				oscDevices.do({|i|
					netAddr.add(NetAddr(i, devicePort));
				});
				this.sendStateOSC;
			});
		}).play;
	}


	prepareOSC {
		NetAddr.broadcastFlag = true;
		thisProcess.removeOSCRecvFunc(functionOSC); // Elimina la función anterior para volverla a introducir
		// función que escuchará la recepción de mensajes OSC de cualquier dispositivo
		functionOSC = {|msg, time, addr, recvPort|
			if(
				"/osc".matchRegexp(msg[0].asString).or({ // TODO: crear otro tipo de comprobación distinta de ver si el mensaje comienza por alguna de las palabras clave (pueden ser muchas).
					"/out".matchRegexp(msg[0].asString).or({
						"/patchATouchOSC".matchRegexp(msg[0].asString)
					})
				}), {
					// se ejecuta la orden recibida por mensaje.
					this.setParameterOSC(msg[0].asString, msg[1]);
					// Se envía el mismo mensaje a todas las direcciones menos a la remitente
					netAddr.do({|i|
						if(addr.ip != i.ip, {
							i.sendMsg(msg[0], msg[1])
						})
					})
			});
		};
		netAddr = NetAddr("255.255.255.255", devicePort);
		thisProcess.addOSCRecvFunc(functionOSC);
	}


	// Envía el estado de todo el Synthi por OSC
	// Para mejorarlo sería bueno mandar un bundle.
	sendStateOSC {
		var lapTime = 0.0001;
		Routine({
			// ponemos los pines de Pathbay de audio a 0
			var numVer = 16;
			var numHor = 16;

			var strings = [
				"/patchATouchOSCA1a",
				"/patchATouchOSCA1b",
				"/patchATouchOSCA2a",
				"/patchATouchOSCA2b",
				"/patchATouchOSCB2a",
				"/patchATouchOSCB2b",
				"/patchATouchOSCC2a",
				"/patchATouchOSCC2b",
			];

			strings.do({|string|
				numVer.do({|i|
					numHor.do({|j|
						strings.do({|string|
							string = string ++ "/" ++ (i + 1) ++ "/" ++ (j + 1);
							netAddr.do({|i| i.sendMsg(string, 0)});
							wait(lapTime);
						})
					});
				});

				// enviamos valores iniciales por OSC a dispositivos
				this.getState.do({|msg|
					wait(lapTime);
					netAddr.do({|i| i.sendMsg(msg[0], msg[1])})
				})
			});
			"Dispositivos comunicados por OSC preparados OK".postln;
		}).play;
	}

	// Setters de la clase
	setGeneralVol {|vol|
		generalVol = vol;
		connectionOut.do({|i|
			if(i != nil, {
				i.set(\vol, vol);
			})
		})
	}

	// Setter de los diferentes parámetros de los módulos en formato OSC
	setParameterOSC {|string, value|
		var splitted = string.split($/);
		switch (splitted[1],
			"osc", { // Ejemplo: "/osc/1/pulse/level"
				var index = splitted[2].asInt - 1;
				var parameter;
				3.do({splitted.removeAt(0)});
				if (splitted.size == 1,
					{parameter = splitted[0]},
					{parameter = splitted[0]++splitted[1]}
				);
				switch (parameter,
					"range", {modulOscillators[index].setRange(value)},
					"frequency", {modulOscillators[index].setFrequency(value)},
					"pulselevel", {modulOscillators[index].setPulseLevel(value)},
					"pulseshape", {modulOscillators[index].setPulseShape(value)},
					"sinelevel", {modulOscillators[index].setSineLevel(value)},
					"sinesymmetry", {modulOscillators[index].setSineSymmetry(value)},
					"trianglelevel", {modulOscillators[index].setTriangleLevel(value)},
					"sawtoothlevel", {modulOscillators[index].setSawtoothLevel(value)}
				)
				//	modulOscillators[index].setParameter(parameter, value);
			},
			"patchA", { // Ejemplo "/patchA/91/36". Origen de coordenadas izquierda arriba / Orden: vertical y horzontal
				2.do({splitted.removeAt(0)});
				modulPatchbayAudio.administrateNode(splitted[0].asInt, splitted[1].asInt, value);
			},


			// Patchbay de Audio de TouchOSC: A2
			"patchATouchOSCA2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 67 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
			},

			// Patchbay de Audio de TouchOSC: B2
			"patchATouchOSCB2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 83 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
			},

			// Patchbay de Audio de TouchOSC: C2
			"patchATouchOSCC2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 99 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
			},
			"out", { // Ejemplo "/out/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {modulOutputChannels[index].setLevel(value)},
					"filter", {modulOutputChannels[index].setFilter(value)},
					"on", {
						modulInputAmplifiers[index].setOn(value);
						modulOutputChannels[index].setOn(value)
					},
					"pan", {modulOutputChannels[index].setPan(value)},
				)
			},
		);
	}

	// Devuelve una colección de pares [mensaje_OSC, valor] con el estado actual de todos los módulos
	getState {
		var data = List.newClear(0);

		// Oscillators:
		modulOscillators.do({|osc, num|
			var string = "/osc/" ++ (num + 1) ++ "/";
			data.add([string ++ "range", osc.range]);
			data.add([string ++ "pulse/level", osc.pulseLevel]);
			data.add([string ++ "pulse/shape", osc.pulseShape]);
			data.add([string ++ "sine/level", osc.sineLevel]);
			data.add([string ++ "sine/symmetry", osc.sineSymmetry]);
			data.add([string ++ "triangle/level", osc.triangleLevel]);
			data.add([string ++ "sawtooth/level", osc.sawtoothLevel]);
			data.add([string ++ "frequency", osc.frequency]);
		});

		// Output channels:
		modulOutputChannels.do({|oc, num|
			var string = "/out/" ++ (num + 1) ++ "/";
			data.add([string ++ "filter", oc.filter]);
			data.add([string ++ "pan", oc.pan]);
			data.add([string ++ "on", oc.on]);
			data.add([string ++ "level", oc.level]);
		});

		// Patchbay Audio:
		modulPatchbayAudio.nodeSynths.do({|node|
			var ver = node[\coordenates][0];
			var hor = node[\coordenates][1];
			data.add("/patchA/" ++ ver ++ "/" ++ hor);
		});

		// Implementar Patchbay Audio (Para TouchOSC)

		^data;
	}
}


