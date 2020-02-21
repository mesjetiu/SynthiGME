S100_GUINode {
	var <view;
	var value;
	var image1, image2;
	var installedPath;
	var synthi100;

	*new {arg synthi, parent, bounds, stringOSC;
		^super.new.init(synthi, parent, bounds, stringOSC);
	}

	init {|synthi, parent, bounds, stringOSC|
		synthi100 = synthi;
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];
		image1 = Image(installedPath ++ "/classes/GUI/images/patchbay_hole.png");
		image2 = Image(installedPath ++ "/classes/GUI/images/patchbay_white_pin.png");
		value = 0;
		view = View(parent, bounds)
		.setBackgroundImage(image1, 10)
		.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			buttonNumber.switch(
				0, {
					if(value==1, {
						value=0;
						view.setBackgroundImage(image1, 10);
					}, {
						value=1;
						view.setBackgroundImage(image2, 10);
					});
					synthi100.setParameterOSC(
						string: stringOSC,
						value: value,
						addrForbidden: \GUI,
					);

				}, // click izquierdo
				1, {"der".postln}, // click derecho
			)
		});
	}

	visible_ {|option|
		view.visible = option;
	}

	bounds {
		^view.bounds;
	}

	bounds_ {|bounds|
		view.bounds = bounds;
	}

	value_ {|val|
		if(value == val, {^this});
		value = val;
		if(value==1, {
			view.setBackgroundImage(image1, 10);
		}, {
			view.setBackgroundImage(image2, 10);
		})
	}
}