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

SGME_Oscillator : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;

	// Valores de los parámetros del Synthi 100
	// Cada vez que sean modificados en el Synth se almacenará aquí su nuevo valor
	var <range = 1; // Valores: 1 = "hi" y 0 = "lo".
	var <pulseLevel = 0; // Todos los valores son entre 0 y 10, como los diales del Synthi 100.
	var <pulseShape = 0; // entre -5 y 5
	var <sineLevel = 0;
	var <sineSymmetry = 0; // entre -5 y 5
	var <triangleLevel = 0;
	var <sawtoothLevel = 0;
	var <frequency = 5;

	var <outputBus1; // Sine y Saw
	var <outputBus2; // Pulse y Triangle
	var <inputBusVoltage; // entrada de voltage para control de la frecuencia
	var <inFeedbackBusVoltage;
	var <inputBusSync; // entrada de audio para control de la sincronización
	var <inFeedbackBusSync;
	var <inputBusPulseMod; // entrada de audio para control de la sincronización
	var <inFeedbackBusPulseMod;

	// Otros atributos de instancia
	var <server;
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth
	var resumeRoutine;
	classvar settings = nil;
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	var outVol; // Entre 0 y 1;


	// Métodos de clase //////////////////////////////////////////////////////////////////


	*new { |server|
		settings = SGME_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = SGME_Settings.get[\oscLag];
		SynthDef(\SGME_oscillator, {
			// Parámetros manuales del SGME convertidos a unidades manejables (niveles de 0 a 1, hercios, etc.)
			arg pulseLevel, // de 0 a 1
			pulseShape, // de 0 a 1
			sineLevel, // de 0 a 1
			sineSymmetry, // de -1 a 1. 0 = sinusoide
			triangleLevel, // de 0 a 1
			sawtoothLevel, // de 0 a 1
			freq, // de 0 a 10 (tal cual desde el dial)
			freqMin, freqMax, // frecuencia mínima y máxima a la que convertir los valores del dial.
			outVol, // de 0 a 1
			outputBus1,
			outputBus2,
			inputBusVoltage,
			inFeedbackBusVoltage,
			inputBusSync,
			inFeedbackBusSync,
			inputBusPulseMod,
			inFeedbackBusPulseMod;

			var scaledFreq, sigPulse, sigSym, sigSine, sigTriangle, fadeTriangle, sigSawtooth, sig1, sig2, voltIn, phase, syncIn, pulseMod;

			voltIn = In.ar(inputBusVoltage) + InFeedback.ar(inFeedbackBusVoltage);
			scaledFreq = freq.linexp(0, 10, freqMin, freqMax); // frecuencia del oscilador
			scaledFreq = scaledFreq * (2**(voltIn)); // Ajustar la influencia del control de voltaje correctamente...
			scaledFreq = scaledFreq.clip(0, 20000); // Se evita que pueda tener una frecuencia superior a 20000.
			syncIn = In.ar(inputBusSync) + InFeedback.ar(inFeedbackBusSync);
			pulseMod = (In.ar(inputBusPulseMod) + InFeedback.ar(inFeedbackBusPulseMod));
			pulseMod = pulseMod + (pulseShape*2);

			// Fase que gobierna a todos los osciladores (en caso de Opción B). Permite "hard sync", aunque los resultados sean con aliassing.
			phase = Phasor.ar(syncIn,
				(scaledFreq*2)/SampleRate.ir,-1,1);

			// PULSE **********************************
			// Opción A: (probada y funcionando hasta v1.2.2)
			//sigPulse=Pulse.ar(freq: scaledFreq,width: 1-pulseShape,mul: pulseLevel); //sin alias.
			// Opción B: (permite hard sync)
			sigPulse = Select.ar(phase<((pulseMod)-1),K2A.ar([1,0]))*pulseLevel;
			// Opción C:
			//sigPulse = LFPulse.ar(freq: scaledFreq, width: pulseShape, mul: pulseLevel);
			// Opción D:
			//sigPulse=PulseDPW.ar(freq: scaledFreq,width: 1-pulseShape,mul: pulseLevel); // sin alias y sin distorsión (forma parte de SC extended)
			// Opción E:
			// Truco para evitar aliasing mezclando dos UGens dependiendo del rango de frecuencia
			/*
			sigPulse = (LFPulse.ar(scaledFreq, mul: pulseLevel, add: (-1*(pulseLevel/2)), width: 1-pulseShape)
			* linlin(scaledFreq, 100, 1000,1,0));
			sigPulse = sigPulse + Pulse.ar(scaledFreq, mul: pulseLevel
			* linlin(scaledFreq, 100, 1000,0,1), width: 1-pulseShape);
			sigPulse = sigPulse/2;
			*/

			// SINE ******************************************
			// Opción A: (probada y funcionando hasta v1.2.2)
			/*
			sigSym = SinOsc.ar(scaledFreq).abs * sineSymmetry * sineLevel;
			sigSine = (sigSym + SinOsc.ar(scaledFreq, 0, (1-sineSymmetry.abs) * sineLevel));
			*/
			// Opción B:
			sigSym = SinOsc.ar(0, phase.linlin(-1,1,0,2pi)).abs * sineSymmetry * sineLevel;
			sigSine = (sigSym + SinOsc.ar(0, phase.linlin(-1,1,0,2pi), (1-sineSymmetry.abs) * sineLevel));

			// TRIANGLE *******************************************
			// Opción A: (probada y funcionando hasta v1.2.2)
			//sigTriangle = LFTri.ar(scaledFreq, 0, triangleLevel); // con aliasing pero más barato computacionalmente...
			// Opción B:
			sigTriangle = Select.ar(phase<0,[(phase*(-1))+0.5,phase+0.5]) * triangleLevel;
			// Opción C:
			// Truco para evitar aliasing. A partir de 600Hz se convierte el triangulo en seno (sin aliasing)
			/*
			fadeTriangle = linlin(scaledFreq, 6000, 12000, 1, 0);
			sigTriangle = LFTri.ar(scaledFreq, mul: triangleLevel * fadeTriangle);
			sigTriangle = sigTriangle + SinOsc.ar(scaledFreq, mul: triangleLevel * (1 - fadeTriangle));
			*/

			// SAWTOOTH ********************************
			// Opción A: (probada y funcionando hasta v1.2.2)
			sigSawtooth = Saw.ar(scaledFreq, sawtoothLevel);
			// Opción B:
			sigSawtooth = phase * sawtoothLevel;
			// Opción C:
			//sigSawtooth = SawDPW.ar(scaledFreq, mul: sawtoothLevel); // sin alias y sin distorsión (forma parte de SC extended)

			// Suma de señales
			sig1 = sigSine + sigSawtooth;
			sig2 = sigPulse + sigTriangle;

			Out.ar(outputBus1, sig1 * outVol);
			Out.ar(outputBus2, sig2 * outVol);

		},[lag, lag, lag, lag, lag, lag, lag, nil, nil, lag, nil, nil, nil, nil, nil, nil]
		).add;
	}



	// Métodos de instancia ////////////////////////////////////////////

	init { arg serv = Server.local;
		this.setSettings;
		server = serv;
		outputBus1 = Bus.audio(server);
		outputBus2 = Bus.audio(server);
		inputBusVoltage = Bus.audio(server);
		inFeedbackBusVoltage = Bus.audio(server);
		inputBusSync = Bus.audio(server);
		inFeedbackBusSync = Bus.audio(server);
		inputBusPulseMod = Bus.audio(server);
		inFeedbackBusPulseMod = Bus.audio(server);
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
			synth = Synth(\SGME_oscillator, [
				\pulseLevel, this.convertPulseLevel(pulseLevel),
				\pulseShape, this.convertPulseShape(pulseShape),
				\sineLevel, this.convertSineLevel(sineLevel),
				\sineSymmetry, this.convertSineSymmetry(sineSymmetry),
				\triangleLevel, this.convertTriangleLevel(triangleLevel),
				\sawtoothLevel, this.convertSawtoothLevel(sawtoothLevel),
				\freq, frequency,
				\freqMin, this.freqMinMax(range, \min),
				\freqMax, this.freqMinMax(range, \max),
				\outputBus1, outputBus1,
				\outputBus2, outputBus2,
				\inputBusVoltage, inputBusVoltage,
				\inFeedbackBusVoltage, inFeedbackBusVoltage,
				\inputBusSync, inputBusSync,
				\inFeedbackBusSync, inFeedbackBusSync,
				\inputBusPulseMod, inputBusPulseMod,
				\inFeedbackBusPulseMod, inFeedbackBusPulseMod,
				\outVol, 1,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}


	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = /*(pulseLevel + sineLevel + triangleLevel + sawtoothLevel) **/ outVol * outCount;
		if (outputTotal == 0, {
			synth.run(false);
		}, {
			synth.run(true);
		});
	}


	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertPulseLevel {|level|
		^level.linlin(0, 10, 0, settings[\oscPulseLevelMax]);
	}

	convertPulseShape {|shape|
		^shape.linlin(-5, 5, settings[\oscPulseShapeMin], settings[\oscPulseShapeMax]);
	}

	convertSineLevel {|level|
		^level.linlin(0, 10, 0, settings[\oscSineLevelMax]);
	}

	convertSineSymmetry {|symmetry|
		^symmetry.linlin(-5, 5, settings[\oscSineSymmetryMin], settings[\oscSineSymmetryMax]);
	}

	convertSawtoothLevel {|level|
		^level.linlin(0, 10, 0, settings[\oscSawtoothLevelMax]);
	}

	convertTriangleLevel {|level|
		^level.linlin(0, 10, 0, settings[\oscTriangleLevelMax]);
	}

	freqMinMax {|rang, option| // option = \min o \max
		switch (option,
			\min, {
				switch (rang,
					1, {^settings[\oscFreqHiMin]},
					0, {^settings[\oscFreqLoMin]}
				)
			},
			\max, {
				switch (rang,
					1, {^settings[\oscFreqHiMax]},
					0, {^settings[\oscFreqLoMax]}
			)},
		)
	}


	// Setters Oscillators////////////////////////////////////////////////////////////////////////
	setRange {| rang |
		range = rang.asInteger;
		synth.run(true);
		synth.set(\freqMin, this.freqMinMax(rang.asInteger, \min));
		synth.set(\freqMax, this.freqMinMax(rang.asInteger, \max));
		this.synthRun();
	}

	setPulseLevel {| level |
		pulseLevel = level;
		synth.run(true);
		synth.set(\pulseLevel, this.convertPulseLevel(level));
		this.synthRun();
	}

	setPulseShape {| shape |
		pulseShape = shape;
		synth.run(true);
		synth.set(\pulseShape, this.convertPulseShape(shape));
		this.synthRun();
	}

	setSineLevel {| level |
		sineLevel = level;
		synth.run(true);
		synth.set(\sineLevel, this.convertSineLevel(level));
		this.synthRun();
	}

	setSineSymmetry {| symmetry |
		sineSymmetry = symmetry;
		synth.run(true);
		synth.set(\sineSymmetry, this.convertSineSymmetry(symmetry));
		this.synthRun();
	}

	setTriangleLevel {| level |
		triangleLevel = level;
		synth.run(true);
		synth.set(\triangleLevel, this.convertTriangleLevel(level));
		this.synthRun();
	}

	setSawtoothLevel {| level |
		sawtoothLevel = level;
		synth.run(true);
		synth.set(\sawtoothLevel, this.convertSawtoothLevel(level));
		this.synthRun();
	}

	setFrequency {| freq |
		frequency = freq;
		synth.run(true);
		synth.set(\freq, freq);
		this.synthRun();
	}

	setOutVol {| level |
		outVol = level;
		synth.run(true);
		synth.set(\outVol, level);
		this.synthRun();
	}
	//End Setters Oscillators//////////////////////////////////////////////////////////////////////


	// Carga la configuración
	setSettings {
		outVol = settings[\oscOutVol];
	}
}