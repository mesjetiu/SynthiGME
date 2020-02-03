S100_GUIPanel7 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/(3/4),
			top: 0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 7";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panel_7.png");
		compositeView.setBackgroundImage(image,10);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 1.5;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePanel(factor)},
					1, {this.resizePanel(1/factor)},
				)
			}, {[x,y].postln})
		});

		this.makeChannel(compositeView, 49, 239, nil);
		window.front;

		Window
	}

	makeChannel{|parent, left, top, num|
		var size = 35;
		var rect;
		var filter, pan, off, level;

		rect = Rect(left, top, size, size);
		filter = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([filter, rect]);

		top = top + 48;
		rect = Rect(left, top, size, size);
		pan = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([pan, rect]);

		top = top + 43;
		rect = Rect(left + 13, top, 10, 15);
		off = Button(parent, rect)
		.states_([
			[nil, nil, Color.black], // value 0
			[nil, nil, Color.red] // value 1
		]).
		value_(1);
		viewSizes = viewSizes.add([off, rect]);
	}
}