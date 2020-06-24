
// Instalar SynthiGME desde GitHub
(
Quarks.install("https://github.com/mesjetiu/SynthiGME.git");
thisProcess.recompile;
)

// Desinstalar SynthiGME (versión de GitHub)
(
Quarks.uninstall("https://github.com/mesjetiu/SynthiGME.git");
Quarks.clear;
thisProcess.recompile;
)

// Ejecutar SynthiGME (comando recomendado): Atención: Se reiniciará el servidor de sonido.
o = SynthiGME()

// Ejecutar y sincronizar otros dispositivos (experimental)
o = SynthiGME().run.pairDevice

// Cerrar
o.close


// Para instalar y desinstalar desarrollando el Quark

// Instalar SynthiGME desde directorio local
(
Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/SynthiGME", "HEAD");
thisProcess.recompile;
)

// Desinstalar SynthiGME (versión de directorio local)
(
Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/SynthiGME");
Quarks.clear;
thisProcess.recompile;
)


<<<<<<< Updated upstream
// Para hacer menús...

(
~menu = Menu(
    MenuAction("A", { "A selected".postln }),
    MenuAction("B", { "B selected".postln }),
    MenuAction("C", { "C selected".postln }),
).front;
)
=======
// Para crear un nuevo servidor (interesante que cada instancia tenga su servidor, con sus conexiones...)
n = Server(\myServer, NetAddr("127.0.0.1", 57000));
n.boot
{PinkNoise.ar(0.1)!2}.play(n)

/////////////////////////////

p = Server(\scideS2, NetAddr("127.0.0.1", 57006));

p.boot

p = Server(\myServer2, NetAddr("127.0.0.1", 57001));

p.boot

{PinkNoise.ar(0.1)!2}.play(n)


Server.named

Server.all


Server.killAll





NetAddr("127.0.0.1", 57006)



/version.reply








b = NetAddr.new("127.0.0.1", 57001);    // create the NetAddr
b.sendMsg("/version");    // send the application the message "hello" with the parameter "there"
b.sendMsg("/status");    // send the application the message "hello" with the parameter "there"
b.sendMsg("/quit");    // send the application the message "hello" with the parameter "there"
b.sendMsg("/ping");    // send the application the message "hello" with the parameter "there"

n = NetAddr.new("127.0.0.1", 57001);    // create the NetAddr
// create the OSCFunc
o = OSCFunc({ arg msg, time, addr, recvPort; [msg, time, addr, recvPort].postln; }, "/version.reply", n);
o.free;    // remove the OSCFunc when you are done.
o
OSCFunc.trace(false)
>>>>>>> Stashed changes
