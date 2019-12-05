S100_Oscillator {
	// Synth de la instancia
	var oscillator = nil;

	// Valores de los parámetros del Synth
	// Cada vez que sean modificados en el Synth se almacenará aquí su nuevo valor
	var <range = "hi"; // Valores: "hi" y "lo". Por ahora no tiene ningún efecto
	var <pulseLevel = 0;
	var <pulseShape = 0.5; // de 0 a 1
	var <sineLevel = 0;
	var <sineSymmetry = 0; // de -1 a 11
	var <triangleLevel = 0;
	var <sawtoothLevel = 0;
	var <freqOscillator = 100;

	// Otras variables de la clase
	var <inBus, <outBus;
	var <server;
	var <outVol = 1;
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth

	// Métodos de clase //////////////////////////////////////////////////////////////////


	*new { |server|
		this.addSynthDef();
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\oscillator, {
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
			inBus,
			outBus;

			// Pulse
			var sigPulse = LFPulse.ar(freq: freq, width: pulseShape, mul: pulseLevel * outVol);
			//var sigPulse=Pulse.ar(freq: freq,width: 1-pulseShape,mul: pulseLevel*outVol); //sin alias.


			// Sine
			var sigSym = SinOsc.ar(freq).abs * sineSymmetry;
			var sigSine =
			(sigSym + SinOsc.ar(freq, 0, (1-sineSymmetry.abs) * sineLevel)) * outVol;

			// Triangle
			var sigTriangle = LFTri.ar(freq, 0, triangleLevel * outVol);

			// Sawtooth
			var sigSawtooth = Saw.ar(freq, sawtoothLevel * outVol);

			// Suma de señales
			var sig = sigPulse + sigSine + sigTriangle + sigSawtooth;

			Out.ar(outBus, sig);
			//	Out.ar(0, sig!2);
		}, [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, nil, nil, 0.5]
		).add
	}



	// Métodos de instancia ////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inBus = Bus.audio(server);
		outBus = Bus.audio(server);
		pauseRoutine = Routine({
			0.5.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			oscillator.run(false);
		});
	}

	// Crea el Synth en el servidor
	play {
		if(oscillator.isPlaying==false, {
			oscillator = nil;
			oscillator = Synth(\oscillator, [
				\pulseLevel, pulseLevel,
				\pulseShape, pulseShape,
				\sineLevel, sineLevel,
				\sineSymmetry, sineSymmetry,
				\triangleLevel, triangleLevel,
				\sawtoothLevel, sawtoothLevel,
				\freq, freqOscillator,
				\inBus, inBus,
				\outBus, outBus,
				\outVol, 1,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.runSynth;
	}

	// Libera el Synth del servidor
	stop {
		if(oscillator.isPlaying, {
			oscillator.free;
		});
		oscillator = nil;
	}

	runSynth {
		var outputTotal = (pulseLevel + sineLevel + triangleLevel + sawtoothLevel) * outVol;
		if (outputTotal==0, {
			running = false;
			pauseRoutine.reset;
			pauseRoutine.play;
		}, {
			pauseRoutine.stop;
			running = true;
			oscillator.run(true);
		});
	}

	// Setters Oscillators////////////////////////////////////////////////////////////////////////
	setRange {| rang |
		if((rang=="hi").and(rang=="lo"), {range = rang}, {
			("S100_Oscillator/setRange: " + rang + " debe contener los valores hi o lo").postln})
	}

	setPulseLevel {| level |
		if((level>=0).and(level<=1), {
			pulseLevel = level;
			this.runSynth();
			oscillator.set(\pulseLevel, level)}, {
			("S100_Oscillator/setPulseLevel: " + level + " no es un valor entre 0 y 1").postln})
	}

	setPulseShape {| shape |
		if((shape>=0).and(shape<=1), {
			pulseShape = shape;
			oscillator.set(\pulseShape, shape)}, {
			("S100_Oscillator/setPulseShape: " + shape + " no es un valor entre 0 y 1").postln});
	}

	setSineLevel {| level |
		if((level>=0).and(level<=1), {
			sineLevel = level;
			this.runSynth();
			oscillator.set(\sineLevel, level)}, {
			("S100_Oscillator/setSineLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSineSymmetry {| symmetry |
		if((symmetry>=-1).and(symmetry<=1), {
			sineSymmetry = symmetry;
			oscillator.set(\sineSymmetry, symmetry)}, {
			("S100_Oscillator/setSineSymmetry: " + symmetry + " no es un valor entre -1 y 1").postln});
	}

	setTriangleLevel {| level |
		if((level>=0).and(level<=1), {
			triangleLevel = level;
			this.runSynth();
			oscillator.set(\triangleLevel, level)}, {
			("S100_Oscillator/setTriangleLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSawtoothLevel {| level |
		if((level>=0).and(level<=1), {
			sawtoothLevel = level;
			this.runSynth();
			oscillator.set(\sawtoothLevel, level)}, {
			("S100_Oscillator/setSawtoothLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setFrequency {| freq |
		if((freq>=0).and(freq<=10000), {
			freqOscillator = freq;
			oscillator.set(\freq, freq)}, {
			("S100_Oscillator/setFreqOscillator: " + freq + " no es un valor entre 0 y 1").postln});
	}

	setOutVol {| level |
		if((level>=0).and(level<=1), {
			outVol = level;
			this.runSynth();
			oscillator.set(\outVol, level)}, {
			("S100_Oscillator/setOutVol: " + level + " no es un valor entre 0 y 1").postln});
	}
	//End Setters Oscillators//////////////////////////////////////////////////////////////////////
}