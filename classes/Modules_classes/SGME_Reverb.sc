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

SGME_Reverb : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de audio
	var <inputBus;
	var <inFeedbackBus;
	var <inputBusMix;
	var <inFeedbackBusMix;
	var <outputBus;

	// Knobs del módulo
	var <mix = 0; // solo para hacer bypass antes de hacer la GUI
	var <level = 0; // solo para hacer bypass antes de hacer la GUI


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
		lag = 0.2;
		SynthDef(\SGME_Reverb, {
			arg inputBus,
			inFeedbackBus,
			inputBusMix,
			inFeedbackBusMix,
			outputBus,
			mix,
			level;

			var sigIn, sigOut, inMix;
			sigIn = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);
			inMix = In.ar(inputBusMix) + InFeedback.ar(inFeedbackBusMix);
			//inMix = inMix.linlin(-1, 1, -1, 1) + mix;
			//inMix = inMix.clip(0, 1);
			inMix = inMix + mix;

			sigOut = FreeVerb.ar(sigIn, mix: inMix, room: 1); // Ajustar valores...

		//	sigOut = sigIn + inMix; // poner aquí la reverb (ahora es un bypass)

			sigOut = sigOut * level;

			Out.ar(outputBus, sigOut);
		},[nil, nil, nil, nil, nil, lag, lag]).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		inputBusMix = Bus.audio(server);
		inFeedbackBusMix = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			1.wait;
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_Reverb, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\inputBusMix, inputBusMix,
				\inFeedbackBusMix, inFeedbackBusMix,
				\outputBus, outputBus,
				\mix, this.convertMix(mix),
				\level, this.convertLevel(level),
			], server).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun { // Dejo esta función aunque no se va a usar. Por ahora no hay manera de saber que no hay output.
		var outputTotal = level * inCount * outCount;
		if (outputTotal == 0, {
			running = false;
			synth.run(false);
		}, {
			running = true;
			synth.run(true);
		});
	}

	// Conversores de unidades.

	convertMix {|m|
		^m.linlin(0, 10, 0, 1);
	}

	convertLevel {|m|
		^m.linlin(0, 10, 0, 1);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setMix {|v|
		mix = v;
		synth.run(true);
		synth.set(\mix, this.convertMix(v));
		//	this.synthRun();
	}

	setLevel {|v|
		level = v;
		synth.run(true);
		synth.set(\level, this.convertLevel(v));
		//	this.synthRun();
	}
}
