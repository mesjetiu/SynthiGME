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

/*
// Módulos a implementar en getState()
var <modulReverb;                       // Hecho
var <modulInputAmplifiers;              // Hecho
var <modulExternalTreatmentReturns;
var <modulFilters;                      // Hecho
var <modulFilterBank;
var <modulEnvelopeShapers;              // Hecho
var <modulOscillators;                  // Hecho
var <modulNoiseGenerators;              // Hecho
var <modulRingModulators;               // Hecho
var <modulEcho;
var <modulRandomGenerator;
var <modulSlewLimiters;
var <modulOutputChannels;               // Hecho
var <modulPatchbayAudio;
var <modulPatchbayVoltage;
*/

+ SynthiGME {

	// Devuelve una colección de pares [mensaje_OSC, valor] con el estado actual de todos los módulos
	// Separado en archivo aparte por su larga extensión.
	getState {
		var data = Dictionary.new;

		// Oscillators:
		modulOscillators.do({|osc, num|
			var string = "/osc/" ++ (num + 1) ++ "/";
			data.put(string ++ "range", osc.range);
			data.put(string ++ "pulse/level", osc.pulseLevel);
			data.put(string ++ "pulse/shape", osc.pulseShape);
			data.put(string ++ "sine/level", osc.sineLevel);
			data.put(string ++ "sine/symmetry", osc.sineSymmetry);
			data.put(string ++ "triangle/level", osc.triangleLevel);
			data.put(string ++ "sawtooth/level", osc.sawtoothLevel);
			data.put(string ++ "frequency", osc.frequency);
		});

		// Filters:
		modulFilters.do({|item, num|
			var string = "/filter/" ++ (num + 1) ++ "/";
			data.put(string ++ "frequency", item.frequency);
			data.put(string ++ "response", item.response);
			data.put(string ++ "level", item.level);
		});

		// Reverb:
		modulReverb.do({|item, num|
			var string = "/reverb/";
			data.put(string ++ "mix", item.mix);
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
			data.put(string ++ "sustain", item.sustain);
			data.put(string ++ "release", item.releaseTime);
			data.put(string ++ "envelopeLevel", item.envelopeLevel);
			data.put(string ++ "signalLevel", item.signalLevel);
			data.put(string ++ "selector",  item.selector);
		});

		^data;
	}
}