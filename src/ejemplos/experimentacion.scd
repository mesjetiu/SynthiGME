Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")
Quarks.uninstall("https://github.com/mesjetiu/TFM_Arte_Sonoro_Supercollider")
Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.clear


o = Synthi100();
o.play


// Unimos los tres primeros osciladores a los tres primeros canales de salida:
(
o.setParameterOSC("/patchA/91/36", 1); // salida de osc1 a output1
o.setParameterOSC("/patchA/92/37", 1); // salida de osc1 a output2

o.setParameterOSC("/patchA/93/38", 1); // salida de osc2 a output3
o.setParameterOSC("/patchA/94/39", 1); // salida de osc2 a output4

o.setParameterOSC("/patchA/95/40", 1); // salida de osc3 a output5
o.setParameterOSC("/patchA/96/41", 1); // salida de osc3 a output6
)

o.stop




o.setParameterOSC("/osc/1/triangle/level",2);
o.setParameterOSC("/osc/1/sawtooth/level", 2);


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

(
o.setParameterOSC("/out/1/filter", 5);
o.setParameterOSC("/out/2/filter", 5);
)

(
o.setParameterOSC("/out/1/pan", 0);
o.setParameterOSC("/out/2/pan", 10);
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
o.modulOutputChannels[0].synth

5.linexp(0, 10, 1, 16000)
16000.pow(1/10).pow(2);

(
var a, b;
#a,b = [2,3];
a.postln;
b.postln;

)




a = {PinkNoise.ar*0.2}.play
a.asString.split($:)[1].split($ )[1].split($))[0]
String

Synth


// no sé por qué no funciona tras instalarse desde Github...
Quarks.install("https://github.com/mesjetiu/TFM_Arte_Sonoro_Supercollider.git")


s.boot;
(
SynthDef("help-Synth-get", { | out, freq = 440|
    Out.ar(out, SinOsc.ar(freq, 0, 0.1));
}).add;
)
x = Synth("help-Synth-get");
x.set(\freq, 220 + 440.rand);
x.get(\freq, { arg value; ("freq is now:" + value + "Hz").postln; });
x.get(\freq).postln;
x.free;
a = [2,3].asString

b = a.asInt



a =  Dictionary.newFrom(List[\a, 1, \b, 2, \c, 4]);
a.findKeyForValue(1)


// EXPERIMENTOS CON OSC ////////////////////////////////////////////////////////////////////////////////
// this example is basically like OSCFunc.trace but filters out
// /status.reply messages
(
f = { |msg, time, addr|
    if(msg[0] != '/status.reply') {
        "time: % sender: %\nmessage: %\n".postf(time, addr, msg);
    }
};
thisProcess.addOSCRecvFunc(f);
);

// stop posting.
thisProcess.removeOSCRecvFunc();





// Este código hace broadcasting!!!
NetAddr.broadcastFlag = true

b =  NetAddr("255.255.255.255", 9000); // el puerto debe ser el mismo que recibe la aplicación

b.sendMsg("/osc/1/pulse/level", 5);

// Recoge cualquier mensaje OSC de cualquier origen y ejecuta la función que lleva como parámetro.
// Perfecto para nuestra aplicación!!!
(
thisProcess.addOSCRecvFunc({ arg msg, time, addr, recvPort;
	if(msg[0] != '/status.reply') {
		[msg, time, addr, recvPort].postln;
		b.sendMsg(msg[0],msg[1]); // reenvía en broadcasting el mensaje recibifo
	}
});
)

OSCFunc.trace(true)


"/osc".matchRegexp("/osc/1/pulse/level", 0, 4);

thisProcess.removeOSCRecvFunc(~functionOSC);