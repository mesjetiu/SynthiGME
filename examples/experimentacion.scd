Quarks.install("https://github.com/mesjetiu/Synthi100.git")
Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")

Quarks.uninstall("https://github.com/mesjetiu/Synthi100.git")

Quarks.clear

[1,2].add(3)

s.options.numOutputBusChannels != 18
{WhiteNoise.ar}.play


o = Synthi100(standalone:true).run().pairDevice


o.modulRingModulators[0].setLevel(10)
o.modulNoiseGenerators[0].setLevel(3)


o.modulInputAmplifiers[0].setLevel(5)
o.modulOutputChannels[0].setLevel(1)

OSCFunc.trace(false, false)
OSCFunc.trace(true, false)

o.setGeneralVol(0.7)
o.run
o = Synthi100().run.pairDevice;
o.pairDevice

SinOsc

// Unimos los tres primeros osciladores a los tres primeros canales de salida:
(
o.setParameterOSC("/patchA/91/36", 1); // salida de osc1 a output1
o.setParameterOSC("/patchA/92/37", 1); // salida de osc1 a output2

o.setParameterOSC("/patchA/93/38", 1); // salida de osc2 a output3
o.setParameterOSC("/patchA/94/39", 1); // salida de osc2 a output4

o.setParameterOSC("/patchA/95/40", 1); // salida de osc3 a output5
o.setParameterOSC("/patchA/96/41", 1); // salida de osc3 a output6

o.setParameterOSC("/osc/1/triangle/level", 5);
o.setParameterOSC("/osc/1/sawtooth/level", 4);


o.setParameterOSC("/out/1/level", 5);
o.setParameterOSC("/out/2/level", 5);

o.setParameterOSC("/out/1/on", 1);
o.setParameterOSC("/out/2/on", 1);
)
"".postln
b = Server(\myserver)
b.boot


ServerOptions
s.options.device = nil

s.serverRunning
Server.local.serverRunning
s.options.numOutputBusChannels
a = Bus.audio
a
o.setParameterOSC("/osc/1/pulse/level", 4);
o.setParameterOSC("/osc/1/pulse/shape", 10);


o.setParameterOSC("/osc/1/range", 1)
o.setParameterOSC("/osc/1/frequency", 3)
(
o.setParameterOSC("/out/1/filter", 5);
o.setParameterOSC("/out/2/filter", 5);
)

(
o.setParameterOSC("/out/1/pan", 0);
o.setParameterOSC("/out/2/pan", 10);
)
Mix

(
o.setParameterOSC("/patchA/91/36", 0); // salida de osc1 a output1
o.setParameterOSC("/patchA/92/37", 0); // salida de osc1 a output2
)

(
o.setParameterOSC("/osc/1/triangle/level",2);
o.setParameterOSC("/osc/1/sawtooth/level", 2);
)
(
o.setParameterOSC("/osc/1/pulse/shape", 9);
o.setParameterOSC("/osc/1/triangle/level", 3);
)


//////////////////////////
(
o.setParameterOSC("/patchA/91/38", 0); // salida de osc1 a output3
o.setParameterOSC("/patchA/92/39", 0); // salida de osc1 a output4
o.setParameterOSC("/patchA/77/36", 0); // salida de output3 a output1
o.setParameterOSC("/patchA/78/37", 0); // salida de output4 a output2
o.setParameterOSC("/out/3/level", 10);
o.setParameterOSC("/out/4/level", 10);
)

(
o.setParameterOSC("/patchA/91/38", 0); // salida de osc1 a output3
o.setParameterOSC("/patchA/92/39", 0); // salida de osc1 a output4
o.setParameterOSC("/patchA/69/36", 0); // salida de output3 a output1
o.setParameterOSC("/patchA/70/37", 0); // salida de output4 a output2
)


(
o.setParameterOSC("/patchA/91/38", 0); // salida de osc1 a output3
o.setParameterOSC("/patchA/92/39", 0); // salida de osc1 a output4
o.setParameterOSC("/patchA/77/36", 0); // salida de output3 a output1
o.setParameterOSC("/patchA/78/37", 0); // salida de output4 a output2
)

o.getState

(
o.setParameterOSC("/out/1/filter", 7);
o.setParameterOSC("/out/2/filter", 4);

)



o.setParameterOSC("/osc/1/sine/level", 1);
o.setParameterOSC("/osc/1/sine/symmetry", 5);
o.setParameterOSC("/osc/1/triangle/level", 0);
o.setParameterOSC("/osc/1/frequency", 7);
o.setParameterOSC("/out/1/level", 1);
o.setParameterOSC("/out/2/level", 1);

OSCFunc.trace(true)




(
SynthDef(\gated, {
	arg gate = 0, t_gate = 0;
	var sig = SinOsc.ar!2;
	//	var env = Env.adsr(0.5, 0.5, 0.5, 5).ar(0, gate);
	var env2 = Env.new.ar(0, t_gate);
	Out.ar(0, sig*env2);
}).add
)

a = Synth(\gated);

a.set(\gate, 1);
a.set(\gate, 0);
a.set(\gate, -1);

a.set(\t_gate, 1);
a.set(\t_gate, 0)
a.set(\t_gate, -1)


g = Group.new(s)

b = Synth(\gated, target: g);













// FREE RUN. Funciona. Implementar en S100_EnvelopeShaper.


(
SynthDef(\S100_FreeRun, {

	arg gate = 0,
	outputBus = 0,
	delayTime=1,
	attackTime=1,
	decayTime=1,
	sustainLevel=1,
	releaseTime=1,
	signalLevel=1;

	var sig, env;
	sig = SinOsc.ar!2;

	env = Env(
		levels: [
			0, // loopNode (ver Help de "Env")
			0,
			1,
			sustainLevel,
			0,
			0, // releaseNode (añadido con valor igual al inicial y con tiempo 0, para que funcione el loop)
		],
		times: [delayTime, attackTime, decayTime, releaseTime, 0],
		releaseNode: 4,
		loopNode: 0,
	).ar(0, gate);


	// Se aplica la envolvente y el nivel (level) a la señal
	sig = sig * env * signalLevel * gate; // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.

	Out.ar(outputBus, sig);

}, [0.5]
).add
)

a = Synth(\S100_FreeRun)
a.set(\gate, 1)
a.set(\gate, 0) // No se corta bruscamente porque tiene un lag y "gate" se usa también como valor de amplitud.







// Forma de redimensionar una interfaz gráfica

(
w = Window("my name is... panel", Rect(128, 600, 200, 100), scroll: true, resizable: false);

b = Button(w,Rect(10, 20, 75, 24));
w.front;
)

(
~amplifica = {|rect, factor|
	var left, top, width, height;

	left = rect.left * factor;
	top = rect.top * factor;
	width = rect.width * factor;
	height = rect.height * factor;

	Rect(left, top, width, height);
}
)

(
var factor = 1.5;
b.bounds = ~amplifica.value(b.bounds, factor);
w.bounds = ~amplifica.value(w.bounds, factor);
w.bounds = w.bounds.left_(128).top_(600);
)
(
~amplifica = {|rect, factor|
	var left, top, width, height;

	left = rect.left * factor;
	top = rect.top * factor;
	width = rect.width * factor;
	height = rect.height * factor;

	Rect(left, top, width, height);
}
)