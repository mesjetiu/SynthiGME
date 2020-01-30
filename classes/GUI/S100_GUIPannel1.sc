S100_GUIPannel1 : S100_GUIPannel {
	makeWindow {
		var image;
		window.name = "Panel 1";
		image = Image(installedPath ++ "/classes/GUI/images/pannel_1.png");
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