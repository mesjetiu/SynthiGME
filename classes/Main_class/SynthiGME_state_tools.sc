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

	// Reiniciamos valores de los parámetros del synthi a los valores iniciales almacenados en initState.
	restartState {
		var oscRecievedMessagesCopy = Dictionary.newFrom(oscRecievedMessages);
		oscRecievedMessagesCopy.keys.do {
			|key|
			var value = initState[key];
			this.setParameterOSC(key, value)
		};

		// reiniciamos oscRecievedMessages para comenzar de cero.
		oscRecievedMessages = Dictionary();
	}

	// Guarda el estado actual del Synthi de forma diferencial: los parámetros que se han modificado desde el inicio.
	saveState { |path, fileName|
		var archivo, exito = false, state, string, extension;
		state = Dictionary.newFrom(this.oscRecievedMessages);
		string = state.getPairs.collect({ |item| item.asString });
		extension = ".spatch";

		if (path.isNil) {path = pathState} {pathState = path};

		if (File.exists(path).not) {File.mkdir(path)};

		// Añadir la extensión .spatch si no está presente
		if (fileName.endsWith(extension).not) {
			fileName = fileName ++ extension;
		};


		// Intenta abrir el archivo y escribir en él
		try {
			archivo = File.new(path +/+ fileName, "w");  // Abrir el archivo para escritura
			// Anotar la versión actual de SynthiGME
			archivo.write("/version" ++ "\t");
			archivo.write("\"" ++ version ++ "\"" ++ "\n");
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
			"Error al guardar el archivo: ".sgmePostln;
			error.errorString.sgmePostln;  // Imprime el mensaje de error
		};

		// Verifica si el archivo se guardó con éxito
		if (exito) {
			modifiedState = false;
			"Archivo guardado correctamente en: ".sgmePostln;
			(path +/+ fileName).sgmePostln;
		}
	}

	// Guardado de estado desde un diálogo de usuario:
	saveStateGUI {
		var path = pathState; // Define un directorio inicial
		if (openDialog) {^this};
		openDialog = true;
		FileDialog(
			{ |path|
				openDialog = false;
				path.notNil.if {
					this.saveState(path.dirname, path.basename);
				}
			},
			{ openDialog = false },
			fileMode: 0,  // Permite la selección de un nombre de archivo, existente o no
			acceptMode: 1,  // Diálogo de guardado
			stripResult: true,  // Pasa la ruta del archivo directamente
			path: path  // Ruta inicial
		);
	}

	// Método de recuperación del estado desde archivo
	loadState { |path, fileName, secure = true|
		var archivo, exito, newState, pairsArray, extension, contenido, oscRecievedMessagesCopy;
		exito = false;
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
			"Error al cargar el archivo: ".sgmePostln;
			error.errorString.sgmePostln;  // Imprime el mensaje de error
		};

		// Verifica si el archivo se cargó con éxito
		if (exito) {
			"Archivo cargado correctamente desde: ".sgmePostln;
			(path +/+ fileName).sgmePostln;

			contenido = contenido.replace("\t", " ").replace("\n", " ").split($ );  // Divide el contenido en líneas

			contenido = contenido.collect({ |item|
				// item = item.stripWhiteSpace; // Usamos stripWhiteSpace para eliminar espacios al principio y al final
				// Convertimos a entero o float si es un número, de lo contrario a símbolo
				if ((item[0] == $/) || (item[0] == $" )) {
					item.asString.replace("\"", "");
				} {
					if (item.interpret.isFloat) {item.asFloat} {item.asInteger}
				}
			});

			newState = Dictionary.newFrom(contenido);

			// Comprobamos si hay entrada de \version. Si la hay, la comparamos con la versión de esta instancia y se lanza mensaje de advertencia.
			if (newState["/version"].notNil) {
				if (newState["/version"].asString != version) {
					("La versión del patch (" ++ newState["/version"].asString ++ ") difiere de la de la instancia de SynthiGME (" ++ version ++ ")").warn;
				};
				newState.removeAt("/version");
			};

			if (secure) {this.restartState}; // por seguridad, se reinicia antes de cambiar de patch, ya que pueden producirse artefactos sonoros indeseados.

			// 1. En oscRecievedMessagesCopy, las claves que no estén en newState, se reinician a valores iniciales.
			oscRecievedMessagesCopy = Dictionary.newFrom(oscRecievedMessages);
			oscRecievedMessagesCopy.keysValuesDo {
				|key, oldValue|
				var initValue;
				if (newState[key].isNil) {
					initValue = initState[key];
					this.setParameterOSC(key, initValue)
					//this.setParameterSmoothedOSC(key, initValue, lagTime: 10, intervalo: 0.5, oldValue: oldValue);
				}
			};
			// Ahora newState contiene todas las claves a actualizar. El resto se han reiniciado a valor inicial en el paso anterior.

			// 2. Reniciar diccionario oscRecievedMessages.
			oscRecievedMessages = Dictionary();

			// 3. Ejecutar todos los parámetros al nuevo estado.
			newState.keysValuesDo {
				|key, value|
				var oldValue = initState[key];
				//this.setParameterSmoothedOSC(key, value, lagTime: 10, intervalo: 0.5, oldValue: oldValue);
				this.setParameterOSC(key, value)
			};

			modifiedState = false;
			"Patch recuperado y ejecutado".sgmePostln;
		}
	}

	loadStateGUI {
		var path = pathState; // Define un directorio inicial
		if (openDialog) {^this};
		openDialog = true;
		FileDialog(
			{ |path|
				openDialog = false;
				path.notNil.if {
					this.loadState(path.dirname, path.basename);
				}
			},
			{ openDialog = false },
			fileMode: 1,  // Modo para un archivo existente
			acceptMode: 0,  // Modo de apertura
			stripResult: true,  // Pasa la ruta del archivo directamente
			path: path  // Ruta inicial
		);
	}

}