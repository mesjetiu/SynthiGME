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

/*
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
*/

Routine({
	var address, port, oscFunc, server, time_answer;
	time_answer = 1;
	port = 57500;
	while ({(time_answer != nil).and(port<58000)}, { // Se prueban 1000 puertos. Si tenemos respuesta del puerto es porque hay un servidor ya.
		time_answer = 1;
		port = port + 1;
		address = NetAddr.new("127.0.0.1", port);
		wait (0.1);
		oscFunc = OSCFunc({ arg msg, time, addr, recvPort; time_answer = time}, "/version.reply", address);
		address.sendMsg("/version");
		wait (0.3);
		oscFunc.free;
		if (time_answer == 1, {time_answer = nil}); // para salir del bucle
	});
	if (time_answer == nil, { // Si en el puerto no hay un servidor de Supercollider no obtenemos respuesta. ¡está libre!
		server = Server(\synthiGME_Server, address);
		wait (0.1);
		("Servidor creado en " + address).postln;
		SynthiGME(server: server).run;
	},{ // Si no se ha podido encontrar un puerto libre (cosa rara probando 1000)...
		"No se ha podido encontrar un puerto libre donde crear un Servidor de SuperCollider".postln;
		0.exit;
	});
}).play(AppClock);

)
