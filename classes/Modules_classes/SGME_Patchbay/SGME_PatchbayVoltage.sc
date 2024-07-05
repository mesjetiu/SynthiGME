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

SGME_PatchbayVoltage : SGME_Patchbay{


	// Realiza las conexiones de cada output e input del pathbay con los módulos una vez en ejecución.
	connect {|reverb, echo, inputAmplifiers, filters, envelopeShapers, oscillators, randomGenerator, slewLimiters, oscilloscope, outputChannels, keyboards, invertor, sequencer|
		inputsOutputs = this.ordenateInputsOutputs(
			reverb: reverb,
			echo: echo,
			inputAmplifiers: inputAmplifiers,
			filters: filters,
			envelopeShapers: envelopeShapers,
			randomGenerator: randomGenerator,
			slewLimiters: slewLimiters,
			oscillators: oscillators,
			oscilloscope: oscilloscope,
			outputChannels: outputChannels,
			keyboards: keyboards,
			invertor: invertor,
			sequencer: sequencer,
		);
		this.makeValues(); // Pone valores de 0 a todos los nodos existentes.
	}

	// Declara todas las entradas y salidas de ambos ejes del patchbay de audio, ocupando el número que indica el Synthi 100
	ordenateInputsOutputs {|reverb, echo, inputAmplifiers, filters, envelopeShapers, oscillators, randomGenerator, slewLimiters, oscilloscope, outputChannels, keyboards, invertor, sequencer|
		// almacena diccionarios [\synth, \in/outBus, \inFeedback/outFeedbackBus] para cada entrada o salida del patchbay
		var array = Array.newClear(126); // 126 = número de entradas y salidas en el patchbay de Audio.
		var index;

		// Inputs horizontales (1-66) /////////////////////////////////////////////////////////////

		index = 1; // Reverb (1)
		array[index-1] = Dictionary.newFrom(List[
			\modul, reverb,
			\synth, reverb.synth,
			\inBus, reverb.inputBusMix,
			\inFeedbackBus, reverb.inFeedbackBusMix,
		]);

		index = 2; // Echo A.D.L. (2 y 3)
		array[index-1] = Dictionary.newFrom(List[
			\modul, echo,
			\synth, echo.synth,
			\inBus, echo.inputBusMix,
			\inFeedbackBus, echo.inFeedbackBusMix,
		]);
		index = index + 1;
		array[index-1] = Dictionary.newFrom(List[
			\modul, echo,
			\synth, echo.synth,
			\inBus, echo.inputBusDelay,
			\inFeedbackBus, echo.inFeedbackBusDelay,
		]);

		index = 4; // EnvelopeShapers ocupan los números 4-21 horizontales
		envelopeShapers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.signalTrigger,
				\inFeedbackBus, i.inFeedbackSignalTrigger,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.inDelayVol,
				\inFeedbackBus, i.inFeedbackDelayVol,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.inAttackVol,
				\inFeedbackBus, i.inFeedbackAttackVol,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.inDecayVol,
				\inFeedbackBus, i.inFeedbackDecayVol,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.inSustainVol,
				\inFeedbackBus, i.inFeedbackSustainVol,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\inBus, i.inReleaseVol,
				\inFeedbackBus, i.inFeedbackReleaseVol,
			]);
			index = index + 1;
		});

		index = 22; // Filters ocupan los números 22-29
		filters.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusVoltage,
				\inFeedbackBus, i.inFeedbackBusVoltage,
			]);
			index = index + 1;
		});

		index = 30; // Oscillators ocupan los números 30-41 horizontales
		12.do({|i|
			i = oscillators[i];
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusVoltage,
				\inFeedbackBus, i.inFeedbackBusVoltage,
			]);
			index = index + 1;
		});

		index = 42; // Output channels "voltage input". Los cuatro primeros canales admiten entrada de voltaje (42-45)
		4.do({|i|
			var o = outputChannels[i];
			array[index-1] = Dictionary.newFrom(List[
				\modul, o,
				\synth, o.synth,
				\inBus, o.inputBus,
				\inFeedbackBus, o.inFeedbackBus,
			]);
			index = index + 1;
		});

		index = 46; // Output channels "Level". (46-53)
		8.do({|i|
			var o = outputChannels[i];
			array[index-1] = Dictionary.newFrom(List[
				\modul, o,
				\synth, o.synth,
				\inBus, o.inputBusLevel,
				\inFeedbackBus, o.inFeedbackBusLevel,
			]);
			index = index + 1;
		});

		index = 54; // Slew Limiters 1, 2 y 3, 42-53
		slewLimiters.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusVol,
				\inFeedbackBus, i.inFeedbackBusVol,
			]);
			index = index + 1;
		});
		slewLimiters.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\inBus, i.inputBusControl,
				\inFeedbackBus, i.inFeedbackBusControl,
			]);
			index = index + 1;
		});

		index = 63; // Entrada al Oscilloscope, números 63 y 64 horizontales
		array[index-1] = Dictionary.newFrom(List[
			\modul, oscilloscope,
			\synth, oscilloscope.synth,
			\inBus, oscilloscope.inputBusCH1,
			\inFeedbackBus, oscilloscope.inFeedbackBusCH1,
		]);
		index = 64;
		array[index-1] = Dictionary.newFrom(List[
			\modul, oscilloscope,
			\synth, oscilloscope.synth,
			\inBus, oscilloscope.inputBusCH2,
			\inFeedbackBus, oscilloscope.inFeedbackBusCH2,
		]);

		index = 65; // Entrada a Invertor
		array[index-1] = Dictionary.newFrom(List[
			\modul, invertor,
			\synth, invertor.synth,
			\inBus, invertor.inBus,
			\inFeedbackBus, invertor.inFeedbackBus,
		]);

		// Outputs verticales (67-126) ////////////////////////////////////////////////////////////
		index = 67; // Inputs de Amplificador 67-74
		inputAmplifiers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

		index = 75; // Output channels 75-82
		outputChannels.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBus, // i.outputBusBypass,
			]);
			index = index + 1;
		});

		index = 94; // Slew Limiters 1, 2 y 3, 94-96
		slewLimiters.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.synth,
				\outBus, i.outputBusVol,
			]);
			index = index + 1;
		});

		index = 83; // Oscillators 10, 11 y 12 ocupan los números 83-88
		3.do({|i|
			i = oscillators[i + 9];
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

		index = 89; // Random Voltage Generator, ocupa 89-91
		array[index-1] = Dictionary.newFrom(List[
			\modul, randomGenerator,
			\synth, randomGenerator.synth,
			\outBus, randomGenerator.outputBusKey,
		]);
		index = index + 1;
		array[index-1] = Dictionary.newFrom(List[
			\modul, randomGenerator,
			\synth, randomGenerator.synth,
			\outBus, randomGenerator.outputBusVoltage1,
		]);
		index = index + 1;
		array[index-1] = Dictionary.newFrom(List[
			\modul, randomGenerator,
			\synth, randomGenerator.synth,
			\outBus, randomGenerator.outputBusVoltage2,
		]);

		index = 97; // 3 Envelope Shapers. 97-99
		envelopeShapers.do({|i|
			array[index-1] = Dictionary.newFrom(List[
				\modul, i,
				\synth, i.group,
				\outBus, i.outputBusVol,
			]);
			index = index + 1;
		});

		index = 111; // 2 Keyboards. 111-116
		keyboards.do({|keyboard|
			array[index-1] = Dictionary.newFrom(List[
				\modul, keyboard,
				\synth, keyboard.synth,
				\outBus, keyboard.outBusPitch,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, keyboard,
				\synth, keyboard.synth,
				\outBus, keyboard.outBusVelocity,
			]);
			index = index + 1;
			array[index-1] = Dictionary.newFrom(List[
				\modul, keyboard,
				\synth, keyboard.synth,
				\outBus, keyboard.outBusGate,
			]);
			index = index + 1;
		});

		index = 122; // Invertor
		array[index-1] = Dictionary.newFrom(List[
			\modul, invertor,
			\synth, invertor.synth,
			\outBus, invertor.outBus,
		]);

		^array;
	}
}