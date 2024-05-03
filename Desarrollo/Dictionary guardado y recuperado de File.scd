// Para guardar y recuperar un Dictionary en un file

// Creamos un Dictionay de prueba
d = Dictionary.newFrom(["uno", 1, "dos", 2])

// Transforma el diccionario en un array
d.getPairs
// lo pasamo a String. Esto ya es guardable en File.
f = e.getPairs.asString

// Una vez recuperado del archivo, tenemos el String f
f
f.class

// Supongamos que f tiene el contenido inicial como "[ uno, 1, dos, 2 ]"
f = f.replace("[", "").replace("]", ""); // Quitamos los corchetes

// En este punto se puede guardar en un archivo y volver a recuperarse.

(
f = "
    uno, 1,
    dos, 2,
    tres, 3,
    cuatro, 4,
    cinco, 5
"
)
f

// Eliminamos tabuladores, espacios, saltos de línea... Dividimos por coma y procesamos cada elemento
i = f.replace("\n", "").replace(" ", "").replace("\t", "").split($,);

i.size
i.class
i
i[0]
i[0].class
i[1].class
(
i = i.collect({ |item|
   // item = item.stripWhiteSpace; // Usamos stripWhiteSpace para eliminar espacios al principio y al final
    // Convertimos a entero si es un número, de lo contrario a símbolo
    if (item.isInteger) { item.asInteger } { item.asString }
});
)

// Ahora reconstruimos el Dictionary
g = Dictionary.newFrom(i);
g.postln;
g.size


// ver cómo hacerlo con JSON
"{\"a\": 1}".parseJSON;

(
g = "{

\"a\":
1}".parseJSON;
)

// no sé cómo funciona...
g.toJSON