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

SGME_RingModulator : SGME_Connectable {
	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida
	var <inputBusA; // Entrada A
	var <inFeedbackBusA; // Entrada A en feedback
	var <inputBusB; // Entrada B
	var <inFeedbackBusB; // Entrada B en feedback
	var <outputBus; // Salida

	// Parámetro correspondiente a los mandos del Synthi (todos escalados entre 0 y 10)
	var <level = 0;

	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var <outVol = 1;
	var pauseRoutine; // Rutina de pausado del Synth
	var resumeRoutine;
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = SGME_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = SGME_Settings.get[\ringLag];
		SynthDef(\SGME_ringModulator, {
			arg inputBusA,
			inputBusB,
			inFeedbackBusA,
			inFeedbackBusB,
			outputBus,
			level,
			outVol;

			var sigA, sigB, sig;
			sigA= In.ar(inputBusA) + InFeedback.ar(inFeedbackBusA);
			sigB= In.ar(inputBusB) + InFeedback.ar(inFeedbackBusB);
			sig = sigA * sigB;
			sig = sig * level * outVol;

			Out.ar(outputBus, sig);
		}, [nil, nil, nil, nil, nil, lag, lag]
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBusA = Bus.audio(server);
		inputBusB = Bus.audio(server);
		inFeedbackBusA = Bus.audio(server);
		inFeedbackBusB = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			if (resumeRoutine.isPlaying) {resumeRoutine.stop};
			running = false;
			1.wait;
			synth.run(false);
		//	1.wait;
		});
		resumeRoutine = Routine({
			if(pauseRoutine.isPlaying) {pauseRoutine.stop};
			running = true;
		//	1.wait;
			synth.run(true);
		//	1.wait;
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_ringModulator, [
				\inputBusA, inputBusA,
				\inFeedbackBusA, inFeedbackBusA,
				\inFeedbackBusB, inFeedbackBusB,
				\inputBusB, inputBusB,
				\outputBus, outputBus,
				\level, this.convertLevel(level),
				\outVol, outVol,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = outVol * level * inCount * outCount;
		if (outputTotal == 0, {
			pauseRoutine.reset;
			pauseRoutine.play;
		}, {
			resumeRoutine.reset;
			resumeRoutine.play;
		});
	}

	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertLevel {|level|
		^level.linlin(0, 10, 0, settings[\ringLevelMax]);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////


	setLevel {|lev|
		level = lev;
		synth.run(true);
		synth.set(\level, this.convertLevel(lev));
		this.synthRun();
	}
}
