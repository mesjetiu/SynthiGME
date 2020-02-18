S100_GUIPanel3 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		var left, top, spacing;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/2,
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 3";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panel_3.png");
		compositeView.setBackgroundImage(image,10);

		// Los 6 osciladores de la izquierda
		left = 24.5;
		top = 75.5;
		spacing = 58;

		6.do({|num|
			this.makeOscillator(compositeView, left, top, num + 1);
			top = top + spacing;
		});

		// Los 6 osciladores de la derecha
		left = 239;
		top = 75.5;
		6.do({|num|
			this.makeOscillator(compositeView, left, top, num + 7);
			top = top + spacing;
		});

		// Los 3 Noise generators
		left = 32.2;
		top = 411.5;
		this.makeNoiseGenerators(compositeView, left, top);


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

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		window.front;
	}

	makeOscillator {|parent, left, top, num|
		var size = 27;
		var spacing = 31;
		var rect;
		var pulseLevel, pulseShape, sineLevel, sineSymmetry, triangleLevel, sawtoothLevel, frequency, range;

		rect = Rect(left, top, size, size);
		pulseLevel = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([pulseLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		pulseShape = Knob(parent, rect)
		.color_([green, black, white, nil])
		.mode_(\horiz).step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([pulseShape, rect]);

		rect = Rect(left + (spacing-7), top - 27, 10, 15);
		range = Button(parent, rect)
		.states_([
			[nil, nil, Color.black], // value 0
			[nil, nil, Color.red] // value 1
		]).
		value_(1);
		viewSizes = viewSizes.add([range, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sineLevel = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([sineLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sineSymmetry = Knob(parent, rect)
		.color_([green, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([sineSymmetry, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		triangleLevel = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([triangleLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sawtoothLevel = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		viewSizes = viewSizes.add([sawtoothLevel, rect]);

		left = left + 26.4;
		rect = Rect(left, top-15, size, size);
		frequency = Knob(parent, rect)
		.color_([black, black, white, nil])
		.mode_(\horiz)
		.step_(step).
		value_(0.5);
		viewSizes = viewSizes.add([frequency, rect]);


		// Se añaden al diccionario todos los mandos del oscilador para poder cambiar su valor.
		parameterViews
		.put("/osc/" ++ num ++ "/pulse/" ++ "level", pulseLevel)
		.put("/osc/" ++ num ++ "/pulse/" ++ "shape", pulseShape)
		.put("/osc/" ++ num ++ "/sine/" ++ "level", sineLevel)
		.put("/osc/" ++ num ++ "/sine/" ++ "symmetry", sineSymmetry)
		.put("/osc/" ++ num ++ "/triangle/" ++ "level", triangleLevel)
		.put("/osc/" ++ num ++ "/sawtooth/" ++ "level", sawtoothLevel)
		.put("/osc/" ++ num ++ "/frequency", frequency)
		.put("/osc/" ++ num ++ "/range", range);

		// Acciones a realizar al cambiar manualmente el valor de cada mando
		pulseLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/pulse/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		pulseShape.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/pulse/" ++ "shape",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		sineLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/sine/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sineSymmetry.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/sine/" ++ "symmetry",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		triangleLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/triangle/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sawtoothLevel.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/sawtooth/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		frequency.action = {|knob|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/frequency",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		range.action = {|button|
			synthi100.setParameterOSC(
				string: "/osc/" ++ num ++ "/range",
				value: button.value,
				addrForbidden: \GUI,
			)
		};
	}



	makeNoiseGenerators {|parent, left, top|
		var size = 35;
		var spacing = 46;
		var rect;
		var colour, level;

		2.do({|n|
			var num = n + 1;

			rect = Rect(left, top, size, size);
			colour = Knob(parent, rect)
			.color_([blue, black, white, nil])
			.mode_(\horiz)
			.step_(step)
			.value_(0.5);
			viewSizes = viewSizes.add([colour, rect]);

			left = left + spacing;
			rect = Rect(left, top, size, size);
			level = Knob(parent, rect)
			.color_([white, black, white, nil])
			.mode_(\horiz)
			.step_(step);
			viewSizes = viewSizes.add([level, rect]);

			left = left + spacing + 5.2;


			// Se añaden al diccionario todos los mandos del Noise Generator para poder cambiar su valor.
			parameterViews
			.put("/noise/" ++ num ++ "/colour", colour)
			.put("/noise/" ++ num ++ "/level", level);

			// Acciones de los knobs
			colour.action = {|knob|
				synthi100.setParameterOSC(
					string: "/noise/" ++ num ++ "/colour",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};

			level.action = {|knob|
				synthi100.setParameterOSC(
					string: "/noise/" ++ num ++ "/level",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
		})
	}
}