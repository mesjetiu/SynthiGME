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

	*new {arg create = false;
		^super.new.init(create);
	}


	// Métodos de instancia ******************************************************************

	init {
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];
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
		var width = rectWindow.width/4;
		var left = (rectWindow.width/4) * 2;
		var top = 0;
		var rect = Rect(left, top, width, width);
		var compositeView = CompositeView(parent, rect); //.background_(Color.rand);

		compositeView.layout = VLayout(
			HLayout(
				VLayout(
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
				),
				VLayout(
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
					this.makeOscillator(),
				),
			),
			HLayout(
				this.makeNoiseGenerator(),
				this.makeNoiseGenerator(),
				this.makeRandomControlVoltageGenerator(),
			).setStretch(0, 2).setStretch(1, 2).setStretch(2, 5),
		).setStretch(0, 6).setStretch(1, 1);

		allViews.add(compositeView);
	}

	makeOscillator {
		var imageOTLL = Image(installedPath ++ "/src/GUI/images/osc_triangle_level_label.png");
		var imageOSLL = Image(installedPath ++ "/src/GUI/images/osc_sawtooth_level_label.png");
		var knobs = 7.collect({this.makeKnob(imageOSLL)});
		^HLayout(*knobs);
	}

	makeKnob {|image|
		^VLayout( // Cada uno de los Knobs del oscilador
			CompositeView(),
			CompositeView().setBackgroundImage(image, 11),
			Knob(),
		).setStretch(0,1).setStretch(1,2).setStretch(2,3);
	}

	makeNoiseGenerator {
		var imageOTLL = Image("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100/src/GUI/images/osc_triangle_level_label.png");
		var knobs = 2.collect({this.makeKnob(imageOTLL)});
		^HLayout(*knobs);
	}

	makeRandomControlVoltageGenerator {
		var imageOTLL = Image("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100/src/GUI/images/osc_triangle_level_label.png");
		var knobs = 5.collect({this.makeKnob(imageOTLL)});
		^HLayout(*knobs);
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
		})
	}

	resetSize {
		window.bounds = windowSize;
		allViews.do({|view, i|
			view.bounds = allSizeViews[i];
		})
	}
}


