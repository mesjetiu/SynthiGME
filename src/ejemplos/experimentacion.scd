Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")


o = Synthi100(s, stereoBuses: [0,1]);
o.play
o.stop
(
o.setParameterOSC("/patchA/91/36", 1);
o.setParameterOSC("/patchA/92/37", 1);
)

(
o.setParameterOSC("/patchA/91/36", 1);
o.setParameterOSC("/patchA/92/37", 1);
)
o.setParameterOSC("/osc/1/pulse/level",5);
o.setParameterOSC("/osc/1/pulse/shape", 1);
o.setParameterOSC("/osc/1/sine/level", 9);
o.setParameterOSC("/osc/1/sine/symmetry", 5);
o.setParameterOSC("/osc/1/sawtooth/level", 10);
o.setParameterOSC("/osc/1/triangle/level", 10);
o.setParameterOSC("/osc/1/frequency", 7);
o.modulOutputChannels[0].synth

{PinkNoise.ar}.play
91-67

(
o.modulPatchbayAudio.inputsOutputs.do({|i|
	i.do({|j|
		i.postln;
		j.postln;
	})
})
)


"2".asInt






Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.clear

// no sé por qué no funciona tras instalarse desde Github...
Quarks.install("https://github.com/mesjetiu/TFM_Arte_Sonoro_Supercollider.git")
