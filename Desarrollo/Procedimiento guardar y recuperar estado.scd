// Procedimiento para guardar estado de SynthiGME en un momento dado.


o = SynthiGME().run

// Diccionario con los últimos valores dados a cada parámetro:
e = o.getState();
e.size

// convertimos en  Array el diccionario:
f = e.getPairs
f.class
// f = e.getPairs.asString

f.size

// convertimos en texto cada elemento:
(
g = f.collect({
	|item|
	item.asString
})
)


//******** Se procede a guardar en un archivo...



// Podemos modificar algunos parámetros, por código o por la GUI...
(
o.setParameterOSC("/osc/1/pulse/level", 1);
o.setParameterOSC("/osc/1/sine/level", 3);
o.setParameterOSC("/osc/1/triangle/level", 10);
o.setParameterOSC("/osc/1/pulse/shape", 3)
)


//******* Se recupera el array de  strings

// Se convierte cada elemento del array en String / Float, secuencialmente:
(
h = g.collect({ |item|
   // item = item.stripWhiteSpace; // Usamos stripWhiteSpace para eliminar espacios al principio y al final
    // Convertimos a entero si es un número, de lo contrario a símbolo
	if (item[0] == $/) {item.asString} {
		if (item.interpret.isFloat) {item.asFloat} {item.asInteger}
	}
});
)



h.do({|item| item.class.postln})


// Ahora reconstruimos el Dictionary
i = Dictionary.newFrom(h);

// El diccionario recuperado debe coincidir con el orginal:
i == e
i.size

// recuperamos valores anteriores en el synthi
(
e.keysValuesDo { // poner "e" o "i" para ver diferencia
	|key, value|
	o.setParameterOSC(key, value)
}
)

i["/osc/1/pulse/level"]
i["/patchA/80/29"]

