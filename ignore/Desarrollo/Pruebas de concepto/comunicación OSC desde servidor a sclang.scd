// Define la función externa
(
var externalFunction = {
	"Function triggered!".postln;
};

// Define un OSCdef que escuche por los triggers enviados desde el Synth
OSCdef(\triggeredFunction, { |msg|
	var value;
	value = msg[3];
	msg.postln;

	// Ejecuta la función externa
	//externalFunction.value;
}, '/tr');
)

// Define el Synth que enviará el trigger
(
SynthDef(\triggerSynth, { |out=0, signal=0|
	var trig;

	// Tu señal de audio, por ejemplo un Oscilador Seno
	// sig = SinOsc.ar(440);

	// Detectar cuando la señal cruza el umbral de 0.5
	trig = Trig.kr(signal > 0.5, 0.1);

	// Enviar un trigger cuando se detecta el cruce
	SendTrig.kr(trig, 1, signal);

	// Out.ar(out, sig);
}).add;
)

// Ejecuta el Synth y prueba enviando una señal

~synth = Synth(\triggerSynth);

// Enviar una señal para activar el trigger
~synth.set(\signal, 0.6);  // Cambiar el valor de la señal según sea necesario

~synth.set(\signal, 0.6);  // Cambiar el valor de la señal según sea necesario
~synth.set(\signal, -1);  // Cambiar el valor de la señal según sea necesario
~synth.set(\signal, 0.6);  // Cambiar el valor de la señal según sea necesario
~synth.set(\signal, -1);  // Cambiar el valor de la señal según sea necesario
~synth.set(\signal, 0.6);  // Cambiar el valor de la señal según sea necesario
~synth.set(\signal, -1);  // Cambiar el valor de la señal según sea necesario
~synth.set(\signal, 0.6);  // Cambiar el valor de la señal según sea necesario
~synth.set(\signal, -1);  // Cambiar el valor de la señal según sea necesario



(
t = Task({
	var waitTime = 0.1;
	loop {
		~synth.set(\signal, 0.6);  // Cambiar el valor de la señal según sea necesario
		0.6.postln;
		waitTime.wait;  // Intervalo de espera para la lectura del bus
		~synth.set(\signal, -1);  // Cambiar el valor de la señal según sea necesario
		1.neg.postln;
		waitTime.wait;  // Intervalo de espera para la lectura del bus
	}
}).start;
)
t.stop