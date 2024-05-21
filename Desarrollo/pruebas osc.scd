o = SynthiGME()
o.myIp
o.getLocalIP

NetAddr.matchLangIP("192.168.111.93")

NetAddr.
OSCFunc.trace(true)

Date.getDate
Date.localtime
Date.gmtime
Date.seed.postln;
Date.seed.postln;
Date.seed.postln;


"ping_" ++ currentDate.asString ++ "_" ++ currentTime.asString;

(
var localIP;

// Función para enviar un mensaje de difusión y capturar la dirección IP local
~getLocalIP = {
    var ip = "255.255.255.255"; // Dirección de difusión de tu red
    var port = 9000;
    var netAddr = NetAddr(ip, port);
    var condition = Condition.new; // Crear una nueva condición

    NetAddr.broadcastFlag = true;

    // Definir un receptor OSC
    OSCdef(\captureIP, { |msg, time, addr, recvPort|
        localIP = addr.ip; // Capturar la dirección IP del remitente
        NetAddr.broadcastFlag = false;
        OSCdef.free(\captureIP); // Remover el receptor después de capturar la IP
        condition.unhang; // Desbloquear la condición
    }, \ping, recvPort: port);

    // Enviar el mensaje de difusión
    netAddr.sendMsg(\ping);

    // Esperar hasta que la condición se desbloquee
    condition.hang;

    // Retornar la IP capturada
    localIP;
};
)

~miIp

(
// Usar una Routine para ejecutar el código y esperar la IP
Routine {
    // Llamar a la función para obtener la IP local y asignarla a una variable
    ~miIp = ~getLocalIP.value.postln;
}.play;
)