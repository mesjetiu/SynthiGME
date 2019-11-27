//********************************
//  OSCILLATOR
//********************************

// Crearé un conjunto de funciones externas al oscilador de forma que se pueda establecer cierta lógica en los parámetros: límites, advertencias, quizás rutinas para pasar de un valor a otro de los parámetros de forma suave, etc.
// Finalmente se encapsulará todo en una clase.




// Synth del módulo Oscillator
(
SynthDef(\oscillator, {
	// Parámetros manuales del S100
	arg pulseLevel = 0,
	pulseShape = 0.5, // de 0 a 1
	sineLevel = 0,
	sineSymmetry = 0, // de -1 a 1
	triangleLevel = 0,
	sawtoothLevel = 0,
	frequency = 0,

	// Parámetros de SC
	outVol = 0;
	outBus = 0;

	// Pulse
	var pulsePos = pulseShape * (1/frequency);
	var pulseNeg = (1/frequency) - pulsePos;
	var sigPulse = Env.new(
		levels: [0,1,0,0],
		times: [pulsePos, pulseNeg],
		curve: \step,
		releaseNode:2,
		loopNode: 0
	).ar() * pulseLevel * outVol;

	// Sine
	var sigSym = SinOsc.ar(frequency).abs * sineSymmetry;
	var sigSine = sigSym + SinOsc.ar(frequency, 0, (1-sineSymmetry.abs) * sineLevel);

	// Triangle
	var sigTriangle = LFTri.ar(frequency, 0, triangleLevel);

	// Sawtooth
	var sigSawtooth = Saw.ar(frequency, sawtoothLevel);

	// Suma de señales
	var sig = sigPulse + sigSine + sigTriangle + sigSawtooth;

	Out.ar(outBus, sig);

}).add;
)


// pruebas //////////////////
o = Synth(\oscillator);
o.plot
o.set(\sineSymmetry, 0)
o.set(\triangleLevel, 0.2)
o.set(\frequency, 120)
o.set(\sawtoothLevel, 0.1)
////////////////////////////




