/*
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

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