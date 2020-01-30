+ S100_GUI {
	makePannel3 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 2,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel3 = Image(installedPath ++ "/classes/GUI/images/pannel_3.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel3,10);

		// Los 6 osciladores de la izquierda
		var left, top, spacing;
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

		compositeView.mouseDownAction_({
			Routine({ // Cuando se hace un click...
				if (click == true, {
					// Cuando se hace doble click...
					if(window.bounds.width == widthRealScreen, {
						// this.zoomPannel3;
						this.openPannel3;
					},{
						this.resetSize;
					})
				}, {
					click = true;
					wait(timeDoubleClick); // tiempo de espera del segundo click
				});
				click = false; // una vez transcurrida la espera, se vuelve false
			}).play(AppClock);
		});
		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
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
		defaultSizes = defaultSizes ++ [
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

	zoomPannel3 {
		var factor, left, top;
		factor = 9/4;
	//	factor = factor * 2; // Para pruebas
		top = ((9/16) * widthRealScreen) - (((9/16) * widthRealScreen) * factor);
		left = -1 * (((((widthRealScreen * factor)  -  widthRealScreen))/2) + (widthRealScreen/8 * factor));
		//this.resize(factor, left: left, top: top);
		this.resize2(factor, left: left, top: top);
	}

	openPannel3{
	}
}