// Define the SimpleRecorderGUI class
SGME_SimpleRecorderGUI {
    var <window, <playButton, <recordButton;
    var isPlaying = false;
    var isRecording = false;

    // Constructor
    *new {
        ^super.new.init;
    }

    // Initialize the GUI
    init {
        this.makeWindow;
    }

    // Create the window and its components
    makeWindow {
        if (window.notNil) {
            window.close;
        };

        // Create the window
        window = Window("Simple Recorder", Rect(100, 100, 300, 100)).front;

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
            this.stopPlaying;
        } {
            this.startPlaying;
        };
    }

    // Toggle record/stop record functionality
    toggleRecord {
        if (isRecording) {
            this.stopRecording;
        } {
            this.startRecording;
        };
    }

    // Start playing (stub function)
    startPlaying {
        isPlaying = true;
        "Playing...".postln; // Replace with actual play function
        this.updateButtons; // Update buttons after starting to play
    }

    // Stop playing (stub function)
    stopPlaying {
        isPlaying = false;
        "Stopped playing.".postln; // Replace with actual stop function
        this.updateButtons; // Update buttons after stopping playing
    }

    // Start recording (stub function)
    startRecording {
        isRecording = true;
        "Recording...".postln; // Replace with actual record function
        this.updateButtons; // Update buttons after starting to record
    }

    // Stop recording (stub function)
    stopRecording {
        isRecording = false;
        "Stopped recording.".postln; // Replace with actual stop recording function
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
