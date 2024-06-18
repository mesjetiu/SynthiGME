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

SGME_SlewLimiter : SGME_Connectable {

	// TODO: (Importante para eficiencia)
	// Hacer que Patchbay cambie un semáforo en cada módulo cada vez que esté conectado. De este modo el módulo podrá saber que tiene input o tiene output. En función de estos semáforos se podrá tomar decisiones como la de poner en pausa los synths. En el caso de este módulo (y algunos más), si no existe este sistema, no hay manera de saber cuándo ponerse en pausa.


	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de voltaje
	var <inputBusVol;
	var <inFeedbackBusVol;
	var <inputBusControl;
	var <inFeedbackBusControl;
	var <outputBusVol;

	// Parámetro correspondiente a los mandos del Synthi (todos escalados entre 0 y 10)
	var <rate = 0;

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
		lag = 0.2; //SGME_Settings.get[\ringLag];
		SynthDef(\SGME_slewLimiter, {
			arg inputBusVol,
			inFeedbackBusVol,
			inputBusControl,
			inFeedbackBusControl,
			outputBusVol,
			rate;

			var sig, control;
			sig = In.ar(inputBusVol) + InFeedback.ar(inFeedbackBusVol);
			control = In.ar(inputBusControl) + InFeedback.ar(inFeedbackBusControl);
			control = control.linlin(-1, 1, 300, -300);
			control = (rate + control).clip(1, 1000);

			sig = Slew.ar(sig, control, control);

			Out.ar(outputBusVol, sig);
		},[nil, nil, nil, nil, nil, lag]
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBusVol = Bus.audio(server);
		inFeedbackBusVol = Bus.audio(server);
		inputBusControl = Bus.audio(server);
		inFeedbackBusControl = Bus.audio(server);
		outputBusVol = Bus.audio(server);
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
			synth = Synth(\SGME_slewLimiter, [
				\inputBusVol, inputBusVol,
				\inFeedbackBusVol, inFeedbackBusVol,
				\inputBusControl, inputBusControl,
				\inFeedbackBusControl, inFeedbackBusControl,
				\outputBusVol, outputBusVol,
				\rate, this.convertRate(rate),
			], server).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun { // Dejo esta función aunque no se va a usar. Por ahora no hay manera de saber que no hay output.
		var outputTotal = inCount + outCount;
		if (outputTotal == 0, {
			synth.run(false);
		}, {
			synth.run(true);
		});
	}

	// Conversores de unidades.

	convertRate {|r|
		^r.linexp(0, 10, 1/0.001, 1/1);//settings[\slewRangeMin], settings[\slewRangeMax]);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setRate {|r|
		rate = r;
		synth.run(true);
		synth.set(\rate, this.convertRate(r));
		this.synthRun();
	}
}
