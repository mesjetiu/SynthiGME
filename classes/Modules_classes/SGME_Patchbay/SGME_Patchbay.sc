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

SGME_Patchbay {

	classvar lag = 0.1;
	var <server = nil;

	// Array que almacena todas las conexiones. Dos dimensiones [from] [to]. Almacena el Synth Node.
	var <nodeSynths = nil;

	// Diccionario con solo los valores del pin. Se busca por mensaje OSC.
	var <nodeValues = nil;

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
		SynthDef(\SGME_patchNode, {
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
		nodeValues = Dictionary.new;
	}


	/*getNumSynth {|synth|
		if (synth.asString.split($()[0] == "Group", {
			^synth.asString.split($()[1].split($))[0]; // retorna el número del group
		}, {
			^synth.asString.split($:)[1].split($ )[1].split($))[0]; // retorna el número de synth
		})
	}*/

	makeValues {
		var string = nil;
		var value = 0;
		var nodes = inputsOutputs; //Dictionary.newFrom(inputsOutputs);
		66.do({|v|
			60.do({|h|
				string = (h+67).asString ++ "/" ++ (v+1).asString;
				if (nodes[v].notNil && nodes[h+66].notNil) {
					nodeValues.put(string, value)
				}
			})
		});
	}

	// Crea nodo de conexión entre dos módulos
	administrateNode {|ver, hor, ganancy|
		var stringOSC = (ver).asString ++ "/" ++ (hor).asString;
		var fromModul = inputsOutputs[ver-1].at(\modul);
		var fromSynth = inputsOutputs[ver-1].at(\synth);
		var fromBus = inputsOutputs[ver-1].at(\outBus);
		var toModul = inputsOutputs[hor-1].at(\modul);
		var toSynth = inputsOutputs[hor-1].at(\synth);
		var toBus; // su valor dependerá de la relación de orden de ejecución de ambos synths
		var numFromSynth =  fromSynth.nodeID; //this.getNumSynth(fromSynth);
		var numToSynth = toSynth.nodeID; //this.getNumSynth(toSynth);

		var oldGanancy = nodeValues[stringOSC];

		ganancy = ganancy.clip(0,1); // clip con máximo 1 y mínimo 0


		// Si el nodo ya tenía el valor solicitado, no se hace nada:
		if (ganancy == oldGanancy) {^this};

		//Se añade el valor del pin a nodeValue
		nodeValues.put(stringOSC, ganancy);

		if(numFromSynth > numToSynth, { // Si el synth de destino se ejecuta después que el de origen
			toBus = inputsOutputs[hor-1].at(\inBus);
		}, { // Si el synth de destino se ejecuta antes que el de origen
			toBus = inputsOutputs[hor-1].at(\inFeedbackBus);
		});

		case
		{(oldGanancy == 0) && (ganancy > 0)} { // Pasa de 0 a conectado...
			fromModul.outPlusOne;
			toModul.inPlusOne;
			if(nodeSynths[[hor,ver].asString] == nil, {
				Routine({
					nodeSynths.put(
						[ver,hor].asString,
						Dictionary.newFrom(List[
							\synth, Synth(
								\SGME_patchNode, [
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
					);
					server.sync;
					nodeSynths[[ver,hor].asString][\synth].set(\ganancy, ganancy);

				}).play;

			}, {
				nodeSynths[[ver,hor].asString][\synth].set(\ganancy, ganancy);
				nodeSynths[[ver,hor].asString][\ganancy] = ganancy;
			})
		}
		{(oldGanancy > 0) && (ganancy > 0)} { // Pasa de conectado a conectado...

			nodeSynths[[ver,hor].asString][\synth].set(\ganancy, ganancy);
			nodeSynths[[ver,hor].asString][\ganancy] = ganancy;
		}
		{(oldGanancy > 0) && (ganancy == 0)} { // Pasa de conectado a 0...
			fromModul.outPlusOne(false);
			toModul.inPlusOne(false);
			if(nodeSynths[[ver,hor].asString] != nil, {
				Routine({
					nodeSynths[[ver,hor].asString][\synth].set(\ganancy, 0);
					//wait(lag); // espera un tiempo para que el synt baje su ganancia a 0;
					nodeSynths[[ver,hor].asString][\synth].free;
					nodeSynths[[ver,hor].asString] = nil;
				}).play;
			})
		};
	}
}