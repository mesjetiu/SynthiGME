Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")
o =Synthi100(gui:false).run
//Quarks.install("https://github.com/mesjetiu/Synthi100.git")

Quarks.uninstall("https://github.com/mesjetiu/Synthi100.git")

Quarks.clear



o = Synthi100(gui:false).run.pairDevice

o = Synthi100(gui:true)
o.run




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
(
w = Window().front;
a = { Button(w) } ! 10;
w.layout = HLayout(*a);
);






(
w = Window.new;

c = CompositeView(w, Rect(50, 0, 300, 300));
a = Slider2D(c, Rect(0, 0, 100, 100)); // actually displays at (50, 0)
b = Slider2D(c, Rect(100, 100, 100, 100));

c.background = Color.rand;

w.front;
)
w.bounds.left

c.bounds_(Rect(100, 0, 300, 300)); // contents adjust since coords are relative
c.resize_(6); // contents adjust since coords are relative






// Experimentos con imágenes de fondo en Window

(
    ~alpha = 1.0;
    ~image = Image.new("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/S100 Cuenca fotografías/2019.12.18/IMG_20191218_191627.jpg");
    ~bounds = Rect(200, 300, 10000, 10000);
    ~window = Window.new("Image background");
    ~window.view.background_(Color.gray);
    ~window.view.setBackgroundImage(~image, 11, ~alpha, ~bounds);
    ~window.front;
//~image.plot;
)



// Esta expresión devuelve el path donde está instalado el Quark. Útil para abrir archivos como imágenes.
Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0]


// Rutina para ser ejecutada cada vez que se hace click en una view y se quiere que reaccione al doble click.
(
Routine({ // Cuando se hace un click...
	if (~click == true, {
		"doble click!!".postln;
		// añadir lo que ha de hacer al doble click
	}, {
		"click!".postln;
		~click = true;
		wait(0.5); // tiempo de espera del segundo click
	});
	~click = false; // una vez transcurrida la espera, se vuelve false
}).play;
)




s.options.numAudioBusChannels



Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];
