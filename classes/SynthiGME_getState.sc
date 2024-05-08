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
		/*
		// Noise Generators:
		modulNoiseGenerators.do({|ng, num|
		var string = "/noise/" ++ (num + 1) ++ "/";
		data.add([string ++ "colour", ng.colour]);
		data.add([string ++ "level", ng.level]);
		});

		// Output channels:
		modulOutputChannels.do({|oc, num|
		var string = "/out/" ++ (num + 1) ++ "/";
		data.add([string ++ "filter", oc.filter]);
		data.add([string ++ "pan", oc.pan]);
		data.add([string ++ "on", oc.on]);
		data.add([string ++ "level", oc.level]);
		});

		// Input Amplifiers:
		modulInputAmplifiers.do({|ia, num|
		var string = "/in/" ++ (num + 1) ++ "/";
		data.add([string ++ "level", ia.level]);
		});

		// Ring Modulators:
		modulRingModulators.do({|ring, num|
		var string = "/ring/" ++ (num + 1) ++ "/";
		data.add([string ++ "level", ring.level]);
		});

		// Envelope Shapers:
		modulEnvelopeShapers.do({|env, num|
		var string = "/env/" ++ (num + 1) ++ "/";
		data.add([string ++ "delay", env.delayTime]);
		data.add([string ++ "attack", env.attackTime]);
		data.add([string ++ "decay", env.decayTime]);
		data.add([string ++ "sustain", env.sustain]);
		data.add([string ++ "release", env.releaseTime]);
		data.add([string ++ "envelopeLevel", env.envelopeLevel]);
		data.add([string ++ "signalLevel", env.signalLevel]);
		data.add([string ++ "selector/1/" ++ env.selector, 1]);
		});
		*/
		^data;
	}
}