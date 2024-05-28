
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
			window = Window("Synthi GME Post window", Rect(100, 100, 400, 300)).front
			.alwaysOnTop_(true);
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
		var timestamp = Date.getDate.hourStamp.format("%T").asString().split($.)[0];
		var message = "[" ++ timestamp ++ "] " ++ string;   // Formatear el mensaje con la marca de tiempo

		storedText = message ++ "\n" ++ storedText;  // Almacenar el texto
		if (textView.notNil) {
			{
				textView.string = storedText;  // Actualizar el TextView si existe
			}.defer;
		};
		message.postln;  // También envía al Post Window estándar
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
