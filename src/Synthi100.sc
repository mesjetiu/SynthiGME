Synthi100 {
	var <server; // Servidor por defecto

	// Módulos que incluye:
	var <modulOscillators;
	var <modulInputAmplifiers;
	var <modulNoiseGenerators;
	var <modulRingModulators;
	var <modulOutputChannels;
	var <modulPatchbayAudio;

	// Almacena los Synths que conectan los canales de salida de SC con los de los módulos
	var <connectionOut = nil;
	var <connectionIn = nil;

	// Puertos externos de entrada y salida
	var <stereoOutBuses; // Puerto no existente en Synthi 100 pero conveniente. Mezcla los 8 canales paneados.
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

	// Interfáz gráfica de SuperCollider (GUI)
	var <guiSC;

	// Otras opciones.
	var <generalVol;
	var <standalone = false;

	classvar settings;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(S100_Settings);
		Class.initClassTree(S100_Oscillator);
		Class.initClassTree(S100_InputAmplifier);
		Class.initClassTree(S100_NoiseGenerator);
		Class.initClassTree(S100_RingModulator);
		Class.initClassTree(S100_OutputChannel);
		Class.initClassTree(S100_PatchbayAudio);
		Class.initClassTree(S100_GUI);
	}

	*new {
		arg server = Server.local,
		standalone = false, // por defecto los canales izquierdo y derecho del sistema
		gui = false;
		^super.new.init(server, standalone, gui);
	}


	*addSynthDef {
		SynthDef(\connectionInputAmplifier, {|outBus, inBus, vol|
			var sig;
			sig = SoundIn.ar(inBus);
			Out.ar(outBus, sig * vol);
		}).add;

		SynthDef(\connection4, {|outBusL, outBusR, inBusR1, inBusL1, inBusR2, inBusL2, inBusR3, inBusL3, inBusR4, inBusL4, vol|
			var sigL, sigR;
			sigL = In.ar([inBusL1, inBusL2, inBusL3, inBusL4]);
			sigR = In.ar([inBusR1, inBusR2, inBusR3, inBusR4]);
			Out.ar(outBusL, sigL.sum * vol);
			Out.ar(outBusR, sigR.sum * vol);
		}).add;

		SynthDef(\connection2, {|outBusL, outBusR, inBusR1, inBusL1, inBusR2, inBusL2, vol|
			var sigL, sigR;
			sigL = In.ar([inBusL1, inBusL2]);
			sigR = In.ar([inBusR1, inBusR2]);
			Out.ar(outBusL, sigL.sum * vol);
			Out.ar(outBusR, sigR.sum * vol);
		}).add;
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init {|serv, standal, gui|

		// Carga la configuración
		settings = S100_Settings.get;


		guiSC = S100_GUI();
		if(gui == true, {guiSC.makeWindow});

		generalVol = settings[\generalVol];
		devicePort = settings[\OSCDevicePort];

		server = serv;
		standalone = standal;

		// Se añaden al servidor las declaracines SynthDefs
		Synthi100.addSynthDef;
		S100_InputAmplifier.addSynthDef;
		S100_Oscillator.addSynthDef;
		S100_NoiseGenerator.addSynthDef;
		S100_RingModulator.addSynthDef;
		S100_OutputChannel.addSynthDef;
		S100_PatchbayAudio.addSynthDef;
	}


	// Métodos de instancia /////////////////////////////////////////////////////////////////////////

	run {
		var waitTime = 0.001;
		if (connectionOut != nil, {"Synthi100 en ejecución".error; ^this});

		Routine({
			while({server == nil}, {wait(waitTime)});

			if (standalone == true, { // Modo "standalone": los buses de salida se conectan directamente a los puertos de SC.
				// Nos aseguramos de que el número de canales de entrada y salida son los correctos
				if ((server.options.numOutputBusChannels != 18).or(
					{server.options.numInputBusChannels != 16}), {
					"Número de canales de entrada y salida incorrectos".postln;
					if (server.serverRunning, {
						"Apagando servidor...".post;
						server.quit;
						wait(waitTime);
						while({server.serverRunning}, {wait(waitTime)});
						"OK\n".post;
					});
					"Estableciendo número correcto de canales de entrada y salida...".post;
					server.options.numOutputBusChannels = 18;
					while({server.options.numOutputBusChannels != 18}, {wait(waitTime)});
					server.options.numInputBusChannels = 16;
					while({server.options.numInputBusChannels != 16}, {wait(waitTime)});
					"OK\n".post;
				});
			});
			stereoOutBuses =  [0,1];
			if (standalone == true, {
				panOutputs1to4Busses = settings[\panOutputs1to4Busses];
				panOutputs5to8Busses = settings[\panOutputs5to8Busses];
			}, {
				panOutputs1to4Busses = 2.collect({Bus.audio(server)});
				panOutputs5to8Busses = 2.collect({Bus.audio(server)});
			});


			// Se arranca el servidor (si no lo está)
			if(server.serverRunning == false, {"Arrancando servidor...".postln});
			server.waitForBoot({
				// Módulos (han de crearse tras arrancar el servidor, ya que se crean buses)
				modulOscillators = 12.collect({S100_Oscillator(server)}); // 12 osciladores generadores de señal de audio
				modulInputAmplifiers = 8.collect({|i| S100_InputAmplifier(server)});
				modulNoiseGenerators = 2.collect({|i| S100_NoiseGenerator(server)});
				modulRingModulators = 3.collect({|i| S100_RingModulator(server)});
				modulOutputChannels = 8.collect({|i| S100_OutputChannel(server)});
				modulPatchbayAudio = S100_PatchbayAudio(server);

				2.do({"".postln}); // líneas en blanco para mostrar después todos los mensajes de arranque
				"Conexión de salida stereo canales 1 a 8...".post;
				connectionOut = [];
				connectionOut.add({
					var result = nil;
					result = Synth(\connection2, [
						\inBusL1, panOutputs1to4Busses[0],
						\inBusR1, panOutputs1to4Busses[1],
						\inBusL2, panOutputs5to8Busses[0],
						\inBusR2, panOutputs5to8Busses[1],
						\outBusL, stereoOutBuses[0],
						\outBusR, stereoOutBuses[1],
						\vol, 1,
					], server).register;
					while({result.isPlaying == false}, {wait(waitTime)});
					result
				}.value
				);
				"OK\n".post;

				"Conexión de salida stereo canales 1 a 4...".post;
				connectionOut.add({
					var result = nil;
					var channels = modulOutputChannels[0..3];
					result = Synth(\connection4, [
						\inBusL1, channels[0].outBusL,
						\inBusR1, channels[0].outBusR,
						\inBusL2, channels[1].outBusL,
						\inBusR2, channels[1].outBusR,
						\inBusL3, channels[2].outBusL,
						\inBusR3, channels[2].outBusR,
						\inBusL4, channels[3].outBusL,
						\inBusR4, channels[3].outBusR,
						\outBusL, panOutputs1to4Busses[0],
						\outBusR, panOutputs1to4Busses[1],
						\vol, generalVol,
					], server).register;
					while({result.isPlaying == false}, {wait(waitTime)});
					result
				}.value);
				"OK\n".post;

				"Conexión de salida stereo canales 4 a 8...".post;
				connectionOut.add({
					var result = nil;
					var channels = modulOutputChannels[4..7];
					result = Synth(\connection4, [
						\inBusL1, channels[0].outBusL,
						\inBusR1, channels[0].outBusR,
						\inBusL2, channels[1].outBusL,
						\inBusR2, channels[1].outBusR,
						\inBusL3, channels[2].outBusL,
						\inBusR3, channels[2].outBusR,
						\inBusL4, channels[3].outBusL,
						\inBusR4, channels[3].outBusR,
						\outBusL, panOutputs5to8Busses[0],
						\outBusR, panOutputs5to8Busses[1],
						\vol, generalVol,
					], server).register;
					while({result.isPlaying == false}, {wait(waitTime)});
					result
				}.value);
				"OK\n".post;



				// Se arrancan todos los Synths de todos los módulos //////////////////////////////////

				// Output Channels
				"Output Channels...".post;
				modulOutputChannels.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Ring Modulators
				"Ring Modulators...".post;
				modulRingModulators.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Noise Generators
				"Noise Generators...".post;
				modulNoiseGenerators.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Oscillators
				"Oscillators...".post;
				modulOscillators.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Input Amplifier
				inputAmplifiersBusses = modulInputAmplifiers.collect({|i| i.inputBus});
				"Input Amplifiers...".post;
				modulInputAmplifiers.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// conecta cada entrada y salida de cada módulo en el patchbay de audio
				"Conexiones en Patchbay de audio...".post;
				modulPatchbayAudio.connect(
					inputAmplifiers: modulInputAmplifiers,
					oscillators: modulOscillators,
					noiseGenerators: modulNoiseGenerators,
					ringModulators: modulRingModulators,
					outputChannels: modulOutputChannels,
				);
				"OK\n".post;


				if (standalone == true, {
					"Conexión de entrada Input Amplifiers, canales 1 a 8 a puertos de SC...".post;
					connectionIn = inputAmplifiersBusses.collect({|item, i|
						var result = Synth(\connectionInputAmplifier, [
							\inBus, settings[\inputAmplifiersBusses][i],
							\outBus, item,
							\vol, 1,
						], server).register;
						while({result.isPlaying == false}, {wait(waitTime)});
						result
					});
					"OK\n".post;
				});
				"Synthi100 en ejecución".postln;
			});
		}).play;
	}

	// Habilita el envío y recepción de mensajes OSC desde otros dispositivos.
	pairDevice {
		var oscDevices = Dictionary.new;
		var searchTime = 5;
		NetAddr.broadcastFlag = true;
		Routine({
			var functionOSC = {|msg, time, addr, recvPort|
				if(
					"/ping".matchRegexp(msg[0].asString), // ...o si está activado el /ping en TouchOSC en menos de 4s
					{
						if(oscDevices.trueAt(addr.ip) == false, {
							oscDevices.put(addr.ip, addr.ip);
							("Found device at " ++ addr.ip).postln;
						})
				})
			};
			"Searching OSC devices...".postln;
			thisProcess.addOSCRecvFunc(functionOSC);
			wait(searchTime);
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
			// se ejecuta la orden recibida por mensaje.
			this.setParameterOSC(msg[0].asString, msg[1], addr);
		};
		netAddr = NetAddr("255.255.255.255", devicePort);
		thisProcess.addOSCRecvFunc(functionOSC);
	}

	// Se envía el mismo mensaje a todas las direcciones menos a la de la dirección "addrForbidden"
	sendBroadcastMsg{|msg, value, addrForbidden|
		netAddr.do({|i|
			if(addrForbidden.ip != i.ip, {
				i.sendMsg(msg, value)
			})
		})
	}

	// Envía el estado de todo el Synthi por OSC
	// Para mejorarlo sería bueno mandar un bundle.
	sendStateOSC {
		var lapTime = 0.0001;
		Routine({
			// ponemos los pines de Pathbay de audio a 0
			var numVer, numHor, strings;
			numVer = 16;
			numHor = 16;
			strings = [
				"/patchATouchOSCA1a",
				"/patchATouchOSCA1b",
				"/patchATouchOSCA2a",
				"/patchATouchOSCA2b",
				"/patchATouchOSCB1a",
				"/patchATouchOSCB1b",
				"/patchATouchOSCB2a",
				"/patchATouchOSCB2b",
				"/patchATouchOSCC1a",
				"/patchATouchOSCC1b",
				"/patchATouchOSCC2a",
				"/patchATouchOSCC2b",
				"/patchATouchOSCD1a",
				"/patchATouchOSCD1b",
				"/patchATouchOSCD2a",
				"/patchATouchOSCD2b",
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

	// Setters de la clase /////////////////////////////////////////////////////////////
	setGeneralVol {|vol|
		generalVol = vol;
		connectionOut.do({|i|
			if(i != nil, {
				i.set(\vol, vol);
			})
		})
	}

	// Setter de los diferentes parámetros de los módulos en formato OSC
	setParameterOSC {|string, value, addrForbidden|
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
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},
			"patchA", { // Ejemplo "/patchA/91/36". Origen de coordenadas izquierda arriba / Orden: vertical y horzontal
				2.do({splitted.removeAt(0)});
				modulPatchbayAudio.administrateNode(splitted[0].asInt, splitted[1].asInt, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},


			// Patchbay de Audio de TouchOSC: A1
			"patchATouchOSCA1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 67 + (16-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: A2
			"patchATouchOSCA2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 67 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: B1
			"patchATouchOSCB1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 83 + (16-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: B2
			"patchATouchOSCB2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 83 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: C1
			"patchATouchOSCC1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 99 + (16-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: C2
			"patchATouchOSCC2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 99 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: D1
			"patchATouchOSCD1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 115 + (12-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: D2
			"patchATouchOSCD2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 115 + (12-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},


			"out", { // Ejemplo "/out/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {modulOutputChannels[index].setLevel(value)},
					"filter", {modulOutputChannels[index].setFilter(value)},
					"on", {
						modulOutputChannels[index].setOn(value)
					},
					"pan", {modulOutputChannels[index].setPan(value)},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"in", { // Ejemplo "/in/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {modulInputAmplifiers[index].setLevel(value)},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"ring", { // Ejemplo "/ring/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {modulRingModulators[index].setLevel(value)},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"noise", { // Ejemplo "/ring/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"colour", {modulNoiseGenerators[index].setColour(value)},
					"level", {modulNoiseGenerators[index].setLevel(value)},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
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

		// Noise Generators:
		modulNoiseGenerators.do({|ng, num|
			var string = "/noise/" ++ (num + 1) ++ "/";
			data.add([string ++ "colour", ng.colour]);
			data.add([string ++ "level", ng.level]);
		});

		// Output channels:
		modulOutputChannels.do({|oc, num|
			var string = "/out/" ++ (num + 1) ++ "/";
			data.add([string ++ "filter", oc.filter]);
			data.add([string ++ "pan", oc.pan]);
			data.add([string ++ "on", oc.on]);
			data.add([string ++ "level", oc.level]);
		});

		// Input Amplifiers:
		modulInputAmplifiers.do({|ia, num|
			var string = "/in/" ++ (num + 1) ++ "/";
			data.add([string ++ "level", ia.level]);
		});

		// Ring Modulators:
		modulRingModulators.do({|ring, num|
			var string = "/ring/" ++ (num + 1) ++ "/";
			data.add([string ++ "level", ring.level]);
		});

		^data;
	}
}


