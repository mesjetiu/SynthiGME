S100_GUI {

	classvar settings;

	var synthi100;

	var <window;
	var <compositeView;
	var <parameterViews; // Dictionary con todas las views y claves para OSC
	var <defaultSizes; // Array con todos los tamaños por defecto
	var <synthi100; // instancia de Synthi100 para callback
	var rectWindow; // Posición y tamaño de la ventana.
	var proportion; // Proporción de la ventana
	var widthRealScreen; // Anchura de la pantalla del ordenador
	var widthScreen; // Anchura de la pantalla virtual sobre la que se trabaja en esta clase.

	var windowPannel1;
	var viewSizesPannel1;
	var zoomLevelPannel1 = 1;
	var pannel1;


	var installedPath; // Dirección absoluta de instalación del Quark.
	var <running; // Es true cuando se enciende la GUI. Sirve de semáforo para enviar o no mensajes desde fuera.

	// Colores de la intefaz
	var blue;
	var green;
	var white;
	var black;
	var whiteBackground;
	var blackForniture;

	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;

	// Al hacer click sobre determinadas views se activa la rutina esperando un segundo click.
	var click;
	var timeDoubleClick = 0.5;

	*new {arg synthi;
		^super.new.init(synthi);
	}


	// Métodos de instancia ******************************************************************

	init {|synthi|
		parameterViews = Dictionary.new;
		defaultSizes =  [];
		synthi100 = synthi;
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];
		blue = Color.new255(61.8, 86.7, 118.4);
		green = Color.new255(68.6, 107.2, 82.6);
		white = Color.new255(172.7, 166.6, 160.3);
		black = Color.new255(34.4, 36.3, 38.7);
		whiteBackground = Color.new255(191, 180, 176); // Color de los paneles del Synthi 100
		blackForniture = Color.new255(18, 18, 19.2); // Color negro del mueble.

		widthRealScreen = Window.availableBounds.width ; // tamaño de la pantalla del ordenador
		widthScreen = 1920; // Anchura de la pantalla virtual (cada pantalla real tendrá un ancho distinto)
		proportion = [16,9]; // Proporciones de la ventana
		rectWindow = Rect(0, 0, widthScreen/4, widthScreen/4);


		pannel1 = S100_GUIPannel();

		click = false;
		running = false;
	}

	makeWindow {
		// Lo primero de todo, se crea la ventana que será padre de todos los "views"
		//	window = Window("EMS Synthi 100", rectWindow, false, true, scroll: true);
		//	window.background = blackForniture;
		//	defaultSizes = defaultSizes.add([window, window.bounds]);

		/*		window.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
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
		*/
		compositeView = CompositeView(window, rectWindow);
		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);

		//  this.makePannel1(compositeView);
		//	this.makePannel2(compositeView);
		//	this.makePannel3(compositeView);
		//	this.makePannel4(compositeView);
		//	this.makePannel5(compositeView);
		//	this.makePannel6(compositeView);

		//	this.resize(widthRealScreen/widthScreen);
		//	this.resize2(0.5);
		//	window.front;

		pannel1.makeWindow;

		running = true;
	}

	resize {arg factor, view = window, left, top;
		var v = view;
		var l, t;
		if (left != nil, {l = left}, {l = view.bounds.left * factor});
		if (top != nil, {t = top}, {t = view.bounds.top * factor});
		if (view.class === Window, {
			v = view.view;
			step = step * factor; // solo se ejecuta una vez (cuando el argumento es "window")
		});
		view.bounds = Rect(
			left: l,
			top: t,
			width: view.bounds.width * factor,
			height: view.bounds.height * factor,
		);
		v.children.do({|v|
			this.resize(factor, v);
		});
	}

	resize2 {arg factor, left, top;
		step = step * factor;
		window.view.bounds = Rect(
			left: left,
			top: top,
			width: window.bounds.width * factor,
			height: window.bounds.height * factor,
		);
		defaultSizes.do({|v|
			var l, t, w, h;
			l = v[1].bounds.left;
			t = v[1].bounds.top;
			w = v[1].bounds.width;
			h = v[1].bounds.height;
			v[0].bounds = Rect(
				left: l * factor,
				top: t * factor,
				width: w * factor,
				height: h * factor,
			)
		})
	}


	resizePannel {arg viewSizes, factorH, factorW, zoomLevelPannel;
		var factor = factorW * zoomLevelPannel;
		step = step * factorW * zoomLevelPannel;
		viewSizes.do({|v|
			if (v[0].class === Window, {
				v[0].bounds = Rect(
					left: v[0].bounds.left,
					top: v[0].bounds.top,
					width: v[1].width * factorW * zoomLevelPannel,
					height: v[1].height * factorH * zoomLevelPannel,
				)
			}, {
				v[0].bounds = Rect(
					left: v[1].left * factor,
					top: v[1].top * factor,
					width: v[1].width * factor,
					height: v[1].height * factor,
				)
			})
		});
		zoomLevelPannel1 = factorW * zoomLevelPannel;
	}

	resetSize {
		/*		var factor = (widthRealScreen/window.bounds.width);
		window.bounds = Rect(
		left: 0,
		top: 0,
		width: window.bounds.width * factor,
		height: window.bounds.height * factor,
		);
		this.resize(factor, window.view);*/
		defaultSizes.do({|par|
			var view, bounds;
			view = par[0];
			bounds = par[1];
			view.bounds = bounds;
		});
		step = stepDefault;
		this.resize(widthRealScreen/widthScreen);
	}
}