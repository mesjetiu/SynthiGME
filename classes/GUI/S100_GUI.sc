S100_GUI {

	classvar settings;

	var synthi100;

	var <window;
	var <parameterViews; // Dictionary con todas las views y claves para OSC
	var <defaultSizes; // Array con todos los tamaños por defecto
	var <synthi100; // instancia de Synthi100 para callback
	var rectWindow; // Posición y tamaño de la ventana.
	var proportion; // Proporción de la ventana
	var widthRealScreen; // Anchura de la pantalla del ordenador
	var widthScreen; // Anchura de la pantalla virtual sobre la que se trabaja en esta clase.

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

		widthRealScreen = Window.availableBounds.width * (95/100); // tamaño de la pantalla del ordenador
		widthScreen = 1920; // Anchura de la pantalla virtual (cada pantalla real tendrá un ancho distinto)
		proportion = [16,9]; // Proporciones de la ventana
		rectWindow = Rect(0, 0, widthScreen, (widthScreen * proportion[1]) / proportion[0]);

		click = false;
		running = false;
	}

	makeWindow {
		// Lo primero de todo, se crea la ventana que será padre de todos los "views"
		window = Window("EMS Synthi 100", rectWindow, false, true, scroll: false);
		window.background = blackForniture;
		defaultSizes = defaultSizes.add([window, window.bounds]);

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

		this.makePannel1(window);
		this.makePannel2(window);
		this.makePannel3(window);
		this.makePannel4(window);
		this.makePannel5(window);
		this.makePannel6(window);

		this.resize(widthRealScreen/widthScreen);
		window.front;

		running = true;
	}

	makePannel1 {|parent|
		var rect = Rect(
			0,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel1 = Image(installedPath ++ "/classes/GUI/images/pannel_1.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel1,10);
		/*
		// Los 6 osciladores de la izquierda
		var left, top, spacing;
		left = 28;
		top = 75.5;
		spacing = 58;

		6.do({|num|
		this.makeOscillator(compositeView, left, top, num + 1);
		top = top + spacing;
		});

		// Los 6 osciladores de la derecha
		left = 239;
		top = 75.5;
		6.do({|num|
		this.makeOscillator(compositeView, left, top, num + 7);
		top = top + spacing;
		});

		compositeView.mouseDownAction_({
		Routine({ // Cuando se hace un click...
		if (click == true, {
		// Cuando se hace doble click...
		if(window.bounds.width == widthRealScreen, {
		this.zoomPannel3;
		},{
		this.resetSize;
		})
		}, {
		click = true;
		wait(timeDoubleClick); // tiempo de espera del segundo click
		});
		click = false; // una vez transcurrida la espera, se vuelve false
		}).play(AppClock);
		});
		*/
		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}

	makePannel2 {|parent|
		var rect = Rect(
			(rectWindow.width/4),
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel2 = Image(installedPath ++ "/classes/GUI/images/pannel_2.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel2,10);
		/*
		// Los 6 osciladores de la izquierda
		var left, top, spacing;
		left = 28;
		top = 75.5;
		spacing = 58;

		6.do({|num|
		this.makeOscillator(compositeView, left, top, num + 1);
		top = top + spacing;
		});

		// Los 6 osciladores de la derecha
		left = 239;
		top = 75.5;
		6.do({|num|
		this.makeOscillator(compositeView, left, top, num + 7);
		top = top + spacing;
		});

		compositeView.mouseDownAction_({
		Routine({ // Cuando se hace un click...
		if (click == true, {
		// Cuando se hace doble click...
		if(window.bounds.width == widthRealScreen, {
		this.zoomPannel3;
		},{
		this.resetSize;
		})
		}, {
		click = true;
		wait(timeDoubleClick); // tiempo de espera del segundo click
		});
		click = false; // una vez transcurrida la espera, se vuelve false
		}).play(AppClock);
		});
		*/
		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}


	makePannel3 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 2,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel3 = Image(installedPath ++ "/classes/GUI/images/pannel_3.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel3,10);

		// Los 6 osciladores de la izquierda
		var left, top, spacing;
		left = 28;
		top = 75.5;
		spacing = 58;

		6.do({|num|
			this.makeOscillator(compositeView, left, top, num + 1);
			top = top + spacing;
		});

		// Los 6 osciladores de la derecha
		left = 239;
		top = 75.5;
		6.do({|num|
			this.makeOscillator(compositeView, left, top, num + 7);
			top = top + spacing;
		});

		compositeView.mouseDownAction_({
			Routine({ // Cuando se hace un click...
				if (click == true, {
					// Cuando se hace doble click...
					if(window.bounds.width == widthRealScreen, {
						this.zoomPannel3;
					},{
						this.resetSize;
					})
				}, {
					click = true;
					wait(timeDoubleClick); // tiempo de espera del segundo click
				});
				click = false; // una vez transcurrida la espera, se vuelve false
			}).play(AppClock);
		});
		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}

	makeOscillator {|parent, left, top, num|
		var size = 27;
		var spacing = 30.4;
		var pulseLevel, pulseShape, sineLevel, sineSymmetry, triangleLevel, sawtoothLevel, frequency;
		pulseLevel = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		pulseShape = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil])
		.mode_(\horiz).step_(step)
		.centered_(true)
		.value_(0.5);
		left = left + spacing;
		sineLevel = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		sineSymmetry = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		left = left + spacing;
		triangleLevel = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		sawtoothLevel = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + 26.4;
		frequency = Knob(parent, Rect(left, top-17, size, size))
		.color_([black, black, white, nil])
		.mode_(\horiz)
		.step_(step);

		// Se añaden las views y sus bounds por defecto para resize
		defaultSizes = defaultSizes ++ [
			[pulseLevel, pulseLevel.bounds],
			[pulseShape, pulseShape.bounds],
			[sineLevel, sineLevel.bounds],
			[sineSymmetry, sineSymmetry.bounds],
			[triangleLevel, triangleLevel.bounds],
			[sawtoothLevel, sawtoothLevel.bounds],
			[frequency, frequency.bounds]
		];


		// Se añaden al diccionario todos los mandos del oscilador para poder cambiar su valor.
		parameterViews
		.put("/osc/" ++ num ++ "/pulse/" ++ "level", pulseLevel)
		.put("/osc/" ++ num ++ "/pulse/" ++ "shape", pulseShape)
		.put("/osc/" ++ num ++ "/sine/" ++ "level", sineLevel)
		.put("/osc/" ++ num ++ "/sine/" ++ "symmetry", sineSymmetry)
		.put("/osc/" ++ num ++ "/triangle/" ++ "level", triangleLevel)
		.put("/osc/" ++ num ++ "/sawtooth/" ++ "level", sawtoothLevel)
		.put("/osc/" ++ num ++ "/frequency", frequency);

		// Acciones a realizar al cambiar manualmente el valor de cada mando
		pulseLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/pulse/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		pulseShape.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/pulse/" ++ "shape",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		sineLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/sine/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sineSymmetry.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/sine/" ++ "symmetry",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		triangleLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/triangle/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sawtoothLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/sawtooth/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		frequency.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/frequency",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
	}



	makeNoiseGenerator {
	}

	makeRandomControlVoltageGenerator {
	}

	makePannel4 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 3,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel4 = Image(installedPath ++ "/classes/GUI/images/pannel_4.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel4,10);
		/*
		// Los 6 osciladores de la izquierda
		var left, top, spacing;
		left = 28;
		top = 75.5;
		spacing = 58;

		6.do({|num|
		this.makeOscillator(compositeView, left, top, num + 1);
		top = top + spacing;
		});

		// Los 6 osciladores de la derecha
		left = 239;
		top = 75.5;
		6.do({|num|
		this.makeOscillator(compositeView, left, top, num + 7);
		top = top + spacing;
		});

		compositeView.mouseDownAction_({
		Routine({ // Cuando se hace un click...
		if (click == true, {
		// Cuando se hace doble click...
		if(window.bounds.width == widthRealScreen, {
		this.zoomPannel3;
		},{
		this.resetSize;
		})
		}, {
		click = true;
		wait(timeDoubleClick); // tiempo de espera del segundo click
		});
		click = false; // una vez transcurrida la espera, se vuelve false
		}).play(AppClock);
		});
		*/
		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}

	makePannel5 {|parent|
		var rect = Rect(
			0,
			rectWindow.width/4,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel5 = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel5,10);
		compositeView.background = whiteBackground;


		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}


	makePannel6 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 2,
			rectWindow.width/4,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel6 = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel6,10);
		compositeView.background = whiteBackground;


		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}


	resize {arg factor, view = window, left, top;
		var v = view;
		var l, t;
		if (left != nil, {l = left}, {l = view.bounds.left * factor});
		if (top != nil, {t = top}, {t = view.bounds.top * factor});
		if(view.asString == "a Window", {
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

	zoomPannel3 {
		var factor, left, top;
		factor = 9/4;
		top = ((9/16) * widthRealScreen) - (((9/16) * widthRealScreen) * factor);
		left = -1 * (((((widthRealScreen * factor)  -  widthRealScreen))/2) + (widthRealScreen/8 * factor));
		this.resize(factor, left: left, top: top);
	}
}