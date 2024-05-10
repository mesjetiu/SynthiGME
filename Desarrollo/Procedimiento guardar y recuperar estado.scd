// Procedimiento para guardar estado de SynthiGME en un momento dado.


o = SynthiGME().run

// Diccionario con los últimos valores dados a cada parámetro:
e = o.getFullState();
e.size

e["/patchA/70/5"]

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

g.size


//******** Se procede a guardar en un archivo...






// Método por código:
(
var archivo = File.new("/home/carlos/Dropbox/SC Quarks/SynthiGME/Desarrollo/archivo.txt", "w");  // Abrir el archivo para escritura

g.do { |string, n|
	if (n.even) {
		archivo.write(string ++ "\t");
	} {
		archivo.write(string ++ "\n");
	}
};

archivo.close();  // Cierra el archivo después de escribir
)

// Podemos modificar algunos parámetros, por código o por la GUI...
(
o.setParameterOSC("/osc/1/pulse/level", 1);
o.setParameterOSC("/osc/1/sine/level", 3);
o.setParameterOSC("/osc/1/triangle/level", 10);
o.setParameterOSC("/osc/1/pulse/shape", 3)
)

(
var archivo = File.new("/home/carlos/Dropbox/SC Quarks/SynthiGME/Desarrollo/archivo.txt", "r");  // Abrir el archivo para lectura
var contenido = archivo.readAllString;  // Lee todo el contenido del archivo como un solo string
h = contenido.replace("\t", " ").replace("\n", " ").split($ );  // Divide el contenido en líneas

archivo.close();  // Cierra el archivo después de leer
)
h.class

~contenido.size
~contenido[13].asAscii

h = g

h.size
h

f.size


//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
// con diálogo de ventanas para elegir archivo.

(
FileDialog({
	|path|
	var archivo = File.new(path[0], "w");  // Abrir el archivo para escritura
	var arrayDeStrings = ["hola", "mundo", "supercollider"];

	g.do { |string, n|
		if (n.even) {
			archivo.write(string ++ "\t");
		} {
			archivo.write(string ++ "\n");
		}
	};

	archivo.close();  // Cierra el archivo después de escribir
}, {
	"Guardado cancelado.".postln;
}, fileMode: 0, // Modo para cualquier nombre de archivo
acceptMode: 1 // Modo de guardar
);
)


// Podemos modificar algunos parámetros, por código o por la GUI...
(
o.setParameterOSC("/osc/1/pulse/level", 1);
o.setParameterOSC("/osc/1/sine/level", 3);
o.setParameterOSC("/osc/1/triangle/level", 10);
o.setParameterOSC("/osc/1/pulse/shape", 3)
)


(
FileDialog({
    |path|
	var archivo = File(path[0], "r");
	var contenido = archivo.readAllString;  // Lee todo el contenido del archivo como un solo string
	h = contenido.replace("\t", " ").replace("\n", " ").split($ );  // Divide el contenido en líneas
	archivo.close();
}, {
    "Apertura cancelada.".postln;
}, fileMode: 1, // Modo para un archivo existente
acceptMode: 0, // Modo de apertura
);
)




// Una vez recuperado el archivo, se convierte cada elemento del array en String / Float, secuencialmente:
(
i = h.collect({ |item|
   // item = item.stripWhiteSpace; // Usamos stripWhiteSpace para eliminar espacios al principio y al final
    // Convertimos a entero si es un número, de lo contrario a símbolo
	if (item[0] == $/) {item.asString} {
		if (item.interpret.isFloat) {item.asFloat} {item.asInteger}
	}
});
)



i.do({|item| item.class.postln})


// Ahora reconstruimos el Dictionary
j = Dictionary.newFrom(i);

// El diccionario recuperado debe coincidir con el orginal:
j == e
j.size == e.size

// recuperamos valores anteriores en el synthi
(
e.keysValuesDo { // poner "e" o "i" para ver diferencia
	|key, value|
	o.setParameterOSC(key, value)
}
)
