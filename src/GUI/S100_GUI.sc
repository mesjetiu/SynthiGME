S100_GUI {

	classvar settings;

	var <window;
	var rectWindow; // Posición y tamaño de la ventana.
	var proportion; // Proporción de la ventana
	var widthRealScreen; // Anchura de la pantalla del ordenador
	var widthScreen; // Anchura de la pantalla virtual sobre la que se trabaja en esta clase.

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
		widthRealScreen = Window.screenBounds.width; // tamaño de la pantalla del ordenador
		widthScreen = 1920; // Anchura de la pantalla virtual (cada pantalla real tendrá un ancho distinto)
		proportion = [16,9]; // Proporciones de la ventana
		rectWindow = Rect(0, 0, widthScreen, (widthScreen * proportion[1]) / proportion[0]);
	}

	makeWindow {
		// Lo primero de todo, se crea la ventana que será padre de todos los "views"
		window = Window("EMS Synthi 100", rectWindow, false, true, scroll: false);
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
			if(yDelta > 0, {this.resize(1.05 ** (yDelta/15))});
			if(yDelta < 0, {this.resize(0.95 ** ((yDelta).abs/15))});
		};



		this.makePannel3(window);

		this.resize(widthRealScreen/widthScreen);
		window.front;
	}

	makePannel3 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 2,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel3 = Image(installedPath ++ "/src/GUI/images/panel_3.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel3,11);

		// Los 6 osciladores de la izquierda
		var left, top, spacing;
		left = 28;
		top = 75.5;
		spacing = 58;

		6.do({
			this.makeOscillator(compositeView, left, top);
			top = top + spacing;
		});

		// Los 6 osciladores de la derecha
		left = 239;
		top = 75.5;
		6.do({
			this.makeOscillator(compositeView, left, top);
			top = top + spacing;
		});
	}

	makeOscillator {|parent, left, top|
		var size = 27;
		var spacing = 30.4;
		var knob1, knob2, knob3, knob4, knob5, knob6, knob7;
		knob1 = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		knob2 = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil])
		.mode_(\horiz).step_(step)
		.centered_(true)
		.value_(0.5);
		left = left + spacing;
		knob3 = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		knob4 = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		left = left + spacing;
		knob5 = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		knob6 = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + 26.4;
		knob7 = Knob(parent, Rect(left, top-17, size, size))
		.color_([black, black, white, nil])
		.mode_(\horiz)
		.step_(step);
	}



	makeNoiseGenerator {
	}

	makeRandomControlVoltageGenerator {
	}


	resize {arg factor, view = window;
		var v = view;
		if(view.asString == "a Window", {
			v = view.view;
			step = step * factor; // solo se ejecuta una vez (cuando el argumento es "window")
		});
		view.bounds = Rect(
			left: view.bounds.left * factor,
			top: view.bounds.top * factor,
			width: view.bounds.width * factor,
			height: view.bounds.height * factor,
		);
		v.children.do({|v|
			this.resize(factor, v);
		})
	}

	resetSize {
		var factor = (widthRealScreen/window.bounds.width);
		window.bounds = Rect(
			left: 0,
			top: 0,
			width: window.bounds.width * factor,
			height: window.bounds.height * factor,
		);
		this.resize(factor, window.view);
		step = step * factor;
	}

	zoomPannel3 {
		var factor, left, top;
		factor = 9/4;
		top = ((9/16) * widthRealScreen) - (((9/16) * widthRealScreen) * factor);
		left = -1 * (((((widthRealScreen * factor)  -  widthRealScreen))/2) + (widthRealScreen/8 * factor));
		this.resize(factor);
		window.bounds = Rect(
			left: left,
			top: top,
			width: window.bounds.width,
			height: window.bounds.height,
		);
	}
}


