S100_GUIPanel2 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		id = 1;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/4,
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 2";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_2.png");
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

		this.makeFilterBank(compositeView, 31.2, 312);

		this.makeInputAmplifiers(compositeView, 31.2, 367);

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		window.front;
	}

	makeFilterBank {|parent, left, top|
		var size = 35;
		var spacing = 53.6;
		var rect;
		8.do({|num|
			var knob;
			rect = Rect(left, top, size, size);
			knob = Knob(parent, rect)
			.color_([blue, black, white, nil])
			.mode_(\horiz)
			.step_(step);
			viewSizes = viewSizes.add([knob, rect]);
			parameterViews.put("/filterBank/" ++ (62.5*(2**(num))).ceil, knob); // Ejemplo: /filterBank/4000
			knob.action = {|knob|
				synthi100.setParameterOSC(
					string:"/filterBank/" ++ (62.5*(2**(num))).ceil,
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
			left = left + spacing;
		});
	}

	makeInputAmplifiers {|parent, left, top|
		var size = 35;
		var spacing = 53.6;
		var rect;
		8.do({|num|
			var knob;
			rect = Rect(left, top, size, size);
			knob = Knob(parent, rect)
			.color_([white, black, white, nil])
			.mode_(\horiz)
			.step_(step);
			viewSizes = viewSizes.add([knob, rect]);
			parameterViews.put("/in/" ++ (num + 1) ++ "/level", knob);
			knob.action = {|knob|
				synthi100.setParameterOSC(
					string: "/in/" ++ (num + 1) ++ "/level",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
			left = left + spacing;
		});
	}
}