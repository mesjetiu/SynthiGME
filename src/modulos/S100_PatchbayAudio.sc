S100_PatchbayAudio {

	classvar lag = 0.1;
	var <server = nil;

	// Array que almacena todas las conexiones. Dos dimensiones [from] [to]. Almacena el Synth Node.
	var <nodeSynths = nil;

	// Módulos del Synthi 100 con entradas o salidas (en experimentación...)
	// debería contener arrays de tres elementos:
	// [synth, in/outputBus, feedbackIn/outputBus]
	// Cada Diccionario del siguiente array corresponde a un número de los elementos de la tabla del Pathbay de audio. El "synth" sirve para colocar justo tras él al synth propio del nodo. Los buses pueden ser de entrada o salida dependiendo de si es de la coordenada horizontal o la vertical. Los buses de feedback son necesarios cuando un synth debe enviar señal a otro synth que se ejecuta antes.
	// el índice de esta variable representará a los números únicos de cada punto de entrada o salida de las coordenadas del Patchbay de Audio. De esta forma, con solo pasar como parámetro las coordenadas del pin a this.administrateNode será posible hacer o deshacer la conexión correctamente.
	var <inputsOutputs = nil;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_patchNode, {
			arg fromBus,
			toBus,
			ganancy; // Entre 0 y 1;

			var sig = In.ar(fromBus) * ganancy;
			Out.ar(toBus, sig);
		}, [nil, nil, lag]
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		nodeSynths = Dictionary.new;
	}

	// Realiza las conexiones de cada output e input del pathbay con los módulos una vez en ejecución.
	connect {|inputAmplifiers, envelopeShapers, oscillators, noiseGenerators, ringModulators, outputChannels|
		inputsOutputs = this.ordenateInputsOutputs(
			inputAmplifiers: inputAmplifiers,
			envelopeShapers: envelopeShapers,
			oscillators: oscillators,
			noiseGenerators: noiseGenerators,
			ringModulators: ringModulators,
			outputChannels: outputChannels,
		);
	}

	// Declara todas las entradas y salidas de ambos ejes del patchbay de audio, ocupando el número que indica el Synthi 100
	ordenateInputsOutputs {|inputAmplifiers, envelopeShapers, oscillators, noiseGenerators, ringModulators, outputChannels|
		// almacena diccionarios [\synth, \in/outBus, \inFeedback/outFeedbackBus] para cada entrada o salida del patchbay
		var array = Array.newClear(126); // 126 = número de entradas y salidas en el patchbay de Audio.
		var index;

		// Inputs horizontales (1-66) /////////////////////////////////////////////////////////////
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
				\synth, i.synth,
				\inBus, i.inputBus,
				\inFeedbackBus, i.inFeedbackBus,
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
				\synth, i.synth,
				\outBus, i.outputBus,
			]);
			index = index + 1;
		});

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

		^array;
	}


	getNumSynth {|synth|
		if (synth.asString.split($()[0] == "Group", {
			^synth.asString.split($()[1].split($))[0]; // retorna el número del group
		}, {
			^synth.asString.split($:)[1].split($ )[1].split($))[0]; // retorna el número de synth
		})
	}


	// Crea nodo de conexión entre dos módulos
	administrateNode {|ver, hor, ganancy|
		var fromSynth = inputsOutputs[ver-1].at(\synth);
		var fromBus = inputsOutputs[ver-1].at(\outBus);
		var toSynth = inputsOutputs[hor-1].at(\synth);
		var toBus; // su valor dependerá de la relación de orden de ejecución de ambos synths
		var numFromSynth =  this.getNumSynth(fromSynth);
		var numToSynth = this.getNumSynth(toSynth);

		if(numFromSynth > numToSynth, { // Si el synth de destino se ejecuta después que el de origen
			toBus = inputsOutputs[hor-1].at(\inBus);
		}, { // Si el synth de destino se ejecuta antes que el de origen
			toBus = inputsOutputs[hor-1].at(\inFeedbackBus);
		});

		if(ganancy > 0, {
			if(nodeSynths[[hor,ver].asString] == nil, {
				Routine({
					nodeSynths.put(
						[ver,hor].asString,
						Dictionary.newFrom(List[
							\synth, Synth(
								\S100_patchNode, [
									\fromBus, fromBus,
									\toBus, toBus,
									\ganancy, 0
								],
								fromSynth,
								\addAfter
							),
							\ganancy, ganancy,
							\coordenates, [ver, hor]
						])
					);
					wait(0.05);
					nodeSynths[[ver,hor].asString][\synth].set(\ganancy, ganancy);

				}).play;

			}, {
				nodeSynths[[ver,hor].asString][\synth].set(\ganancy, ganancy);
				nodeSynths[[ver,hor].asString][\ganancy] = ganancy;
			})
		},{
			if(nodeSynths[[ver,hor].asString] != nil, {
				Routine({
					nodeSynths[[ver,hor].asString][\synth].set(\ganancy, 0);
					wait(lag); // espera un tiempo para que el synt baje su ganancia a 0;
					nodeSynths[[ver,hor].asString][\synth].free;
					nodeSynths[[ver,hor].asString] = nil;
				}).play;
			})
		})
	}
}