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
		image = Image(installedPath +/+ "classes" +/+ "GUI" +/+ "images" +/+ "panels" +/+ "panel_6");
		compositeView
		.setBackgroundImage(image,10)
		.background_(whiteBackground);

		this.makeNodeTable;

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
		window.front;
	}

	makeNodeTable {
		// Se crean los nodos (botones)
		var left = 56.2;
		var top = 55;
		var spacing = 6.1;
		var nodeCountHor = 67;
		var numRows = 63;
		var forbidenRows = []; // Estas filas no se dibujarán. Son nodos válidos pero no implementados. Es una conveniencia para que Windows no tenga demasiados nodos. De este modo no dibujamos los nodos no utilizados.
		Platform.case(
			\osx,       { },
			\linux,     { },
			\windows,   { forbidenRows = [25, 26] ++ (36..62) }
		);

		numRows.do({|row|
			if((row < 30).or(row > 32), {
				if (forbidenRows.any({|n| n == row}).not,
					{this.makeRow(compositeView, left, top, row, nodeCountHor)});
				nodeCountHor = nodeCountHor + 1;
			});
			top = top + spacing;
		});
	}


	makeRow {|parent, left, top, row, nodeCountHor|
		var nodeCountVer = 1;
		var spacing = 5.75;
		var numColumns = 67;
		var forbidenColumns = []; // Estas columnas no se dibujarán. Son nodos válidos pero no implementados. Es una conveniencia para que Windows no tenga demasiados nodos. De este modo no dibujamos los nodos no utilizados.
		Platform.case(
			\osx,       { },
			\linux,     { },
			\windows,   { forbidenColumns = (60..66) }
		);
		numColumns.do({|column|
			if(column != 33, {
				if (forbidenColumns.any({|n| n == column}).not,
					{this.makeNode(parent, left, top, column, row, nodeCountHor, nodeCountVer)});
				nodeCountVer = nodeCountVer + 1;
			});
			left = left + spacing;
		})
	}

	makeNode {|parent, left, top, column, row, nodeCountHor, nodeCountVer|
		var stringOSC = "/patchV/" ++ nodeCountHor ++ "/" ++ nodeCountVer;
		var side = 5;
		var bounds = Rect(left, top, side, side);

		var node = SGME_GUINode(synthiGME, parent, bounds, stringOSC);


		// Se añaden al diccionario cada uno de los nodos para poder cambiar su valor. /patchC/91/36
		parameterViews.put(stringOSC, node);
		nodes.put([nodeCountHor, nodeCountVer], node);

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
				node = nodes[[h+67,v+1]];
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