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

SGME_Echo : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de audio
	var <inputBus;
	var <inFeedbackBus;
	var <inputBusMix;
	var <inFeedbackBusMix;
	var <inputBusDelay;
	var <inFeedbackBusDelay;
	var <outputBus;

	// Knobs del módulo
	var <delay = 0;
	var <mix = 0;
	var <feedback = 0;
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
		lag = 0.2;
		SynthDef(\SGME_Echo, {
			arg inputBus,
			inFeedbackBus,
			inputBusMix,
			inFeedbackBusMix,
			inputBusDelay,
			inFeedbackBusDelay,
			outputBus,
			delay,
			mix,
			feedback,
			level;

			var sigIn, sigOut, inMix, inDelay;
			sigIn = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);
			inMix = In.ar(inputBusMix) + InFeedback.ar(inFeedbackBusMix);
			inMix = inMix.linlin(-1, 1, -0.25, 0.25) + mix;
			inMix = inMix.clip(0, 1);
			inDelay = In.ar(inputBusDelay) + InFeedback.ar(inFeedbackBusDelay);
			inDelay = inDelay.linlin(-1, 1, -5, 5) + delay;
			inDelay = inDelay.clip(0.002, 20);
			// Implementación usando SC3plugins
			//sigOut = SwitchDelay.ar(sigIn, 1-mix, mix, VarLag.ar(K2A.ar(delay),0.001), feedback * 0.7) * level;
			// Implementación sin usar SC3plugins
			sigOut = (CombC.ar(sigIn, 20, inDelay, 20 * feedback) * inMix) + (sigIn * (1-inMix));

			sigOut = sigOut * level;

			Out.ar(outputBus, sigOut);
		},[nil, nil, nil, nil, nil, nil, lag, lag, lag, lag, lag]).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv;
		server = serv;
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		inputBusMix = Bus.audio(server);
		inFeedbackBusMix = Bus.audio(server);
		inputBusDelay = Bus.audio(server);
		inFeedbackBusDelay = Bus.audio(server);
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
			synth = Synth(\SGME_Echo, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\inputBusMix, inputBusMix,
				\inFeedbackBusMix, inFeedbackBusMix,
				\inputBusDelay, inputBusDelay,
				\inFeedbackBusDelay, inFeedbackBusDelay,
				\outputBus, outputBus,
				\delay, this.convertDelay(delay),
				\mix, this.convertMix(mix),
				\feedback, this.convertFeedback(feedback),
				\level, this.convertLevel(level),
			], server).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun { // Dejo esta función aunque no se va a usar. Por ahora no hay manera de saber que no hay output.
		var outputTotal = level * inCount * outCount;
		if (outputTotal == 0, {
			synth.run(false);
		}, {
			synth.run(true);
		});
	}

	// Conversores de unidades.

	convertDelay {|d|
		^d.linexp(0, 10, 0.002, 20);
	}

	convertMix {|m|
		^m.linlin(0, 10, 0, 1);
	}

	convertFeedback {|f|
		^f.linlin(0, 10, 0, 1);
	}

	convertLevel {|m|
		^m.linlin(0, 10, 0, 2);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setDelay {|v|
		delay = v;
		synth.run(true);
		synth.set(\delay, this.convertDelay(v));
		//	this.synthRun();
	}

	setMix {|v|
		mix = v;
		synth.run(true);
		synth.set(\mix, this.convertMix(v));
		//	this.synthRun();
	}

	setFeedback {|v|
		feedback = v;
		synth.run(true);
		synth.set(\feedback, this.convertFeedback(v));
		//	this.synthRun();
	}

	setLevel {|v|
		level = v;
		synth.run(true);
		synth.set(\level, this.convertLevel(v));
		//	this.synthRun();
	}
}
