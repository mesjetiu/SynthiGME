S100_GUIPannel {

	var window;
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

	// Colores de la intefaz
	var blue;
	var green;
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
		whiteBackground = Color.new255(191, 180, 176); // Color de los paneles del Synthi 100
		blackForniture = Color.new255(18, 18, 19.2); // Color negro del mueble.

		rectWindow = Rect(0, 0,  virtualWidth/4,  virtualWidth/4);
		rectCompositeView = Rect(
			0,
			0,
			rectWindow.width,
			rectWindow.height,
		);
		window =  Window("", rectWindow, false, true, scroll: true);
		compositeView = CompositeView(window, rectCompositeView);
		viewSizes = [];
		viewSizes = viewSizes.add([window, rectWindow]);
		viewSizes = viewSizes.add([compositeView, rectCompositeView]);
	}

	makeWindow {

	}


	resizePannel {arg factor;
		var factorH, factorW;
		if ((factor * zoomWLevel * viewSizes[0][1s].width) > (Window.availableBounds.width * 1), {
			factorW = (Window.availableBounds.width * 1) /  (viewSizes[0][1].width);
		}, {factorW = factor * zoomWLevel});
		if ((factor * zoomHLevel * viewSizes[0][1].height) > (Window.availableBounds.height * 1), {
			factorH = (Window.availableBounds.height * 1) / (viewSizes[0][1].height);
		}, {factorH = factorW});

		viewSizes.do({|v|
			if (v[0].class === Window, {
				v[0].bounds = Rect(
					left: v[0].bounds.left,
					top: v[0].bounds.top,
					width: v[1].width * factorW,
					height: v[1].height * factorH,
				)
			}, {
				v[0].bounds = Rect(
					left: v[1].left * factorW,
					top: v[1].top * factorW,
					width: v[1].width * factorW,
					height: v[1].height * factorW,
				)
			})
		});
		zoomWLevel = factorW;
		zoomHLevel = factorH;
	}
}