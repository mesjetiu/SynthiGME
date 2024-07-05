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
	var horizontalNames;
	var verticalNames;

	 *new {|synthi, parameters|
		synthiGME = synthi;
	 	^super.new.init(synthi, parameters);
	 }

	 init {|synthi, parameters|
	 	horizontalNames = Dictionary.newFrom(SGME_GUIPanel5.horizontalNames);
	 	verticalNames = Dictionary.newFrom(SGME_GUIPanel5.verticalNames);
		^super.init(synthi, parameters);
	 }

	makeWindow {
		var rect;
		var image;
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
		image = Image(imagesPath +/+ "panels" +/+ "panel_5");
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
		topPanel.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			if(clickCount == 2) {
				/*buttonNumber.switch(
					0, {this.resizePanel(factor)}, // botón izquierdo
					1, {this.resizePanel(1/factor)}, // botón derecho
				)*/
			} {SGME_ContextualMenu.contextualMenu(synthiGME, view, x, y, modifiers, buttonNumber)}
		});
		viewSizes = viewSizes.add([topPanel, topPanelBounds]);
		//topPanel.setBackgroundImage(image,10);

		// Panel inferior
		bottomPanelBounds = Rect(0, halfHeight, window.bounds.width, halfHeight);
		bottomPanel = UserView(compositeView, bottomPanelBounds)
		//.background_(Color.blue)
		.canFocus_(true);
		bottomPanel.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			if(clickCount == 2) {
				/*buttonNumber.switch(
					0, {this.resizePanel(factor)}, // botón izquierdo
					1, {this.resizePanel(1/factor)}, // botón derecho
				)*/
			} {SGME_ContextualMenu.contextualMenu(synthiGME, view, x, y, modifiers, buttonNumber)}
		});
		viewSizes = viewSizes.add([bottomPanel, bottomPanelBounds]);
		//bottomPanel.setBackgroundImage(image,10,fromRect: Rect(0, 2995/2, 2997, 2995/2)); // 10, 7, 8, 5


		this.makeNodeTable;

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
		window.front;
	}

	makeNodeTable {
		// Se crean los nodos (botones)
		var left = 56.2;
		var top = 55; // 55
		var spacing = 6.1;
		var nodeCountHor = 67;
		var numRows = 63;
		var panel;
		var forbidenRows = [21, 62]; // Estas filas no se dibujarán ni se instanciarán sus nodos. Conveniente para ahorro de memoria.

		Platform.case(
			\osx,       { },
			\linux,     { },
			//	\windows,   { forbidenRows = [20, 21, 62] }
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
		var forbidenColumns = (44..50) ++ [56, 65, 66]; // Estas columnas no se dibujarán ni se instanciarán sus nodos. Conveniente para ahorro de memoria.

		Platform.case(
			\osx,       { },
			\linux,     { },
			//	\windows,   { forbidenColumns = (44..58) ++ [65, 66] }
		);
		numColumns.do({|column| // 67
			if(column != 33, {
				if (forbidenColumns.includes(column).not)
				{this.makeNode(parent, left, top, column, row, nodeCountHor, nodeCountVer)};
				nodeCountVer = nodeCountVer + 1;
			});
			left = left + spacing;
		});
	}



	makeNode {|parent, left, top, column, row, nodeCountHor, nodeCountVer|
		var stringOSC = "/patchA/" ++ nodeCountHor ++ "/" ++ nodeCountVer;
		var side = 5;
		var bounds = Rect(left, top, side, side);
		// ⟶⇒⟹➜
		var tooltipText = verticalNames[nodeCountHor].asString + "⟶" + horizontalNames[nodeCountVer].asString;
		var node = SGME_GUINode(synthiGME, parent, bounds, stringOSC, tooltipTextFunc: {tooltipText});

		// Se añaden al diccionario cada uno de los nodos para poder cambiar su valor. /patchA/91/36
		parameterViews.put(stringOSC, node);

		// Se añaden el view node y sus bound por defecto para resize
		viewSizes = viewSizes.add([node, bounds]);
	}

	enableNodes { // Enable o disable los nodos según si el sintetizador los usa o no. No es necesario si se está usando forbidenColumns y forbidenRows en la presente clase, ya que los nodos, al no implementarse, tampoco se dibujan y, por tanto, no es necesario enable o disable. Ahorramos memoria.
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