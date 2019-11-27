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
	outVol = 0,
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
	var sigSine = (sigSym + SinOsc.ar(frequency, 0, (1-sineSymmetry.abs) * sineLevel)) * outVol;

	// Triangle
	var sigTriangle = LFTri.ar(frequency, 0, triangleLevel * outVol);

	// Sawtooth
	var sigSawtooth = Saw.ar(frequency, sawtoothLevel * outVol);

	// Suma de señales
	var sig = sigPulse + sigSine + sigTriangle + sigSawtooth;

	Out.ar(outBus, sig);
}).add;
)




(
// Synth de la instancia
~oscillator = nil;

// Valores de los parámetros del Synth
// Cada vez que sean modificados en el Synth se almacenará aquí su nuevo valor
~pulseLevel = 0;
~pulseShape = 0.5; // de 0 a 1
~sineLevel = 0;
~sineSymmetry = 0; // de -1 a 1
~triangleLevel = 0;
~sawtoothLevel = 0;
~freqOscillator = 0;
~outVol = 0;

// Funciones (futuros métodos de clase) ////////////////////////////////////////////

// Crea el Synth en el servidor
~createSynth = {
	if(~oscillator.isNil, {~oscillator = Synth(\oscillator)});
};

// Libera el Synth del servidor
~freeSynth = {
	~oscillator.free;
	~oscillator = nil;
};

// Funciones (futuros métodos de instancia) ///////////////////////////////////////
~setPulseLevel = {| level |
	if(level>=0 && level<=1, {~oscillator.set(\pulseLevel, level)});
};


)


// pruebas //////////////////
o = Synth(\oscillator);
o.plot
o.set(\sineSymmetry, 0)
o.set(\triangleLevel, 0.2)
o.set(\frequency, 120)
o.set(\sawtoothLevel, 0)
o.set(\pulseLevel, 0.2)
o.set(\outVol, 1)


~createSynth.value();
~freeSynth.value();
~oscillator.set(\outVol, 1)
~oscillator.set(\frequency, 100)
~oscillator.set(\pulseLevel, 0.3)
~setPulseLevel.value(0.1)

