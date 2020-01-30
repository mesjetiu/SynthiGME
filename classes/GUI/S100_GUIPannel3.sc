S100_GUIPannel3 : S100_GUIPannel {
	makeWindow {
		var image;
		var left, top, spacing;
		window.name = "Panel 3";
		image = Image(installedPath ++ "/classes/GUI/images/pannel_3.png");
		compositeView.setBackgroundImage(image,10);

		// Los 6 osciladores de la izquierda
		left = 28;
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

	makeOscillator {|parent, left, top, num|
		var size = 27;
		var spacing = 30.4;
		var pulseLevel, pulseShape, sineLevel, sineSymmetry, triangleLevel, sawtoothLevel, frequency;
		pulseLevel = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		pulseShape = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil])
		.mode_(\horiz).step_(step)
		.centered_(true)
		.value_(0.5);
		left = left + spacing;
		sineLevel = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		sineSymmetry = Knob(parent, Rect(left, top, size, size))
		.color_([green, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		left = left + spacing;
		triangleLevel = Knob(parent, Rect(left, top, size, size))
		.color_([blue, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + spacing;
		sawtoothLevel = Knob(parent, Rect(left, top, size, size))
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step);
		left = left + 26.4;
		frequency = Knob(parent, Rect(left, top-17, size, size))
		.color_([black, black, white, nil])
		.mode_(\horiz)
		.step_(step);

		// Se añaden las views y sus bounds por defecto para resize
		viewSizes = viewSizes ++ [
			[pulseLevel, pulseLevel.bounds],
			[pulseShape, pulseShape.bounds],
			[sineLevel, sineLevel.bounds],
			[sineSymmetry, sineSymmetry.bounds],
			[triangleLevel, triangleLevel.bounds],
			[sawtoothLevel, sawtoothLevel.bounds],
			[frequency, frequency.bounds]
		];


		// Se añaden al diccionario todos los mandos del oscilador para poder cambiar su valor.
		parameterViews
		.put("/osc/" ++ num ++ "/pulse/" ++ "level", pulseLevel)
		.put("/osc/" ++ num ++ "/pulse/" ++ "shape", pulseShape)
		.put("/osc/" ++ num ++ "/sine/" ++ "level", sineLevel)
		.put("/osc/" ++ num ++ "/sine/" ++ "symmetry", sineSymmetry)
		.put("/osc/" ++ num ++ "/triangle/" ++ "level", triangleLevel)
		.put("/osc/" ++ num ++ "/sawtooth/" ++ "level", sawtoothLevel)
		.put("/osc/" ++ num ++ "/frequency", frequency);

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
	}



	makeNoiseGenerator {
	}

	makeRandomControlVoltageGenerator {
	}

}