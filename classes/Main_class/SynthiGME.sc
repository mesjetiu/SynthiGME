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

	classvar <version = "1.8.1";
	classvar <appName = "SynthiGME"; // Nombre del Quark o extensión

	// Opciones de inicio:
	var <server; // Servidor de audio a utilizar
	var <>verboseOSC; // true: se muestran en Post Window los mensajes OSC enviados al synthi.
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
	var <modulOscilloscope;

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

	// Network address
	var <netAddr;
	// Función que se utiliza para escuchar todos los puertos OSC. Es variable de clase para poder añadirla y suprimirla desde cualquier instancia.
	classvar functionOSC = nil;
	// Puerto por defecto de envío de mensajes OSC (por defecto en TouchOSC)
	var <>devicePort;
	// ip de la red local para evitar ecos de mensajes OSC
	var <>myIp;
	// Puede o no enviar mensajes OSC a la red
	var <>canSendOSC;
	// Puede o no recibir mensajes OSC de la red
	var <>canRecieveOSC;


	// ****** Variables para rastrear, guardar y recuperar estados (patches)
	// Diccionario con todos los estados de todos los parámetros nada más iniciar. Se rellena con getFullState()
	var initState;
	// Diccionario que guarda el último valor de cada string recibido de OSC
	var <oscRecievedMessages;
	// Path donde se guardan los estados
	var <pathState;
	// será true en el momento que se haga un cambio en el patch. Utilizado para preguntar guardar antes de salir.
	var modifiedState = false;
	var openDialog = false; //Si hay diálogo abierto, no es nil.

	// Interfáz gráfica de SuperCollider (GUI)
	var <guiSC = nil;
	classvar appPath = nil; // Path de la aplicación

	// Otras opciones.
	var <generalVol;

	var <isRunning = false; // Indica si se ha ejecutado run(), para que no se le llame dos veces.

	classvar <settings;
	classvar <instance; // aquí se guardará la instancia del Synthi, ya que solo se podrá tener una abierta.

	var <postWindow; // Instancia única (singleton) de Post Window



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
		Class.initClassTree(SGME_Oscilloscope);
		Class.initClassTree(SGME_GUI);

	//	appPath = SynthiGME.getAppPath();
	}

	*new {
		arg server = Server.local,
		/*gui = true,*/
		verboseOSC = true, // Muestra en Post window todo mensaje OSC procesado
		numOutputChannels = inf, // Número de canales de salida unidos a salidas de SC. Mínimo 2 (2 canales por defecto del sistema) Máximo 16
		numInputChannels = inf, // Mínimo 2 (del sistema por defecto) Máximo 8
		numReturnChannels = inf, // Mínimo 0, Máximo 4
		blockSize = 64,
		alwaysRebootServer = false, // false: no se reinicia si se cumple la configuración del servidor.
		postWin = true; // se abre una ventana para post window.

		// Se guarda la instancia:
		if (instance != nil) {"Ya existe una instancia"; ^this};

		^super.new.init(server, /*gui,*/ verboseOSC, numOutputChannels.clip(2,14).asInteger, numInputChannels.clip(2,8).asInteger, numReturnChannels.clip(0,4).asInteger, blockSize, alwaysRebootServer, postWin);
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
			sigR = In.ar([inBusR1, inBusR2, inBusR3, inBusR4, inBusR5, inBusR6, inBusR7, inBusR8]);
			Out.ar(outBusL, sigL.sum * vol);
			Out.ar(outBusR, sigR.sum * vol);
		}).add;
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init {|serv, /*gui,*/ verboseOSC, numOutputChan, numInputChan, numReturnChan, blockSiz, alwaysRebootServ, postWin|

		instance = this;
		//version = "1.8.0";
		//appName = "SynthiGME";

		// Carga la configuración
		settings = SGME_Settings.get;

		//pathState = Platform.userHomeDir; // path por defecto donde guardar estados
		oscRecievedMessages = Dictionary.new;

		// Pasos para cargar imágenes en las clases que las necesitan:
		SGME_Path.initPath; // Se consigue el Path de aplicación (Quark o Extension)
		appPath = SGME_Path.rootPath;
		pathState = appPath +/+ "Patches";
		if (appPath.isNil) {"No se ha podido obtener el path de la aplicación".error; ^this};
		SGME_GUIPanel.loadImages; // Se cargan las imágenes desde el Path adecuado
		SGME_GUINode.loadImages; // ídem

		// Post window preparado y abierto:
		postWindow = MessageRedirector.getInstance;

		guiSC = SGME_GUI(this, postWin); // Se arranca finalmente la GUI
		// if(gui == true, {guiSC.makeWindow}); // por ahora la GUI es obligatoria. No funciona bien sin ella.

		generalVol = settings[\generalVol];
		devicePort = settings[\OSCDevicePort];

		// Se prepara OSC
		this.prepareOSC;
		canSendOSC = true;
		canRecieveOSC = true;

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
		SGME_Oscilloscope.addSynthDef;




		this.run;
	}


	// Métodos de instancia /////////////////////////////////////////////////////////////////////////

	// EXTENSIONES DE LA CLASE **********************************************************
	// run() en SynthiGME_run.sc
	// getFullState() en SynthiGME_getFullState.sc
	// Herramientas de guardado y recuperación de patches en archivos, en SynthiGME_file_tools.sc

	// OBSOLETO. QUIZÁS APROVECHABLE...
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
			"Dispositivos comunicados por OSC preparados OK".sgmePostln;
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

	// Libera todos los Synths del servidor y cierra la GUI
	close {
		var screenBounds = Window.availableBounds;
		var windowWidth = 300;
		var windowHeight = 100;
		var xPos = (screenBounds.width - windowWidth) / 2;
		var yPos = (screenBounds.height - windowHeight) / 2;

		var dialog = Window("Salir de SynthiGME", Rect(xPos, yPos, windowWidth, windowHeight));
		var mainText, buttonLayout;
		var discardButton, cancelButton, saveButton, acceptButton;

		if (openDialog) {^this};
		openDialog = true;

		if (modifiedState) {
			mainText = "¿Desea guardar el patch actual?";
			buttonLayout = HLayout(
				discardButton = Button().states_([["Descartar", Color.black, Color.white]]).action_({
					// Acción para descartar cambios y salir
					"Descartando cambios y saliendo...".sgmePostln;
					// Aquí iría la lógica para salir de la aplicación
					dialog.close;
					this.exit;
				}),
				cancelButton = Button().states_([["Cancelar", Color.black, Color.white]]).action_({
					// Acción para cancelar el cierre
					openDialog = false;
					dialog.close;
					// Salir de la función de cierre actual
					^nil;
				}),
				saveButton = Button().states_([["Guardar", Color.black, Color.white]]).action_({
					// Acción para guardar y salir
					openDialog = false;
					this.saveStateGUI;
					dialog.close;
					// Aquí puedes continuar con la función de salir existente
				})
			);
		} {
			mainText = "¿Desea salir?";
			buttonLayout = HLayout(
				acceptButton = Button().states_([["Aceptar", Color.black, Color.white]]).action_({
					// Acción para aceptar y salir
					openDialog = false;
					dialog.close;
					this.exit;
				}),
				cancelButton = Button().states_([["Cancelar", Color.black, Color.white]]).action_({
					// Acción para cancelar el cierre
					openDialog = false;
					dialog.close;
					// Salir de la función de cierre actual
					^nil;
				})
			);

		};
		dialog.layout = VLayout(
			StaticText().string_(mainText),
			buttonLayout
		);

		dialog.front;
	}

	exit {
		{Window.closeAll}.defer(0);
		server.freeAll;
		modulRandomGenerator.randomRoutine.stop;
		if (Platform.ideName == "none", { // Si se está ejecutando desde una terminal
			0.exit;
		});
		thisProcess.recompile;
	}

	// Actualiza SynthiGME:
	update {
		Quark(appName).update;
		"Para que la actualización tenga efecto, es necesario recompilar la biblioteca de clases, con Ctrl + Shift + L, o abriendo y cerrando SuperCollider.".sgmePostln;
	}

}