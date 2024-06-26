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

	prepareOSC {
		var ipDifusion = "255.255.255.255"; // Dirección de difusión de tu red
		NetAddr.broadcastFlag = true;
		netAddr = NetAddr(ipDifusion, devicePort);
		// Busca la IP de la red local:
		this.getLocalIP; // no es necesario, es solo informativo
		thisProcess.removeOSCRecvFunc(functionOSC); // Elimina la función anterior para volverla a introducir
		// función que escuchará la recepción de mensajes OSC de cualquier dispositivo
		functionOSC = {|msg, time, addr, recvPort|
			var prefix, message;
			message = msg[0].asString;
			// se ejecuta la orden recibida por mensaje.
			// Para recibir mensajes ha de estar habilitada la opción.
			if (canRecieveOSC == false) {^this};
			// Calcular las condiciones en las que se ha de ejecutar el comando y las que no.
			if ((recvPort == devicePort) && (addr.ip != myIp) && (addr.ip != NetAddr.localAddr.ip) && (message != "/ping")){
				//"recibido".sgmePostln;
				prefix = "/" ++ oscGroup;
				if (message.beginsWith(prefix)) {
					message = message[prefix.size..]; // Eliminar el prefijo
					// ejecutar el mensaje si coincide con el nombre del grupo
					this.setParameterOSC(message, msg[1], addr, broadcast: false, ipOrigin: addr.ip)
				};
			};
		};
		thisProcess.addOSCRecvFunc(functionOSC);
	}

	// No es necesario para operar con OSC, ya que la comprobación de la ip local se hace con NetAddr.matchLantIP()
	getLocalIP {

		var ipDifusion = "255.255.255.255"; // Dirección de difusión de tu red
		var port = rrand(8000, 8999);
		// Encuentra la IP de este dispositivo en la red local haciendo un ping en broadcasting.
		var functionGetIP = {
			var localIP;
			var condition = Condition.new; // Crear una nueva condición

			// Generar un identificador único usando Date.seed, para no entrar en conflicto con otros mensajes de \ping dentro de la misma red local.
			var uniqueID = Date.seed; // número aleatorio dependiente de la hora.

			var netAddr = NetAddr(ipDifusion, port);
			// NetAddr.broadcastFlag = true;

			// Definir un receptor OSC
			OSCdef(\captureIP, { |msg, time, addr, recvPort|
				if (msg[1] == uniqueID) {
					localIP = addr.ip; // Capturar la dirección IP del remitente
					//      NetAddr.broadcastFlag = false;
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
				"Error: No se pudo conseguir la dirección de IP local. La red puede estar inaccesible.".sgmePostln;
				nil; // Retornar nil en caso de error
			};
		};

		// Usar una Routine para ejecutar el código y esperar la IP
		Routine {
			// Llamar a la función para obtener la IP local y asignarla a una variable
			myIp = functionGetIP.();

			// Verificar si se obtuvo una IP o se produjo un error
			if(myIp.notNil) {
				("IP Local obtenida: " ++ myIp).sgmePostln;
			} {
				"No se pudo obtener la IP Local.".sgmePostln;
			}
		}.play;
	}


	sendBroadcastMsg{|msg, value|
		if(myIp.notNil && (canSendOSC == true)) {
			//	"enviando".sgmePostln;
			var message = "/" ++ oscGroup ++ msg; // Se añade un prefijo de grupo de OSC
			netAddr.sendMsg(message, value);
		} {
			"No está definida la dirección IP de este dispositivo. No se pueden enviar mensajes en broadcasting.".sgmeWarn;
		}
	}
}