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
					archivo.write(string ++ "\t");
				} {
					archivo.write(string ++ "\n");
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

	// Definir la función saveStateGUI
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



}