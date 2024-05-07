// Procedimiento para guardar estado de SynthiGME en un momento dado.


o = SynthiGME().run

// Diccionario con los últimos valores dados a cada parámetro:
e = Dictionary.newFrom(o.oscRecievedMessages);

// convertimos en String el diccionario:
f = e.getPairs.asString

// Eliminamos corchetes:
f = f.replace("[", "").replace("]", ""); // Quitamos los corchetes



//******** Se procede a guardar en un archivo...

(
var dir, path, archivo;

// Construye el path completo al directorio donde se guardará el archivo
dir = Platform.userAppSupportDir +/+ "SynthiGME";
// Verifica si el directorio existe, si no, lo crea
if(File.exists(dir).not, { File.mkdir(dir); });

// Construye el path al archivo específico
path = dir +/+ "ultimo_estado.std";

// Intenta abrir el archivo en modo de escritura
archivo = File(path, "w");

// Asume que 'f' es la variable que contiene el string a guardar
if (archivo.isOpen) {
    archivo.write(f);  // Escribe la variable 'f' en el archivo
    archivo.close;     // Cierra el archivo
    "Archivo guardado en: ".post; path.postln;  // Muestra dónde se guardó el archivo
} {
    "No se pudo abrir el archivo para escritura en: ".post; path.postln;
}
)

// Podemos modificar algunos parámetros, por código o por la GUI...
(
o.setParameterOSC("/osc/1/pulse/level", 1);
o.setParameterOSC("/osc/1/sine/level", 3);
o.setParameterOSC("/osc/1/triangle/level", 10);
o.setParameterOSC("/osc/1/pulse/shape", 3)
)


//******* Se recupera el string
(
var dir, path, archivo, contenido;

// Construye el path completo al directorio donde se recuperará el archivo
dir = Platform.userAppSupportDir +/+ "SynthiGME";
// Construye el path al archivo específico
path = dir +/+ "ultimo_estado.std";

// Intenta abrir el archivo en modo de lectura
archivo = File(path, "r");

// Verifica si el archivo se ha abierto correctamente
if (archivo.isOpen) {
    contenido = archivo.readAllString;  // Lee todo el contenido del archivo como un string
    archivo.close;                       // Cierra el archivo
    contenido.postln;                    // Imprime el contenido en la ventana de post
	g = contenido;
} {
    "No se pudo abrir el archivo para lectura en: ".post; path.postln;
}
)


// Dividimos por coma y se convierte en array
g = f.replace("\n", "").replace(" ", "").replace("\t", "").split($,);


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

// recuperamos valores anteriores en el synthi
(
i.keysValuesDo {
	|key, value|
	o.setParameterOSC(key, value)
}
)
