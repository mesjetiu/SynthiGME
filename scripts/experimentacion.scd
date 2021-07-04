
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

// Instalar SynthiGME desde directorio local (Windows)
(
Quarks.install("C:/Users/mesje/Dropbox/Máster Arte Sonoro TFM/TFM/SynthiGME", "HEAD");
thisProcess.recompile;
)

// Desinstalar SynthiGME (versión de directorio local)
(
Quarks.uninstall("C:/Users/mesje/Dropbox/Máster Arte Sonoro TFM/TFM/SynthiGME");
Quarks.clear;
thisProcess.recompile;
)

// Instalar SynthiGME desde directorio local (Linux)
(
Quarks.install("/home/carlos/Software/SynthiGME", "HEAD");
thisProcess.recompile;
)

// Desinstalar SynthiGME (versión de directorio local)
(
Quarks.uninstall("/home/carlos/Software/SynthiGME");
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
<<<<<<< HEAD
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
)
=======
>>>>>>> Stashed changes
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



<<<<<<< Updated upstream

=======
Server
>>>>>>> Stashed changes

NetAddr("127.0.0.1", 57006)



/version.reply




<<<<<<< Updated upstream
=======
1000.rand + 58000

>>>>>>> Stashed changes




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
=======
)

(
var viewSizes = [];
var window = Window();
var bounds = Rect(10,10,10,10);
(80*67).do({
	viewSizes = viewSizes.add([window, bounds]);
});
viewSizes.size.postln;

)


Platform
>>>>>>> Windows: reducido número de nodos en matrices

"h" === Char
5 === Integer
'h'.ascii
"h"[0] === Char








// frecuencia de corte en un solo oscilador (para limitar bandas y tener antialiasing)
(
{ var freq, sig, amp, nyquist;
    freq = MouseX.kr(50, 50000, 1);
	nyquist = SampleRate.ir / 2;
	amp = Select.kr(freq < nyquist, [0, 1]);
	sig = amp * SinOsc.ar(freq)!2;
}.play;
)



// frecuencia de corte en varios osciladores generando una onda cuadrada
(
{ var freq, sig, nyquist, numSin = 100;
    freq = MouseX.kr(10, 50000, 1);
	nyquist = SampleRate.ir / 2;
	sig = Array.fill(numSin, {|n|
		var f = freq*(1,3..199)[n];
		var a = (1,3..199)[n];
		var amp = Select.kr(f < nyquist, [0, 1]);
		SinOsc.ar(f, mul: amp/a)
	});
	sig = sig.sum;
	sig = sig*0.5;
	sig!2
}.play;
)

// Es la única manera que veo posible para tener al mismo tiempo la posibilidad de hacer sync y modular la anchura del pulso, así como de respetar el cambio de forma de onda de la sinusoidal. La única pega es el aliasing... pero todo no se puede tener :-)
(
{var fase, sig, freq, width, masterFreq, masterSig;
	freq = 100;//
	masterFreq = MouseY.kr(10, 500, 1); //
	masterSig = SinOsc.ar(masterFreq);
	width = MouseX.kr(-1,1,0,1);
	// marca la fase de la onda (para hardsync)
	fase = Phasor.ar(masterSig,(freq*2)/SampleRate.ir,-1,1);
	// pulso cuadrada
	//sig = Select.ar(fase<width,K2A.ar([1,0]));
	// Sinusoidal
	//sig = SinOsc.ar(0,fase);
	// Triangular
	sig = Select.ar(fase<0,[(fase*(-1))+0.5,fase+0.5]);
	// Diente de sierra
	sig = fase;
	sig = sig*0.1;
	sig!2
}.play;
)