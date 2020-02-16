Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")
o =Synthi100(gui:false).run
//Quarks.install("https://github.com/mesjetiu/Synthi100.git")

Quarks.uninstall("https://github.com/mesjetiu/Synthi100.git")

Quarks.clear



o = Synthi100(gui:false).run.pairDevice
o = Synthi100(gui:true).run
o.run
o.close
o.guiSC.makeWindow
o.pairDevice

o = Synthi100(gui:true)

o.setParameterOSC("\env\1\gate", 1)
o.close
o.guiSC.closeWindows;
o.guiSC.frontWindows;
o.guiSC.panels[0].window.front
o.guiSC.panels[2].parameterVisibility(true)

o.guiSC.pannels.do({|i| i.window.front})

o.guiSC.resetSize
o.run

View

ScopeView
Stethoscope


// FREE RUN. Funciona. Implementar en S100_EnvelopeShaper.


(
SynthDef(\S100_FreeRun, {

	arg gate = 0,
	outputBus = 0,
	delayTime=1,
	attackTime=1,
	decayTime=1,
	sustainLevel=0.5,
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
			0, // releaseNode (añadido con valor igual al inicial y con tiempo 0, para que funcione el loop)
		],
		times: [delayTime, attackTime, decayTime, releaseTime],
		releaseNode: 3,
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



CheckBox
(
var window, size = 32; // try different sizes - from 15 to 200 or more!
window = Window.new("Knob", Rect(640,630,270,70)).front;
k = Knob.new(window, Rect(20, 10, size, size));
k.action_({|v,x,y,m| postf("action func called: %\n", v.value); });
//k.color[1] = Color.gray(alpha:0);
)

k.value
k.value = 0.25
k.valueAction = 0.125
k.keystep = 10

// modes
k.mode = \vert;
k.mode = \horiz;
k.mode = \round; // default

k.visible
k.visible = false
k.visible = true
k.enabled = false
k.enabled_(true)
k.canFocus = false
k.canFocus = true

