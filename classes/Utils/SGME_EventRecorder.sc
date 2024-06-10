// Define a class for the event isRecordinger
SGME_EventRecorder {
	var <events; // Class variable to store events
	var <lastTime = 0;
	var <player;
	var <isRecording = false;
	var <isPlaying = false;
	var <synthiGME;
	// variables propias de GUI
	var <window, <playButton, <recordButton;
	var <imagePlay, <imageStop, <imageRecord;


	*new {|synt|
		^super.new.init(synt)
	}

	init {|synt|
		var imagesPath = SGME_Path.imagesPath +/+ "player";
		synthiGME = synt;
		imagePlay = imagesPath +/+ "play";
		imageStop = imagesPath +/+ "stop";
		imageRecord = imagesPath +/+ "record";
		events = List.new;
		player = Routine {};
	//	this.makeWindow;
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
				time.wait;
				{synthiGME.setParameterOSC(string, value)}.defer(0);
				//("Time: %.3f, String: %s, Value: %s".format(time, string, value)).postln;
			};
			isPlaying = false;
			if (window.isNil.not) {
				{this.updateButtons}.defer(0);
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
        var windowWidth = 300;
        var windowHeight = 100;
        var xPos = (screenBounds.width - windowWidth) / 2 + screenBounds.left;
        var yPos = (screenBounds.height - windowHeight) / 2 + screenBounds.top;

        if (window.notNil) {
            window.close;
        };

        // Create the window
        window = Window("Grabador de eventos", Rect(xPos, yPos, windowWidth, windowHeight), resizable: false)
		.front;

        // Create the Play/Stop button
        playButton = Button(window, Rect(10, 10, 80, 30))
            .states_([
                ["Play"], // Default color
                ["Stop"]  // Default color
            ])
            .action_({
                this.togglePlay;
            });

        // Create the Record/Stop Record button
        recordButton = Button(window, Rect(110, 10, 80, 30))
            .states_([
                ["Record"], // Default color
                ["Stop Rec"] // Default color
            ])
            .action_({
                this.toggleRecord;
            });

        // Initial state setup
        this.updateButtons;
    }

    // Toggle play/stop functionality
    togglePlay {
        if (isPlaying) {
            this.stopPlayingGUI;
        } {
            this.startPlayingGUI;
        };
    }

    // Toggle record/stop record functionality
    toggleRecord {
        if (isRecording) {
            this.stopRecordingGUI;
        } {
            this.startRecordingGUI;
        };
    }

    // Start playing (stub function)
    startPlayingGUI {
		this.play;
        this.updateButtons; // Update buttons after starting to play
    }

    // Stop playing (stub function)
    stopPlayingGUI {
		this.stop;
        this.updateButtons; // Update buttons after stopping playing
    }

    // Start recording (stub function)
    startRecordingGUI {
		this.startRecording;
        this.updateButtons; // Update buttons after starting to record
    }

    // Stop recording (stub function)
    stopRecordingGUI {
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
}

