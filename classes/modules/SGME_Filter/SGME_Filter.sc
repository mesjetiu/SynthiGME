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

SGME_Filter : SGME_Connectable {
	// Esta clase ha de ser heredada por SGME_LPFilter y SGME_HPFilter. Ambas clases han de sobrescribir *addSynthDef y this.createSynth

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida
	var <inputBus;
	var <inFeedbackBus;
	var <inputBusVoltage;
	var <inFeedbackBusVoltage;
	var <outputBus; // Salida

	// Parámetro correspondiente a los mandos del Synthi (todos escalados entre 0 y 10)
	var <frequency = 0;
	var <response = 0;
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

	*addSynthDef { // Esta función ha de ser sobrescrita.
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		inputBusVoltage = Bus.audio(server);
		inFeedbackBusVoltage = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			1.wait;
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth { // Este método ha de ser sobrescrito
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

	convertFrequency {|f|
		^f.linexp(0, 10, 5, 20000); // Datos tomados del Synthi 100 (1971)
	}

	convertResponse {|r|
		^r.linexp(0, 10, 1, 0.000001);
	}

	convertLevel {|l|
		^l.linlin(0, 10, 0, 1);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setFrequency {|f|
		frequency = f;
		synth.run(true);
		synth.set(\frequency, this.convertFrequency(f));
		this.synthRun();
	}

	setResponse {|r|
		response = r;
		synth.run(true);
		synth.set(\response, this.convertResponse(r));
		this.synthRun();
	}

	setLevel {|l|
		level = l;
		synth.run(true);
		synth.set(\level, this.convertLevel(l));
		this.synthRun();
	}
}
