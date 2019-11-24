// Experimentos para conseguir hacer el shaping de los osciladores

b = Buffer.alloc(s, 2048);

(
// Se puede variar el contenido de la tabla de onda en tiempo real.
// Para hacer el shaping de los osciladores, hay que rellenar la tabla cada vez que se cambia la forma de onda.
var array = (-0.3!1024).rand;
var sig = Signal.newFrom(array).asWavetable;
b.loadCollection(sig);
)

{Osc.ar(b, mul:1!2)}.play

b.zero // para poner a cero el buffer


//////////////////////////////////////////////////////////////////////////////////

// Onda cuadrada variando la simetría

b = Buffer.alloc(s, 2**11);
(
var proporcionPositiva = 1; // shape variable en onda cuadrada.
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

{Osc.ar(b, mul:0.2!2, freq:100)}.play