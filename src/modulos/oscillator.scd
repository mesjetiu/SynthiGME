//********************************
//  OSCILLATOR
//********************************

// Crearé un conjunto de funciones externas al oscilador de forma que se pueda establecer cierta lógica en los parámetros: límites, advertencias, quizás rutinas para pasar de un valor a otro de los parámetros de forma suave, etc.
// Finalmente se encapsulará todo en una clase.
(
SynthDef(\oscillator, {
	// Parámetros manuales del S100
	arg pulseLevel = 0,
	pulseShape = 0.5,
	sineLevel = 0,
	sineSymmetry = 0.5,
	triangleLevel = 0,
	sawtoothLevel = 0,
	frequency = 100,
	range = 1,

	// Parámetros de Voltage Control

	// Parámetros propios de SC


	var sigPulse = 0;
	var sigSine = 0;
	var sigTriangle = 0;
	var sigSawtooth = 0;
	var sig = 0;

	Out.ar(0, sig);

}).add;
)



// Código de muestra ************************************************************************
// Onda cuadrada generada por una envolvente variando la forma (shape)
// Solución más sencilla y eficiente ***************************************************
(
{
	var proporcionPos = 0.1;
	var freq = 100;
	var ratioPos = proporcionPos * (1/freq);
	var ratioNeg = (1/freq)-ratioPos;
	var sig;
	sig = Env.new(levels: [0,1,0,0], times: [ratioPos, ratioNeg], curve: \step ,releaseNode:2, loopNode: 0).ar() * 0.2;
}.play;
)