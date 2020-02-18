S100_GUIPanel1 : S100_GUIPanel {

	// Valores de las opciones de selector: 1 = 0.293; 2 = 0.405; 3 = 0.5; 4 = 0.59; 5 = 0.691
	*selectorValuesConvert {|value|
		value.switch(
			1, {^0.293},
			2, {^0.405},
			3, {^0.5},
			4, {^0.59},
			5, {^ 0.691},
		);
	}


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

		this.makeEnvelopes(compositeView, 38, 238, 59.7);
		this.makeRingModulators(compositeView, 41.5, 417);

		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
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
		var size = 35;
		var spacing = 53.4;
		var rect;
		var selector, gate, delay, attack, decay, sustain, release, envelopeLevel, signalLevel;
		var gatedFR, freeRun, gated, triggered, hold; // Opciones del selector

		rect = Rect(left, top, size, size);
		selector = Knob(parent, rect)
		.color_([yellow, white, white, nil])
		.mode_(\horiz)
		.step_(step)
		.enabled_(false)
		.action_({|s| s.value.postln});
		viewSizes = viewSizes.add([selector, rect]);


		rect = Rect(left - 7, top + 4, 10, 5);
		gatedFR = View(parent, rect);
		viewSizes = viewSizes.add([gatedFR, rect]);

		rect = Rect(left - 2, top - 3, 10, 5);
		freeRun = View(parent, rect);
		viewSizes = viewSizes.add([freeRun, rect]);

		rect = Rect(left + 13, top - 5, 10, 5);
		gated = View(parent, rect);
		viewSizes = viewSizes.add([gated, rect]);

		rect = Rect(left + 27, top - 3, 19, 5);
		triggered = View(parent, rect);
		viewSizes = viewSizes.add([triggered, rect]);

		rect = Rect(left + 30, top + 4, 15, 5);
		hold = View(parent, rect);
		viewSizes = viewSizes.add([hold, rect]);


		rect = Rect(left + (spacing/2) + 9.5, top + 30, 12, 12);
		gate = Button(parent, rect).states_([
			[nil, nil, black], // único valor con color negro
		]);
		viewSizes = viewSizes.add([gate, rect]);

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
		.put("/env/" ++ num ++ "/signalLevel", signalLevel)
		.put("/env/" ++ num ++ "/selector", selector);

		// Acciones a realizar al cambiar manualmente el valor de cada mando

		gatedFR.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 1;
			selector.value = S100_GUIPanel1.selectorValuesConvert(value);
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		freeRun.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 2;
			selector.value = S100_GUIPanel1.selectorValuesConvert(value);
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		gated.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 3;
			selector.value = S100_GUIPanel1.selectorValuesConvert(value);
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		triggered.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 4;
			selector.value = S100_GUIPanel1.selectorValuesConvert(value);
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		hold.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 5;
			selector.value = S100_GUIPanel1.selectorValuesConvert(value);
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});



		gate.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/gate",
				value: 1,
				addrForbidden: \GUI,
			)
		});

		gate.mouseUpAction_({|view, x, y, modifiers|
			synthi100.setParameterOSC(
				string: "/env/" ++ num ++ "/gate",
				value: 0,
				addrForbidden: \GUI,
			)
		});


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

	makeRingModulators {|parent, left, top|
		var size = 35;
		var spacing = 61.3;
		var rect;
		var ring1, ring2, ring3;

		rect = Rect(left, top, size, size);
		ring1 = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([ring1, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		ring2 = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([ring2, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		ring3 = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([ring3, rect]);


		// Se añaden al diccionario todos los mandos del Ring Modulator para poder cambiar su valor.
		parameterViews
		.put("/ring/1/level", ring1)
		.put("/ring/2/level", ring2)
		.put("/ring/3/level", ring3);

		// Acciones de los knobs
		ring1.action = {|knob|
			synthi100.setParameterOSC(
				string: "/ring/1/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		ring2.action = {|knob|
			synthi100.setParameterOSC(
				string: "/ring/2/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		ring3.action = {|knob|
			synthi100.setParameterOSC(
				string: "/ring/3/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
	}
}