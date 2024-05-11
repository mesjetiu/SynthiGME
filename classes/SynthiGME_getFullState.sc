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
			data.put(string ++ "range", item.range);
			data.put(string ++ "pulse/level", item.pulseLevel);
			data.put(string ++ "pulse/shape", item.pulseShape);
			data.put(string ++ "sine/level", item.sineLevel);
			data.put(string ++ "sine/symmetry", item.sineSymmetry);
			data.put(string ++ "triangle/level", item.triangleLevel);
			data.put(string ++ "sawtooth/level", item.sawtoothLevel);
			data.put(string ++ "frequency", item.frequency);
		});

		// Filters:
		modulFilters.do({|item, num|
			var string = "/filter/" ++ (num + 1) ++ "/";
			data.put(string ++ "frequency", item.frequency);
			data.put(string ++ "response", item.response);
			data.put(string ++ "level", item.level);
		});

		// Filter Bank:
		modulFilterBank.do({|item|
			var string = "/filterBank/";
			data.put(string ++ "63", item.bands[0]);
			data.put(string ++ "125", item.bands[1]);
			data.put(string ++ "250", item.bands[2]);
			data.put(string ++ "500", item.bands[3]);
			data.put(string ++ "1000", item.bands[4]);
			data.put(string ++ "2000", item.bands[5]);
			data.put(string ++ "4000", item.bands[6]);
			data.put(string ++ "8000", item.bands[7]);
		});

		// Reverb:
		modulReverb.do({|item| // solo hay uno
			var string = "/reverb/";
			data.put(string ++ "mix", item.mix);
			data.put(string ++ "level", item.level);
		});

		// Random Generator:
		modulRandomGenerator.do({|item| // solo hay uno
			var string = "/random/";
			data.put(string ++ "mean", item.mean);
			data.put(string ++ "variance", item.variance);
			data.put(string ++ "voltage1", item.voltage1);
			data.put(string ++ "voltage2", item.voltage2);
			data.put(string ++ "key", item.key);
		});

		// Echo:
		modulEcho.do({|item, num|
			var string = "/echo/";
			data.put(string ++ "delay", item.delay);
			data.put(string ++ "mix", item.mix);
			data.put(string ++ "feedback", item.feedback);
			data.put(string ++ "level", item.level);
		});

		// Noise Generators:
		modulNoiseGenerators.do({|item, num|
			var string = "/noise/" ++ (num + 1) ++ "/";
			data.put(string ++ "colour", item.colour);
			data.put(string ++ "level", item.level);
		});

		// Output channels:
		modulOutputChannels.do({|item, num|
			var string = "/out/" ++ (num + 1) ++ "/";
			data.put(string ++ "filter", item.filter);
			data.put(string ++ "pan", item.pan);
			data.put(string ++ "on", item.on);
			data.put(string ++ "level", item.level);
		});

		// Input Amplifiers:
		modulInputAmplifiers.do({|item, num|
			var string = "/in/" ++ (num + 1) ++ "/";
			data.put(string ++ "level", item.level);
		});

		// Slew Limiters:
		modulSlewLimiters.do({|item, num|
			var string = "/slew/" ++ (num + 1) ++ "/";
			data.put(string ++ "rate", item.rate);
		});

		// External Treatment Returns:
		modulExternalTreatmentReturns.do({|item, num|
			var string = "/return/" ++ (num + 1) ++ "/";
			data.put(string ++ "level", item.level);
		});

		// Ring Modulators:
		modulRingModulators.do({|item, num|
			var string = "/ring/" ++ (num + 1) ++ "/";
			data.put(string ++ "level", item.level);
		});

		// Envelope Shapers:
		modulEnvelopeShapers.do({|item, num|
			var string = "/env/" ++ (num + 1) ++ "/";
			data.put(string ++ "delay", item.delayTime);
			data.put(string ++ "attack", item.attackTime);
			data.put(string ++ "decay", item.decayTime);
			data.put(string ++ "sustain", item.sustainLevel);
			data.put(string ++ "release", item.releaseTime);
			data.put(string ++ "envelopeLevel", item.envelopeLevel);
			data.put(string ++ "signalLevel", item.signalLevel);
			data.put(string ++ "selector",  item.selector);
		});


/*	var <modulPatchbayAudio;
	var <modulPatchbayVoltage;*/

		// Patchbay Audio:
		66.do({|v|
			var string = nil;
			var value = nil;
			60.do({|h|
				string = (h+67).asString ++ "/" ++ (v+1).asString;
				value = modulPatchbayAudio.nodeValues[string].value;
				if (value != nil) {
					string = "/patchA/" ++ string;
					data.put(string, value)
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
					data.put(string, value)
				}
			})
		});

		^data;
	}
}