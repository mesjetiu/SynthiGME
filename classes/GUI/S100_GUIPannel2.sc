S100_GUIPannel2 : S100_GUIPannel {
	makeWindow {
		var rect = Rect(
			left: Window.availableBounds.width/4,
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		var image;
		window.name = "Panel 2";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/pannel_2.png");
		compositeView.setBackgroundImage(image,10);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 1.5;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePannel(factor)},
					1, {this.resizePannel(1/factor)},
				)
			})
		});
		window.front;
	}
}