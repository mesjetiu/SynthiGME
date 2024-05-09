(
var archivo = File.new("/home/carlos/Dropbox/SC Quarks/SynthiGME/Desarrollo/archivo.txt", "w");  // Abrir el archivo para escritura
var arrayDeStrings = ["hola", "mundo", "supercollider"];

arrayDeStrings.do { |string|
    archivo.write(string ++ " ");  // Escribe cada string en una nueva línea
};

archivo.close();  // Cierra el archivo después de escribir
)





(
var archivo = File.new("/home/carlos/Dropbox/SC Quarks/SynthiGME/Desarrollo/archivo.txt", "r");  // Abrir el archivo para lectura
var contenido, lineas;

contenido = archivo.readAllString;  // Lee todo el contenido del archivo como un solo string
lineas = contenido.split(" ");  // Divide el contenido en líneas

lineas.do { |linea|
    linea.postln;  // Imprime cada línea
};

archivo.close();  // Cierra el archivo después de leer
)



//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
// con diálogo de ventanas para elegir archivo.

(
FileDialog({
	|path|
	var archivo = File.new(path[0], "w");  // Abrir el archivo para escritura
	var arrayDeStrings = ["hola", "mundo", "supercollider"];

	arrayDeStrings.do { |string|
		archivo.write(string ++ " ");  // Escribe cada string en una nueva línea
	};

	archivo.close();  // Cierra el archivo después de escribir
}, {
	"Guardado cancelado.".postln;
}, fileMode: 0, // Modo para cualquier nombre de archivo
acceptMode: 1 // Modo de guardar
);
)



(
FileDialog({
    |path|
    var archivo = File(path[0], "r");
    var contenido = archivo.readAllString;
    contenido.postln;
    archivo.close();
}, {
    "Apertura cancelada.".postln;
}, fileMode: 1, // Modo para un archivo existente
acceptMode: 0, // Modo de apertura
);
)
