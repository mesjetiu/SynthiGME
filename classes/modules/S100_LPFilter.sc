S100_LPFilter : S100_Filter {

	*addSynthDef {
		lag = 0.01; //= S100_Settings.get[\ringLag];
		SynthDef(\S100_LPFilter, {
			arg inputBus,
			inFeedbackBus,
			outputBus,
			level,
			outVol;

			var sig;
			sig= In.ar(inputBus) + InFeedback.ar(inFeedbackBus);

			sig = SinOsc.ar; // Salida de prueba

			Out.ar(outputBus, sig);
		}, [nil, nil, nil, lag, lag]
		).add
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_LPFilter, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\level, this.convertLevel(level),
				\outVol, outVol,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
	//	this.synthRun;
	}
}