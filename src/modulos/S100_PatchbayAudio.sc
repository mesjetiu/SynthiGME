S100_PatchbayAudio {

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

			var sig = (In.ar(fromBus) * ganancy);
			Out.ar(toBus, sig);
		}
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		nodeSynths = Dictionary.new;
	}

	// Realiza las conexiones de cada output e input del pathbay con los módulos una vez en ejecución.
	connect {|oscillators, inputAmplifiers, outputChannels|
		inputsOutputs = this.ordenateInputsOutputs(oscillators, inputAmplifiers, outputChannels);
	}

	// Declara todas las entradas y salidas de ambos ejes del patchbay de audio, ocupando el número que indica el Synthi 100
	ordenateInputsOutputs {|oscillators, inputAmplifiers, outputChannels|
		// almacena diccionarios [\synth, \in/outBus, \inFeedback/outFeedbackBus] para cada entrada o salida del patchbay
		var array = Array.newClear(126); // 126 = número de entradas y salidas en el patchbay de Audio.
		var index;

		// Inputs horizontales (1-66) /////////////////////////////////////////////////////////////
		index = 36; // Output Channels ocupan los números 36-43 horizontales
		inputAmplifiers.do({|i|
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
		index = 75; // Outputs de amplificador (Output channels) del 75-82
		outputChannels.do({|i|
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

		^array;
	}




	// Crea nodo de conexión entre dos módulos
	administrateNode {|ver, hor, ganancy|
		var fromSynth = inputsOutputs[ver-1].at(\synth);
		var fromBus = inputsOutputs[ver-1].at(\outBus);
		var toSynth = inputsOutputs[hor-1].at(\synth);
		var toBus; // su valor dependerá de la relación de orden de ejecución de ambos synths
		var numFromSynth = fromSynth.asString.split($:)[1].split($ )[1].split($))[0];
		var numToSynth = toSynth.asString.split($:)[1].split($ )[1].split($))[0];

		if(numFromSynth > numToSynth, { // Si el synth de destino se ejecuta después que el de origen
			toBus = inputsOutputs[hor-1].at(\inBus);
		}, { // Si el synth de destino se ejecuta antes que el de origen
			toBus = inputsOutputs[hor-1].at(\inFeedbackBus);
		});

		if(ganancy > 0, {
			if(nodeSynths[[hor,ver].asString] == nil, {
				nodeSynths.put(
					[ver,hor].asString,
					Dictionary.newFrom(List[
						\synth, Synth(
							\S100_patchNode, [
								\fromBus, fromBus,
								\toBus, toBus,
								\ganancy, ganancy
							],
							fromSynth,
							\addAfter
						),
						\ganancy, ganancy,
						\coordenates, [ver, hor]
					])
				)
			}, {
				nodeSynths[[ver,hor].asString][\synth].set(\ganancy, ganancy);
				nodeSynths[[ver,hor].asString][\ganancy] = ganancy;
			})
		},{
			if(nodeSynths[[ver,hor].asString] != nil, {
				nodeSynths[[ver,hor].asString][\synth].free;
				nodeSynths[[ver,hor].asString] = nil;
			})
		})
	}
}