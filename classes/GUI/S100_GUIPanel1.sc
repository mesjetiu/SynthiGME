S100_GUIPanel1 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		super.makeWindow;
		rect = Rect(
			left: 0,
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 1";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panel_1.png");
		compositeView.setBackgroundImage(image,10);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 1.5;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePanel(factor)},
					1, {this.resizePanel(1/factor)},
				)
			})
		});

		this.makeEnvelopes(compositeView, 42, 242, 60);

		window.front;
	}

	makeEnvelopes {|parent, left, top, spacing|
		var envelopeNum = 1;
		3.do({
			this.makeEnvelope(parent, left, top, envelopeNum);
			envelopeNum = envelopeNum + 1;
			top = top + spacing;
		})
	}

	makeEnvelope {|parent, left, top, num|
		var size = 27;
		var spacing = 53.4;
		var rect;
		var selector, delay, attack, decay, sustain, release, envelopeLevel, signalLevel;

		rect = Rect(left, top, size, size);
		selector = Knob(parent, rect)
		.color_([yellow, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([selector, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		delay = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([delay, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		attack = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([attack, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		decay = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([decay, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sustain = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([sustain, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		release = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([release, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		envelopeLevel = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([envelopeLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		signalLevel = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([signalLevel, rect]);

		// Se añaden al diccionario todos los mandos del Envelope shaper para poder cambiar su valor.
		parameterViews
		.put("/env/" ++ num ++ "/delay", delay)
		.put("/env/" ++ num ++ "/attack", attack)
		.put("/env/" ++ num ++ "/decay", decay)
		.put("/env/" ++ num ++ "/sustain", sustain)
		.put("/env/" ++ num ++ "/release", release)
		.put("/env/" ++ num ++ "/envelopeLevel", envelopeLevel)
		.put("/env/" ++ num ++ "/signalLevel", signalLevel);

		// Acciones a realizar al cambiar manualmente el valor de cada mando
		delay.action = {|knob|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/delay",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		attack.action = {|knob|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/attack",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		decay.action = {|knob|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/decay",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sustain.action = {|knob|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/sustain",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		release.action = {|knob|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/release",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		envelopeLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/envelopeLevel",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		signalLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/signalLevel",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
	}
}