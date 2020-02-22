S100_GUIPanel4 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/(3/4),
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 4";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_4.png");
		compositeView.setBackgroundImage(image,10);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePanel(factor)},
					1, {this.resizePanel(1/factor)},
				)
			})
		});

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		window.front;
	}
}