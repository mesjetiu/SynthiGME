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


// De esta clase heredan todas las clases de ventanas que aceptan los atajos de teclado de la aplicación. Simplemente añade una función para aplicar a las ventanas, con los atajos de teclado.
SGME_GUIShortcuts {

	makeShortcuts {|win, synthiGME|
		win.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
			var factor = 2;
			//keycode.postln;
			//[char, unicode, keycode, key].postln;

			char.switch(
				"v"[0], {
					synthiGME.guiSC.panels.do({|panel|
						panel.conmuteVisibility
					})
				},  // "v" Activa y desactiva la visibilidad de los mandos de la ventana en foco.
				"+"[0], {this.resizeFocusedPanel(factor)}, // +
				"-"[0], {this.resizeFocusedPanel(1/factor)}, // -
				"f"[0], {synthiGME.guiSC.frontWindows}, // f (front) Todas las ventanas al frente
				"1"[0], {synthiGME.guiSC.panels[0].window.front; synthiGME.guiSC.panels[0].focus(0)}, // Tecla 1: Panel 1 al frente
				"2"[0], {synthiGME.guiSC.panels[1].window.front; synthiGME.guiSC.panels[0].focus(1)}, // Tecla 2: Panel 2 al frente
				"3"[0], {synthiGME.guiSC.panels[2].window.front; synthiGME.guiSC.panels[0].focus(2)}, // Tecla 3: Panel 3 al frente
				"4"[0], {synthiGME.guiSC.panels[3].window.front; synthiGME.guiSC.panels[0].focus(3)}, // Tecla 4: Panel 4 al frente
				"5"[0], {synthiGME.guiSC.panels[4].window.front; synthiGME.guiSC.panels[0].focus(4)}, // Tecla 5: Panel 5 al frente
				"6"[0], {synthiGME.guiSC.panels[5].window.front; synthiGME.guiSC.panels[0].focus(5)}, // Tecla 6: Panel 6 al frente
				"7"[0], {synthiGME.guiSC.panels[6].window.front; synthiGME.guiSC.panels[0].focus(6)}, // Tecla 7: Panel 7 al frente
				"O"[0], {// Tecla O: Todos los Paneles a posición y tamaño original
					synthiGME.guiSC.panels.do({|panel|
						panel.goToOrigin
					})
				},
				"o"[0], {
					if(mod.isCtrl, { // Ctrl + O. Establece un nuevo origen de todas las ventanas
						synthiGME.guiSC.panels.do({|panel|
							panel.saveOrigin;
						})
					}, {// Tecla o: Panel a posición y tamaño original
						this.goToOriginFocusedPanel
					})
				},
				"e"[0], { // Tecla e: Hace visibles o invisibles todos los nodos dependiendo de si son posibles de usar en el SynthiGME
					synthiGME.guiSC.panels[4].enableNodes;
					synthiGME.guiSC.panels[5].enableNodes;
				},
				"c"[0], { // Tecla Ctrl+C: Para cerrar la aplicación.
					if (mod.isCtrl, { // Comprueba si está el modificador "Control"
						synthiGME.close;
					})
				},
				"h"[0], { // Tecla h: (help) ayuda con los atajos de teclado
					synthiGME.guiSC.helpWindow.conmuteVisibility;
				},
			)
		}
	}

}