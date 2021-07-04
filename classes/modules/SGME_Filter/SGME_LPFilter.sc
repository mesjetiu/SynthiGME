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

Copyright 2020 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_LPFilter : SGME_Filter {

	*addSynthDef {
		lag = 0.2; //= SGME_Settings.get[\ringLag];
		SynthDef(\SGME_LPFilter, {
			arg inputBus,
			inFeedbackBus,
			inputBusVoltage,
			inFeedbackBusVoltage,
			outputBus,
			frequency,
			response,
			level,
			outVol;

			var sigIn, sigOut, inVoltage, freq;
			sigIn = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);

			sigIn = sigIn + WhiteNoise.ar(0.001); // Ruido no audible para poder crear sonido sin entrada
			inVoltage = In.ar(inputBusVoltage) + InFeedback.ar(inFeedbackBusVoltage);

			freq = inVoltage.linlin(-1, 1, -1000, 1000, nil);
			freq = (freq + frequency).clip(5, 20000).poll;

			sigOut = BLowPass.ar(sigIn, freq, response) * level;

			Out.ar(outputBus, sigOut);
		}, [nil, nil, nil, nil, nil, lag, lag, lag, lag]
		).add
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_LPFilter, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\inputBusVoltage, inputBusVoltage,
				\inFeedbackBusVoltage, inFeedbackBusVoltage,
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