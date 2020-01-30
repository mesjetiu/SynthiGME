+ S100_GUI {
	makePannel2{|parent|
		var rect = Rect(
			0,
			0,
			rectWindow.width,
			rectWindow.height,
		);

		var imagePannel2 = Image(installedPath ++ "/classes/GUI/images/pannel_2.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel2,10);

		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);


		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({
			Routine({ // Cuando se hace un click...
				if (click == true, {

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
	}
}


