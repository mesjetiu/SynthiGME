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


// Ventana de ayuda con atajos de teclado
SGME_GUIHelp : SGME_GUIShortcuts{

	var <window;
	var <synthiGME;

	//*********************************************************************************************

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(SGME_GUIShortcuts);
	}

	*new {|synthi|
		^super.new.init(synthi);
	}

	init {|synthi|
		synthiGME = synthi;
	}

	// Crea una ventana con la información de atajos de teclado
	makeWindow {
		var row, allTexts;
		window = Window.new("Atajos de teclado",
			Rect(
				Window.availableBounds.width/3.9,
				Window.availableBounds.height/6.5,
				450,
				380
			),
			resizable: false, scroll: true).userCanClose_(false);
		row = VLayoutView.new(window, Rect(0, 0, 450, 380)); //cada una de las filas

		allTexts = [
			["h", "Abre y cierra esta ventana de ayuda"],
			["1", "Trae al frente el panel 1"],
			["2", "Trae al frente el panel 2"],
			["3", "Trae al frente el panel 3"],
			["4", "Trae al frente el panel 4"],
			["5", "Trae al frente el panel 5"],
			["6", "Trae al frente el panel 6"],
			["7", "Trae al frente el panel 7"],
			["f", "Trae al frente todas los paneles"],
			["v", "Conmuta visibilidad de las views"],
			["e", "Activa o desactiva los pines sin función de las matrices"],
			["o", "Lleva al tamaño y posición original el panel en foco"],
			["Shift+O", "Lleva al tamaño y posición original todas las ventanas"],
			["Ctrl+O", "Establece una nueva posición \"origen\" de ventanas"],
			["+", "Zoom in (también con doble click izquierdo)"],
			["-", "Zoom out (también con doble click derecho)"],
			["Ctrl+C", "Cierra la aplicación"],
		];

		allTexts.do({|textRow|
			var columns = HLayoutView.new(row, Rect(0, 0, 485, 20));
			CompositeView.new(columns, Rect(0, 0, 10, 20));
			StaticText.new(columns, Rect(0, 0, 60, 20)).string_(textRow[0]);
			StaticText.new(columns, Rect(0, 0, 400, 20)).string_(textRow[1]);
		});

		this.makeShortcuts(window, synthiGME);
		window.alwaysOnTop = false;

		window.asView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {}, // botón izquierdo
					1, {}, // botón derecho
				)
			}, { // si se hace un solo click...
				buttonNumber.switch(
					0, {}, // botón izquierdo
					1, {
						var onTopText;
						if (window.alwaysOnTop == true, {
							onTopText = "No poner sobre las demás"
						},{
							onTopText = "Poner sobre las demás"
						});
						Menu(
							MenuAction("Salir", { synthiGME.close }),
							MenuAction(onTopText, {
								if (window.alwaysOnTop == true, {
									window.alwaysOnTop = false
								},{
									window.alwaysOnTop = true
								});
							}),
							MenuAction("Cerrar ayuda", { window.visible = false }),
						).front;
					}, // botón derecho
				)
			}
			)
		});

		window.front;
	//	window.visible = false; // Comento esto porque tras volverse invisible no es posible volverlo visible... en cambio cuando se vuelve invisible más adelante, todo funciona bien.
	//	window.alwaysOnTop = true; // posible configuración... aunque es posible que en Windows no funcione.
	}


	conmuteVisibility {
		if (window.visible, {
			window.visible_(false);
		}, {
			window.visible_(true);
		})
	}

/*	focus {arg numPanel;
		synthiGME.guiSC.panels.do({|panel, i|
			if (i == numPanel, {
				panel.hasFocus = true;
			}, {
				panel.hasFocus = false;
			})
		})
	} */

}
