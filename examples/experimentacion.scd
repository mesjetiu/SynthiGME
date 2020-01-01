Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")
Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")

Quarks.uninstall("https://github.com/supercollider-quarks/Generators")

Quarks.clear

[1,2].add(3)

s.options.numOutputBusChannels != 18

or(false, true)


o = Synthi100(standalone: true).run().pairDevice


o.modulInputAmplifiers[0].setLevel(5)
o.modulOutputChannels[0].setLevel(1)

OSCFunc.trace(false, false)
OSCFunc.trace(true, false)

o.setGeneralVol(0.7)
o.run
o = Synthi100().run.pairDevice;
o.pairDevice

// Unimos los tres primeros osciladores a los tres primeros canales de salida:
(
o.setParameterOSC("/patchA/91/36", 1); // salida de osc1 a output1
o.setParameterOSC("/patchA/92/37", 1); // salida de osc1 a output2

o.setParameterOSC("/patchA/93/38", 1); // salida de osc2 a output3
o.setParameterOSC("/patchA/94/39", 1); // salida de osc2 a output4

o.setParameterOSC("/patchA/95/40", 1); // salida de osc3 a output5
o.setParameterOSC("/patchA/96/41", 1); // salida de osc3 a output6

o.setParameterOSC("/osc/1/triangle/level", 5);
o.setParameterOSC("/osc/1/sawtooth/level", 4);


o.setParameterOSC("/out/1/level", 5);
o.setParameterOSC("/out/2/level", 5);

o.setParameterOSC("/out/1/on", 1);
o.setParameterOSC("/out/2/on", 1);
)
"".postln
b = Server(\myserver)
b.boot


ServerOptions
s.options.device = nil

s.serverRunning
Server.local.serverRunning
s.options.numOutputBusChannels
a = Bus.audio
a
o.setParameterOSC("/osc/1/pulse/level", 4);
o.setParameterOSC("/osc/1/pulse/shape", 10);


o.setParameterOSC("/osc/1/range", 1)
o.setParameterOSC("/osc/1/frequency", 3)
(
o.setParameterOSC("/out/1/filter", 5);
o.setParameterOSC("/out/2/filter", 5);
)

(
o.setParameterOSC("/out/1/pan", 0);
o.setParameterOSC("/out/2/pan", 10);
)
Mix

(
o.setParameterOSC("/patchA/91/36", 0); // salida de osc1 a output1
o.setParameterOSC("/patchA/92/37", 0); // salida de osc1 a output2
)

(
o.setParameterOSC("/osc/1/triangle/level",2);
o.setParameterOSC("/osc/1/sawtooth/level", 2);
)
(
o.setParameterOSC("/osc/1/pulse/shape", 9);
o.setParameterOSC("/osc/1/triangle/level", 3);
)


//////////////////////////
(
o.setParameterOSC("/patchA/91/38", 0); // salida de osc1 a output3
o.setParameterOSC("/patchA/92/39", 0); // salida de osc1 a output4
o.setParameterOSC("/patchA/77/36", 0); // salida de output3 a output1
o.setParameterOSC("/patchA/78/37", 0); // salida de output4 a output2
o.setParameterOSC("/out/3/level", 10);
o.setParameterOSC("/out/4/level", 10);
)

(
o.setParameterOSC("/patchA/91/38", 0); // salida de osc1 a output3
o.setParameterOSC("/patchA/92/39", 0); // salida de osc1 a output4
o.setParameterOSC("/patchA/69/36", 0); // salida de output3 a output1
o.setParameterOSC("/patchA/70/37", 0); // salida de output4 a output2
)


(
o.setParameterOSC("/patchA/91/38", 0); // salida de osc1 a output3
o.setParameterOSC("/patchA/92/39", 0); // salida de osc1 a output4
o.setParameterOSC("/patchA/77/36", 0); // salida de output3 a output1
o.setParameterOSC("/patchA/78/37", 0); // salida de output4 a output2
)

o.getState

(
o.setParameterOSC("/out/1/filter", 7);
o.setParameterOSC("/out/2/filter", 4);

)



o.setParameterOSC("/osc/1/sine/level", 1);
o.setParameterOSC("/osc/1/sine/symmetry", 5);
o.setParameterOSC("/osc/1/triangle/level", 0);
o.setParameterOSC("/osc/1/frequency", 7);
o.setParameterOSC("/out/1/level", 1);
o.setParameterOSC("/out/2/level", 1);

OSCFunc.trace(true)




// Solución de triangulo sin aliasing (para 48000 sample rate)
// La solucion pasa por covertirse en sinusoide según avanza la frecuencia hacia el agudo. El resultado es similar que el de filtrar la señal.
(
{
	var freq, sig;
	freq = MouseX.kr(20,20000).poll;
	//freq = 1000;
	sig = LFTri.ar(freq, mul: 0.2
		* linlin(freq, 6000, 12000,1,0).poll)!2;
	sig = sig + SinOsc.ar(freq, mul: 0.2
		* linlin(freq, 6000, 12000,0,1).poll)!2
}.play;
)

(
{
	var freq, sig;
	freq = MouseX.kr(20,20000).poll;
	//freq = 1000;
	sig = LFTri.ar(freq, mul: 0.2
		* linlin(freq, 6000, 12000,1,0).poll)!2;
	sig = sig + DPW3Tri.ar(freq, mul: 0.2
		* linlin(freq, 6000, 12000,0,1).poll)!2
}.play;
)
DPW3Tri


// Pulse
//var sigPulse = LFPulse.ar(freq: scaledFreq, width: pulseShape, mul: pulseLevel);
var sigPulse=Pulse.ar(freq: scaledFreq,width: 1-pulseShape,mul: pulseLevel); //sin alias.
// PulseDPW


(
{
	var freq, amp = 0.2, sig = 0;
	freq = MouseX.kr(20,20000).poll;
	freq = 1000;
	//sig = Pulse.ar(freq, mul: 0.2)!2;
	sig = sig + LFPulse.ar(freq, mul: 0.2, add: -1*(amp/2))!2;
}.plot;
)


// Muy buen Pulse!!! sin aliasing y sin distorsión en LF
(
{
	var freq, sig;
	freq = MouseX.kr(2,20000).poll;
	freq = 100;
	sig = PulseDPW.ar(freq, mul: 0.2)!2;
}.play;
)


(
SynthDef(\pulso, {arg freq = 100, bus;
	var sig;
	sig = SinOsc.ar(freq, mul: 0.2)!2;
	Out.ar(bus, sig);
}).add;
)
a = Synth(\pulso)


a.set(\bus, 0)
a.set(\freq, 200)



Server.quitAll
Server.all.do({|i| i.serverRunning.postln})

s.quit

(
{
	var freq, sig;
	freq = MouseX.kr(2,20000).poll;
	//freq = 10;
	sig = SawDPW.ar(freq, mul: 0.2)!2;
}.play //.plot(0.5);
)

(
{
	var freq, sig = 0, amp = 0.2;
	freq = MouseX.kr(5,5000).poll;
	//freq = 100.568;
	sig = (LFPulse.ar(freq, mul: amp, add: (-1*(amp/2))) * linlin(freq, 100, 1000,1,0).poll)!2;
	sig = sig + Pulse.ar(freq, mul: amp
		* linlin(freq, 100, 1000,0,1).poll)!2;
	sig = sig/2;
}.play;
)

Server.local.options.sampleRate = 192000
Server.local.options.sampleRate

(
{
	var freq, sig;
	freq = MouseX.kr(20,20000).poll;
	//freq = 1000;
	sig = SinOsc.ar(freq, mul: 0.2
		* linlin(freq, 800, 10000,1,0).poll)!2;
	sig = sig + SinOsc.ar(freq, mul: 0.1
		* linlin(freq, 800, 10000,0,1).poll)!2
}.play;
)


[1,2,3].sum

a = [1,2,3,4]
a[0..2]


Mix.new([1,2,3]).postln

