/*
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_GUIPanel3 : SGME_GUIPanel {
	makeWindow {
		var rect;
		var image;
		var left, top, spacing;
		id = 2;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/2,
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 3";
		window.bounds = rect;
		image = Image(imagesPath +/+ "panels" +/+ "panel_3");
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

		// Los 2 Noise generators
		left = 32.2;
		top = 411.5;
		this.makeNoiseGenerators(compositeView, left, top);

		// El Random voltage generator
		left = 228;
		top = 411.5;
		this.makeRandomGenerator(compositeView, left, top);


		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
		window.front;
	}

	makeOscillator {|parent, left, top, num|
		var size = 27;
		var spacing = 31;
		var rect;
		var pulseLevel, pulseShape, sineLevel, sineSymmetry, triangleLevel, sawtoothLevel, frequency, range;

		rect = Rect(left, top, size, size);
		pulseLevel = SGME_Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([pulseLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		pulseShape = SGME_Knob(parent, rect, -5, 5)
		.color_([green, black, white, nil])
		.mode_(\vert).step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([pulseShape, rect]);

		rect = Rect(left + (spacing-7), top - 27, 10, 15);
		range = SGME_Button(parent, rect, funcParam: {|v| if (v==1) {"Hi Freq"} {"Low Freq"} })
		.states_([
			["Lo", Color.white, Color.black], // value 0
			["Hi", Color.white, Color.red] // value 1
		]).
		value_(1);
		viewSizes = viewSizes.add([range, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sineLevel = SGME_Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([sineLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sineSymmetry = SGME_Knob(parent, rect, -5, 5)
		.color_([green, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([sineSymmetry, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		triangleLevel = SGME_Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([triangleLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sawtoothLevel = SGME_Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([sawtoothLevel, rect]);

		left = left + 26.4;
		rect = Rect(left, top-15, size, size);
		frequency = SGME_Knob(parent, rect)
		.color_([black, black, white, nil])
		.mode_(\vert)
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
			synthiGME.setParameterOSC(
				string: "/osc/" ++ num ++ "/pulse/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		pulseShape.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/osc/" ++ num ++ "/pulse/" ++ "shape",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		sineLevel.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/osc/" ++ num ++ "/sine/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sineSymmetry.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/osc/" ++ num ++ "/sine/" ++ "symmetry",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		triangleLevel.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/osc/" ++ num ++ "/triangle/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sawtoothLevel.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/osc/" ++ num ++ "/sawtooth/" ++ "level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		frequency.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/osc/" ++ num ++ "/frequency",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		range.action = {|button|
			synthiGME.setParameterOSC(
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
			colour = SGME_Knob(parent, rect)
			.color_([blue, black, white, nil])
			.mode_(\vert)
			.step_(step)
			.value_(0.5);
			viewSizes = viewSizes.add([colour, rect]);

			left = left + spacing;
			rect = Rect(left, top, size, size);
			level = SGME_Knob(parent, rect)
			.color_([white, black, white, nil])
			.mode_(\vert)
			.step_(step);
			viewSizes = viewSizes.add([level, rect]);

			left = left + spacing + 5.2;


			// Se añaden al diccionario todos los mandos del Noise Generator para poder cambiar su valor.
			parameterViews
			.put("/noise/" ++ num ++ "/colour", colour)
			.put("/noise/" ++ num ++ "/level", level);

			// Acciones de los knobs
			colour.action = {|knob|
				synthiGME.setParameterOSC(
					string: "/noise/" ++ num ++ "/colour",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};

			level.action = {|knob|
				synthiGME.setParameterOSC(
					string: "/noise/" ++ num ++ "/level",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
		})
	}

	makeRandomGenerator {|parent, left, top|
		var size = 35;
		var spacing = 45;
		var rect;
		var mean, variance, voltage1, voltage2, key;

		rect = Rect(left, top, size, size);
		mean = SGME_Knob(parent, rect, -5, 5)
		.color_([red, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([mean, rect]);

		left = left + spacing;

		rect = Rect(left, top, size, size);
		variance = SGME_Knob(parent, rect, -5, 5)
		.color_([red, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([variance, rect]);

		left = left + spacing;

		rect = Rect(left, top, size, size);
		voltage1 = SGME_Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([voltage1, rect]);
		left = left + spacing;

		rect = Rect(left, top, size, size);
		voltage2 = SGME_Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([voltage2, rect]);

		left = left + spacing;

		rect = Rect(left, top, size, size);
		key = SGME_Knob(parent, rect, -5, 5)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([key, rect]);

		// Se añaden al diccionario todos los mandos del Random Voltage Generator para poder cambiar su valor.
		parameterViews
		.put("/random" ++ "/mean", mean)
		.put("/random" ++ "/variance", variance)
		.put("/random" ++ "/voltage1", voltage1)
		.put("/random" ++ "/voltage2", voltage2)
		.put("/random" ++ "/key", key);

		// Acciones de los knobs
		mean.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/random" ++ "/mean",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};

		variance.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/random" ++ "/variance",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};


		voltage1.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/random" ++ "/voltage1",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		voltage2.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/random" ++ "/voltage2",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		key.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/random" ++ "/key",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
	}
}