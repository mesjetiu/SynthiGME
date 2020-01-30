S100_GUIPannel6 : S100_GUIPannel {
	makeWindow {
		var image;
		window.name = "Panel 6";
		image = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		compositeView.setBackgroundImage(image,10).background_(whiteBackground);
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