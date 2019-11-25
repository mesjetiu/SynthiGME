// Experimentos para conseguir hacer el shaping de los osciladores



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

//////////////////////////////////////////////////////////////////////////////////

// Onda cuadrada variando la forma (shape) (aproximación con tabla de onda)

b = Buffer.alloc(s, 2**11);
(
var proporcionPositiva = 0.1; // shape variable en onda cuadrada.
var table;
var tablePos;
var tableNeg;
var tableSize = 2**10;
var sizePositivo = (tableSize * proporcionPositiva).round;
var sig;
tablePos = 1!sizePositivo;
tableNeg = -1!(tableSize-sizePositivo);
table = tablePos++tableNeg;

// creada la tabla dependiendo de la proporción, se pasa al buffer
sig = Signal.newFrom(table).asWavetable;
b.loadCollection(sig);
)

{Osc.ar(b, mul:0.2, freq:100)}.play


///////////////////////////////////////////////////////////////////////////////////

// Onda cuadrada variando la forma (shape) (aproximación con frecuencia modulada)
// Solo funciona cuando el índice de modulación no es mayor que la frecuencia

(
{
	var freq = 100;
	var vol = 0.5;
	var indiceMod = 100;
	var sigPort, sigMod;
	sigMod = SinOsc.ar(freq, pi, indiceMod);

	sigPort = PulseDPW.ar(freq + sigMod, 0.5);
}.plot;
)







//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

// Onda SINUSOIDAL variando la simetría (morphing)
// Método más eficiente y sencillo. **************************************************

(
{
	var freq = 200;
	var symmetry = 1; // -1 = simetría negativa, 0 = senoidal, 1 =  simetría positiva
	var sigSin = SinOsc.ar(freq, 0, 1-symmetry.abs);
	var sigSym = SinOsc.ar(freq, 0, symmetry.abs).abs * symmetry;
	var sig = (sigSym + sigSin);
	sig = sig * 0.2;

}.plot;

)


// Onda SINUSOIDAL variando la simetría (aproximación con tabla de onda)
// En el punto 0 se produce un cambio brusco, pero es la mejor aproximación por ahora

b = Buffer.alloc(s, 2**11);
(
var proporcionPos = 0.9; // shape variable en onda cuadrada.
var table;
var tablePos;
var tableNeg;
var tableSize = (2**10).asInt;
var sizePos = (tableSize * proporcionPos).round.asInt;
var sizeNeg = tableSize-sizePos;
var sig;
tablePos = sizePos.collect({arg i; (i/sizePos*(pi)).sin});
tableNeg = sizeNeg.collect({arg i; (i/sizeNeg*(pi)).sin})*(-1);
table = tablePos++tableNeg;

// Quizás si la fase se multiplica por la función exponenciaĺ adecuada se puede "distorsionar" la onda sinusoidal evitando los cambios bruscos en 0...
//table = tableSize.collect({arg i; (i/tableSize*(2pi)).sin});


// creada la tabla dependiendo de la proporción, se pasa al buffer
sig = Signal.newFrom(table).asWavetable;
b.loadCollection(sig);

{Osc.ar(b, mul:0.2!2, freq:100)}.plot;
)

{Osc.ar(b, mul:0.2!2, freq:180)}.play;



/////////////////////////////////////////////////
// Ensayo de oscilación con una envolvente.
(
{
	var freq = 100;
	var sig;
	sig = Env.new(levels: [0,1,0,-1,0,0], times: ((1/freq)/4)!4, curve: [-3,3,-3,3],releaseNode:4, loopNode: 0).ar() * 0.2;
}.play;
)



Env.new(levels: [0,1,0,-1,0], times: ((1/200)/4)!4, curve: [-8,8,-8,8]).plot




