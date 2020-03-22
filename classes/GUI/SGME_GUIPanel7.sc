SGME_GUIPanel7 : SGME_GUIPanel {
	makeWindow {
		var rect;
		var image;
		id = 6;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/(3/4),
			top: 0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 7";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_7.png");
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

		this.makeChannels;

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		window.front;
	}

	makeChannels{
		var left = 49;
		var top = 239;
		var channelNum = 1;
		8.do({
			this.makeChannel(compositeView, left, top, channelNum);
			channelNum = channelNum + 1;
			left = left + 48.9;
		});
	}

	makeChannel{|parent, left, top, num|
		var size = 35;
		var rect;
		var filter, pan, on, level;

		rect = Rect(left, top, size, size);
		filter = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
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
		rect = Rect(left + 12.3, top, 10, 15);
		on = Button(parent, rect)
		.states_([
			[nil, nil, Color.black], // value 0
			[nil, nil, Color.red] // value 1
		]).
		value_(0);
		viewSizes = viewSizes.add([on, rect]);

		top = top + 40;
		rect = Rect(left + 10, top, 15, 85);
		level = Slider(parent, rect)
		.background_(white)
		.knobColor_(black); // no funciona, al menos en mi versión de SuperCollider (3.8.0)
		viewSizes = viewSizes.add([level, rect]);

		// Se añaden al diccionario todos los mandos del canal para poder cambiar su valor.
		parameterViews
		.put("/out/" ++ num ++ "/filter", filter)
		.put("/out/" ++ num ++ "/pan", pan)
		.put("/out/" ++ num ++ "/on", on)
		.put("/out/" ++ num ++ "/level", level);

		// Acciones a realizar al cambiar manualmente el valor de cada mando
		filter.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/out/" ++ num ++ "/filter",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		pan.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/out/" ++ num ++ "/pan",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		on.action = {|button|
			synthiGME.setParameterOSC(
				string: "/out/" ++ num ++ "/on",
				value: button.value,
				addrForbidden: \GUI,
			)
		};

		level.action = {|slider|
			synthiGME.setParameterOSC(
				string: "/out/" ++ num ++ "/level",
				value: slider.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
	}
}