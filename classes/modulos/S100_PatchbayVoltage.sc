S100_PatchbayVoltage : S100_Patchbay{

	// Realiza las conexiones de cada output e input del pathbay con los módulos una vez en ejecución.
	connect {|inputAmplifiers, envelopeShapers, oscillators, noiseGenerators, ringModulators, outputChannels|
		inputsOutputs = this.ordenateInputsOutputs(
			inputAmplifiers: inputAmplifiers,
			envelopeShapers: envelopeShapers,
			oscillators: oscillators,
			outputChannels: outputChannels,
		);
	}

	// Declara todas las entradas y salidas de ambos ejes del patchbay de audio, ocupando el número que indica el Synthi 100
	ordenateInputsOutputs {|inputAmplifiers, envelopeShapers, oscillators, noiseGenerators, ringModulators, outputChannels|
		// almacena diccionarios [\synth, \in/outBus, \inFeedback/outFeedbackBus] para cada entrada o salida del patchbay
		var array = Array.newClear(126); // 126 = número de entradas y salidas en el patchbay de Audio.
		var index;

		// Inputs horizontales (1-66) /////////////////////////////////////////////////////////////

		index = 30; // Oscillators ocupan los números 30-41 horizontales
		12.do({|i|
			i = oscillators[i];
			array[index-1] = Dictionary.newFrom(List[
				\synth, i.synth,
				\inBus, i.inputBusVoltage,
				\inFeedbackBus, i.inFeedbackBusVoltage,
			]);
			index = index + 1;
		});


		/*
		index = 3; // Ring Modulators ocupan los números 3-8 horizontales
		ringModulators.do({|i|
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.synth,
		\inBus, i.inputBusA,
		\inFeedbackBus, i.inFeedbackBusA,
		]);
		index = index + 1;
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.synth,
		\inBus, i.inputBusB,
		\inFeedbackBus, i.inFeedbackBusB,
		]);
		index = index + 1;
		});

		index = 9; // Envelope Shapers ocupan los números 9 a 14 horizontales
		envelopeShapers.do({|i|
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.group,
		\inBus, i.inputBus,
		\inFeedbackBus, i.inFeedbackBus,
		]);
		index = index + 1;
		});
		envelopeShapers.do({|i|
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.group,
		\inBus, i.signalTrigger,
		\inFeedbackBus, i.inFeedbackSignalTrigger,
		]);
		index = index + 1;
		});

		index = 36; // Output Channels ocupan los números 36-43 horizontales
		outputChannels.do({|i|
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.group,
		\inBus, i.inputBus,
		\inFeedbackBus, i.inFeedbackBus,
		]);
		index = index + 1;
		});
		*/
		// Outputs verticales (67-126) ////////////////////////////////////////////////////////////
		index = 67; // Inputs de Amplificador del 67-74
		inputAmplifiers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 75; // Output channels del 75-82
		outputChannels.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\synth, i.group,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 83; // Oscillators 10, 11 y 12 ocupan los números 83-88
		3.do({|i|
			i = oscillators[i + 9];
			array[index-1] = Dictionary.newFrom(List[ // Sine y Saw
				\synth, i.synth,
				\outBus, i.outputBus1,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[ // Pulse y Triangle
				\synth, i.synth,
				\outBus, i.outputBus2,
			]);
			index = index + 1;
		});
		/*
		index = 89; // Noise Generators 89 y 90
		noiseGenerators.do({|i|
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.synth,
		\outBus, i.outputBus,
		]);
		index = index + 1;
		});

		index = 91; // Oscillators ocupan los números 91-108 (9 osciladores)
		oscillators.do({|i|
		array[index-1] = Dictionary.newFrom(List[ // Sine y Saw
		\synth, i.synth,
		\outBus, i.outputBus1,
		]);
		index = index + 1;
		array[index-1] = Dictionary.newFrom(List[ // Pulse y Triangle
		\synth, i.synth,
		\outBus, i.outputBus2,
		]);
		index = index + 1;
		});

		index = 118; // Envelope Shapers del 118-120
		envelopeShapers.do({|i|
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.group,
		\outBus, i.outputBus,
		]);
		index = index + 1;
		});

		index = 121; // Ring Modulators del 121-123
		ringModulators.do({|i|
		array[index-1] = Dictionary.newFrom(List[
		\synth, i.synth,
		\outBus, i.outputBus,
		]);
		index = index + 1;
		});
		*/
		^array;
	}
}