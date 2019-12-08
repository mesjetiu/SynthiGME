S100_Oscillator {
	// Synth de la instancia
	var oscillatorSynth = nil;

	// Valores de los parámetros del Synthi 100
	// Cada vez que sean modificados en el Synth se almacenará aquí su nuevo valor
	var <range = "hi"; // Valores: "hi" y "lo". Por ahora no tiene ningún efecto
	var <pulseLevel = 0; // Todos los valores son entre 0 y 10.
	var <pulseShape = 5;
	var <sineLevel = 0;
	var <sineSymmetry = 5;
	var <triangleLevel = 0;
	var <sawtoothLevel = 0;
	var <freqOscillator = 6;

	// Otros atributos de instancia
	var <outBus1; // pulso y al senoide (por comprobar en Synthi)
	var <outBus2; // triángulo y diente de sierra (por comprobar en Synthi)
	var <server;
	var <outVol = 1; // Entre 0 y 1;
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth

	// Métodos de clase //////////////////////////////////////////////////////////////////


	*new { |server|
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_oscillator, {
			// Parámetros manuales del S100
			arg pulseLevel,
			pulseShape, // de 0 a 1
			sineLevel,
			sineSymmetry, // de -1 a 1
			triangleLevel,
			sawtoothLevel,
			freq,

			// Parámetros de SC
			outVol,
			outBus1,
			outBus2;

			// Pulse
			var sigPulse = LFPulse.ar(freq: freq, width: pulseShape, mul: pulseLevel);
			//var sigPulse=Pulse.ar(freq: freq,width: 1-pulseShape,mul: pulseLevel*outVol); //sin alias.


			// Sine
			var sigSym = SinOsc.ar(freq).abs * sineSymmetry * sineLevel;
			var sigSine =
			(sigSym + SinOsc.ar(freq, 0, (1-sineSymmetry.abs) * sineLevel));

			// Triangle
			var sigTriangle = LFTri.ar(freq, 0, triangleLevel);

			// Sawtooth
			var sigSawtooth = Saw.ar(freq, sawtoothLevel);

			// Suma de señales
			var sig1 = sigPulse + sigSine;
			var sig2 = sigTriangle + sigSawtooth;

			Out.ar(outBus1, sig1 * outVol);
			Out.ar(outBus2, sig2 * outVol);
			//	Out.ar(0, sig!2);
		}, [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, nil, nil]
		).add
	}



	// Métodos de instancia ////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		outBus1 = Bus.audio(server);
		outBus2 = Bus.audio(server);
		pauseRoutine = Routine({
			0.5.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			oscillatorSynth.run(false);
		});
	}

	// Crea el Synth en el servidor
	play {
		if(oscillatorSynth.isPlaying==false, {
			oscillatorSynth = nil;
			oscillatorSynth = Synth(\S100_oscillator, [
				\pulseLevel, pulseLevel/10,
				\pulseShape, pulseShape/10,
				\sineLevel, sineLevel/10,
				\sineSymmetry, (sineSymmetry/5)-1,
				\triangleLevel, triangleLevel/10,
				\sawtoothLevel, sawtoothLevel/10,
				\freq, (10000.pow(1/10)).pow(freqOscillator),
				\outBus1, outBus1,
				\outBus2, outBus2,
				\outVol, 1,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Libera el Synth del servidor
	stop {
		if(oscillatorSynth.isPlaying, {
			oscillatorSynth.free;
		});
		oscillatorSynth = nil;
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
			oscillatorSynth.run(true);
		});
	}

	// Setters Oscillators////////////////////////////////////////////////////////////////////////
	setRange {| rang |
		if((rang=="hi").and(rang=="lo"), {range = rang}, {
			("S100_Oscillator/setRange: " + rang + " debe contener los valores hi o lo").postln})
	}

	setPulseLevel {| level |
		if((level>=0).and(level<=10), {
			pulseLevel = level;
			this.synthRun();
			oscillatorSynth.set(\pulseLevel, level/10)}, {
			("S100_Oscillator/setPulseLevel: " + level + " no es un valor entre 0 y 1").postln})
	}

	setPulseShape {| shape |
		if((shape>=0).and(shape<=10), {
			pulseShape = shape;
			oscillatorSynth.set(\pulseShape, shape/10)}, {
			("S100_Oscillator/setPulseShape: " + shape + " no es un valor entre 0 y 1").postln});
	}

	setSineLevel {| level |
		if((level>=0).and(level<=10), {
			sineLevel = level;
			this.synthRun();
			oscillatorSynth.set(\sineLevel, level/10)}, {
			("S100_Oscillator/setSineLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSineSymmetry {| symmetry |
		if((symmetry>=0).and(symmetry<=10), {
			sineSymmetry = symmetry;
			oscillatorSynth.set(\sineSymmetry, (symmetry/5)-1)}, {
			("S100_Oscillator/setSineSymmetry: " + symmetry + " no es un valor entre -1 y 1").postln});
	}

	setTriangleLevel {| level |
		if((level>=0).and(level<=10), {
			triangleLevel = level;
			this.synthRun();
			oscillatorSynth.set(\triangleLevel, level/10)}, {
			("S100_Oscillator/setTriangleLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSawtoothLevel {| level |
		if((level>=0).and(level<=10), {
			sawtoothLevel = level;
			this.synthRun();
			oscillatorSynth.set(\sawtoothLevel, level/10)}, {
			("S100_Oscillator/setSawtoothLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setFrequency {| freq |
		if((freq>=0).and(freq<=10), {
			// frecuencias entre 1 y 10000 Hz
			freqOscillator = freq;
			oscillatorSynth.set(\freq, (10000.pow(1/10)).pow(freq))}, {
			("S100_Oscillator/setFreqOscillator: " + freq + " no es un valor entre 0 y 10000").postln});
	}

	setOutVol {| level |
		if((level>=0).and(level<=1), {
			outVol = level;
			this.synthRun();
			oscillatorSynth.set(\outVol, level)}, {
			("S100_Oscillator/setOutVol: " + level + " no es un valor entre 0 y 1").postln});
	}
	//End Setters Oscillators//////////////////////////////////////////////////////////////////////
}