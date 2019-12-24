S100_Oscillator {

	// Variables de la clase
	classvar lag = 0.5; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar outVol = 1; // Entre 0 y 1;

	// Synth de la instancia
	var <synth = nil;

	// Valores de los parámetros del Synthi 100
	// Cada vez que sean modificados en el Synth se almacenará aquí su nuevo valor
	var <range = 1; // Valores: 1 = "hi" y 0 = "lo". Por ahora no tiene ningún efecto
	var <pulseLevel = 0; // Todos los valores son entre 0 y 10, como los diales del Synthi 100.
	var <pulseShape = 5;
	var <sineLevel = 0;
	var <sineSymmetry = 5;
	var <triangleLevel = 0;
	var <sawtoothLevel = 0;
	var <frequency = 5;

	// Otros atributos de instancia
	var <outputBus1; // Sine y Saw
	var <outputBus2; // Pulse y Triangle
	var <server;
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth
	var settings = nil;


	// Métodos de clase //////////////////////////////////////////////////////////////////


	*new { |server|
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_oscillator, {
			// Parámetros manuales del S100 convertidos a unidades manejables (niveles de 0 a 1, hercios, etc.)
			arg pulseLevel, // de 0 a 1
			pulseShape, // de 0 a 1
			sineLevel, // de 0 a 1
			sineSymmetry, // de -1 a 1. 0 = sinusoide
			triangleLevel, // de 0 a 1
			sawtoothLevel, // de 0 a 1
			freq, // de 0 a 10 (tal cual desde el dial)
			freqMin, freqMax, // frecuencia mínima y máxima a la que convertir los valores del dial.

			// Parámetros de SC
			outVol, // de 0 a 1
			outputBus1,
			outputBus2;

			var scaledFreq = freq.linexp(0, 10, freqMin, freqMax); // frecuencia del oscilador

			// Pulse
			var sigPulse = LFPulse.ar(freq: scaledFreq, width: pulseShape, mul: pulseLevel);
			//var sigPulse=Pulse.ar(freq: freq,width: 1-pulseShape,mul: pulseLevel*outVol); //sin alias.

			// Sine
			var sigSym = SinOsc.ar(scaledFreq).abs * sineSymmetry * sineLevel;
			var sigSine =
			(sigSym + SinOsc.ar(scaledFreq, 0, (1-sineSymmetry.abs) * sineLevel));

			// Triangle
			var sigTriangle = LFTri.ar(scaledFreq, 0, triangleLevel);

			// Sawtooth
			var sigSawtooth = Saw.ar(scaledFreq, sawtoothLevel);

			// Suma de señales
			var sig1 = sigSine + sigSawtooth;
			var sig2 = sigPulse + sigTriangle;

			Out.ar(outputBus1, sig1 * outVol);
			Out.ar(outputBus2, sig2 * outVol);

		},[lag, lag, lag, lag, lag, lag, lag, nil, nil, lag, nil, nil]
		).add;

		// experimentando con un oscilador en el que todos sus UGens estén en fase
		// Funciona con Phasor.ar. Para que funcionara perfectamente es necesario encontrar UGens que tengan parámetro de audio "phase". No todos lo tienen...
		/*		SynthDef(\S100_oscillator2, {
		// Parámetros manuales del S100 convertidos a unidades manejables (niveles de 0 a 1, hercios, etc.)
		arg pulseLevel, // de 0 a 1
		pulseShape, // de 0 a 1
		sineLevel, // de 0 a 1
		sineSymmetry, // de -1 a 1. 0 = sinusoide
		triangleLevel, // de 0 a 1
		sawtoothLevel, // de 0 a 1
		freq, // de 0 a 10 (tal cual desde el dial)
		freqMin, freqMax, // frecuencia mínima y máxima a la que convertir los valores del dial.

		// Parámetros de SC
		outVol, // de 0 a 1
		outputBus1,
		outputBus2;

		var scaledFreq = freq.linexp(0, 10, freqMin, freqMax); // frecuencia del oscilador

		// Phasor director
		var phasor = Phasor.ar(trig: sigPulse, rate: 2pi * scaledFreq / 48000);

		// Pulse
		var sigPulse = LFPulse.ar(freq: scaledFreq, width: pulseShape, mul: pulseLevel);
		//var sigPulse=Pulse.ar(freq: freq,width: 1-pulseShape,mul: pulseLevel*outVol); //sin alias.

		// Sine
		var sigSym = SinOsc.ar(0, phasor).abs * sineSymmetry * sineLevel;
		var sigSine =
		(sigSym + SinOsc.ar(0, phasor, (1-sineSymmetry.abs) * sineLevel));

		// Triangle
		var sigTriangle = LFTri.ar(0, phasor, triangleLevel);

		// Sawtooth
		//	var sigSawtooth = Saw.ar(scaledFreq, sawtoothLevel);

		// Suma de señales
		var sig1 = sigSine;// + sigSawtooth;
		var sig2 = sigPulse + sigTriangle;

		Out.ar(outputBus1, sig1 * outVol);
		Out.ar(outputBus2, sig2 * outVol);

		},[lag, lag, lag, lag, lag, lag, lag, nil, nil, lag, nil, nil]
		).add
		*/
	}



	// Métodos de instancia ////////////////////////////////////////////

	init { arg serv = Server.local;
		settings = S100_Settings.get;
		this.setSettings;
		server = serv;
		outputBus1 = Bus.audio(server);
		outputBus2 = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_oscillator, [
				\pulseLevel, this.convertPulseLevel(pulseLevel),
				\pulseShape, this.convertPulseShape(pulseShape),
				\sineLevel, this.convertSineLevel(sineLevel),
				\sineSymmetry, this.convertSineSymmetry(sineSymmetry),
				\triangleLevel, this.convertTriangleLevel(triangleLevel),
				\sawtoothLevel, this.convertSawtoothLevel(sawtoothLevel),
				\freq, frequency, //this.convertFrequency(frequency),
				\freqMin, this.freqMinMax(range, \min),
				\freqMax, this.freqMinMax(range, \max),
				\outputBus1, outputBus1,
				\outputBus2, outputBus2,
				\outVol, 1,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}


	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = (pulseLevel + sineLevel + triangleLevel + sawtoothLevel) * outVol;
		if (outputTotal==0, {
			running = false;
			pauseRoutine.reset;
			pauseRoutine.play;
		}, {
			pauseRoutine.stop;
			running = true;
			synth.run(true);
		});
	}


	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertPulseLevel {|level|
		^level.linlin(0, 10, 0, 1);
	}

	convertPulseShape {|shape|
		^shape.linlin(0, 10, 0, 1);
	}

	convertSineLevel {|level|
		^level.linlin(0, 10, 0, 1);
	}

	convertSineSymmetry {|symmetry|
		^symmetry.linlin(0, 10, -1, 1);
	}

	convertSawtoothLevel {|level|
		^level.linlin(0, 10, 0, 1);
	}

	convertTriangleLevel {|level|
		^level.linlin(0, 10, 0, 1);
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
		if((rang==1).or(rang==0), {
			range = rang;
			synth.set(\freqMin, this.freqMinMax(rang, \min));
			synth.set(\freqMax, this.freqMinMax(rang, \max));
		}, {
			("S100_Oscillator/setRange: " + rang + " debe contener los valores hi o lo").postln})
	}

	setPulseLevel {| level |
		if((level>=0).and(level<=10), {
			pulseLevel = level;
			this.synthRun();
			synth.set(\pulseLevel, this.convertPulseLevel(level))}, {
			("S100_Oscillator/setPulseLevel: " + level + " no es un valor entre 0 y 1").postln})
	}

	setPulseShape {| shape |
		if((shape>=0).and(shape<=10), {
			pulseShape = shape;
			synth.set(\pulseShape, this.convertPulseShape(shape))}, {
			("S100_Oscillator/setPulseShape: " + shape + " no es un valor entre 0 y 1").postln});
	}

	setSineLevel {| level |
		if((level>=0).and(level<=10), {
			sineLevel = level;
			this.synthRun();
			synth.set(\sineLevel, this.convertSineLevel(level))}, {
			("S100_Oscillator/setSineLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSineSymmetry {| symmetry |
		if((symmetry>=0).and(symmetry<=10), {
			sineSymmetry = symmetry;
			synth.set(\sineSymmetry, this.convertSineSymmetry(symmetry))}, {
			("S100_Oscillator/setSineSymmetry: " + symmetry + " no es un valor entre -1 y 1").postln});
	}

	setTriangleLevel {| level |
		if((level>=0).and(level<=10), {
			triangleLevel = level;
			this.synthRun();
			synth.set(\triangleLevel, this.convertTriangleLevel(level))}, {
			("S100_Oscillator/setTriangleLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSawtoothLevel {| level |
		if((level>=0).and(level<=10), {
			sawtoothLevel = level;
			this.synthRun();
			synth.set(\sawtoothLevel, this.convertSawtoothLevel(level))}, {
			("S100_Oscillator/setSawtoothLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setFrequency {| freq |
		if((freq>=0).and(freq<=10), {
			frequency = freq;
			//synth.set(\freq, this.convertFrequency(freq))}, {
			synth.set(\freq, freq)}, {
			("S100_Oscillator/setfrequency: " + freq + " no es un valor entre 0 y 10000").postln});
	}

	setOutVol {| level |
		if((level>=0).and(level<=1), {
			outVol = level;
			this.synthRun();
			synth.set(\outVol, level)}, {
			("S100_Oscillator/setOutVol: " + level + " no es un valor entre 0 y 1").postln});
	}
	//End Setters Oscillators//////////////////////////////////////////////////////////////////////


	// Carga la configuración
	setSettings {
		outVol = settings[\oscOutVol];
		lag = settings[\oscLag];
	}
}