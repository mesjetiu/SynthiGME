S100_GUIPannel {

	var window;
	var viewSizes;
	var zoomHLevel = 1;
	var zoomWLevel = 1;

	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;

	// Al hacer click sobre determinadas views se activa la rutina esperando un segundo click.
	var click;
	var timeDoubleClick = 0.5;

	var installedPath;

	*new {
		^super.new.init();
	}

	init {
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];
	}

	makeWindow {
		var rect, image, compositeView, rectWindow;

		rectWindow = Rect(0, 0,  1920/4,  1920/4);

		rect = Rect(
			0,
			0,
			rectWindow.width,
			rectWindow.height,
		);

		window =  Window("Pannel 1", rectWindow, false, true, scroll: true);

		image = Image(installedPath ++ "/classes/GUI/images/pannel_1.png");
		compositeView = CompositeView(window, rect).setBackgroundImage(image,10);
		viewSizes = [];

		//window.background = blackForniture;

		viewSizes = viewSizes.add([window, rectWindow]);
		//defaultSizes = defaultSizes.add([window, window.bounds]);
		viewSizes = viewSizes.add([compositeView, rect]);


		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({
			Routine({ // Cuando se hace un click...
				if (click == true, {
					var factor = 1.5;
					this.resizePannel(factor);

					// Cuando se hace doble click...
					/*if(window.bounds.width == widthRealScreen, {
					// this.zoomPannel3;
					this.openPannel3;
					},{
					this.resetSize;
					})*/
				}, {
					click = true;
					wait(timeDoubleClick); // tiempo de espera del segundo click
				});
				click = false; // una vez transcurrida la espera, se vuelve false
			}).play(AppClock);
		});

		//	compositeView.visible = false;

		this.resizePannel(0.5);

		window.front;
	}


	resizePannel {arg factor;
		//var factor = factorW * zoomLevelPannel;
		//step = step * factorW * zoomLevelPannel;
		var factorH, factorW;


		if ((factor * zoomHLevel * viewSizes[0][1].height) > (Window.availableBounds.height * 1), {
			factorH = (Window.availableBounds.height * 1) / (viewSizes[0][1].height);
		}, {factorH = factor * zoomHLevel});
		if ((factor * zoomWLevel * viewSizes[0][1s].width) > (Window.availableBounds.width * 1), {
			factorW = (Window.availableBounds.width * 1) /  (viewSizes[0][1].width);
		}, {factorW = factor * zoomWLevel});

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