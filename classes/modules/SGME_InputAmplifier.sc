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

SGME_InputAmplifier : SGME_Connectable  {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida
	var <inputBus; // Entrada del amplificador.
	var <outputBus; // Salida del amplificador.

	// Parámetro correspondiente a los mandos del Synthi (todos escalados entre 0 y 10)
	var <level = 0;

	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var <outVol = 1;
	var pauseRoutine; // Rutina de pausado del Synth
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = SGME_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = SGME_Settings.get[\inputLag];
		SynthDef(\SGME_inputAmplifier, {
			arg inputBus,
			outputBus,
			level,
			outVol;

			var sig;
			sig = In.ar(inputBus);
			sig = sig * level * outVol;

			Out.ar(outputBus, sig);
		}, [nil, nil, lag, lag]
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			1.wait;
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_inputAmplifier, [
				\inputBus, inputBus,
				\outputBus, outputBus,
				\level, this.convertLevel(level),
				\outVol, outVol,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = outVol * level * outCount;
		if (outputTotal == 0, {
			running = false;
			synth.run(false);
		}, {
			running = true;
			synth.run(true);
		});
	}

	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertLevel {|level|
		^level.linlin(0, 10, 0, settings[\inputLevelMax]);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////


	setLevel {|lev|
		level = lev;
		synth.run(true);
		synth.set(\level, this.convertLevel(lev));
		this.synthRun();
	}
}