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


// De esta clase heredan los paneles.
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
		window = Window.new("Atajos de teclado", Rect(200,200,255,800), resizable: false).userCanClose_(false);
		row = VLayoutView.new(window, Rect(0, 0, 255, 800)); //cada una de las filas

		allTexts = [
			["f", "Trae al frente"],
			["h", "ayuda"],
		];

		allTexts.do({|textRow|
			var columns = HLayoutView.new(row, Rect(0, 0, 255, 20));
			CompositeView.new(columns, Rect(0, 0, 10, 20));
			StaticText.new(columns, Rect(0, 0, 20, 20)).string_(textRow[0]);
			StaticText.new(columns, Rect(0, 0, 100, 20)).string_(textRow[1]);
		});

		this.makeShortcuts(window, synthiGME);
		window.alwaysOnTop = true;

		window.front;
	}


	conmuteVisibility {
		if (window.visible, {
			window.visible_(false);
		}, {
			window.visible_(true);
		})
	}

}
