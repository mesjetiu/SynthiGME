Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")


o = Synthi100(s, stereoBuses: [0,1]);
o.play
o.stop
(
o.setParameterOSC("/patchA/91/36", 0.4);
o.setParameterOSC("/patchA/92/37", 0.5);
)
(
o.setParameterOSC("/osc/1/pulse/level",4);
o.setParameterOSC("/osc/1/sawtooth/level", 4);
)
o.setParameterOSC("/osc/1/pulse/shape", 1);
o.setParameterOSC("/osc/1/sine/level", 0);
o.setParameterOSC("/osc/1/sine/symmetry", 5);
o.setParameterOSC("/osc/1/triangle/level", 0);
o.setParameterOSC("/osc/1/frequency", 7);
o.setParameterOSC("/out/1/level", 1);
o.setParameterOSC("/out/2/level", 1);
o.modulOutputChannels[0].synth





Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.clear

// no sé por qué no funciona tras instalarse desde Github...
Quarks.install("https://github.com/mesjetiu/TFM_Arte_Sonoro_Supercollider.git")
