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

+ SynthiGME {
	run {
		var thisRoutine, splashWindow;

		if (isRunning || (connectionOut != nil)) {"SynthiGME en ejecución".error; ^this};
		isRunning = true;

		splashWindow = SGME_SplashWindow();
		splashWindow.showSplashWindow;
		splashWindow.numSteps = 23;

		thisRoutine = Routine({
			// Comprobamos si coinciden las opciones pedidas con las opciones actuales del Server:
			var serverOptionsOK = false; // true si lo pedido coincide con las opciones actuales

			if (server.isNil) {
				var address, port, remoteServer, isFree, serverOptions, serverName, numIntentos;
				"Buscando puertos libres para un nuevo servidor de audio".sgmePostln;
				port = 57200;
				numIntentos = 5; // Además del número de intentos, representa el número máximo de instancias en la misma máquina.

				// Rutina para encontrar un puerto libre
				isFree = false;
				while ({ isFree.not && (port < (port+numIntentos)) }, {
					address = NetAddr.new("127.0.0.1", port);
					serverName = ("synthiGME_" ++ Date.seed.asHexString).asSymbol;
					serverOptions = ServerOptions();
					serverOptions.maxLogins = 2;

					// Intentar conectar a un servidor remoto
					remoteServer = Server.remote(serverName, address, serverOptions);
					//remoteServer.connect;

					wait(1); //este tiempo de espera es indispensable para que el Server se comunique con el eventual server que está en el puerto dado. Menor tiempo da serverRunning == false, aunque no sea cierto.


					// Verificar si el servidor remoto está corriendo
					if (remoteServer.serverRunning.not) {
						isFree = true;
					} {
						remoteServer.notify = false;
						port = port + 1;
					}
				});

				if (isFree) {
					server = remoteServer;
					("Servidor creado en " + address).sgmePostln;
				} {
					("No se encontró un puerto libre para crear un nuevo servidor.").error;
					^this
				}
			};

			if (server.isNil) {"No se ha encontrado un puerto libre para crear un nuevo servidor".sgmePostln; this.close};

			if (
				server.options.numOutputBusChannels == numOutputChannels,
				{serverOptionsOK = true},
				{serverOptionsOK = false}
			);

			// Apaga el servidor si es necesario
			if (
				(server.serverRunning && (serverOptionsOK == false || alwaysRebootServer)),
				{
					"El servidor de audio está encendido. Apagando servidor...".sgmePostln;
					server.quit;
					//server.sync;
					if (server.serverRunning, {
						"Servidor no apagado correctamente".error;
						"Saliendo del programa...".sgmePostln;
						thisRoutine.stop();
					}, {
						"Servidor apagado correctamente".sgmePostln;
					});
			});


			if (
				(serverOptionsOK == false) && (server.serverRunning == false),
				{
					"Estableciendo número correcto de canales de entrada y salida:".sgmePostln;
					server.options.device_("Synthi GME")
					.numAudioBusChannels_(settings[\numAudioBusChannels])
					//.numOutputBusChannels_(settings[\numOutputBusChannels])
					.numOutputBusChannels_(numOutputChannels)
					.numInputBusChannels_(numInputChannels + numReturnChannels)
					.blockSize_(blockSize) // Control rate. Si es hardware lo permite se puede aproximar a 1
					.bindAddress_("0.0.0.0")
					.maxLogins_(2);
					server.latency = 0.03;
					//server.sync;

					("Número de canales de Audio:" + server.options.numAudioBusChannels).sgmePostln;
					("Número de canales de output:" + server.options.numOutputBusChannels).sgmePostln;
					("Número de canales de input:" + server.options.numInputBusChannels).sgmePostln;
					("Tamaño del bloque:" + server.options.blockSize).sgmePostln;
					("Latencia del servidor:" + server.latency).sgmePostln;

					if(
						server.options.numAudioBusChannels >= settings[\numAudioBusChannels]
						//&& server.options.numOutputBusChannels >= settings[\numOutputBusChannels]
						&& server.options.numOutputBusChannels >= numOutputChannels
						&& server.options.numInputBusChannels >= numInputChannels
						&& server.options.blockSize >= blockSize
					){
						"Opciones actualizadas correctamente".sgmePostln;
					}{
						"No se han podido establecer las opciones adecuadas del servidor".error;
						"Saliendo del programa...".sgmePostln;
						thisRoutine.stop();
					};

				}
			);


			// Se anuncia que se arrancará el servidor (si no lo está)
			if(server.serverRunning == false, {"Arrancando servidor...".sgmePostln});
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
					modulOscilloscope = SGME_Oscilloscope(server);
					modulKeyboards = 2.collect({|n| SGME_Keyboard(server, n+1)});
					modulInvertor = SGME_Invertor(server);

					server.sync;

					2.do({"".sgmePostln}); // líneas en blanco para mostrar después todos los mensajes de arranque




					"Conexión de salida stereo canales 1 a 8 mezclados a salidas 1 y 2".sgmePostln;
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
					splashWindow.progress();

					if (
						numOutputChannels >= 4,
						{
							"Conexión de salida stereo canales 1 - 4 a salidas 3 y 4".sgmePostln;
							connectionOut = connectionOut.add({
								var result = nil;
								var channels = modulOutputChannels[0..3]; // canales 1-4 para mezclar
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

					splashWindow.progress();

					if (
						numOutputChannels >= 6,
						{
							"Conexión de salida stereo canales 5 - 8 a salidas 5 y 6".sgmePostln;
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
									\outBusL, 4,
									\outBusR, 5,
									\vol, generalVol,
								], server).register;
							}.value);
							server.sync;
						}
					);

					splashWindow.progress();

					//"Conexión de salida de cada canal individual...".sgmePostln;
					modulOutputChannels.do({|out, n|
						if (n+7 <= numOutputChannels //server.options.numOutputBusChannels
							//numOutputChannels >= (n+7)
						)
						{
							connectionOut = connectionOut.add({
								var result = nil;
								("Output Channel" + (n+1) + "conectado a salida" + (n+7)).sgmePostln;
								result = Synth(\connectionMono, [
									\inputBus, out.outputBus, // En este momento la salida mono sale prefader (se puede cambiar fácilmente)
									\outputBus, settings[\individualChannelOutputsBusses][n]-1,
									\vol, generalVol,
								], server).register;
								server.sync;
								result;
							}.value);
						}
					});

					splashWindow.progress();


					// Se arrancan todos los Synths de todos los módulos //////////////////////////////////

					// Output Channels
					"Output Channels...".sgmePostln;
					modulOutputChannels.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Reverb
					"Reverb...".sgmePostln;
					modulReverb.createSynth;
					server.sync;
					splashWindow.progress();

					// Filters
					"Filters...".sgmePostln;
					modulFilters.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Filters
					"Octave Filter Bank...".sgmePostln;
					modulFilterBank.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Ring Modulators
					"Ring Modulators...".sgmePostln;
					modulRingModulators.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Echo A. D. L.
					"Echo A.D.L...".sgmePostln;
					modulEcho.createSynth;
					server.sync;
					splashWindow.progress();

					// Noise Generators
					"Noise Generators...".sgmePostln;
					modulNoiseGenerators.do({|i|
						i.createSynth;
						server.sync;
					});

					// Random Generator
					"Random Voltage Generator...".sgmePostln;
					modulRandomGenerator.createSynth;
					server.sync;
					splashWindow.progress();

					// Slew Limiters
					"Slew Limiters...".sgmePostln;
					modulSlewLimiters.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Oscillators
					"Oscillators...".sgmePostln;
					modulOscillators.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Envelope Shapers
					"Envelope Shapers...".sgmePostln;
					modulEnvelopeShapers.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Input Amplifier
					inputAmplifiersBusses = modulInputAmplifiers.collect({|i| i.inputBus});
					"Input Amplifiers Level...".sgmePostln;
					modulInputAmplifiers.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// External Treatment Returns
					returnFromDeviceBusses = modulExternalTreatmentReturns.collect({|i| i.inputBus});
					"External Treatment Returns...".sgmePostln;
					modulExternalTreatmentReturns.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Oscilloscope
					"Oscilloscope...".sgmePostln;
					modulOscilloscope.createSynth;
					server.sync;
					splashWindow.progress();

					// keyboards
					"Keyboards...".sgmePostln;
					modulKeyboards.do({|i|
						i.createSynth;
						server.sync;
					});
					splashWindow.progress();

					// Oscilloscope
					"Invertor/Buffer...".sgmePostln;
					modulInvertor.createSynth;
					server.sync;
					splashWindow.progress();

					// conexiones de entrada y salida de cada módulo en el patchbay de audio
					"Conexiones en Patchbay de audio...".sgmePostln;
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
						oscilloscope: modulOscilloscope,
					);
					splashWindow.progress();

					// conecta cada entrada y salida de cada módulo en el patchbay de voltaje
					"Conexiones en Patchbay de voltage...".sgmePostln;
					modulPatchbayVoltage.connect(
						reverb: modulReverb,
						echo: modulEcho,
						inputAmplifiers: modulInputAmplifiers,
						filters: modulFilters,
						envelopeShapers: modulEnvelopeShapers,
						oscillators: modulOscillators,
						randomGenerator: modulRandomGenerator,
						slewLimiters: modulSlewLimiters,
						oscilloscope: modulOscilloscope,
						outputChannels: modulOutputChannels,
						keyboards: modulKeyboards,
						invertor: modulInvertor,
					);
					splashWindow.progress();


					//"Conexión de entrada Input Amplifiers, canales 1 a 8 a puertos de SC...".sgmePostln;
					connectionIn = inputAmplifiersBusses.collect({|item, i|
						if (//i+1 <= server.options.numInputBusChannels
							(i+1) <= numInputChannels
						)
						{
							var result = Synth(\connectionInputAmplifier, [
								\inBus, settings[\inputAmplifiersBusses][i],
								\outBus, item,
								\vol, 1,
							], server).register;
							("Input Channel" + (i+1) + "conectado a entrada" + (i+1)).sgmePostln;
							server.sync;
							result
						}
					});
					splashWindow.progress();

					//"Conexión de entrada External Treatment Returns, canales 1 a 4 a puertos de SC...".sgmePostln;
					connectionIn = connectionIn ++ returnFromDeviceBusses.collect({|item, i|
						if (//i+8 <= server.options.numInputBusChannels
							i <= numReturnChannels
						)
						{
							var result = Synth(\connectionExternalTreatmentReturn, [
								\inBus, settings[\returnFromDeviceBusses][i],
								\outBus, item,
								\vol, 1,
							], server).register;
							("External Input Channel" + (i+1) + "conectado a entrada" + (i+9)).sgmePostln;
							server.sync;
							result
						}
					});
					splashWindow.progress();

					/* No se ejecuta enableNodes ya que los nodos no implementados no están dibujados.
					// Se ocultan en GUI los nodos que no tienen conexión entre módulos.
					if (guiSC != nil, {
					guiSC.panels[4].enableNodes(true); // PatchbayAudio
					guiSC.panels[5].enableNodes(true); // PatchbayVoltage
					});
					*/

					// Se abre puerto para recibir mensajes OSC
					if (thisProcess.openUDPPort(devicePort)) {
						("Abierto puerto" + devicePort + "para OSC.").sgmePostln;} {
						("No se ha podido abrir el puerto" + devicePort + "para OSC").sgmePostln;
					};
					splashWindow.progress();

					// Se lanza todo el sistema gráfico de ventanas (antes que el MIDI porque retrasa mucho la aparición de las ventanas)
					guiSC.makeWindow;

					modulKeyboards[0].initMIDI;
					"Inicializados puertos y funciones MIDI".sgmePostln;
					("Escuchando Upper Manual por canal MIDI" + modulKeyboards[0].midiChannel).sgmePostln;
					("Escuchando Lower Manual por canal MIDI" + modulKeyboards[1].midiChannel).sgmePostln;
					//splashWindow.progress();



					wait (0.1); // Para que le de tiempo a iniciarse correctamente antes de guardar el estado.

					// Se almacena el estado inicial de todos los parámetros:
					initState = this.getFullState;
					(initState.size.asString + "parámetros iniciados a sus valores por defecto.").sgmePostln;

					if (myIp.isNil == false) {
						("La IP de la red local es:" + myIp).sgmePostln;
					} {
						"No se ha podido obtener la IP de red local".error;
					};

					// Preparación para la grabación:
				//	server.prepareForRecord;

					"\n".postln;
					("*** SynthiGME (" ++ version ++ ") en ejecución ***\n").sgmePostln;


				},
				onFailure: {
					"No se ha podido arrancar el servidor de audio".error;
					"Saliendo del programa...".sgmePostln;
					this.close;
					thisRoutine.stop();
				}
			);
		}).play;
	}
}