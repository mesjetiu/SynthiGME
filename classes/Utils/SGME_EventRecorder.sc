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


	*new {|synt|
		^super.new.init(synt)
	}

	init {|synt|
		synthiGME = synt;
		events = List.new;
	}

	initRecording {
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
			"Reproducción de eventos terminada.".sgmePostln;
		}.play;
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
}

