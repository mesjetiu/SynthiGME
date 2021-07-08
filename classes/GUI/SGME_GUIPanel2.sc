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

SGME_GUIPanel2 : SGME_GUIPanel {
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
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_2.jpg");
		compositeView.setBackgroundImage(image,10);

		this.makeFilterBank(compositeView, 31.2, 312);
		this.makeInputAmplifiers(compositeView, 31.2, 367);
		this.makeExternalTreatmenDevices(compositeView, 31.2, 422);


		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
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
			.mode_(\vert)
			.step_(step);
			viewSizes = viewSizes.add([knob, rect]);
			parameterViews.put("/filterBank/" ++ (62.5*(2**(num))).ceil, knob); // Ejemplo: /filterBank/4000
			knob.action = {|knob|
				synthiGME.setParameterOSC(
					string:"/filterBank/" ++ (62.5*(2**(num))).ceil.asInt,
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
			.mode_(\vert)
			.step_(step);
			viewSizes = viewSizes.add([knob, rect]);
			parameterViews.put("/in/" ++ (num + 1) ++ "/level", knob);
			knob.action = {|knob|
				synthiGME.setParameterOSC(
					string: "/in/" ++ (num + 1) ++ "/level",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
			left = left + spacing;
		});
	}

	makeExternalTreatmenDevices {|parent, left, top|
		var size = 35;
		var spacing = 53.6;
		var rect;
		4.do({|num|
			var knob;
			rect = Rect(left, top, size, size);
			knob = Knob(parent, rect)
			.color_([white, black, white, nil])
			.mode_(\vert)
			.step_(step);
			viewSizes = viewSizes.add([knob, rect]);
			parameterViews.put("/send/" ++ (num + 1) ++ "/level", knob);
			knob.action = {|knob|
				synthiGME.setParameterOSC(
					string: "/send/" ++ (num + 1) ++ "/level",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
			left = left + spacing;
		});
		4.do({|num|
			var knob;
			rect = Rect(left, top, size, size);
			knob = Knob(parent, rect)
			.color_([white, black, white, nil])
			.mode_(\vert)
			.step_(step);
			viewSizes = viewSizes.add([knob, rect]);
			parameterViews.put("/return/" ++ (num + 1) ++ "/level", knob);
			knob.action = {|knob|
				synthiGME.setParameterOSC(
					string: "/return/" ++ (num + 1) ++ "/level",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
			left = left + spacing;
		});
	}
}