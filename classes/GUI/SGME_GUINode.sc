SGME_GUINode {
	var <view;
	var value;
	var image1, image2;
	var installedPath;
	var synthiGME;

	*new {arg synthi, parent, bounds, stringOSC;
		^super.new.init(synthi, parent, bounds, stringOSC);
	}

	init {|synthi, parent, bounds, stringOSC|
		synthiGME = synthi;
		installedPath = Quarks.installedPaths.select({|path| "SynthiGME".matchRegexp(path)})[0];
		image1 = Image(installedPath ++ "/classes/GUI/images/widgets/patchbay_hole.png");
		image2 = Image(installedPath ++ "/classes/GUI/images/widgets/patchbay_white_pin.png");
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
					synthiGME.setParameterOSC(
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

	enable_ {|option| // habilita y deshabilita el nodo
		view.enabled = option;
		if (option==false, {
			if(value==1, {
				view.setBackgroundImage(image2, 10, alpha: 0.3);
			}, {
				view.setBackgroundImage(image1, 10, alpha: 0.3);
			})
		}, {
			if(value==1, {
				view.setBackgroundImage(image2, 10, alpha: 1);
			}, {
				view.setBackgroundImage(image1, 10, alpha: 1);
			})
		})
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