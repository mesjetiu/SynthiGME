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

SGME_GUIPanel4 : SGME_GUIPanel {
	makeWindow {
		var rect;
		var image;
		var left, top;
		id = 3;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/(3/4),
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 4";
		window.bounds = rect;
		image = Image(imagesPath +/+ "panels" +/+ "panel_4");
		compositeView.setBackgroundImage(image,10);

		// Se crean los módulos
		// Envelope Followers
		left = 57;
		top = 329;
		this.makeEnvolopeFollowers(compositeView, left, top);

		// Keyboards controls
		left = 112;
		top = 258;
		this.makeKeyboard(compositeView, left, top, 1);
		left = 166;
		this.makeKeyboard(compositeView, left, top, 2);

		// Slew Limiters
		left = 221.3;
		top = 409;
		this.makeSlewLimiters(compositeView, left, top);

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
		window.front;
	}


	makeEnvolopeFollowers{|parent, left, top|
		var size = 35;
		var rect;
		var range1, range2;

		rect = Rect(left, top, size, size);
		range1 = SGME_Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([range1, rect]);

		top = top + 72;
		rect = Rect(left, top, size, size);
		range2 = SGME_Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([range2, rect]);

		// Se añaden al diccionario todos sendos mandos de Envelope Followers para poder cambiar su valor.
		parameterViews
		.put("/envFollower/" ++ 1 ++ "/range", range1)
		.put("/envFollower/" ++ 1 ++ "/range", range2);


		// Acciones a realizar al cambiar manualmente el valor de cada mando
		range1.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/envFollower/" ++ 1 ++ "/range",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		range2.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/envFollower/" ++ 2 ++ "/range",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
	}

	makeKeyboard{|parent, left, top, n|
		var size = 35;
		var rect;
		var pitch, velocity, env;
		var space = 48;

		rect = Rect(left, top, size, size);
		pitch = SGME_Knob(parent, rect)
		.color_([black, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.9);
		viewSizes = viewSizes.add([pitch, rect]);

		top = top + space;
		rect = Rect(left, top, size, size);
		velocity = SGME_Knob(parent, rect)
		.color_([yellow, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([velocity, rect]);

		top = top + space;
		rect = Rect(left, top, size, size);
		env = SGME_Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([env, rect]);

		// Se añaden al diccionario todos sendos mandos de Envelope Followers para poder cambiar su valor.
		parameterViews
		.put("/keyboard/" ++ n ++ "/pitch", pitch)
		.put("/keyboard/" ++ n ++ "/velocity", velocity)
		.put("/keyboard/" ++ n ++ "/env", env);

		// Acciones a realizar al cambiar manualmente el valor de cada mando
		pitch.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/keyboard/" ++ n ++ "/pitch",
				value: knob.value.linlin(0,1,0,10),
				addrForbidden: \GUI,
			)
		};
		velocity.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/keyboard/" ++ n ++ "/velocity",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		env.action = {|knob|
			synthiGME.setParameterOSC(
				string: "/keyboard/" ++ n ++ "/env",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
	}


	makeSlewLimiters {|parent, left, top|
		var size = 35;
		var rect;
		var slewRate;

		3.do({|num|
			rect = Rect(left, top, size, size);
			slewRate = SGME_Knob(parent, rect)
			.color_([red, black, white, nil])
			.mode_(\vert)
			.step_(step);
			viewSizes = viewSizes.add([slewRate, rect]);

			// Se añaden al diccionario el mando de Slew Rate para poder cambiar su valor.
			parameterViews
			.put("/slew/" ++ (num+1) ++ "/rate", slewRate);

			// Acciones a realizar al cambiar manualmente el valor de cada mando
			slewRate.action = {|knob|
				synthiGME.setParameterOSC(
					string: "/slew/" ++ (num+1) ++ "/rate",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
			left = left + 55.2;
		});
	}

}