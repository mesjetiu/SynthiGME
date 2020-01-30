+ S100_GUI {
	makePannel1 {
		var rect, imagePannel1, compositeView;

		rect = Rect(
			0,
			0,
			rectWindow.width,
			rectWindow.height,
		);

		windowPannel1 =  Window("Pannel 1", rectWindow, false, true, scroll: true);

		imagePannel1 = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		compositeView = CompositeView(windowPannel1, rect).setBackgroundImage(imagePannel1,10);
		viewSizesPannel1 = [];

		windowPannel1.background = blackForniture;

		viewSizesPannel1 = viewSizesPannel1.add([windowPannel1, rectWindow]);
		//defaultSizes = defaultSizes.add([window, window.bounds]);
		viewSizesPannel1 = viewSizesPannel1.add([compositeView, rectWindow]);


		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({
			Routine({ // Cuando se hace un click...
				if (click == true, {
					var factorH = 1.5;
					var factorW = 1.5;
					if ((factorH * windowPannel1.bounds.height) > (Window.availableBounds.height * 0.8), {
						factorH = (Window.availableBounds.height * 0.8) /  windowPannel1.bounds.height;
					});
					if ((factorW * windowPannel1.bounds.width) > (Window.availableBounds.width * 1), {
						factorW = (Window.availableBounds.width * 1) /  windowPannel1.bounds.width;
					});
					this.resizePannel(viewSizesPannel1, factorH, factorW, zoomLevelPannel1);

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

		this.resizePannel(viewSizesPannel1, 0.5, 0.5, zoomLevelPannel1);

		windowPannel1.front;
	}
}