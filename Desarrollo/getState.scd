o = SynthiGME().run

// Recuperamos el estado de todas las variables de la GUI
a = o.getState()

// Podemos modificar algunas...
(
o.setParameterOSC("/osc/1/pulse/level", 1);
o.setParameterOSC("/osc/1/sine/level", 3);
o.setParameterOSC("/osc/1/triangle/level", 10);
o.setParameterOSC("/osc/1/pulse/shape", 3)
)

// El Sinthi vuelve al estado que se recuperó
(
a.[0..7].do{|osc|
	o.setParameterOSC(osc[0], osc[1])
}
)