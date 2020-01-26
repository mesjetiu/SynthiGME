+ S100_GUI {
	makePannel5 {|parent|
		var rect = Rect(
			0,
			rectWindow.width/4,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel5 = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel5,10)
		.background_(whiteBackground);

		var left, top, spacing;
		left = 56.2;
		top = 55;
		spacing = 6.4;

		60.do({|row|
			this.makeRow(compositeView, left, top, row);
			top = top + spacing;
		});

		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
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
		defaultSizes = defaultSizes ++ [
			[button, button.bounds]
		];
	}
}