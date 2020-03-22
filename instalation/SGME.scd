/*

Se puede ejecutar este archivo desde una terminal:
sclang ./SGME.scd

*/
(

Routine({
	var myPath = PathName.new(Platform.systemExtensionDir);
	wait(3);
	10.do("".postln);
	if (myPath.entries.select({|path| "SC3".matchRegexp(path.asString)})[0] == nil, {
		"No está instalado SC3-plugins, necesario para poder ejecutar Synthi GME".postln;
	}, { // Si SC3 está instalado, comprobamos que está instalado el Quark SynthiGME:
		SynthiGME().run;
	})
}).play(AppClock);

)
