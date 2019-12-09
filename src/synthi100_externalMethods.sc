+ Synthi100 {

	// devuelve un diccionario con todos los parámetros modificables con los mandos del Synthi 100
	createParameterDictionary {
		^Dictionary.newFrom(List[

			// Osciladores ///////////////////////////////////////////////////////////////////////

			"/osc/1/range", {|range| modulOscillators[0].setRange(range)},
			"/osc/1/pulse/level", {|level| modulOscillators[0].setPulseLevel(level)},
			"/osc/1/pulse/shape", {|shape| modulOscillators[0].setPulseShape(shape)},
			"/osc/1/sine/level", {|level| modulOscillators[0].setSineLevel(level)},
			"/osc/1/sine/symmetry", {|symmetry| modulOscillators[0].setSineSymmetry(symmetry)},
			"/osc/1/triangle/level", {|level| modulOscillators[0].setTriangleLevel(level)},
			"/osc/1/sawtooth/level", {|level| modulOscillators[0].setSawtoothLevel(level)},
			"/osc/1/frequency", {|freq| modulOscillators[0].setFrequency(freq)},

			"/osc/2/range", {|range| modulOscillators[1].setRange(range)},
			"/osc/2/pulse/level", {|level| modulOscillators[1].setPulseLevel(level)},
			"/osc/2/pulse/shape", {|shape| modulOscillators[1].setPulseShape(shape)},
			"/osc/2/sine/level", {|level| modulOscillators[1].setSineLevel(level)},
			"/osc/2/sine/symmetry", {|symmetry| modulOscillators[1].setSineSymmetry(symmetry)},
			"/osc/2/triangle/level", {|level| modulOscillators[1].setTriangleLevel(level)},
			"/osc/2/sawtooth/level", {|level| modulOscillators[1].setSawtoothLevel(level)},
			"/osc/2/frequency", {|freq| modulOscillators[1].setFrequency(freq)},

			"/osc/3/range", {|range| modulOscillators[2].setRange(range)},
			"/osc/3/pulse/level", {|level| modulOscillators[2].setPulseLevel(level)},
			"/osc/3/pulse/shape", {|shape| modulOscillators[2].setPulseShape(shape)},
			"/osc/3/sine/level", {|level| modulOscillators[2].setSineLevel(level)},
			"/osc/3/sine/symmetry", {|symmetry| modulOscillators[2].setSineSymmetry(symmetry)},
			"/osc/3/triangle/level", {|level| modulOscillators[2].setTriangleLevel(level)},
			"/osc/3/sawtooth/level", {|level| modulOscillators[2].setSawtoothLevel(level)},
			"/osc/3/frequency", {|freq| modulOscillators[2].setFrequency(freq)},

			"/osc/4/range", {|range| modulOscillators[3].setRange(range)},
			"/osc/4/pulse/level", {|level| modulOscillators[3].setPulseLevel(level)},
			"/osc/4/pulse/shape", {|shape| modulOscillators[3].setPulseShape(shape)},
			"/osc/4/sine/level", {|level| modulOscillators[3].setSineLevel(level)},
			"/osc/4/sine/symmetry", {|symmetry| modulOscillators[3].setSineSymmetry(symmetry)},
			"/osc/4/triangle/level", {|level| modulOscillators[3].setTriangleLevel(level)},
			"/osc/4/sawtooth/level", {|level| modulOscillators[3].setSawtoothLevel(level)},
			"/osc/4/frequency", {|freq| modulOscillators[3].setFrequency(freq)},

			"/osc/5/range", {|range| modulOscillators[4].setRange(range)},
			"/osc/5/pulse/level", {|level| modulOscillators[4].setPulseLevel(level)},
			"/osc/5/pulse/shape", {|shape| modulOscillators[4].setPulseShape(shape)},
			"/osc/5/sine/level", {|level| modulOscillators[4].setSineLevel(level)},
			"/osc/5/sine/symmetry", {|symmetry| modulOscillators[4].setSineSymmetry(symmetry)},
			"/osc/5/triangle/level", {|level| modulOscillators[4].setTriangleLevel(level)},
			"/osc/5/sawtooth/level", {|level| modulOscillators[4].setSawtoothLevel(level)},
			"/osc/5/frequency", {|freq| modulOscillators[4].setFrequency(freq)},

			"/osc/6/range", {|range| modulOscillators[5].setRange(range)},
			"/osc/6/pulse/level", {|level| modulOscillators[5].setPulseLevel(level)},
			"/osc/6/pulse/shape", {|shape| modulOscillators[5].setPulseShape(shape)},
			"/osc/6/sine/level", {|level| modulOscillators[5].setSineLevel(level)},
			"/osc/6/sine/symmetry", {|symmetry| modulOscillators[5].setSineSymmetry(symmetry)},
			"/osc/6/triangle/level", {|level| modulOscillators[5].setTriangleLevel(level)},
			"/osc/6/sawtooth/level", {|level| modulOscillators[5].setSawtoothLevel(level)},
			"/osc/6/frequency", {|freq| modulOscillators[5].setFrequency(freq)},

			"/osc/7/range", {|range| modulOscillators[6].setRange(range)},
			"/osc/7/pulse/level", {|level| modulOscillators[6].setPulseLevel(level)},
			"/osc/7/pulse/shape", {|shape| modulOscillators[6].setPulseShape(shape)},
			"/osc/7/sine/level", {|level| modulOscillators[6].setSineLevel(level)},
			"/osc/7/sine/symmetry", {|symmetry| modulOscillators[6].setSineSymmetry(symmetry)},
			"/osc/7/triangle/level", {|level| modulOscillators[6].setTriangleLevel(level)},
			"/osc/7/sawtooth/level", {|level| modulOscillators[6].setSawtoothLevel(level)},
			"/osc/7/frequency", {|freq| modulOscillators[6].setFrequency(freq)},

			"/osc/8/range", {|range| modulOscillators[7].setRange(range)},
			"/osc/8/pulse/level", {|level| modulOscillators[7].setPulseLevel(level)},
			"/osc/8/pulse/shape", {|shape| modulOscillators[7].setPulseShape(shape)},
			"/osc/8/sine/level", {|level| modulOscillators[7].setSineLevel(level)},
			"/osc/8/sine/symmetry", {|symmetry| modulOscillators[7].setSineSymmetry(symmetry)},
			"/osc/8/triangle/level", {|level| modulOscillators[7].setTriangleLevel(level)},
			"/osc/8/sawtooth/level", {|level| modulOscillators[7].setSawtoothLevel(level)},
			"/osc/8/frequency", {|freq| modulOscillators[7].setFrequency(freq)},

			"/osc/9/range", {|range| modulOscillators[8].setRange(range)},
			"/osc/9/pulse/level", {|level| modulOscillators[8].setPulseLevel(level)},
			"/osc/9/pulse/shape", {|shape| modulOscillators[8].setPulseShape(shape)},
			"/osc/9/sine/level", {|level| modulOscillators[8].setSineLevel(level)},
			"/osc/9/sine/symmetry", {|symmetry| modulOscillators[8].setSineSymmetry(symmetry)},
			"/osc/9/triangle/level", {|level| modulOscillators[8].setTriangleLevel(level)},
			"/osc/9/sawtooth/level", {|level| modulOscillators[8].setSawtoothLevel(level)},
			"/osc/9/frequency", {|freq| modulOscillators[8].setFrequency(freq)},

			"/osc/10/range", {|range| modulOscillators[9].setRange(range)},
			"/osc/10/pulse/level", {|level| modulOscillators[9].setPulseLevel(level)},
			"/osc/10/pulse/shape", {|shape| modulOscillators[9].setPulseShape(shape)},
			"/osc/10/sine/level", {|level| modulOscillators[9].setSineLevel(level)},
			"/osc/10/sine/symmetry", {|symmetry| modulOscillators[9].setSineSymmetry(symmetry)},
			"/osc/10/triangle/level", {|level| modulOscillators[9].setTriangleLevel(level)},
			"/osc/10/sawtooth/level", {|level| modulOscillators[9].setSawtoothLevel(level)},
			"/osc/10/frequency", {|freq| modulOscillators[9].setFrequency(freq)},

			"/osc/11/range", {|range| modulOscillators[10].setRange(range)},
			"/osc/11/pulse/level", {|level| modulOscillators[10].setPulseLevel(level)},
			"/osc/11/pulse/shape", {|shape| modulOscillators[10].setPulseShape(shape)},
			"/osc/11/sine/level", {|level| modulOscillators[10].setSineLevel(level)},
			"/osc/11/sine/symmetry", {|symmetry| modulOscillators[10].setSineSymmetry(symmetry)},
			"/osc/11/triangle/level", {|level| modulOscillators[10].setTriangleLevel(level)},
			"/osc/11/sawtooth/level", {|level| modulOscillators[10].setSawtoothLevel(level)},
			"/osc/11/frequency", {|freq| modulOscillators[10].setFrequency(freq)},

			"/osc/12/range", {|range| modulOscillators[11].setRange(range)},
			"/osc/12/pulse/level", {|level| modulOscillators[11].setPulseLevel(level)},
			"/osc/12/pulse/shape", {|shape| modulOscillators[11].setPulseShape(shape)},
			"/osc/12/sine/level", {|level| modulOscillators[11].setSineLevel(level)},
			"/osc/12/sine/symmetry", {|symmetry| modulOscillators[11].setSineSymmetry(symmetry)},
			"/osc/12/triangle/level", {|level| modulOscillators[11].setTriangleLevel(level)},
			"/osc/12/sawtooth/level", {|level| modulOscillators[11].setSawtoothLevel(level)},
			"/osc/12/frequency", {|freq| modulOscillators[11].setFrequency(freq)},


			// Nodos de PatchbayAudio //////////////////////////////////////////////////////////
			"/patchA/91/36", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outBus1,
				toBus: modulOutputChannels[0].inputBus,
				coordenate: [91-66,36],
				ganancy: ganancy,
			)},

			"/patchA/91/37", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outBus1,
				toBus: modulOutputChannels[1].inputBus,
				coordenate: [91-66,37],
				ganancy: ganancy,
			)},

			"/patchA/92/36", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outBus2,
				toBus: modulOutputChannels[0].inputBus,
				coordenate: [92-66,36],
				ganancy: ganancy,
			)},

			"/patchA/92/37", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outBus2,
				toBus: modulOutputChannels[1].inputBus,
				coordenate: [92-66,37],
				ganancy: ganancy,
			)},




		]);
	}
}