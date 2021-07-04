/*
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2020 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SynthiGME {
	var <server; // Servidor por defecto

	// Módulos que incluye:
	var <modulInputAmplifiers;
	var <modulFilters;
	var <modulFilterBank;
	var <modulEnvelopeShapers;
	var <modulOscillators;
	var <modulNoiseGenerators;
	var <modulRingModulators;
	var <modulEcho;
	var <modulRandomGenerator;
	var <modulSlewLimiters;
	var <modulOutputChannels;
	var <modulPatchbayAudio;
	var <modulPatchbayVoltage;

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

	// Array que almacena los dispositivos OSC con los que se comunica SynthiGME
	var <netAddr;
	// Función que se utiliza para escuchar todos los puertos OSC. Es variable de clase para poder añadirla y suprimirla desde cualquier instancia.
	classvar functionOSC = nil;
	// Puerto por defecto de envío de mensajes OSC (por defecto en TouchOSC)
	var devicePort;

	// Diccionario que guarda el último valor de cada string recibido de OSC
	var oscRecievedMessages;

	// Interfáz gráfica de SuperCollider (GUI)
	var <guiSC = nil;

	// Otras opciones.
	var <generalVol;

	classvar settings;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(SGME_Settings);
		Class.initClassTree(SGME_Oscillator);
		Class.initClassTree(SGME_Filter);
		Class.initClassTree(SGME_FilterBank);
		Class.initClassTree(SGME_InputAmplifier);
		Class.initClassTree(SGME_NoiseGenerator);
		Class.initClassTree(SGME_RingModulator);
		Class.initClassTree(SGME_Echo);
		Class.initClassTree(SGME_RandomGenerator);
		Class.initClassTree(SGME_SlewLimiter);
		Class.initClassTree(SGME_OutputChannel);
		Class.initClassTree(SGME_PatchbayAudio);
		Class.initClassTree(SGME_PatchbayVoltage);
		Class.initClassTree(SGME_GUI);
	}

	*new {
		arg server = Server.local,
		gui = true;
		^super.new.init(server, gui);
	}


	*addSynthDef {
		SynthDef(\connectionInputAmplifier, {|outBus, inBus, vol|
			var sig;
			sig = SoundIn.ar(inBus);
			Out.ar(outBus, sig * vol);
		}).add;

		SynthDef(\connectionMono, {|outputBus, inputBus, vol|
			var sig;
			sig = In.ar(inputBus);
			Out.ar(outputBus, sig * vol);
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

	init {|serv, gui|

		// Carga la configuración
		settings = SGME_Settings.get;

		oscRecievedMessages = Dictionary.new;

		guiSC = SGME_GUI(this);
		if(gui == true, {guiSC.makeWindow});

		generalVol = settings[\generalVol];
		devicePort = settings[\OSCDevicePort];

		server = serv;
		stereoOutBuses =  [0,1];

		// Se añaden al servidor las declaracines SynthDefs
		SynthiGME.addSynthDef;
		SGME_InputAmplifier.addSynthDef;
		SGME_LPFilter.addSynthDef;
		SGME_HPFilter.addSynthDef;
		SGME_FilterBank.addSynthDef;
		SGME_EnvelopeShaper.addSynthDef;
		SGME_Oscillator.addSynthDef;
		SGME_NoiseGenerator.addSynthDef;
		SGME_RingModulator.addSynthDef;
		SGME_Echo.addSynthDef;
		SGME_RandomGenerator.addSynthDef;
		SGME_SlewLimiter.addSynthDef;
		SGME_OutputChannel.addSynthDef;
		SGME_PatchbayAudio.addSynthDef;
		SGME_PatchbayVoltage.addSynthDef;
	}


	// Métodos de instancia /////////////////////////////////////////////////////////////////////////

	run {
		var waitTime = 0.001;
		if (connectionOut != nil, {"SynthiGME en ejecución".error; ^this});

		Routine({
			while({server == nil}, {wait(waitTime)});
			if (server.serverRunning, {
				"Apagando servidor...".post;
				server.quit;
				wait(waitTime);
				while({server.serverRunning}, {wait(waitTime)});
				"OK\n".post;
			});
			"Estableciendo número correcto de canales de entrada y salida...".post;
			server.options.device = "Synthi GME";
			server.options.numAudioBusChannels = 2048; // Número de buses de Audio permitidos.
			server.options.numOutputBusChannels = 18;
			while({server.options.numOutputBusChannels != 18}, {wait(waitTime)});
			server.options.numInputBusChannels = 16;
			while({server.options.numInputBusChannels != 16}, {wait(waitTime)});
			server.options.blockSize = 64; // Control rate. Si es hardware lo permite se puede aproximar a 1
			while({server.options.blockSize != 64}, {wait(waitTime)});
			"OK\n".post;

			// Se arranca el servidor (si no lo está)
			if(server.serverRunning == false, {"Arrancando servidor...".postln});
			server.waitForBoot({

				panOutputs1to4Busses = settings[\panOutputs1to4Busses];
				panOutputs5to8Busses = settings[\panOutputs5to8Busses];
				individualChannelOutputsBusses = settings[\individualChannelOutputsBusses];


				// Módulos.
				modulFilters = 4.collect({SGME_LPFilter(server)}) ++ 4.collect({SGME_HPFilter(server)});
				modulFilterBank = SGME_FilterBank(server);
				modulInputAmplifiers = 8.collect({SGME_InputAmplifier(server)});
				modulEnvelopeShapers = 3.collect({SGME_EnvelopeShaper(server)});
				modulOscillators = 12.collect({SGME_Oscillator(server)});
				modulNoiseGenerators = 2.collect({SGME_NoiseGenerator(server)});
				modulRingModulators = 3.collect({SGME_RingModulator(server)});
				modulEcho = SGME_Echo(server);
				modulRandomGenerator = SGME_RandomGenerator(server);
				modulSlewLimiters = 3.collect({SGME_SlewLimiter(server)});
				modulOutputChannels = 8.collect({SGME_OutputChannel(server)});
				modulPatchbayAudio = SGME_PatchbayAudio(server);
				modulPatchbayVoltage = SGME_PatchbayVoltage(server);


				wait(0.2); // Tiempo de seguridad para estar seguros que se han creado correctamente los módulos y sus buses. De otro modo puede que se oiga sonido sin conectar nada. Quizás se pueda encontrar otra solución más elegante...

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
						\vol, generalVol,
					], server).register;
					while({result.isPlaying == false}, {wait(waitTime)});
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
				}.value);
				"OK\n".post;

				"Conexión de salida stereo canales 5 a 8...".post;
				connectionOut.add({
					var result = nil;
					var channels = modulOutputChannels[4..7];
					result = Synth(\connection4, [ // Las salidas stereo salen postfader
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
				}.value);
				"OK\n".post;

				"Conexión de salida de cada canal individual...".post;
				connectionOut.add({
					var result = nil;
					modulOutputChannels.do({|out, n|
						result = Synth(\connectionMono, [
							\inputBus, out.outputBus, // En este momento la salida mono sale prefader (se puede cambiar fácilmente)
							\outputBus, settings[\individualChannelOutputsBusses][n],
							\vol, generalVol,
						], server).register;
						while({result.isPlaying == false}, {wait(waitTime)});
					});
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

				// Filters
				"Filters...".post;
				modulFilters.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Filters
				"Octave Filter Bank...".post;
				modulFilterBank.do({|i|
					i.createSynth;
					while({i.group.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Ring Modulators
				"Ring Modulators...".post;
				modulRingModulators.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Echo A. D. L.
				"Echo A.D.L...".post;
				modulEcho.createSynth;
				while({modulEcho.synth.isPlaying == false}, {wait(waitTime)});
				"OK\n".post;

				// Noise Generators
				"Noise Generators...".post;
				modulNoiseGenerators.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Random Generator
				"Random Voltage Generator...".post;
				modulRandomGenerator.createSynth;
				while({modulRandomGenerator.synth.isPlaying == false}, {wait(waitTime)});
				"OK\n".post;

				// Slew Limiters
				"Slew Limiters...".post;
				modulSlewLimiters.do({|i|
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

				// Envelope Shapers
				"Envelope Shapers...".post;
				modulEnvelopeShapers.do({|i|
					i.createSynth;
					while({i.group.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// Input Amplifier
				inputAmplifiersBusses = modulInputAmplifiers.collect({|i| i.inputBus});
				"Input Amplifier Level...".post;
				modulInputAmplifiers.do({|i|
					i.createSynth;
					while({i.synth.isPlaying == false}, {wait(waitTime)});
				});
				"OK\n".post;

				// conecta cada entrada y salida de cada módulo en el patchbay de audio
				"Conexiones en Patchbay de audio...".post;
				modulPatchbayAudio.connect(
					inputAmplifiers: modulInputAmplifiers,
					filters: modulFilters,
					filterBank: modulFilterBank,
					envelopeShapers: modulEnvelopeShapers,
					oscillators: modulOscillators,
					noiseGenerators: modulNoiseGenerators,
					ringModulators: modulRingModulators,
					echo: modulEcho,
					outputChannels: modulOutputChannels,
				);
				"OK\n".post;

				// conecta cada entrada y salida de cada módulo en el patchbay de voltaje
				"Conexiones en Patchbay de voltage...".post;
				modulPatchbayVoltage.connect(
					inputAmplifiers: modulInputAmplifiers,
					filters: modulFilters,
					envelopeShapers: modulEnvelopeShapers,
					oscillators: modulOscillators,
					randomGenerator: modulRandomGenerator,
					slewLimiters: modulSlewLimiters,
					outputChannels: modulOutputChannels,
				);
				"OK\n".post;

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
				"SynthiGME en ejecución".postln;
				// Se ocultan en GUI los nodos que no tienen conexión entre módulos.
				if (guiSC != nil, {
					guiSC.panels[4].enableNodes(true); // PatchbayAudio
					guiSC.panels[5].enableNodes(true); // PatchbayVoltage
				});
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
							oscDevices.put(addr.ip, [addr.ip, recvPort]);
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
					netAddr.add(NetAddr(i[0], i[1]));
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
			//	{this.setParameterOSC(msg[0].asString, msg[1], addr)}.defer(0);
			this.setParameterOSC(msg[0].asString, msg[1], addr)
		};
		netAddr = NetAddr("255.255.255.255", devicePort);
		thisProcess.addOSCRecvFunc(functionOSC);
	}

	// Se envía el mismo mensaje a todas las direcciones menos a la de la dirección "addrForbidden"
	sendBroadcastMsg{|msg, value, addrForbidden|
		if(addrForbidden == \GUI, {
			netAddr.do({|i|
				i.sendMsg(msg, value).postln;
			})
		}, {
			// Poner aquí código de reenvío a GUI de los mensajes recibidos de otros dispositivos
			netAddr.do({|i|
				if(addrForbidden.ip != i.ip, {
					i.sendMsg(msg, value)
				})
			})
		});
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
					netAddr.do({|i| i.sendMsg(msg[0], msg[1])});
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

		// Envelope Shapers:
		modulEnvelopeShapers.do({|env, num|
			var string = "/env/" ++ (num + 1) ++ "/";
			data.add([string ++ "delay", env.delayTime]);
			data.add([string ++ "attack", env.attackTime]);
			data.add([string ++ "decay", env.decayTime]);
			data.add([string ++ "sustain", env.sustain]);
			data.add([string ++ "release", env.releaseTime]);
			data.add([string ++ "envelopeLevel", env.envelopeLevel]);
			data.add([string ++ "signalLevel", env.signalLevel]);
			data.add([string ++ "selector/1/" ++ env.selector, 1]);
		});

		^data;
	}

	// Libera todos los Synths del servidor y cierra la GUI
	close {
		{Window.closeAll}.defer(0);
		server.freeAll;
		modulRandomGenerator.randomRoutine.stop;
		if (Platform.ideName == "none", { // Si se está ejecutando desde una terminal
			0.exit;
		});
		thisProcess.recompile;
	}
}