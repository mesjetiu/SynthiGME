S100_GUIPanel {

	var <window;
	var compositeView;
	var viewSizes;
	var zoomHLevel = 1;
	var zoomWLevel = 1;
	var rectWindow;
	var rectCompositeView;
	var virtualWidth = 1920;

	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;

	var synthi100;
	var parameterViews;

	var installedPath;

	// Colores de la intefaz (tomados de fotografías del Synthi 100)
	var blue;
	var green;
	var yellow;
	var red;
	var white;
	var black;
	var whiteBackground;
	var blackForniture;


	//*********************************************************************************************

	*new {|synthi, parameters|
		^super.new.init(synthi, parameters);
	}

	init {|synthi, parameters|
		synthi100 = synthi;
		parameterViews = parameters;
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];

		blue = Color.new255(61.8, 86.7, 118.4);
		green = Color.new255(68.6, 107.2, 82.6);
		white = Color.new255(172.7, 166.6, 160.3);
		black = Color.new255(34.4, 36.3, 38.7);
		yellow = Color.new255(224, 186, 69);
		red = Color.new255(183, 56, 66);
		whiteBackground = Color.new255(191, 180, 176); // Color de los paneles del Synthi 100
		blackForniture = Color.new255(18, 18, 19.2); // Color negro del mueble.
	}

	makeWindow { // Para sobrescribir desde clases que heredan
		rectWindow = Rect(0, 0,  virtualWidth/4,  virtualWidth/4);
		rectCompositeView = Rect(
			0,
			0,
			rectWindow.width,
			rectWindow.height,
		);
		window =  Window("", rectWindow, false, true, scroll: true)
		.userCanClose_(false);
		compositeView = CompositeView(window, rectCompositeView);

		window.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
			var factor = 1.5;
			keycode.switch(
				118, {this.commuteVisibility},  // 'v' Activa y desactiva la visibilidad de los mandos de la ventana en foco.
				65451, {this.resizePanel(factor)}, // +
				65453, {this.resizePanel(1/factor)}, // -
				43, {this.resizePanel(factor)}, // + (en mi portatil Slimbook)
				45, {this.resizePanel(1/factor)}, // - (en mi portatil Slimbook)
				102, {synthi100.guiSC.frontWindows}, // f (front) Todas las ventanas al frente
				49, {synthi100.guiSC.panels[0].window.front}, // Tecla 1: Panel 1 al frente
				50, {synthi100.guiSC.panels[1].window.front}, // Tecla 2: Panel 2 al frente
				51, {synthi100.guiSC.panels[2].window.front}, // Tecla 3: Panel 3 al frente
				52, {synthi100.guiSC.panels[3].window.front}, // Tecla 4: Panel 4 al frente
				53, {synthi100.guiSC.panels[4].window.front}, // Tecla 5: Panel 5 al frente
				54, {synthi100.guiSC.panels[5].window.front}, // Tecla 6: Panel 6 al frente
				55, {synthi100.guiSC.panels[6].window.front}, // Tecla 7: Panel 7 al frente
			);
		};

		viewSizes = [];
		viewSizes = viewSizes.add([window, rectWindow]);
		viewSizes = viewSizes.add([compositeView, rectCompositeView]);
	}


	resizePanel {arg factor;
		var factorH, factorW;
		if ((factor * zoomWLevel * viewSizes[0][1s].width) > (Window.availableBounds.width * 1), {
			factorW = (Window.availableBounds.width * 1) /  (viewSizes[0][1].width);
		}, {factorW = factor * zoomWLevel});
		if ((factor * zoomHLevel * viewSizes[0][1].height) > (Window.availableBounds.height * 1), {
			factorH = (Window.availableBounds.height * 1) / (viewSizes[0][1].height);
		}, {factorH = factorW});

		viewSizes.do({|v|
			if (v[0].class === Window, {
				v[0].bounds_(Rect(
					left: v[0].bounds.left,
					top: v[0].bounds.top,
					width: v[1].width * factorW,
					height: v[1].height * factorH,
				))
			}, {
				v[0].bounds_(Rect(
					left: v[1].left * factorW,
					top: v[1].top * factorW,
					width: v[1].width * factorW,
					height: v[1].height * factorW,
				))
			})
		});
		zoomWLevel = factorW;
		zoomHLevel = factorH;
	}

	parameterVisibility {|bool|
		viewSizes.do({|v|
			if(
				(v[0].class === Window)
				.or(v[0].class === CompositeView),
				{},
				{v[0].visible_(bool)}
			)
		})
	}

	// Hace visible o invisible los mandos de una ventana
	commuteVisibility {
		var visible;
		viewSizes.do({|v|
			if(
				(v[0].class === Window)
				.or(v[0].class === CompositeView),
				{},
				{if(v[0].visible == true,
					{v[0].visible = false},
					{v[0].visible = true}
				)}
			)
		})
	}
}