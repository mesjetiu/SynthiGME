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


SGME_GUIAbout {
	classvar <window;

	//*********************************************************************************************

	*initClass {
		// Inicializa otras clases antes de esta
		// Class.initClassTree(SGME_GUIShortcuts);
	}

	*new {
		^super.new.init();
	}

	init {
	}

	// Crea una ventana con la información de atajos de teclado
	*makeWindow {
		if (window.notNil and: { window.visible }) {
			window.front;
		} {
			var aboutText;
			var windowWidth = 400;
			var windowHeight = 200;
			var screenWidth = Window.availableBounds.width;
			var screenHeight = Window.availableBounds.height;
			var xPos = (screenWidth - windowWidth) / 2;
			var yPos = (screenHeight - windowHeight) / 2;

			window = Window("Acerca de Synthi GME", Rect(xPos, yPos, windowWidth, windowHeight));

			aboutText = "Synthi GME\n\n" ++
			"Version" + SynthiGME.version ++ "\n\n" ++
			"Desarrollador: Carlos Arturo Guerra Parra\n" ++
			"Correo: carlosarturoguerra@gmail.com\n\n" ++
			"Licencia: GPL 3.0\n" ++
			"© 2024\n\n" ++
			"Repositorio en GitHub:\n" ++
			"https://github.com/mesjetiu/SynthiGME";



			StaticText(window, Rect(10, 10, 380, 180))
			.string_(aboutText)
			.align_(\center)
			.font_(Font("Helvetica", 12));

			window.onClose = { window = nil };

			window.front;
		}
	}
}



