S100_PatchbayVoltage : S100_Patchbay{

	// Realiza las conexiones de cada output e input del pathbay con los módulos una vez en ejecución.
	connect {|inputAmplifiers, envelopeShapers, oscillators, randomGenerator, outputChannels|
		inputsOutputs = this.ordenateInputsOutputs(
			inputAmplifiers: inputAmplifiers,
			envelopeShapers: envelopeShapers,
			randomGenerator: randomGenerator,
			oscillators: oscillators,
			outputChannels: outputChannels,
		);
	}

	// Declara todas las entradas y salidas de ambos ejes del patchbay de audio, ocupando el número que indica el Synthi 100
	ordenateInputsOutputs {|inputAmplifiers, envelopeShapers, oscillators, randomGenerator, outputChannels|
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

		index = 89; // Random Voltage Generator, ocupa 89-91
		array[index-1] = Dictionary.newFrom(List[
			\synth, randomGenerator.synth,
			\outBus, randomGenerator.outputBusKey,
		]);
		index = index + 1;
		array[index-1] = Dictionary.newFrom(List[
			\synth, randomGenerator.synth,
			\outBus, randomGenerator.outputBusVoltage1,
		]);
		index = index + 1;
		array[index-1] = Dictionary.newFrom(List[
			\synth, randomGenerator.synth,
			\outBus, randomGenerator.outputBusVoltage2,
		]);

		index = 97; // 3 Envelope Shapers. 97-99
		envelopeShapers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\synth, i.group,
				\outBus, i.outputBusVol,
			]);
			index = index + 1;
		});

		^array;
	}
}