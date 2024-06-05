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

MessageRedirector {
	classvar instance,
	synthiGME,
	<window,
	textView,
	storedText;

	*initClass {
		instance = nil;
		window = nil;
		textView = nil;
		storedText = "";  // Inicializar el texto almacenado
	}

	*getInstance {
		var instance;
		if (instance.isNil) {
			instance = MessageRedirector.new.init;
		};
		^instance
	}

	init {
		synthiGME = SynthiGME.instance;
		^this
	}

	*createWindow {|synthi|
		if (window.isNil) {
			var layout;
			var rect = Rect(
				Window.availableBounds.width/3.9,
				Window.availableBounds.height/20,
				width: 450,
				height: 400
			);
			window = Window("Synthi GME Post window", rect);
			window.view.palette_(QPalette.dark);
			window.front;
			//	.alwaysOnTop_(true);
			layout = VLayout(8);
			textView = TextView()
			.string_(storedText) // Inicializar el TextView con el texto almacenado
			.editable_(false)
			.palette_(QPalette.dark);
			layout.add(textView);
			window.layout = layout;

			SGME_GUIShortcuts.makeShortcuts(instance, window, synthi);

			window.onClose = {
				MessageRedirector.endRedirect();
			};
		}
	}

	postMessage { |string|
		//var timestamp = Date.getDate.asString("%H:%M:%S");  // Obtener la hora actual con formato de horas, minutos y segundos
		var message = nil;
		var timestamp = Date.getDate.hourStamp.format("%T").asString().split($.)[0];
		if (MessageRedirector.containsPrintableChars(string)) {
			message = "[" ++ timestamp ++ "] " ++ string;   // Formatear el mensaje con la marca de tiempo
		} {message = string}; // No se muestra la hora en líneas vacías.


		storedText = message ++ "\n" ++ storedText;  // Almacenar el texto
		if (textView.notNil) {
			{
				textView.string = storedText;  // Actualizar el TextView si existe
			}.defer;
		};
		string.postln;  // También envía al Post Window estándar (solo string, sin la hora)
	}

	*containsPrintableChars { |stringToCheck|
		// La expresión regular para caracteres imprimibles
		var regexp = "[\\x21-\\x7E]";

		// Utilizar .matchRegexp para verificar si hay caracteres imprimibles en la cadena
		^regexp.matchRegexp(stringToCheck)
	}

	*endRedirect {
		if (window.notNil) {
			window.close;
			window = nil;
			textView = nil;
		}
	}

	*closeWindow {
		if (window.notNil) {
			window.close;
			window = nil;
		}
	}

	*showWindow {
		if (window.notNil) {
			window.front;
		} {
			MessageRedirector.createWindow;
		}
	}

	*hideWindow {
		if (window.notNil) {
			window.visible = false;
		}
	}

	// método vacíos a propósito. Se llama desde shortcuts
	resizeFocusedPanel{

	}

	goFront {
		if (window.isNil.not){
			window.front
		}
	}
}
