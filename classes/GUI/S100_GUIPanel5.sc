S100_GUIPanel5 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		super.makeWindow;
		rect = Rect(
			left: 0,
			top: 0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 5";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panel_5.png");
		compositeView
		.setBackgroundImage(image,10)
		.background_(whiteBackground);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 1.5;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePanel(factor)},
					1, {this.resizePanel(1/factor)},
				)
			})
		});

		this.makeNodeTable;

		window.front;
	}

	makeNodeTable {
		// Se crean los nodos (botones)
		var left = 56.2;
		var top = 55;
		var spacing = 6.1;
		var nodeCountHor = 67;

		63.do({|row|
			if((row < 30).or(row > 32), {
				this.makeRow(compositeView, left, top, row, nodeCountHor);
				nodeCountHor = nodeCountHor + 1;
			});
			top = top + spacing;
		});
	}


	makeRow {|parent, left, top, row, nodeCountHor|
		var nodeCountVer = 1;
		var spacing = 5.75;
		67.do({|column|
			if(column != 33, {
				this.makeNode(parent, left, top, column, row, nodeCountHor, nodeCountVer);
				nodeCountVer = nodeCountVer + 1;
			});
			left = left + spacing;
		})
	}

	makeNode {|parent, left, top, column, row, nodeCountHor, nodeCountVer|
		var stringOSC = "/patchA/" ++ nodeCountHor ++ "/" ++ nodeCountVer;
		var side = 5;
		var bounds = Rect(left, top, side, side);

		var node = S100_GUINode(synthi100, parent, bounds, stringOSC);


		/*
		var button = Button(parent, bounds)
		.setBackgroundImage(image, 10)
		.states_([
		[nil, nil, Color.black], // value 0
		[nil, nil, Color.red] // value 1
		]).action_({ arg butt;
		synthi100.setParameterOSC(
		string: stringOSC,
		value: butt.value,
		addrForbidden: \GUI,
		)
		});

		*/
		// Se añaden al diccionario cada uno de los nodos para poder cambiar su valor. /patchA/91/36
		parameterViews.put(stringOSC, node);

		// Se añaden el view node y sus bound por defecto para resize
		viewSizes = viewSizes ++ [
			[node, bounds]
		];
	}
}




S100_GUINode {
	var view;
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



