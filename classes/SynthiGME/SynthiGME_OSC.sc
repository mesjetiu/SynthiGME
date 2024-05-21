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

+ SynthiGME {

	getLocalIP {
    // Encuentra la IP de este dispositivo en la red local haciendo un ping en broadcasting.
    var functionGetIP = {
        var ipDifusion = "255.255.255.255"; // Dirección de difusión de tu red
        var port = devicePort;
        var localIP;
        var netAddr = NetAddr(ipDifusion, port);
        var condition = Condition.new; // Crear una nueva condición

        // Generar un identificador único usando Date.seed
        var uniqueID = Date.seed;

        NetAddr.broadcastFlag = true;

        // Definir un receptor OSC
        OSCdef(\captureIP, { |msg, time, addr, recvPort|
            if (msg[1] == uniqueID) {
                localIP = addr.ip; // Capturar la dirección IP del remitente
                NetAddr.broadcastFlag = false;
                OSCdef.free(\captureIP); // Remover el receptor después de capturar la IP
                condition.unhang; // Desbloquear la condición
            }
        }, \ping, recvPort: port);

        try {
            // Enviar el mensaje de difusión con el identificador único
            netAddr.sendMsg(\ping, uniqueID);

            // Esperar hasta que la condición se desbloquee
            condition.hang;

            // Retornar la IP capturada
            localIP;
        } {
            // Manejar la excepción y dar un mensaje inteligible
            |err|
            "Error: No se pudo enviar el mensaje de difusión. La red puede estar inaccesible.".postln;
            nil; // Retornar nil en caso de error
        }
    };

    // Usar una Routine para ejecutar el código y esperar la IP
    Routine {
        // Llamar a la función para obtener la IP local y asignarla a una variable
        myIp = functionGetIP.();

        // Verificar si se obtuvo una IP o se produjo un error
        if(myIp.notNil) {
            ("IP Local obtenida: " ++ myIp).postln;
        } {
            "No se pudo obtener la IP Local.".postln;
        }
    }.play;
}





}