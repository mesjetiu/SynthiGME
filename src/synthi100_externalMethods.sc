+ Synthi100 {

	// devuelve un diccionario con todos los parámetros modificables con los mandos del Synthi 100
	createParameterDictionary {
		^Dictionary.newFrom(List[


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