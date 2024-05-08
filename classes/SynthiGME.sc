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

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SynthiGME {

	// Opciones de inicio:
	var <server; // Servidor de audio a utilizar
	var <>verboseOSC; // true: se muestran en Post Window los mensajes OSC enviados al synti.
	var <numOutputChannels;
	var <numInputChannels;
	var <numReturnChannels;
	var <blockSize;
	var alwaysRebootServer;
	// fin opciones de inicio.

	// Módulos que incluye:
	var <modulReverb;
	var <modulInputAmplifiers;
	var <modulExternalTreatmentReturns;
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
	var <oscRecievedMessages;

	// Interfáz gráfica de SuperCollider (GUI)
	var <guiSC = nil;

	// Otras opciones.
	var <generalVol;

	classvar settings;

	classvar instance; // aquí se guardará la instancia del Synthi, ya que solo se podrá tener una abierta.



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(SGME_Reverb);
		Class.initClassTree(SGME_Settings);
		Class.initClassTree(SGME_Oscillator);
		Class.initClassTree(SGME_Filter);
		Class.initClassTree(SGME_FilterBank);
		Class.initClassTree(SGME_InputAmplifier);
		Class.initClassTree(SGME_ExternalTreatmentReturn);
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
		gui = true,
		verboseOSC = true, // Muestra en Post window todo mensaje OSC procesado
		numOutputChannels = 2, // Número de canales de salida unidos a salidas de SC. Mínimo 2 (2 canales por defecto del sistema) Máximo 16
		numInputChannels = 2, // Mínimo 2 (del sistema por defecto) Máximo 8
		numReturnChannels = 0, // Mínimo 0, Máximo 4
		blockSize = 64,
		alwaysRebootServer = false; // false: no se reinicia si se cumple la configuración del servidor.

		// Se guarda la instancia:
		if (instance != nil) {"Ya existe una instancia"; ^this};
		instance = this;

		^super.new.init(server, gui, verboseOSC, numOutputChannels.clip(2,16), numInputChannels.clip(2,8), numReturnChannels.clip(0,4), blockSize, alwaysRebootServer);
	}


	*addSynthDef {
		SynthDef(\connectionInputAmplifier, {|outBus, inBus, vol|
			var sig;
			sig = SoundIn.ar(inBus);
			Out.ar(outBus, sig * vol);
		}).add;

		SynthDef(\connectionExternalTreatmentReturn, {|outBus, inBus, vol|
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

		SynthDef(\connection2, {|outBusL, outBusR, inBusR1, inBusL1, inBusR2, inBusL2, inBusL3, inBusR3, inBusL4, inBusR4, inBusL5, inBusR5, inBusL6, inBusR6, inBusL7, inBusR7, inBusL8, inBusR8,  vol|
			var sigL, sigR;
			sigL = In.ar([inBusL1, inBusL2, inBusL3, inBusL4, inBusL5, inBusL6, inBusL7, inBusL8]);
			sigR = In.ar([inBusR2, inBusR2, inBusR3, inBusR4, inBusR5, inBusR6, inBusR7, inBusR8]);
			Out.ar(outBusL, sigL.sum * vol);
			Out.ar(outBusR, sigR.sum * vol);
		}).add;
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init {|serv, gui, verboseOSC, numOutputChan, numInputChan, numReturnChan, blockSiz, alwaysRebootServ|
		// Carga la configuración
		settings = SGME_Settings.get;

		oscRecievedMessages = Dictionary.new;

		guiSC = SGME_GUI(this);
		if(gui == true, {guiSC.makeWindow});

		generalVol = settings[\generalVol];
		devicePort = settings[\OSCDevicePort];
		if (thisProcess.openUDPPort(devicePort)) {
			("Abierto puerto" + devicePort + "para OSC.").postln;} {
			("No se ha podido abrir el puerto" + devicePort + "para OSC").postln;
		};

		server = serv;
		this.verboseOSC = verboseOSC;
		numOutputChannels = numOutputChan;
		numInputChannels = numInputChan;
		numReturnChannels = numReturnChan;
		blockSize = blockSiz;
		alwaysRebootServer = alwaysRebootServ;

		stereoOutBuses = [0,1];

		// Se añaden al servidor las declaracines SynthDefs
		SynthiGME.addSynthDef;
		SGME_Reverb.addSynthDef;
		SGME_InputAmplifier.addSynthDef;
		SGME_ExternalTreatmentReturn.addSynthDef;
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
		var thisRoutine;
		if (connectionOut != nil, {"SynthiGME en ejecución".error; ^this});
		thisRoutine = Routine({
		/*  var <numStereoOutputChannels;
			var <numInputChannels;
			var <numReturnChannels;
			var alwaysRebootServer;
		*/
			// Comprobamos si coinciden las opciones pedidas con las opciones actuales del Server:
			var serverOptionsOK; // true si lo pedido coincide con las opciones actuales
			if (
				server.options.numOutputBusChannels == numOutputChannels,
				{serverOptionsOK = true},
				{serverOptionsOK = false}
			);

			// Apaga el servidor si es necesario
			if (
				(server.serverRunning && (serverOptionsOK == false || alwaysRebootServer)),
				{
					"El servidor de audio está encendido. Apagando servidor...".postln;
					server.quit;
					//server.sync;
					if (server.serverRunning, {
						"Servidor no apagado correctamente".error;
						"Saliendo del programa...".postln;
						thisRoutine.stop();
					}, {
						"Servidor apagado correctamente".postln;
					});
			});


			if (
				(serverOptionsOK == false) && (server.serverRunning == false),
				{
					"Estableciendo número correcto de canales de entrada y salida:".postln;
					server.options.device_("Synthi GME")
					.numAudioBusChannels_(settings[\numAudioBusChannels])
					//.numOutputBusChannels_(settings[\numOutputBusChannels])
					.numOutputBusChannels_(numOutputChannels)
					.numInputBusChannels_(numInputChannels)
					.blockSize_(blockSize); // Control rate. Si es hardware lo permite se puede aproximar a 1
					//server.sync;

					("Número de canales de Audio:" + server.options.numAudioBusChannels).postln;
					("Número de canales de output:" + server.options.numOutputBusChannels).postln;
					("Número de canales de input:" + server.options.numInputBusChannels).postln;
					("Tamaño del bloque:" + server.options.blockSize).postln;

					if(
						server.options.numAudioBusChannels >= settings[\numAudioBusChannels]
						//&& server.options.numOutputBusChannels >= settings[\numOutputBusChannels]
						&& server.options.numOutputBusChannels >= numOutputChannels
						&& server.options.numInputBusChannels >= numInputChannels
						&& server.options.blockSize >= blockSize
					){
						"Opciones actualizadas correctamente".postln;
					}{
						"No se han podido establecer las opciones adecuadas del servidor".error;
						"Saliendo del programa...".postln;
						thisRoutine.stop();
					};

				}
			);


			// Se anuncia que se arrancará el servidor (si no lo está)
			if(server.serverRunning == false, {"Arrancando servidor...".postln});
			// Arrancamos el servidor si aún no lo está
			server.waitForBoot(
				onComplete: {
					panOutputs1to4Busses = settings[\panOutputs1to4Busses];
					panOutputs5to8Busses = settings[\panOutputs5to8Busses];
					individualChannelOutputsBusses = settings[\individualChannelOutputsBusses];


					// Instanciación de los Módulos.
					modulReverb = SGME_Reverb(server);
					modulFilters = 4.collect({SGME_LPFilter(server)}) ++ 4.collect({SGME_HPFilter(server)});
					modulFilterBank = SGME_FilterBank(server);
					modulInputAmplifiers = 8.collect({SGME_InputAmplifier(server)});
					modulExternalTreatmentReturns = 4.collect({SGME_ExternalTreatmentReturn(server)});
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

					server.sync;

					2.do({"".postln}); // líneas en blanco para mostrar después todos los mensajes de arranque




					"Conexión de salida stereo canales 1 a 8 mezclados a salidas 1 y 2".postln;
					connectionOut = [];
					connectionOut = connectionOut.add({
						var result = nil;
						var channels = modulOutputChannels[0..7];
						result = Synth(\connection2, [
							\inBusL1, channels[0].outBusL,
							\inBusR1, channels[0].outBusR,
							\inBusL2, channels[1].outBusL,
							\inBusR2, channels[1].outBusR,
							\inBusL3, channels[2].outBusL,
							\inBusR3, channels[2].outBusR,
							\inBusL4, channels[3].outBusL,
							\inBusR4, channels[3].outBusR,
							\inBusL5, channels[4].outBusL,
							\inBusR5, channels[4].outBusR,
							\inBusL6, channels[5].outBusL,
							\inBusR6, channels[5].outBusR,
							\inBusL7, channels[6].outBusL,
							\inBusR7, channels[6].outBusR,
							\inBusL8, channels[7].outBusL,
							\inBusR8, channels[7].outBusR,
							\outBusL, 0,
							\outBusR, 1,
							\vol, generalVol,
						], server).register;
					}.value);
					server.sync;


					if (
						numOutputChannels >= 4,
						{
							"Conexión de salida stereo canales 1 - 4 a salidas 3 y 4".postln;
							connectionOut = connectionOut.add({
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
									\outBusL, 2,
									\outBusR, 3,
									\vol, generalVol,
								], server).register;
							}.value);
							server.sync;
						}
					);


					if (
						numOutputChannels >= 6,
						{
							"Conexión de salida stereo canales 5 - 8 a salidas 5 y 6".postln;
							connectionOut = connectionOut.add({
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
									\outBusL, 2,
									\outBusR, 3,
									\vol, generalVol,
								], server).register;
							}.value);
							server.sync;
						}
					);


					//"Conexión de salida de cada canal individual...".postln;
					modulOutputChannels.do({|out, n|
						if (n+6 <= server.options.numOutputBusChannels)
						{
							connectionOut = connectionOut.add({
								var result = nil;
								("Output Channel" + (n+1) + "conectado a salida" + (n+7)).postln;
								result = Synth(\connectionMono, [
									\inputBus, out.outputBus, // En este momento la salida mono sale prefader (se puede cambiar fácilmente)
									\outputBus, settings[\individualChannelOutputsBusses][n],
									\vol, generalVol,
								], server).register;
								server.sync;
								result;
							}.value);
						}
					});



					// Se arrancan todos los Synths de todos los módulos //////////////////////////////////

					// Output Channels
					"Output Channels...".post;
					modulOutputChannels.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Reverb
					"Reverb...".post;
					modulReverb.createSynth;
					server.sync;
					"OK\n".post;

					// Filters
					"Filters...".post;
					modulFilters.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Filters
					"Octave Filter Bank...".post;
					modulFilterBank.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Ring Modulators
					"Ring Modulators...".post;
					modulRingModulators.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Echo A. D. L.
					"Echo A.D.L...".post;
					modulEcho.createSynth;
					server.sync;
					"OK\n".post;

					// Noise Generators
					"Noise Generators...".post;
					modulNoiseGenerators.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Random Generator
					"Random Voltage Generator...".post;
					modulRandomGenerator.createSynth;
					server.sync;
					"OK\n".post;

					// Slew Limiters
					"Slew Limiters...".post;
					modulSlewLimiters.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Oscillators
					"Oscillators...".post;
					modulOscillators.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Envelope Shapers
					"Envelope Shapers...".post;
					modulEnvelopeShapers.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// Input Amplifier
					inputAmplifiersBusses = modulInputAmplifiers.collect({|i| i.inputBus});
					"Input Amplifiers Level...".post;
					modulInputAmplifiers.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// External Treatment Returns
					returnFromDeviceBusses = modulExternalTreatmentReturns.collect({|i| i.inputBus});
					"External Treatment Returns...".post;
					modulExternalTreatmentReturns.do({|i|
						i.createSynth;
						server.sync;
					});
					"OK\n".post;

					// conexiones de entrada y salida de cada módulo en el patchbay de audio
					"Conexiones en Patchbay de audio...".post;
					modulPatchbayAudio.connect(
						reverb: modulReverb,
						inputAmplifiers: modulInputAmplifiers,
						externalTreatmentReturns: modulExternalTreatmentReturns,
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
						reverb: modulReverb,
						echo: modulEcho,
						inputAmplifiers: modulInputAmplifiers,
						filters: modulFilters,
						envelopeShapers: modulEnvelopeShapers,
						oscillators: modulOscillators,
						randomGenerator: modulRandomGenerator,
						slewLimiters: modulSlewLimiters,
						outputChannels: modulOutputChannels,
					);
					"OK\n".post;

					//"Conexión de entrada Input Amplifiers, canales 1 a 8 a puertos de SC...".postln;
					connectionIn = inputAmplifiersBusses.collect({|item, i|
						if (i+1 <= server.options.numInputBusChannels)
						{
							var result = Synth(\connectionInputAmplifier, [
								\inBus, settings[\inputAmplifiersBusses][i],
								\outBus, item,
								\vol, 1,
							], server).register;
							("Input Channel" + (i+1) + "conectado a entrada" + (i+1)).postln;
							server.sync;
							result
						}
					});

					//"Conexión de entrada External Treatment Returns, canales 1 a 4 a puertos de SC...".postln;
					connectionIn = connectionIn ++ returnFromDeviceBusses.collect({|item, i|
						if (i+8 <= server.options.numInputBusChannels)
						{
							var result = Synth(\connectionExternalTreatmentReturn, [
								\inBus, settings[\returnFromDeviceBusses][i],
								\outBus, item,
								\vol, 1,
							], server).register;
							("External Input Channel" + (i+1) + "conectado a entrada" + (i+9)).postln;
							server.sync;
							result
						}
					});

					"SynthiGME en ejecución".postln;
					// Se ocultan en GUI los nodos que no tienen conexión entre módulos.
					if (guiSC != nil, {
						guiSC.panels[4].enableNodes(true); // PatchbayAudio
						guiSC.panels[5].enableNodes(true); // PatchbayVoltage
					});
				},
				onFailure: {
					"No se ha podido arrancar el servidor de audio".error;
					"Saliendo del programa...".postln;
					this.close;
					thisRoutine.stop();
				}
			);
		}).play;
	}

	// Habilita el envío y recepción de mensajes OSC desde otros dispositivos.
	pairDevice { arg searchTime = 10;
		var oscDevices = Dictionary.new;
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
		var data = Dictionary.new;

		// Oscillators:
		modulOscillators.do({|osc, num|
			var string = "/osc/" ++ (num + 1) ++ "/";
			data.put(string ++ "range", osc.range);
			data.put(string ++ "pulse/level", osc.pulseLevel);
			data.put(string ++ "pulse/shape", osc.pulseShape);
			data.put(string ++ "sine/level", osc.sineLevel);
			data.put(string ++ "sine/symmetry", osc.sineSymmetry);
			data.put(string ++ "triangle/level", osc.triangleLevel);
			data.put(string ++ "sawtooth/level", osc.sawtoothLevel);
			data.put(string ++ "frequency", osc.frequency);
		});
/*
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
*/
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