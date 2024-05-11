/*
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/


+ SynthiGME {

	// Guarda el estado actual del Synthi de forma diferencial: los parámetros que se han modificado desde el inicio.
	saveState { |path, fileName|
		var archivo, exito = false, state, string, extension;
		state = Dictionary.newFrom(this.oscRecievedMessages);
		string = state.getPairs.collect({ |item| item.asString });
		extension = ".spatch";
		if (path.isNil) {path = pathState} {pathState = path};

		// Añadir la extensión .spatch si no está presente
		if (fileName.endsWith(extension).not) {
			fileName = fileName ++ extension;
		};


		// Intenta abrir el archivo y escribir en él
		try {
			archivo = File.new(path +/+ fileName, "w");  // Abrir el archivo para escritura
			string.do { |string, n|
				if (n.even) {
					archivo.write(string ++ "\t"); // Entre clave y valor, tabulador
				} {
					archivo.write(string ++ "\n"); // tras cada asociación, salto de línea
				}
			};
			archivo.close();  // Cierra el archivo después de escribir
			exito = true;
		} {|error|
			// En caso de error durante la apertura o escritura del archivo
			archivo.notNil.if { archivo.close };  // Asegúrate de cerrar el archivo si se abrió
			"Error al guardar el archivo: ".postln;
			error.errorString.postln;  // Imprime el mensaje de error
		};

		// Verifica si el archivo se guardó con éxito
		if (exito) {
			"Archivo guardado correctamente en: ".postln;
			(path +/+ fileName).postln;
		}
	}

	// Guardado de estado desde un diálogo de usuario:
	saveStateGUI {
		var path = pathState; // Define un directorio inicial

		FileDialog(
			{ |path|
				path.notNil.if {
					this.saveState(path.dirname, path.basename);
				}
			},
			{ "Cancelado por el usuario".postln },
			fileMode: 0,  // Permite la selección de un nombre de archivo, existente o no
			acceptMode: 1,  // Diálogo de guardado
			stripResult: true,  // Pasa la ruta del archivo directamente
			path: path  // Ruta inicial
		);
	}

	// Método de recuperación del estado desde archivo
	loadState { |path, fileName|
		var archivo, exito = false, newState, pairsArray, extension, contenido, oscRecievedMessagesCopy;
		extension = ".spatch";
		if (path.isNil) {path = pathState} {pathState = path};

		// Intenta abrir el archivo y cargar desde él
		try {
			archivo = File.new(path +/+ fileName, "r");  // Abrir el archivo para lectura
			contenido = archivo.readAllString;  // Lee todo el contenido del archivo como un solo string
			archivo.close();  // Cierra el archivo después de leer
			exito = true;
		} {|error|
			// En caso de error durante la apertura o lectura del archivo
			archivo.notNil.if { archivo.close };  // Asegúrate de cerrar el archivo si se abrió
			"Error al cargar el archivo: ".postln;
			error.errorString.postln;  // Imprime el mensaje de error
		};

		// Verifica si el archivo se cargó con éxito
		if (exito) {
			"Archivo cargado correctamente desde: ".postln;
			(path +/+ fileName).postln;

			contenido = contenido.replace("\t", " ").replace("\n", " ").split($ );  // Divide el contenido en líneas

			contenido = contenido.collect({ |item|
				// item = item.stripWhiteSpace; // Usamos stripWhiteSpace para eliminar espacios al principio y al final
				// Convertimos a entero o float si es un número, de lo contrario a símbolo
				if (item[0] == $/) {item.asString} {
					if (item.interpret.isFloat) {item.asFloat} {item.asInteger}
				}
			});

			newState = Dictionary.newFrom(contenido);

			// Reiniciamos valores de los parámetros del synthi a los valores iniciales almacenados en initState.
			oscRecievedMessages = Dictionary.newFrom(oscRecievedMessages);
			oscRecievedMessagesCopy.keys.do {
				|key|
				var value = initState[key];
				this.setParameterOSC(key, value)
			};

			// reiniciamos oscRecievedMessages para comenzar de cero.
			oscRecievedMessages = Dictionary();

			// recuperamos valores anteriores en el synthi
			newState.keysValuesDo {
				|key, value|
				this.setParameterOSC(key, value)
			};

			"Patch recuperado y ejecutado".postln;
		}
	}


	loadStateGUI {
		var path = pathState; // Define un directorio inicial

		FileDialog(
			{ |path|
				path.notNil.if {
					this.loadState(path.dirname, path.basename);
				}
			},
			{ "Cancelado por el usuario".postln },
			fileMode: 1,  // Modo para un archivo existente
			acceptMode: 0,  // Modo de apertura
			stripResult: true,  // Pasa la ruta del archivo directamente
			path: path  // Ruta inicial
		);
	}

}