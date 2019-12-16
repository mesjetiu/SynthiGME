Synthi100 {
	var <server; // Servidor por defecto

	// Módulos que incluye:
	var <modulOscillators;
	var <modulOutputChannels;
	var <modulPatchbayAudio;
	var <conectionOut;

	// Buses internos de entrada y salida:
	var <audioInBuses;
	var <audioOutBuses;

	// Buses externos de entrada y salida:
	var <stereoOutBuses;

	// Dirección a la que enviar los mensajes OSC
	var netAddr;

	// Opciones:
	const numAudioInBuses = 8;
	const numAudioOutBuses = 8;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(S100_Oscillator);
		Class.initClassTree(S100_OutputChannel);
		Class.initClassTree(S100_PatchbayAudio);
	}

	*new { arg server = Server.local, stereoBuses = [0,1];
		^super.new.init(server, stereoBuses);
	}



	// Métodos de instancia //////////////////////////////////////////////////////////////

	init {|serv, stereoBuses|
		this.prepareOSC;
		// Se añaden al servidor las declaracines SynthDefs
		S100_Oscillator.addSynthDef;
		S100_OutputChannel.addSynthDef;
		S100_PatchbayAudio.addSynthDef;
		server = serv;
		// Buses de audio de entrada y salida
		audioInBuses = numAudioInBuses.collect({Bus.audio(server, 1)});
		audioOutBuses = numAudioOutBuses.collect({Bus.audio(server, 1)});
		stereoOutBuses = stereoBuses;

		// Módulos
		modulOscillators = 12.collect({S100_Oscillator(serv)});
		modulOutputChannels = 8.collect({S100_OutputChannel(serv)});
		modulPatchbayAudio = S100_PatchbayAudio(server);
	}


	// Métodos de instancia /////////////////////////////////////////////////////////////////////////

	play {
		server.waitForBoot({
			// TODO: Crear alguna rutina para colocar en orden todos los Synths en el servidor. Ahora están colocados mezclados los osciladores y los conectores.

			// Rutina para espaciar temporalmente la creacion de cada Synth, de forma que queden ordenados.
			Routine({
				var waitTime = 0.01; // Tiempo de espera entre la creación de cada Synth

				// Se conectan provisionalmente las salidas de todos los módulos a los dos primeros buses de salida especificados en externAudioBuses
				conectionOut = modulOutputChannels.collect({|i|
					SynthDef(\conection, {
						Out.ar(stereoOutBuses[0], In.ar(i.outBusL, 1));
						Out.ar(stereoOutBuses[1], In.ar(i.outBusR, 1));
					}).play(server);
					wait(waitTime);
				});
				wait(waitTime);

				// Se arrancan todos los Synths de todos los módulos //////////////////////////////////

				// Output Channels
				modulOutputChannels.do({|i|
					i.createSynth;
					wait(waitTime);
				});
				wait(waitTime);
				//		modulOutputChannels = modulOutputChannels.reverse; // para tenerlos en orden de arriba a abajo según la visibilidad entre synths en el mismo sentido

				// Oscillators
				modulOscillators.do({|i|
					i.createSynth;
					wait(waitTime);
				});
				wait(waitTime);
				modulPatchbayAudio.connect(modulOscillators, modulOutputChannels);
				wait(waitTime);
				this.sendStateOSC;
			}).play;
		});
	}

	stop {
		// Por ahora no hace nada. Lo que debe hacer es apagar todos los synths de todos los módulos manteniendo todos los valores de los parámetros

	/*	conectionOut.do({|i| i.free}); // provisional
		modulOscillators.do({|i| i.freeSynth});
		modulOutputChannels.do({|i| i.freeSynth});
		modulPatchbayAudio.freeSynths;
		*/
		thisProcess.removeOSCRecvFunc(~s100functionOSC);
	}



	// Habilita el envío y recepción de mensajes OSC desde otros dispositivos.
	prepareOSC {
		// ~s100functionOSC es una función para tratamiento de mensajes de entrada de OSC desde otros dispositivos.
		// Está en variable para poder pasarse como parámetro, y es global para poder remover la función al crear una nueva instancia.
		thisProcess.removeOSCRecvFunc(~s100functionOSC);
		NetAddr.broadcastFlag = true;
		netAddr = NetAddr("255.255.255.255", 9000); // el puerto 9000 es por el que se enviarán los mensajes OSC
		~s100functionOSC = {|msg, time, addr, recvPort|
			if(
				"/osc".matchRegexp(msg[0].asString).or({
					"/out".matchRegexp(msg[0].asString).or({
						"/patchATouchOSC".matchRegexp(msg[0].asString)
					})
				}), {
				//	[msg, time, addr, recvPort].postln;
				//	netAddr.sendMsg(msg[0],msg[1]); // reenvía en broadcasting el mensaje recibido para que otros dispositivos se puedan sicronizar con los cambios realizados en cualquiera de ellos.
					//	this.setParameterOSC(msg[0], msg[1]);
					this.setParameterOSC(msg[0].asString, msg[1]);
			});
		};
		thisProcess.addOSCRecvFunc(~s100functionOSC);
	}


	// Envía el estado de todo el Synthi por OSC
	// Para mejorarlo sería bueno mandar un bundle.
	sendStateOSC {
		var b =  NetAddr("255.255.255.255", 9000);
		Routine({
			this.getState.do({|msg|
				wait(0.02);
				b.sendMsg(msg[0], msg[1]);
			})
		}).play;
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
			// Mensaje DE PRUEBAS para TouchOSC con 3 osciladores
			"patchATouchOSC", { // Patchbay de Audio desde TouchOSC, cuyo origen de coordenadas es izquierda abajo. Orden: horizontal y vertical (como un sistema normal de coordenadas)
				var x, y;
				2.do({splitted.removeAt(0)});
				y = 7 - splitted[0].asInt + 90;
				x = splitted[1].asInt + 35;
				modulPatchbayAudio.administrateNode(y, x, value);
			},
			"out", { // Ejemplo "/out/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {modulOutputChannels[index].setLevel(value)},
					"filter", {modulOutputChannels[index].setFilter(value)},
					"on", {modulOutputChannels[index].setOn(value)},
					"pan", {modulOutputChannels[index].setPan(value)},
				)
			},
		);
		netAddr.sendMsg(string, value);
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

		// Patchbay Audio (Para pruebas con TouchOSC:
		modulPatchbayAudio.nodeSynths.do({|node|
			var ver = node[\coordenates][0];
			var hor = node[\coordenates][1];
			data.add("/patchATouchOSC/" ++ 7-ver-90 ++ "/" ++ hor-35);
		});

		^data;
	}
}
