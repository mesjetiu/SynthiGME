// Procedimiento para guardar estado de SynthiGME en un momento dado.


o = SynthiGME().run

// Diccionario con los últimos valores dados a cada parámetro:
e = o.oscRecievedMessages

// convertimos en String el diccionario:
f = e.getPairs.asString

// Eliminamos corchetes:
f = f.replace("[", "").replace("]", ""); // Quitamos los corchetes


//******* Se guarda el archivo y se recupera posteriormente el mismo string

// Dividimos por coma y se convierte en array
g = f.split($,);

// Se convierte cada elemento del array en
(
h = g.collect({ |item|
   // item = item.stripWhiteSpace; // Usamos stripWhiteSpace para eliminar espacios al principio y al final
    // Convertimos a entero si es un número, de lo contrario a símbolo
    if (item.isNumber) { item.asFloat.round(0.01) } { item.asString }
});
)

// Ahora reconstruimos el Dictionary
i = Dictionary.newFrom(h);

i
e

i.size
e.size