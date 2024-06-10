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
	var <window, <playButton, <recordButton, <statusText, <intervalControl;
	var <imagePlay, <imageStop, <imageRecord;

	*new {|synt|
		^super.new.init(synt)
	}

	init {|synt|
		var imagesPath = SGME_Path.imagesPath +/+ "player";
		synthiGME = synt;
		imagePlay = Image(imagesPath +/+ "play");
		imageStop = Image(imagesPath +/+ "stop");
		imageRecord = Image(imagesPath +/+ "record");
		events = List.new;
		player = Routine {};
	}

	startRecording {
		events = List.new;
		lastTime = 0;
		player = nil;
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
		events.add([(time - lastTime), string, value]);
		//"Event added: [%, %, %]".format((time - lastTime), string, value).postln;
		lastTime = time;
	}

	// Function to execute events
	play {
		"Reproducción de eventos iniciada...".sgmePostln;
		isPlaying = true;
		player = Routine {
			events.do { |event, i|
				var time, string, value;
				#time, string, value = event;
				// Wait for the adjusted time for subsequent events
				time.clip(0, if (maxPlaybackInterval == 0) {inf} {maxPlaybackInterval}).wait;
				{synthiGME.setParameterOSC(string, value)}.defer(0);
				//("Time: %.3f, String: %s, Value: %s".format(time, string, value)).postln;
			};
			isPlaying = false;
			if (window.isNil.not) {
				{this.updateButtons}.defer(0);
				{playButton.icon = imagePlay}.defer(0);
				{statusText.string = "Reproducción terminada"}.defer(0);
			};
			"Reproducción de eventos terminada.".sgmePostln;
		}.play;
	}

	stop {
		player.stop;
		player.reset;
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
		var windowHeight = 100;
		var xPos = (screenBounds.width - windowWidth) / 2 + screenBounds.left;
		var yPos = (screenBounds.height - windowHeight) / 2 + screenBounds.top;

		if (window.notNil) {
			if (window.isClosed.not){
				^this
			} {
				window.close;
			}
		};

		// Create the window
		window = Window("Grabador de eventos", Rect(xPos, yPos, windowWidth, windowHeight), resizable: false)
		.front;

		SGME_GUIShortcuts.makeShortcuts(this, window, synthiGME);

		// Create the Play/Stop button
		playButton = Button(window, Rect(10, 10, 80, 80))
		.icon_(imagePlay)
		.iconSize_(80)
		.action_({
			this.togglePlay;
		});

		// Create the Record/Stop Record button
		recordButton = Button(window, Rect(110, 10, 80, 80))
		.icon_(imageRecord)
		.iconSize_(80)
		.action_({
			this.toggleRecord;
		});

		// Create the status text
		statusText = StaticText(window, Rect(220, 10, 160, 80))
		.string_("Preparado")
		.align_(\center);

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
		statusText.string = "Reproduciendo...";
		this.play;
		this.updateButtons; // Update buttons after starting to play
	}

	// Stop playing (stub function)
	stopPlayingGUI {
		statusText.string = "Reproducción terminada";
		this.stop;
		this.updateButtons; // Update buttons after stopping playing
	}

	// Start recording (stub function)
	startRecordingGUI {
		statusText.string = "Grabando...";
		this.startRecording;
		this.updateButtons; // Update buttons after starting to record
	}

	// Stop recording (stub function)
	stopRecordingGUI {
		statusText.string = "Grabación terminada";
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
		if (window.isNil.not){
			window.front
		}
	}
}
