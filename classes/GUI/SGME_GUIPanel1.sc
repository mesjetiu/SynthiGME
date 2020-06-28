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

Copyright 2020 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_GUIPanel1 : SGME_GUIPanel {

	// Valores de las opciones de selector: 1 = 0.293; 2 = 0.405; 3 = 0.5; 4 = 0.59; 5 = 0.691
	*selectorValuesConvert {|value|
		value.switch(
			1, {^0.293},
			2, {^0.405},
			3, {^0.5},
			4, {^0.59},
			5, {^0.691},
		);
	}


	makeWindow {
		var rect;
		var image;
		id = 0;
		super.makeWindow;
		rect = Rect(
			left: 0,
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 1";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_1.png");
		compositeView.setBackgroundImage(image,10);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePanel(factor)}, // botón izquierdo
					1, {this.resizePanel(1/factor)}, // botón derecho
				)
			}, { // si se hace un solo click...
				buttonNumber.switch(
					0, {}, // botón izquierdo
					1, {
						Menu(
							MenuAction("Salir (Ctrl+C)", { synthiGME.close }),
							MenuAction("Zoom In", { this.resizePanel(factor) }),
							MenuAction("Zoom Out", { this.resizePanel(1/factor) }),
						).front;
					}, // botón derecho
				)
			}
			)
		});

		this.makeFilters(compositeView, 38, 80, 53.4);
		this.makeEnvelopes(compositeView, 38, 238, 59.7);
		this.makeRingModulators(compositeView, 41.5, 417);
		this.makeEcho(compositeView, 200, 417);

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
		window.front;
	}

	makeFilters {|parent, left, top, spacing|
		8.do({|num|
			this.makeFilter(parent, left, top, num+1);
			left = left + spacing;
		})
	}

	makeFilter {|parent, left, top, num|
		var size = 35;
		var spacing = 50;
		var rect;
		var frequency, response, level;

		rect = Rect(left, top, size, size);
		frequency = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([frequency, rect]);

		top = top + spacing;
		rect = Rect(left, top, size, size);
		response = Knob(parent, rect)
		.color_([yellow, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([response, rect]);

		top = top + spacing;
		rect = Rect(left, top, size, size);
		level = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([level, rect]);

		// Se añaden al diccionario todos los mandos para poder cambiar su valor.
		parameterViews
		.put("/filter/" ++ num ++ "/frequency", frequency)
		.put("/filter/" ++ num ++ "/response", response)
		.put("/filter/" ++ num ++ "/level", level);

		// Acciones de los knobs

		frequency.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/filter/" ++ num ++ "/frequency",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		response.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/filter/" ++ num ++ "/response",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		level.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/filter/" ++ num ++ "/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
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
		.mode_(\vert)
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
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([delay, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		attack = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([attack, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		decay = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([decay, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		sustain = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([sustain, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		release = Knob(parent, rect)
		.color_([red, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([release, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		envelopeLevel = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([envelopeLevel, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		signalLevel = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
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
			selector.value = SGME_GUIPanel1.selectorValuesConvert(value);
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		freeRun.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 2;
			selector.value = SGME_GUIPanel1.selectorValuesConvert(value);
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		gated.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 3;
			selector.value = SGME_GUIPanel1.selectorValuesConvert(value);
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		triggered.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 4;
			selector.value = SGME_GUIPanel1.selectorValuesConvert(value);
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});

		hold.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var value = 5;
			selector.value = SGME_GUIPanel1.selectorValuesConvert(value);
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/selector",
				value: value,
				addrForbidden: \GUI,
			);
		});



		gate.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/gate",
				value: 1,
				addrForbidden: \GUI,
			)
		});

		gate.mouseUpAction_({|view, x, y, modifiers|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/gate",
				value: 0,
				addrForbidden: \GUI,
			)
		});


		delay.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/delay",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		attack.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/attack",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		decay.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/decay",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		sustain.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/sustain",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		release.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/release",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		envelopeLevel.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/env/" ++ num ++ "/envelopeLevel",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		signalLevel.action = {|knob|
			synthiGME.setParameterOSC(
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
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([ring1, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		ring2 = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([ring2, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		ring3 = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([ring3, rect]);


		// Se añaden al diccionario todos los mandos del Ring Modulator para poder cambiar su valor.
		parameterViews
		.put("/ring/1/level", ring1)
		.put("/ring/2/level", ring2)
		.put("/ring/3/level", ring3);

		// Acciones de los knobs
		ring1.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/ring/1/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		ring2.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/ring/2/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		ring3.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/ring/3/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
	}

	makeEcho {|parent, left, top|
		var size = 35;
		var spacing = 38.5;
		var rect;
		var delay, mix, feedback, level;

		left = 301.5;
		top = 417;

		rect = Rect(left, top, size, size);
		delay = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([delay, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		mix = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([mix, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		feedback = Knob(parent, rect)
		.color_([blue, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([feedback, rect]);

		left = left + spacing;
		rect = Rect(left, top, size, size);
		level = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step);
		viewSizes = viewSizes.add([level, rect]);

		// Se añaden al diccionario todos los mandos.
		parameterViews
		.put("/echo/delay", delay)
		.put("/echo/mix", mix)
		.put("/echo/feedback", feedback)
		.put("/echo/level", level);

		// Acciones de los knobs
		delay.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/echo/delay",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		mix.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/echo/mix",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		feedback.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/echo/feedback",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

		level.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/echo/level",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};

	}
}