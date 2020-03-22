/*
Experimentos para ejecutar un archivo .scd desde la terminal.

Antes de instalar el quark SynthiGME, se requiere tener instalado en el ordenador los siguientes programas:
* SuperCollider 3.8.0 o superior
* sc3-plugins
* git

Se puede ejecutar en terminal la siguiente orden:
sclang ./install_SGME.scd

*/



(

Routine({
	var myPath = PathName.new(Platform.systemExtensionDir);
	wait(3);
	10.do("".postln);
	// Comprobamos que está instalado SC3-plugins. Si no, mostramos advertencia y cerramos.
	if (myPath.entries.select({|path| "SC3".matchRegexp(path.asString)})[0] == nil, {
		"No está instalado SC3-plugins, necesario para poder ejecutar Synthi GME".postln;
	}, { // Si SC3 está instalado, instalamos/acutalizamos y comprobamos que está instalado el Quark SynthiGME:
		if (Quarks.installedPaths('SynthiGME')[0] != nil, {
			Quarks.install("https://github.com/mesjetiu/SynthiGME.git"); // actualizamos (para la próxima vez que se ejecute)
			"Synthi GME actualizado. Ejecute \"sclang ./SGME.scd\" para abrir la aplicación".postln;
		}, { // Si no está instalado...
			"Instalando el Quark Synthi GME...".postln;
			Quarks.install("https://github.com/mesjetiu/SynthiGME.git");
			if (Quarks.installedPaths('SynthiGME')[0] != nil, {
				"Synthi GME instalado exitosamente. Ejecute sclang \"sclang ./SGME.scd\" para abrir la aplicación".postln;
			}, {
				"No se ha podido instalar el Quark Synthi GME. Comprueba que tienes conexión a internet y que está instalado \"Git\" en tu sistema. Después vuelve a intentarlo.".postln;
			})
		})
	})
}).play;

)