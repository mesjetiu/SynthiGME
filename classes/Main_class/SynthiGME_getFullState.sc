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


+ SynthiGME {

	// Devuelve una colección de pares [mensaje_OSC, valor] con el estado actual de TODOS los módulos
	// Separado en archivo aparte por su larga extensión.
	getFullState {
		var data = Dictionary.new;

		// Oscillators:
		modulOscillators.do({|item, num|
			var string = "/osc/" ++ (num + 1) ++ "/";
			data.put(string ++ "range", item.range.round(0.01));
			data.put(string ++ "pulse/level", item.pulseLevel.round(0.01));
			data.put(string ++ "pulse/shape", item.pulseShape.round(0.01));
			data.put(string ++ "sine/level", item.sineLevel.round(0.01));
			data.put(string ++ "sine/symmetry", item.sineSymmetry.round(0.01));
			data.put(string ++ "triangle/level", item.triangleLevel.round(0.01));
			data.put(string ++ "sawtooth/level", item.sawtoothLevel.round(0.01));
			data.put(string ++ "frequency", item.frequency.round(0.01));
		});

		// Filters:
		modulFilters.do({|item, num|
			var string = "/filter/" ++ (num + 1) ++ "/";
			data.put(string ++ "frequency", item.frequency.round(0.01));
			data.put(string ++ "response", item.response.round(0.01));
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Filter Bank:
		modulFilterBank.do({|item|
			var string = "/filterBank/";
			data.put(string ++ "63", item.bands[0].round(0.01));
			data.put(string ++ "125", item.bands[1].round(0.01));
			data.put(string ++ "250", item.bands[2].round(0.01));
			data.put(string ++ "500", item.bands[3].round(0.01));
			data.put(string ++ "1000", item.bands[4].round(0.01));
			data.put(string ++ "2000", item.bands[5].round(0.01));
			data.put(string ++ "4000", item.bands[6].round(0.01));
			data.put(string ++ "8000", item.bands[7].round(0.01));
		});

		// Reverb:
		modulReverb.do({|item| // solo hay uno
			var string = "/reverb/";
			data.put(string ++ "mix", item.mix.round(0.01));
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Random Generator:
		modulRandomGenerator.do({|item| // solo hay uno
			var string = "/random/";
			data.put(string ++ "mean", item.mean.round(0.01));
			data.put(string ++ "variance", item.variance.round(0.01));
			data.put(string ++ "voltage1", item.voltage1.round(0.01));
			data.put(string ++ "voltage2", item.voltage2.round(0.01));
			data.put(string ++ "key", item.key.round(0.01));
		});

		// Echo:
		modulEcho.do({|item, num|
			var string = "/echo/";
			data.put(string ++ "delay", item.delay.round(0.01));
			data.put(string ++ "mix", item.mix.round(0.01));
			data.put(string ++ "feedback", item.feedback.round(0.01));
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Noise Generators:
		modulNoiseGenerators.do({|item, num|
			var string = "/noise/" ++ (num + 1) ++ "/";
			data.put(string ++ "colour", item.colour.round(0.01));
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Output channels:
		modulOutputChannels.do({|item, num|
			var string = "/out/" ++ (num + 1) ++ "/";
			data.put(string ++ "filter", item.filter.round(0.01));
			data.put(string ++ "pan", item.pan.round(0.01));
			data.put(string ++ "on", item.on.round(0.01));
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Input Amplifiers:
		modulInputAmplifiers.do({|item, num|
			var string = "/in/" ++ (num + 1) ++ "/";
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Slew Limiters:
		modulSlewLimiters.do({|item, num|
			var string = "/slew/" ++ (num + 1) ++ "/";
			data.put(string ++ "rate", item.rate.round(0.01));
		});

		// External Treatment Returns:
		modulExternalTreatmentReturns.do({|item, num|
			var string = "/return/" ++ (num + 1) ++ "/";
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Ring Modulators:
		modulRingModulators.do({|item, num|
			var string = "/ring/" ++ (num + 1) ++ "/";
			data.put(string ++ "level", item.level.round(0.01));
		});

		// Envelope Shapers:
		modulEnvelopeShapers.do({|item, num|
			var string = "/env/" ++ (num + 1) ++ "/";
			data.put(string ++ "delay", item.delayTime.round(0.01));
			data.put(string ++ "attack", item.attackTime.round(0.01));
			data.put(string ++ "decay", item.decayTime.round(0.01));
			data.put(string ++ "sustain", item.sustainLevel.round(0.01));
			data.put(string ++ "release", item.releaseTime.round(0.01));
			data.put(string ++ "envelopeLevel", item.envelopeLevel.round(0.01));
			data.put(string ++ "signalLevel", item.signalLevel.round(0.01));
			data.put(string ++ "selector",  item.selector.round(0.01));
		});

		// Keyboards:
		modulKeyboards.do({|item, num|
			var string = "/keyboard/" ++ (num + 1) ++ "/";
			data.put(string ++ "pitch", item.pitch.round(0.01));
			data.put(string ++ "velocity", item.velocity.round(0.01));
			data.put(string ++ "gate", item.gate.round(0.01));
			data.put(string ++ "retrigger", item.retrigger);
		});

		// Invertor:
		modulInvertor.do({|item, num|
			var string = "/invertor/";
			data.put(string ++ "gain", item.gain.round(0.01));
			data.put(string ++ "offset", item.offset.round(0.01));
		});


		// Patchbay Audio:
		66.do({|v|
			var string = nil;
			var value = nil;
			60.do({|h|
				string = (h+67).asString ++ "/" ++ (v+1).asString;
				value = modulPatchbayAudio.nodeValues[string].value;
				if (value != nil) {
					string = "/patchA/" ++ string;
					data.put(string, value.round(0.01))
				}
			})
		});

		// Patchbay Volgate:
		66.do({|v|
			var string = nil;
			var value = nil;
			60.do({|h|
				string = (h+67).asString ++ "/" ++ (v+1).asString;
				value = modulPatchbayVoltage.nodeValues[string].value;
				if (value != nil) {
					string = "/patchV/" ++ string;
					data.put(string, value.round(0.01))
				}
			})
		});

		^data;
	}
}