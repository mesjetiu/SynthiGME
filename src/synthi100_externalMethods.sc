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

			// Nodos de PatchbayAudio //////////////////////////////////////////////////////////
			"/patchA/91/36", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outputBus1,
				toBus: modulOutputChannels[0].inputBus,
				coordenate: [91-67,36],
				ganancy: ganancy,
			)},

			"/patchA/91/37", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outputBus1,
				toBus: modulOutputChannels[1].inputBus,
				coordenate: [91-67,37],
				ganancy: ganancy,
			)},

			"/patchA/92/36", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outputBus2,
				toBus: modulOutputChannels[0].inputBus,
				coordenate: [92-67,36],
				ganancy: ganancy,
			)},

			"/patchA/92/37", {|ganancy| modulPatchbayAudio.administrateNode(
				fromModul: modulOscillators[0],
				fromBus: modulOscillators[0].outputBus2,
				toBus: modulOutputChannels[1].inputBus,
				coordenate: [92-67,37],
				ganancy: ganancy,
			)},




		]);
	}
}