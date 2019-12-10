Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.clear

// no sé por qué no funciona tras instalarse desde Github...
Quarks.install("https://github.com/mesjetiu/TFM_Arte_Sonoro_Supercollider.git")

o = Synthi100(s, stereoBuses: [0,1]);
o.play
o.stop
o.setParameterOSC("/osc/1/pulse/level", 1);
o.setParameterOSC("/osc/1/pulse/shape", 1);
o.setParameterOSC("/osc/1/sine/level", 4);
o.setParameterOSC("/osc/1/sine/symmetry", 5);
o.setParameterOSC("/osc/1/sawtooth/level", 2);
o.setParameterOSC("/osc/1/triangle/level", 2);
o.setParameterOSC("/osc/1/frequency", 5);

o.setParameterOSC("/patchA/91/36", 0.2);
o.setParameterOSC("/patchA/92/37", 0.3);



s.scope;

"/osc/1/triangle/level".split($/)[1]