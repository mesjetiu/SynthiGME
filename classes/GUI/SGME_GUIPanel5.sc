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

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_GUIPanel5 : SGME_GUIPanelPatchbay {
	makeWindow {
		var rect;
		var image;
		var topPanelBounds;
		var bottomPanelBounds;
		id = 4;
		super.makeWindow;
		rect = Rect(
			left: 0,
			top: (Window.availableBounds.height/2.1)
			-(window.bounds.height
				* (Window.availableBounds.width/virtualWidth)),//0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 5";
		window.bounds = rect;
		image = Image(installedPath +/+ "classes" +/+ "GUI" +/+ "images" +/+ "panels" +/+ "panel_5");
		compositeView
		.setBackgroundImage(image,10)
		.background_(whiteBackground);



		// Calculamos la mitad de la altura de la ventana para los paneles
		halfHeight = window.bounds.height * 0.5;

		// Panel superior
		topPanelBounds = Rect(0, 0, window.bounds.width, halfHeight);
		topPanel = UserView(compositeView, topPanelBounds)
		//.background_(Color.red)
		.canFocus_(true);
		viewSizes = viewSizes.add([topPanel, topPanelBounds]);
		//topPanel.setBackgroundImage(image,10);

		// Panel inferior
		bottomPanelBounds = Rect(0, halfHeight, window.bounds.width, halfHeight);
		bottomPanel = UserView(compositeView, bottomPanelBounds)
		//.background_(Color.blue)
		.canFocus_(true);
		viewSizes = viewSizes.add([bottomPanel, bottomPanelBounds]);
		//bottomPanel.setBackgroundImage(image,10,fromRect: Rect(0, 2995/2, 2997, 2995/2)); // 10, 7, 8, 5

		"hechos paneles!!".postln;

		this.makeNodeTable;

		this.saveOrigin;
		//this.resizePanel(Window.availableBounds.width/virtualWidth);
		//this.saveOrigin;
		window.front;
	}

	makeNodeTable { /////////BUG AQUÍ DENTRO... (WINDOWS)
		// Se crean los nodos (botones)
		var left = 56.2;
		var top = 55; // 55
		var spacing = 6.1;
		var nodeCountHor = 67;
		var numRows = 63;
		var panel;
		var forbidenRows = []; // Estas filas no se dibujarán. Son nodos válidos pero no implementados. Es una conveniencia para que Windows no tenga demasiados nodos. De este modo no dibujamos los nodos no utilizados.

		Platform.case(
			\osx,       { },
			\linux,     { },
			\windows,   { forbidenRows = [20, 21, 62] }
		);

		numRows.do({|row|
			if (row > 32) { panel = topPanel } { panel = bottomPanel };
			if (row == 33) {top = 5}; // reiniciamos top para comenzar en el bottomPanel
			if((row < 30).or(row > 32), {
				if (forbidenRows.any({|n| n == row}).not,
					{this.makeRow(panel, left, top, row, nodeCountHor)});
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
			\windows,   { forbidenColumns = (44..58) ++ [65, 66] }
		);
		numColumns.do({|column| // 67
			if(column != 33, {
				if (forbidenColumns.any({|n| n == column}).not,
					{this.makeNode(parent, left, top, column, row, nodeCountHor, nodeCountVer)});
				nodeCountVer = nodeCountVer + 1;
			});
			left = left + spacing;
		});
	}



	makeNode {|parent, left, top, column, row, nodeCountHor, nodeCountVer|
		var stringOSC = "/patchA/" ++ nodeCountHor ++ "/" ++ nodeCountVer;
		var side = 5;
		var bounds = Rect(left, top, side, side);
		var node = SGME_GUINode(synthiGME, parent, bounds, stringOSC);

		// Se añaden al diccionario cada uno de los nodos para poder cambiar su valor. /patchA/91/36
		parameterViews.put(stringOSC, node);

		// Se añaden el view node y sus bound por defecto para resize
		viewSizes = viewSizes.add([node, bounds]);
	}

	enableNodes { // Enable o disable los nodos según si el sintetizador los usa o no.
		var option, node;
		if (visibleNodes == true, {option = false; visibleNodes = false}, {option = true; visibleNodes = true});
		66.do({|v|
			60.do({|h|
				node = parameterViews["/patchA/" ++ (h+67) ++ "/" ++ (v+1)];
				if(node != nil, {
					if ((synthiGME.modulPatchbayAudio.inputsOutputs[v] == nil)
						.or(synthiGME.modulPatchbayAudio.inputsOutputs[h+66] == nil), {
							node.enable_(option);
						}
					)
				})
			})
		})
	}

}