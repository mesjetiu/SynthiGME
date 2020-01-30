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

	var installedPath;

	*new {
		^super.new.init();
	}

	init {
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];
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