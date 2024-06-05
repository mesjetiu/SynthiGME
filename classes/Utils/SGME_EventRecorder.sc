// Define a class for the event recorder
SGME_EventRecorder {
	classvar <events; // Class variable to store events
	classvar <lastTime;
	classvar <player;
	classvar <record;
	classvar <isPlaying;


	*initClass {
		events = List.new;
		lastTime = 0;
		player = nil;
		record = false;
		isPlaying = false;
	}

	*initRecording {
		events = List.new;
		lastTime = 0;
		player = nil;
		record = true;
	}

	*stopRecording {
		record = false;
	}

	// Function to push events with timestamp
	*push { |string, value|
		var time;
		if (record == false) {^this};
		time = Main.elapsedTime;
		if (events.size == 0) {
			lastTime = time;
		};
		events.add([(time - lastTime), string, value]);
		//"Event added: [%, %, %]".format((time - lastTime), string, value).postln;
		lastTime = time;
	}

	// Function to execute events
	*play {
		isPlaying = true;
		if (events.size == 0) {
			"No events to execute.".postln;
			isPlaying = false;
			^nil;
		};

		player = Routine {
			events.do { |event, i|
				var time, string, value;
				#time, string, value = event;
				// Wait for the adjusted time for subsequent events
				time.wait;
				{SynthiGME.instance.setParameterOSC(string, value)}.defer(0);
				//("Time: %.3f, String: %s, Value: %s".format(time, string, value)).postln;
			};

			isPlaying = false;
		}.play;
	}

	*stop {
		player.stop;
		isPlaying = false;
	}


	*totalDur {
		var dur = 0;
		events.do{|event|
			dur = dur + event[0];
		}
		^dur;
	}
}

