S100_GUI {

	classvar settings;

	var <window;
	var windowSize; // Rect, tamaño y coordenadas por defecto (para reiniciar)
	var <allViews; // Array con todos los views que componen la ventana (excepto la ventana misma, de la variable "window"). Se usará para cambiar el tamaño de todos los elementos al mismo tiempo.
	var allSizeViews; // Array que almacenará todos los Rect de todos los Views de la ventana. Serán los tamaños por defecto, para reiniciar.
	var rectWindow; // Posición y tamaño de la ventana.
	var proportion; // Proporción de la ventana
	var widthScreen; // Anchura de la pantalla del ordenador

	var installedPath; // Dirección absoluta de instalación del Quark.

	// Colores de la intefaz
	var blue;
	var green;
	var white;
	var black;

	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;

	*new {arg create = false;
		^super.new.init(create);
	}


	// Métodos de instancia ******************************************************************

	init {
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];
		blue = Color.new255(61.8, 86.7, 118.4);
		green = Color.new255(68.6, 107.2, 82.6);
		white = Color.new255(172.7, 166.6, 160.3);
		black = Color.new255(34.4, 36.3, 38.7);
		widthScreen = Window.screenBounds.width; // tamaño de la pantalla del ordenador
		//widthScreen = 700; // solo para pruebas, por comodidad.
		proportion = [16,9]; // Proporciones de la ventana
		rectWindow = Rect(0, 0, widthScreen, (widthScreen * proportion[1]) / proportion[0]);
		allViews = [];
	}

	makeWindow {
		var vLayout, hUpLayout, hDownLayout, pannelsUp, pannelsDown;

		// Lo primero de todo, se crea la ventana que será padre de todos los "views"
		//	var image = Image.new("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/imágenes del Synthi 100 montadas/vista general.jpg");
		window = Window("EMS Synthi 100", rectWindow, false, true, scroll: false);
		//	window.view.setBackgroundImage(image, 10);
		window.background = Color.new255(191, 180, 176); // Color de los paneles del Synthi 100
		window.view.mouseDownAction = {|view, x, y, modifiers, buttonNumber, clickCount| [x,y].postln};
		window.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
			char.postln;
			keycode.postln;
			if(keycode==65451, {this.resize(1.3)}); // '+'
			if(keycode==65453, {this.resize(0.7)}); // '-'
			if(keycode==65450, {this.resetSize}); // '*'
		};
		window.view.mouseWheelAction = {|view, x, y, modifiers, xDelta, yDelta|
			[x,y,xDelta,yDelta].postln;
			if(yDelta == 15, {this.resize(1.1)});
			if(yDelta == -15, {this.resize(0.9)});
		};
		windowSize = window.bounds;



		this.makePannel3(window);


		// Se almacenan todos los Rect de todos los Views para saber el tamaño por defecto y poder resetear.
		allSizeViews = allViews.collect({|v| v.bounds});
		window.front;
	}

	makePannel3 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 2,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imageOTLL = Image(installedPath ++ "/src/GUI/images/panel_3.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imageOTLL,11);

		// Los 6 osciladores de la izquierda
		var left, top, spacing;
		left = 28;
		top = 75.5;
		spacing = 58;

		6.do({
			this.makeOscillator(compositeView, left, top);
			top = top + spacing;
		});

		allViews = allViews.add(compositeView);
	}

	makeOscillator {|parent, left, top|
		var size = 27;
		var spacing = 30.4;
		var knob1, knob2, knob3, knob4, knob5, knob6, knob7;
		knob1 = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil]).mode_(\horiz).step_(step);
		left = left + spacing;
		knob2 = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil]).mode_(\horiz).step_(step).centered_(true).value_(0.5);
		left = left + spacing;
		knob3 = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil]).mode_(\horiz).step_(step);
		left = left + spacing;
		knob4 = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil]).mode_(\horiz).step_(step).centered_(true).value_(0.5);
		left = left + spacing;
		knob5 = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil]).mode_(\horiz).step_(step);
		left = left + spacing;
		knob6 = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil]).mode_(\horiz).step_(step);
		left = left + 26.4;
		knob7 = Knob(parent, Rect(left, top-17, size, size))
		.color_([black, black, white, nil]).mode_(\horiz).step_(step);
		allViews = allViews ++ [knob1, knob2, knob3, knob4, knob5, knob6, knob7];
	}



	makeNoiseGenerator {
	}

	makeRandomControlVoltageGenerator {
	}


	resize {arg factor;
		window.bounds = Rect(
			left: window.bounds.left,
			top: window.bounds.top,
			width: window.bounds.width * factor,
			height: window.bounds.height * factor,
		);
		allViews.do({|view|
			view.bounds = Rect(
				left: view.bounds.left * factor,
				top: view.bounds.top * factor,
				width: view.bounds.width * factor,
				height: view.bounds.height * factor,
			);
		});
		step = step * factor;
	}

	resetSize {
		window.bounds = windowSize;
		allViews.do({|view, i|
			view.bounds = allSizeViews[i];
		});
		step = stepDefault;
	}
}


