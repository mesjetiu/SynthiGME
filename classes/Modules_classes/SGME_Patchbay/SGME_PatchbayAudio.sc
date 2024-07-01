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

SGME_PatchbayAudio : SGME_Patchbay{


	// Realiza las conexiones de cada output e input del pathbay con los módulos una vez en ejecución.
	connect {|reverb, inputAmplifiers, externalTreatmentReturns, filters, filterBank, envelopeShapers, oscillators, noiseGenerators, ringModulators, echo, oscilloscope, outputChannels|
		inputsOutputs = this.ordenateInputsOutputs(
			reverb: reverb,
			inputAmplifiers: inputAmplifiers,
			externalTreatmentReturns: externalTreatmentReturns,
			filters: filters,
			filterBank: filterBank,
			envelopeShapers: envelopeShapers,
			oscillators: oscillators,
			noiseGenerators: noiseGenerators,
			ringModulators: ringModulators,
			echo: echo,
			oscilloscope: oscilloscope,
			outputChannels: outputChannels,
		);
		this.makeValues(); // Pone valores de 0 a todos los nodos existentes.
	}

	// Declara todas las entradas y salidas de ambos ejes del patchbay de audio, ocupando el número que indica el Synthi 100
	ordenateInputsOutputs {|reverb, inputAmplifiers, externalTreatmentReturns, filters, filterBank, envelopeShapers, oscillators, noiseGenerators, ringModulators, echo, oscilloscope, outputChannels|
		// almacena diccionarios [\synth, \in/outBus, \inFeedback/outFeedbackBus] para cada entrada o salida del patchbay
		var array = Array.newClear(126); // 126 = número de entradas y salidas en el patchbay de Audio.
		var index;

		// Inputs horizontales (1-66) /////////////////////////////////////////////////////////////

		index = 1; // Reverb
		array[index-1] = Dictionary.newFrom(List[
			\modul, reverb,
			\synth, reverb.synth,
			\inBus, reverb.inputBus,
			\inFeedbackBus, reverb.inFeedbackBus,
		]);

		index = 2; // Echo A.D.L.
		array[index-1] = Dictionary.newFrom(List[
			\modul, echo,
			\synth, echo.synth,
			\inBus, echo.inputBus,
			\inFeedbackBus, echo.inFeedbackBus,
		]);

		index = 3; // Ring Modulators ocupan los números 3-8 horizontales
		ringModulators.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusA,
				\inFeedbackBus, i.inFeedbackBusA,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusB,
				\inFeedbackBus, i.inFeedbackBusB,
			]);
			index = index + 1;
		});

		index = 9; // Envelope Shapers ocupan los números 9 a 14 horizontales
		envelopeShapers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.inputBus,
				\inFeedbackBus, i.inFeedbackBus,
			]);
			index = index + 1;
		});
		envelopeShapers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.signalTrigger,
				\inFeedbackBus, i.inFeedbackSignalTrigger,
			]);
			index = index + 1;
		});

		index = 15; // Filters ocupan los números 15-22
		filters.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBus,
				\inFeedbackBus, i.inFeedbackBus,
			]);
			index = index + 1;
		});

		index = 23; // Filter Bank, número 23
		array[index-1] = Dictionary.newFrom(List[
			\modul, filterBank,
			\synth, filterBank.group,
			\inBus, filterBank.inputBus,
			\inFeedbackBus, filterBank.inFeedbackBus,
		]);

		index = 24; // Oscillators (synchronisation) ocupan los números 24-35 horizontales
		12.do({|i|
			i = oscillators[i];
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusSync,
				\inFeedbackBus, i.inFeedbackBusSync,
			]);
			index = index + 1;
		});

		index = 36; // Output Channels ocupan los números 36-43 horizontales
		outputChannels.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBus,
				\inFeedbackBus, i.inFeedbackBus,
			]);
			index = index + 1;
		});

		index = 57; // Entrada al Oscilloscope, números 57 y 58 horizontales
		array[index-1] = Dictionary.newFrom(List[
			\modul, oscilloscope,
			\synth, oscilloscope.synth,
			\inBus, oscilloscope.inputBusCH1,
			\inFeedbackBus, oscilloscope.inFeedbackBusCH1,
		]);
		index = 58;
		array[index-1] = Dictionary.newFrom(List[
			\modul, oscilloscope,
			\synth, oscilloscope.synth,
			\inBus, oscilloscope.inputBusCH2,
			\inFeedbackBus, oscilloscope.inFeedbackBusCH2,
		]);


		// Outputs verticales (67-126) ///////////////////////////////////////////////////////////////////////////////////////////
		index = 67; // Inputs de Amplificador del 67-74
		inputAmplifiers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 75; // Output channels del 75-82
		outputChannels.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus, // i.outputBusBypass,
			]);
			index = index + 1;
		});

		index = 83; // Inputs de Amplificador del 83-86
		externalTreatmentReturns.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 89; // Noise Generators 89 y 90
		noiseGenerators.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 91; // Oscillators ocupan los números 91-108 (9 osciladores)
		9.do({|i|
			i = oscillators[i];
			array[index-1] = Dictionary.newFrom(List[ // Sine y Saw
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus1,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[ // Pulse y Triangle
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus2,
			]);
			index = index + 1;
		});

		index = 109; // Filter Bank, número 109
		array[index-1] = Dictionary.newFrom(List[
			\modul, filterBank,
			\synth, filterBank.group,
			\outBus, filterBank.outputBus,
		]);

		index = 110; // Filters ocupan los números 110-117
		filters.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 118; // Envelope Shapers del 118-120
		envelopeShapers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 121; // Ring Modulators del 121-123
		ringModulators.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 124; // Reverb
		array[index-1] = Dictionary.newFrom(List[
			\modul, reverb,
			\synth, reverb.synth,
			\outBus, reverb.outputBus,
		]);

		index = 125; // Echo A.D.L.
		array[index-1] = Dictionary.newFrom(List[
			\modul, echo,
			\synth, echo.synth,
			\outBus, echo.outputBus,
		]);

		index = 59; // Oscillators (Pulse Modulation) ocupan los números 59-64 horizontales
		6.do({|i| // Solo los 6 primeros osciladores pueden ser modulados en el pulso.
			i = oscillators[i];
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusPulseMod,
				\inFeedbackBus, i.inFeedbackBusPulseMod,
			]);
			index = index + 1;
		});

		^array;
	}
}