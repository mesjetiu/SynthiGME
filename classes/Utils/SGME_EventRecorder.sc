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

// Define a class for the event isRecordinger
SGME_EventRecorder : SGME_GUIShortcuts{
	var <events; // Class variable to store events
	var <lastTime = 0;
	var <player;
	var <isRecording = false;
	var <isPlaying = false;
	var <synthiGME;
	var <maxPlaybackInterval = 0; // segundos de máximo intervalo temporal entre eventos. 0 == infinito
	// variables propias de GUI
	var <window, <compositeView, <playButton, <recordButton, <openButton, <saveButton, <statusText, <intervalControl;
	var <imagePlay, <imageStop, <imageRecord, <imageOpen, <imageSave;
	var <messageDisplay = "Preparado";
	var pathEvents;

	*new {|synt|
		^super.new.init(synt)
	}

	init {|synt|
		var imagesPath = SGME_Path.imagesPath +/+ "player";
		synthiGME = synt;
		imagePlay = Image(imagesPath +/+ "play");
		imageStop = Image(imagesPath +/+ "stop");
		imageRecord = Image(imagesPath +/+ "record");
		imageOpen = Image(imagesPath +/+ "open");
		imageSave = Image(imagesPath +/+ "save");
		events = List.new;
		pathEvents = SGME_Path.rootPath +/+ "Events";
		player = Routine {
			events.do { |event, i|
				var time, string, value;
				#time, string, value = event;
				// Wait for the adjusted time for subsequent events
				time.clip(0, if (maxPlaybackInterval == 0) {inf} {maxPlaybackInterval}).wait;
				{synthiGME.setParameterOSC(string, value)}.defer();
				//("Time: %.3f, String: %s, Value: %s".format(time, string, value)).postln;
			};
			isPlaying = false;
			if (window.notNil) {
				{this.updateButtons}.defer();
				{playButton.icon = imagePlay}.defer();
				messageDisplay = "Reproducción terminada";
				{statusText.string = messageDisplay}.defer();
			};
			"Reproducción de eventos terminada.".sgmePostln;
		};
	}

	startRecording {
		events = List.new;
		lastTime = 0;
		isRecording = true;
		"Grabación de eventos iniciada...".sgmePostln;
	}

	stopRecording {
		isRecording = false;
		"Grabación de eventos terminada.".sgmePostln;
	}

	// Function to push events with timestamp
	push { |string, value|
		var time;
		if (isRecording == false) {^this};
		time = Main.elapsedTime;
		if (events.size == 0) {
			lastTime = time;
		};
		events.add([(time - lastTime)/*.round(0.001)*/, string, value.round(0.01)]);
		//"Event added: [%, %, %]".format((time - lastTime), string, value).postln;
		lastTime = time;
	}

	// Function to execute events
	play {
		player.reset;
		"Reproducción de eventos iniciada...".sgmePostln;
		isPlaying = true;
		player.play;
	}

	stop {
		player.stop;
		isPlaying = false;
		"Reproducción de eventos terminada por el usuario.".sgmePostln;
	}

	totalDur {
		var dur = 0;
		events.do{|event|
			dur = dur + event[0];
		}
		^dur;
	}

	// Métodos de GUI
	// Create the window and its components
	makeWindow {
		// Get available screen bounds
		var screenBounds = Window.availableBounds;

		// Calculate the center position
		var windowWidth = 500;
		var windowHeight = 200;
		var xPos = (screenBounds.width - windowWidth) / 2 + screenBounds.left;
		var yPos = (screenBounds.height - windowHeight) / 2 + screenBounds.top;
		var rectWindow, rectCompositeView;

		if (window.notNil) {
			if (window.isClosed.not){
				^this
			} {
				window.close;
			}
		};

		rectWindow = Rect(xPos, yPos, windowWidth, windowHeight);
		rectCompositeView = Rect(0, 0, rectWindow.width, rectWindow.height);

		// Create the window
		window = Window("Grabador de eventos", rectWindow, resizable: false)
		.front;

		compositeView = CompositeView(window, rectCompositeView);

		SGME_GUIShortcuts.makeShortcuts(this, window, synthiGME);

		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			SGME_ContextualMenu.contextualMenu(synthiGME, view, x, y, modifiers, buttonNumber)
		});

		// Create the Play/Stop button
		playButton = Button(window, Rect(10, 10, 80, 80))
		.icon_(if(isPlaying){imageStop}{imagePlay})
		.iconSize_(80)
		.action_({
			this.togglePlay;
		})
		.enabled_(if(isRecording){false}{true});

		// Create the Record/Stop Record button
		recordButton = Button(window, Rect(110, 10, 80, 80))
		.icon_(if(isRecording){imageStop}{imageRecord})
		.iconSize_(80)
		.action_({
			this.toggleRecord;
		})
		.enabled_(if(isPlaying){false}{true});

		// Create the status text
		statusText = StaticText(window, Rect(220, 10, 160, 80))
		.string_(messageDisplay)
		.align_(\center);

		// Create de Open button
		openButton = Button(window, Rect(10, 110, 80, 80))
		.icon_(imageOpen)
		.iconSize_(80)
		.action_({
			this.loadEventsGUI;
		});

		// Create de Open button
		saveButton = Button(window, Rect(110, 110, 80, 80))
		.icon_(imageSave)
		.iconSize_(80)
		.action_({
			this.saveEventsGUI;
		});

		// Create the interval control
		intervalControl = EZNumber(window, Rect(390, 40, 100, 20), "Max (s)", ControlSpec(0, 3600.0, \lin, 0.1), {|ez|
			maxPlaybackInterval = ez.value;
		}, maxPlaybackInterval);
		intervalControl.setColors(Color.grey, Color.white);

		// Initial state setup
		this.updateButtons;
	}

	// Toggle play/stop functionality
	togglePlay {
		if (isPlaying) {
			this.stopPlayingGUI;
			playButton.icon = imagePlay;
		} {
			this.startPlayingGUI;
			playButton.icon = imageStop;
		};
	}

	// Toggle record/stop record functionality
	toggleRecord {
		if (isRecording) {
			this.stopRecordingGUI;
			recordButton.icon = imageRecord;
		} {
			this.startRecordingGUI;
			recordButton.icon = imageStop;
		};
	}

	// Start playing (stub function)
	startPlayingGUI {
		messageDisplay = "Reproduciendo...";
		statusText.string = messageDisplay;
		this.play;
		this.updateButtons; // Update buttons after starting to play
	}

	// Stop playing (stub function)
	stopPlayingGUI {
		messageDisplay = "Reproducción terminada";
		statusText.string = messageDisplay;
		this.stop;
		this.updateButtons; // Update buttons after stopping playing
	}

	// Start recording (stub function)
	startRecordingGUI {
		messageDisplay = "Grabando...";
		statusText.string = messageDisplay;
		this.startRecording;
		this.updateButtons; // Update buttons after starting to record
	}

	// Stop recording (stub function)
	stopRecordingGUI {
		messageDisplay = "Grabación terminada";
		statusText.string = messageDisplay;
		this.stopRecording;
		this.updateButtons; // Update buttons after stopping recording
	}

	// Update the buttons' enabled/disabled states
	updateButtons {
		// Disable action temporarily to avoid triggering toggle actions
		playButton.action_({ }); // Disable action to prevent looping
		recordButton.action_({ }); // Disable action to prevent looping

		// Update the button states
		playButton.valueAction_(isPlaying.if(1, 0));
		recordButton.valueAction_(isRecording.if(1, 0));

		// Enable or disable buttons based on current state
		if (isRecording) {
			playButton.enabled = false;
		} {
			playButton.enabled = true;
		};

		if (isPlaying) {
			recordButton.enabled = false;
		} {
			recordButton.enabled = true;
		};

		// Re-enable actions
		playButton.action_({
			this.togglePlay;
		});
		recordButton.action_({
			this.toggleRecord;
		});
	}

	goFront {
		if (window.notNil){
			window.front
		}
	}

	// Save events to a file
	saveEvents { |path, fileName|
		var archivo, exito, extension;
		extension = ".seq"; // "seq" de "Sequence"
		exito = false;

		if (path.isNil) {path = pathEvents} {pathEvents = path};

		if (File.exists(path).not) {File.mkdir(path)};

		// Añadir la extensión .seq si no está presente
		if (fileName.endsWith(extension).not) {
			fileName = fileName ++ extension;
		};

		try {
			archivo = File.new(path +/+ fileName, "w");  // Abrir el archivo para escritura
			events.do { |event|
				archivo.write(event.join(" ") + "\n");
			};
			archivo.close();  // Cerrar el archivo después de escribir
			exito = true;
		} {|error|
			// En caso de error durante la apertura o escritura del archivo
			archivo.notNil.if { archivo.close };  // Asegúrate de cerrar el archivo si se abrió
			"Error al guardar el archivo: ".sgmePostln;
			error.errorString.sgmePostln;  // Imprime el mensaje de error
		};

		if (exito) {
			"Eventos guardados correctamente en: ".sgmePostln;
			(path +/+ fileName).sgmePostln;
		};
	}

	// Load events from a file
	loadEvents { |path, fileName|
		var archivo, exito, contenido;
		exito = false;
		try {
			archivo = File.new(path +/+ fileName, "r");  // Abrir el archivo para lectura
			contenido = archivo.readAllString;  // Lee todo el contenido del archivo como un solo string
			archivo.close();  // Cierra el archivo después de leer
			exito = true;
		} {|error|
			// En caso de error durante la apertura o lectura del archivo
			archivo.notNil.if { archivo.close };  // Asegúrate de cerrar el archivo si se abrió
			"Error al cargar el archivo: ".sgmePostln;
			error.errorString.sgmePostln;  // Imprime el mensaje de error
		};

		if (exito) {
			"Archivo cargado correctamente desde: ".sgmePostln;
			(path +/+ fileName).sgmePostln;

			contenido = contenido.split($\n);  // Divide el contenido en líneas

			events = List.new;
			contenido.do { |line|
				var event;
				if (line.notEmpty) {
					// eliminamos espacios dentro de corchetes (si hay)
					line = line.replace("[ ", "[").replace(", ", ",").replace(" ]", "]");
					event = line.split($ ).collect { |item|
						if (item[0] == $/) {
							item.asString
						} {
							if (item.contains("[")) {
								// procesamos el array de floats
								item.replace("[", "").replace("]", "").split($,).collect { |num| num.asFloat }
							} {
								item.asFloat
							}
						}
					};
					events.add(event);
				}
			};


			"Eventos cargados correctamente.".sgmePostln;
			^exito;
		}
	}

	// Guardado de estado desde un diálogo de usuario:
	saveEventsGUI {
		var path = pathEvents; // Define un directorio inicial
		if (synthiGME.openDialog) {^this};
		synthiGME.openDialog = true;
		FileDialog(
			{ |path|
				synthiGME.openDialog = false;
				path.notNil.if {
					this.saveEvents(path.dirname, path.basename);
				}
			},
			{ synthiGME.openDialog = false },
			fileMode: 0,  // Permite la selección de un nombre de archivo, existente o no
			acceptMode: 1,  // Diálogo de guardado
			stripResult: true,  // Pasa la ruta del archivo directamente
			path: path  // Ruta inicial
		);
	}

	loadEventsGUI {
		var path = pathEvents; // Define un directorio inicial
		var exit = false;
		if (synthiGME.openDialog) {^this};
		synthiGME.openDialog = true;
		FileDialog(
			{ |path|
				synthiGME.openDialog = false;
				path.notNil.if {
					exit = this.loadEvents(path.dirname, path.basename);
					if (exit) { this.makeWindow; };
					messageDisplay = "Secuencia cargada";
					statusText.string = messageDisplay;
				}
			},
			{ synthiGME.openDialog = false },
			fileMode: 1,  // Modo para un archivo existente
			acceptMode: 0,  // Modo de apertura
			stripResult: true,  // Pasa la ruta del archivo directamente
			path: path  // Ruta inicial
		);
	}
}
