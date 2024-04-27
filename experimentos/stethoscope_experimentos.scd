// Experimentación con Stethoscope para incluirlo


a = Bus.audio(s, 2)
// Stethoscope toma siempre dos o más canales de audio consecutivos, por lo que basta darle el primero de ellos para que tenga los siguientes.
// Sería conveniente quizás crear un bus stereo como input para asegurarnos de que tomamos dos consecutivos sin duda alguna...
a.index // con este parámetro le pasamos \index a Stethoscope.

(
w = Window.new("my own scope", Rect(20, 20, 400, 500));
w.view.decorator = FlowLayout(w.view.bounds);
c = Stethoscope.new(s, view:w.view, index: a.index);
w.onClose = { c.free }; // don't forget this
w.front;
)


c.index = 1
c.numChannels
c.zoom = 1
c.view.visible = true
c.view.bounds // para cambiar el tamaño de la view
c.setProperties(bufsize: 4096)


(
SynthDef(\sonido,{
	arg bus;
	var sig = SinOsc.ar([300,302], mul: 0.2);
	Out.ar(bus, sig);
}).add
)


p = Synth(\sonido,[\bus, a])


p.set(\bus, 0)

