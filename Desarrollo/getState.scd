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


o.verbose_OSC

// estado actual... no hace falta getState!!!!
// testear más y más.
o.oscRecievedMessages

// tomamos un estado concreto
// No copiarlo simplemente con asignación, ya que no se hace una copia sino una referencia:
d = o.oscRecievedMessages // copia la referencia, no es una copia distinta
d == o.oscRecievedMessages // true siempre, aunque se modifique uno de los dos

// MEJOR: copiar con la siguiente función:
d = Dictionary.newFrom(o.oscRecievedMessages)
d.size

d


// podemos ahora cambiar el valor del synthi para después recuperarlo
// ...

// modo de acceso al diccionario con par key value
d.keysValuesDo {|key, value| (key + value).postln}

// Otra forma de ver los valores, en un solo array
d.asPairs

// recuperamos valores anteriores
(
d.keysValuesDo {
	|key, value|
	o.setParameterOSC(key, value)
}
)

