
MessageRedirector {
	classvar instance,
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
		^this
	}

	*createWindow {
		if (window.isNil) {
			var layout;
			var rect = Rect(
				Window.availableBounds.width/3.9,
				Window.availableBounds.height/20,
				width: 450,
				height: 400
			);
			window = Window("Synthi GME Post window", rect).front;
			//	.alwaysOnTop_(true);
			layout = VLayout(8);
			textView = TextView()
			.string_(storedText) // Inicializar el TextView con el texto almacenado
			.editable_(false);
			layout.add(textView);
			window.layout = layout;

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
}
