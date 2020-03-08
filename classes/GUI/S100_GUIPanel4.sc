S100_GUIPanel4 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		var left, top;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/(3/4),
			top: Window.availableBounds.height/1.9,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 4";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_4.png");
		compositeView.setBackgroundImage(image,10);

		// Se crean los módulos
		left = 57;
		top = 329;
		// Envelope Followers
		this.makeEnvolopeFollowers(compositeView, left, top);

		// Slew Limiters
		left = left + 164.3;
		top = 329 + 80;
		this.makeSlewLimiters(compositeView, left, top);



		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
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


	makeEnvolopeFollowers{|parent, left, top|
		var size = 35;
		var rect;
		var range1, range2;

		rect = Rect(left, top, size, size);
		range1 = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
		.step_(step)
		.centered_(true)
		.value_(0.5);
		viewSizes = viewSizes.add([range1, rect]);

		top = top + 72;
		rect = Rect(left, top, size, size);
		range2 = Knob(parent, rect)
		.color_([white, black, white, nil])
		.mode_(\horiz)
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
			synthi100.setParameterOSC(
				string: "/envFollower/" ++ 1 ++ "/range",
				value: knob.value.linlin(0,1,-5,5),
				addrForbidden: \GUI,
			)
		};
		range2.action = {|knob|
			synthi100.setParameterOSC(
				string: "/envFollower/" ++ 2 ++ "/range",
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
			slewRate = Knob(parent, rect)
			.color_([red, black, white, nil])
			.mode_(\horiz)
			.step_(step);
			viewSizes = viewSizes.add([slewRate, rect]);

			// Se añaden al diccionario el mando de Slew Rate para poder cambiar su valor.
			parameterViews
			.put("/slew/" ++ num+1 ++ "/rate", slewRate);

			// Acciones a realizar al cambiar manualmente el valor de cada mando
			slewRate.action = {|knob|
				synthi100.setParameterOSC(
					string: "/slew/" ++ num+1 ++ "/rate",
					value: knob.value.linlin(0,1,0,10),
					addrForbidden: \GUI,
				)
			};
			left = left + 55.2;
		});
	}

}