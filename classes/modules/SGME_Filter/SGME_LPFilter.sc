SGME_LPFilter : SGME_Filter {

	*addSynthDef {
		lag = 0.2; //= SGME_Settings.get[\ringLag];
		SynthDef(\SGME_LPFilter, {
			arg inputBus,
			inFeedbackBus,
			outputBus,
			frequency,
			response,
			level,
			outVol;

			var sigIn, sigOut;
			sigIn = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);

			sigIn = sigIn + WhiteNoise.ar(0.001); // Ruido no audible para poder crear sonido sin entrada
			sigOut = BLowPass.ar(sigIn, frequency, response) * level;

			Out.ar(outputBus, sigOut);
		}, [nil, nil, nil, lag, lag, lag, lag]
		).add
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_LPFilter, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\frequency, this.convertFrequency(frequency),
				\response, this.convertResponse(response),
				\level, this.convertLevel(level),
				\outVol, outVol,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}
}