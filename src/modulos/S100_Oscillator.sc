
S100_Oscillator {
	// Synth de la instancia
	var oscillator = nil;

	// Valores de los parámetros del Synth
	// Cada vez que sean modificados en el Synth se almacenará aquí su nuevo valor
	var <pulseLevel = 0;
	var <pulseShape = 0.5; // de 0 a 1
	var <sineLevel = 0;
	var <sineSymmetry = 0; // de -1 a 1
	var <triangleLevel = 0;
	var <sawtoothLevel = 0;
	var <freqOscillator = 100;
	var <>outBus = 0;
	var <server;

	// Opciones
	var <outVol = 1;

	// Métodos de clase //////////////////////////////////////////////////////////////////


	*new { |server, audioOutBus|
		^super.new.init(server, audioOutBus);
	}



	// Métodos de instancia ////////////////////////////////////////////

	init { arg serv = Server.local, aOutBus;
		outBus = aOutBus;
		server = serv;
	}

	// Crea el Synth en el servidor
	createSynth {
		if(oscillator.isNil, {
			oscillator = SynthDef(\oscillator, {
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
				outBus;

				// Pulse
				var pulsePos = pulseShape * (1/freq);
				var pulseNeg = (1/freq) - pulsePos;
				var sigPulse = Env.new(
					levels: [0,1,0,0],
					times: [pulsePos, pulseNeg],
					curve: \step,
					releaseNode:2,
					loopNode: 0
				).ar() * pulseLevel * outVol;

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
			}).play(server, args:[
				\pulseLevel, pulseLevel,
				\pulseShape, pulseShape,
				\sineLevel, sineLevel,
				\sineSymmetry, sineSymmetry,
				\triangleLevel, triangleLevel,
				\sawtoothLevel, sawtoothLevel,
				\freq, freqOscillator,
				\outBus, outBus,
				\outVol, 1,
			]);
		});
	}

	// Libera el Synth del servidor
	freeSynth {
		oscillator.free;
		oscillator = nil;
	}

	setPulseLevel {| level |
		if((level>=0).and( {level<=1}), {
			pulseLevel = level;
			oscillator.set(\pulseLevel, level)}, {
			("S100_Oscillator/setPulseLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setPulseShape {| shape |
		if((shape>=0).and( {shape<=1}), {
			pulseShape = shape;
			oscillator.set(\pulseShape, shape)}, {
			("S100_Oscillator/setPulseShape: " + shape + " no es un valor entre 0 y 1").postln});
	}

	setSineLevel {| level |
		if((level>=0).and( {level<=1}), {
			sineLevel = level;
			oscillator.set(\sineLevel, level)}, {
			("S100_Oscillator/setSineLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSineSymmetry {| symmetry |
		if((symmetry>=-1).and( {symmetry<=1}), {
			sineSymmetry = symmetry;
			oscillator.set(\sineSymmetry, symmetry)}, {
			("S100_Oscillator/setSineSymmetry: " + symmetry + " no es un valor entre -1 y 1").postln});
	}

	setTriangleLevel {| level |
		if((level>=0).and( {level<=1}), {
			pulseLevel = level;
			oscillator.set(\triangleLevel, level)}, {
			("S100_Oscillator/setTriangleLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSawtoothLevel {| level |
		if((level>=0).and( {level<=1}), {
			sawtoothLevel = level;
			oscillator.set(\sawtoothLevel, level)}, {
			("S100_Oscillator/setSawtoothLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setFreqOscillator {| freq |
		if((freq>=0).and( {freq<=10000}), {
			freqOscillator = freq;
			oscillator.set(\freq, freq)}, {
			("S100_Oscillator/setFreqOscillator: " + freq + " no es un valor entre 0 y 1").postln});
	}
}