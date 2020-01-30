S100_GUIPannel5 : S100_GUIPannel {
	makeWindow {
		var rect = Rect(
			left: 0,
			top: 0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		var image;
		var left, top, spacing;
		window.name = "Panel 5";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		compositeView.setBackgroundImage(image,10).background_(whiteBackground);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 1.5;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePannel(factor)},
					1, {this.resizePannel(1/factor)},
				)
			})
		});

		// Se crean los nodos (botones)
		left = 56.2;
		top = 55;
		spacing = 6.4;

		60.do({|row|
			this.makeRow(compositeView, left, top, row);
			top = top + spacing;
		});

		window.front;
	}


	makeRow {|parent, left, top, row|
		var spacing = 5.85;
		66.do({|column|
			this.makeNode(parent, left, top, column, row);
			left = left + spacing;
		})
	}

	makeNode {|parent, left, top, column, row|
		var side = 5;
		var bounds = Rect(left, top, side, side);
		var button = Button(parent, bounds).
		states_([
			[nil, nil, Color.black], // value 0
			[nil, nil, Color.red] // value 1
		]). action_({ arg butt;
			butt.value.postln;
		});

		// Se añaden el view node y sus bound por defecto para resize
		viewSizes = viewSizes ++ [
			[button, bounds]
		];
	}
}








