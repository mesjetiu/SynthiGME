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

SGME_GUIPanel7 : SGME_GUIPanel {
	makeWindow {
		var rect;
		var image;
		id = 6;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/(3/4),
			top: (Window.availableBounds.height/2.1)
			-(window.bounds.height
				* (Window.availableBounds.width/virtualWidth)),//0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 7";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_7.jpg");
		compositeView.setBackgroundImage(image,10);

		this.makeChannels;

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
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
		.mode_(\vert)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([filter, rect]);

		top = top + 48;
		rect = Rect(left, top, size, size);
		pan = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\vert)
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