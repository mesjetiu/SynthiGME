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

Copyright 2020 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

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
