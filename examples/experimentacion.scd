LQuarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")
Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")

Quarks.uninstall("https://github.com/supercollider-quarks/Generators")

Quarks.clear

o = Synthi100().run
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

OSCFunc.trace(true, true)




// Solución de triangulo sin aliasing (para 48000 sample rate)
// La solucion pasa por covertirse en sinusoide según avanza la frecuencia hacia el agudo. El resultado es similar que el de filtrar la señal.
(
{
	var freq, sig;
	freq = MouseX.kr(20,20000).poll;
	//freq = 8000;
	sig = LFTri.ar(freq, mul: 0.2
		* lincurve(freq, 2000, 12000,1,0, -10).poll)!2;
	sig = sig + SinOsc.ar(freq, mul: 0.2
		* lincurve(freq, 2000, 12000,0,1,-10).poll)!2
}.play;
)


GUI