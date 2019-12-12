Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")


o = Synthi100(s, stereoBuses: [0,1]);
o.play
o.stop
(
o.setParameterOSC("/patchA/91/38", 1); // salida de osc1 a output3
o.setParameterOSC("/patchA/92/39", 1); // salida de osc1 a output4
o.setParameterOSC("/patchA/77/36", 1); // salida de output3 a output1
o.setParameterOSC("/patchA/78/37", 1); // salida de output4 a output2
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


(
o.setParameterOSC("/patchA/91/36", 1); // salida de osc1 a output1
o.setParameterOSC("/patchA/92/37", 1); // salida de osc1 a output2
)

(
o.setParameterOSC("/patchA/91/36", 0); // salida de osc1 a output1
o.setParameterOSC("/patchA/92/37", 0); // salida de osc1 a output2
)

(
o.setParameterOSC("/osc/1/triangle/level",2);
o.setParameterOSC("/osc/1/sawtooth/level", 2);
)
(
o.setParameterOSC("/osc/1/pulse/shape", 0);
o.setParameterOSC("/osc/1/triangle/level", 0);
)

o.setParameterOSC("/osc/1/sine/level", 1);
o.setParameterOSC("/osc/1/sine/symmetry", 5);
o.setParameterOSC("/osc/1/triangle/level", 0);
o.setParameterOSC("/osc/1/frequency", 7);
o.setParameterOSC("/out/1/level", 1);
o.setParameterOSC("/out/2/level", 1);
o.modulOutputChannels[0].synth


a = {PinkNoise.ar*0.2}.play
a.asString.split($:)[1].split($ )[1].split($))[0]
String

Synth

Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.clear

// no sé por qué no funciona tras instalarse desde Github...
Quarks.install("https://github.com/mesjetiu/TFM_Arte_Sonoro_Supercollider.git")
