S100_GUIPanel6 : S100_GUIPanel {
	makeWindow {
		var rect;
		var image;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/2,
			top: 0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 6";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panel_6.png");
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

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
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
		var stringOSC = "/patchV/" ++ nodeCountHor ++ "/" ++ nodeCountVer;
		var side = 5;
		var bounds = Rect(left, top, side, side);

		var node = S100_GUINode(synthi100, parent, bounds, stringOSC);


		// Se añaden al diccionario cada uno de los nodos para poder cambiar su valor. /patchC/91/36
		parameterViews.put(stringOSC, node);

		// Se añaden el view node y sus bound por defecto para resize
		viewSizes = viewSizes ++ [
			[node, bounds]
		];
	}
}