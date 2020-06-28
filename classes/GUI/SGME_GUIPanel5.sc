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

Copyright 2020 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_GUIPanel5 : SGME_GUIPanelPatchbay {
	makeWindow {
		var rect;
		var image;
		id = 4;
		super.makeWindow;
		rect = Rect(
			left: 0,
			top: 0,
			width: window.bounds.width,
			height: window.bounds.height,
		);
		window.name = "Panel 5";
		window.bounds = rect;
		image = Image(installedPath ++ "/classes/GUI/images/panels/panel_5.png");
		compositeView
		.setBackgroundImage(image,10)
		.background_(whiteBackground);
		// Cuando se hace doble click se hace zoom
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePanel(factor)}, // botón izquierdo
					1, {this.resizePanel(1/factor)}, // botón derecho
				)
			}, { // si se hace un solo click...
				buttonNumber.switch(
					0, {}, // botón izquierdo
					1, {
						Menu(
							MenuAction("Salir (Ctrl+C)", { synthiGME.close }),
							MenuAction("Zoom In", { this.resizePanel(factor) }),
							MenuAction("Zoom Out", { this.resizePanel(1/factor) }),
						).front;
					}, // botón derecho
				)
			}
			)
		});
		this.makeNodeTable;

		this.saveOrigin;
		this.resizePanel(Window.availableBounds.width/virtualWidth);
		this.saveOrigin;
		window.front;
	}

	makeNodeTable { /////////BUG AQUÍ DENTRO... (WINDOWS)
		// Se crean los nodos (botones)
		var left = 56.2;
		var top = 55;
		var spacing = 6.1;
		var nodeCountHor = 67;

		63.do({|row| // 63
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
		var numColumns = 67;
		Platform.case(
			\osx,       { },
			\linux,     { },
			\windows,   { numColumns = 59 }
		);
		numColumns.do({|column| // 67
			if(column != 33, {
				this.makeNode(parent, left, top, column, row, nodeCountHor, nodeCountVer);
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
		nodes.put([nodeCountHor, nodeCountVer], node);

		// Se añaden el view node y sus bound por defecto para resize
		viewSizes = viewSizes.add([node, bounds]);
	}

	enableNodes { // Enable o disable los nodos según si el sintetizador los usa o no.
		var option, node;
		if (visibleNodes == true, {option = false; visibleNodes = false}, {option = true; visibleNodes = true});
		66.do({|v|
			60.do({|h|
				node = nodes[[h+67,v+1]];
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