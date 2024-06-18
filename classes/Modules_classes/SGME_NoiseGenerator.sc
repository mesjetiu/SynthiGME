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

SGME_NoiseGenerator : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;

	var <server;
	var <outputBus; // Salida.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <colour = 5; // Filtro pasabajos y pasaaltos.
	var <level = 0; // Entre 0 y 1. Nivel de volumen de salida.


	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
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
		lag = SGME_Settings.get[\noiseLag];
		SynthDef(\SGME_noiseGenerator, {
			arg outputBus,
			freqHP,
			freqLP,
			level; // entre 0 y 1

			var sig;

			sig = WhiteNoise.ar;

			// Se realiza el filtrado
			sig = HPF.ar(sig, freqHP);
			sig = LPF.ar(sig, freqLP);

			// Se aplica el nivel (level)
			sig = sig * level;

			Out.ar(outputBus, sig);

		}, [nil, lag, lag, lag]
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
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
			synth = Synth(\SGME_noiseGenerator, [
				\outputBus, outputBus,
				\freqHP, this.convertColour(colour)[0],
				\freqLP, this.convertColour(colour)[1],
				\level, this.convertLevel(level),
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = outCount; //* level
		if (outputTotal == 0, {
			synth.run(false);
		}, {
			synth.run(true);
		});
	}

	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertLevel {|level|
		^level.linlin(0, 10, 0, settings[\noiseLevelMax]);
	}

	convertColour {|col| // Retorna las frecuencias de corte de ambos filtros: pasabajos y pasaaltos
		var filterHP, filterLP;
		filterHP = col.linexp(
			inMin: 5, // valor mínimo del dial
			inMax: 10, // valor máximo del dial
			outMin: settings[\noiseHPFreqMin], // frecuencia mínima (valor del dial: 5 o menos)
			outMax: settings[\noiseHPFreqMax] //frecuencia máxima (valor del dial: 10)
		);
		filterLP = col.linexp(
			inMin: 0, // valor mínimo del dial
			inMax: 5, // valor máximo del dial
			outMin: settings[\noiseLPFreqMin], // frecuencia mínima (valor del dial: 1)
			outMax: settings[\noiseLPFreqMax] //frecuencia máxima (valor del dial: 5 o más)
		);
		^[filterHP, filterLP];
	}


	// Setters de los parámetros
	setLevel {|lev|
		level = lev;
		synth.run(true);
		synth.set(\level, this.convertLevel(lev));
		this.synthRun();
	}

	setColour {|col|
		var freqHP, freqLP;
		#freqHP,freqLP = this.convertColour(col);
		colour = col;
		synth.run(true);
		synth.set(\freqHP, freqHP);
		synth.set(\freqLP, freqLP);
		this.synthRun();
	}
}