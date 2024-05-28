SGME_GUIPanel6 : SGME_GUIPanelPatchbay {
	makeWindow {
		var rect;
		var image;
		id = 5;
		super.makeWindow;
		rect = Rect(
			left: Window.availableBounds.width/2,
			top: (Window.availableBounds.height/2.1)
			-(window.bounds.height
				* (Window.availableBounds.width/virtualWidth)),//0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 6";
		window.bounds = rect;
		image = Image(imagesPath +/+ "panels" +/+ "panel_6");
		compositeView
		.setBackgroundImage(image,10)
		.background_(whiteBackground);


		// Calculamos la mitad de la altura de la ventana para los paneles
		halfHeight = window.bounds.height * 0.5;

		// Panel superior
		topPanelBounds = Rect(0, 0, window.bounds.width, halfHeight);
		topPanel = UserView(compositeView, topPanelBounds)
		.canFocus_(true);
		topPanel.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|this.contextualMenu(view, x, y, modifiers, buttonNumber, clickCount)});
		viewSizes = viewSizes.add([topPanel, topPanelBounds]);
		//topPanel.setBackgroundImage(image,10);

		// Panel inferior
		bottomPanelBounds = Rect(0, halfHeight, window.bounds.width, halfHeight);
		bottomPanel = UserView(compositeView, bottomPanelBounds)
		.canFocus_(true);
		bottomPanel.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|this.contextualMenu(view, x, y, modifiers, buttonNumber, clickCount)});
		viewSizes = viewSizes.add([bottomPanel, bottomPanelBounds]);
		//bottomPanel.setBackgroundImage(image,10,fromRect: Rect(0, 2995/2, 2997, 2995/2)); // 10, 7, 8, 5


		this.makeNodeTable;

		this.saveOrigin;
		//this.resizePanel(Window.availableBounds.width/virtualWidth);
		//this.saveOrigin;
		window.front;
	}

	makeNodeTable {
		// Se crean los nodos (botones)
		var left = 56.2;
		var top = 55;
		var spacing = 6.1;
		var nodeCountHor = 67;
		var numRows = 63;
		var panel;
		var forbidenRows = [25, 26] ++ (36..62); // Estas filas no se dibujarán ni se instanciarán sus nodos. Conveniente para ahorro de memoria.

		Platform.case(
			\osx,       { },
			\linux,     { },
		//	\windows,   { forbidenRows = [25, 26] ++ (36..62) }
		);

		numRows.do({|row|
			if (row > 32) { panel = bottomPanel } { panel = topPanel };
			if (row == 33) {top = 16.5}; // reiniciamos top para comenzar en el bottomPanel
			if((row < 30).or(row > 32), {
				if (forbidenRows.includes(row).not)
					{this.makeRow(panel, left, top, row, nodeCountHor)};
				nodeCountHor = nodeCountHor + 1;
			});
			top = top + spacing;
		});
	}


	makeRow {|parent, left, top, row, nodeCountHor|
		var nodeCountVer = 1;
		var spacing = 5.75;
		var numColumns = 67;
		var forbidenColumns = (60..62) ++ [65, 66]; // Estas columnas no se dibujarán ni se instanciarán sus nodos. Conveniente para ahorro de memoria.
		Platform.case(
			\osx,       { },
			\linux,     { },
		//	\windows,   { forbidenColumns = (60..66) }
		);
		numColumns.do({|column|
			if(column != 33, {
				if (forbidenColumns.includes(column).not)
					{this.makeNode(parent, left, top, column, row, nodeCountHor, nodeCountVer)};
				nodeCountVer = nodeCountVer + 1;
			});
			left = left + spacing;
		})
	}

	makeNode {|parent, left, top, column, row, nodeCountHor, nodeCountVer|
		var stringOSC = "/patchV/" ++ nodeCountHor ++ "/" ++ nodeCountVer;
		var side = 5;
		var bounds = Rect(left, top, side, side);

		var node = SGME_GUINode(synthiGME, parent, bounds, stringOSC, imagesPath);


		// Se añaden al diccionario cada uno de los nodos para poder cambiar su valor. /patchC/91/36
		parameterViews.put(stringOSC, node);

		// Se añaden el view node y sus bound por defecto para resize
		viewSizes = viewSizes ++ [
			[node, bounds]
		];
	}

	enableNodes { // Enable o disable los nodos según si el sintetizador los usa o no.
		var option, node;
		if (visibleNodes == true, {option = false; visibleNodes = false}, {option = true; visibleNodes = true});
		66.do({|v|
			60.do({|h|
				node = parameterViews["/patchV/" ++ (h+67) ++ "/" ++ (v+1)];
				if(node != nil, {
					if ((synthiGME.modulPatchbayVoltage.inputsOutputs[v] == nil)
						.or(synthiGME.modulPatchbayVoltage.inputsOutputs[h+66] == nil), {
							node.enable_(option);
						}
					)
				})
			})
		})
	}
}