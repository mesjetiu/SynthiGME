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
			keycode.switch(
				118, {this.commuteVisibility},  // 'v' Activa y desactiva la visibilidad de los mandos de la ventana en foco.
				65451, {this.resizeFocusedPanel(factor)}, // +
				65453, {this.resizeFocusedPanel(1/factor)}, // -
				43, {this.resizeFocusedPanel(factor)}, // + (en mi portatil Slimbook)
				45, {this.resizeFocusedPanel(1/factor)}, // - (en mi portatil Slimbook)
				102, {synthiGME.guiSC.frontWindows}, // f (front) Todas las ventanas al frente
				49, {synthiGME.guiSC.panels[0].window.front; this.focus(0)}, // Tecla 1: Panel 1 al frente
				50, {synthiGME.guiSC.panels[1].window.front; this.focus(1)}, // Tecla 2: Panel 2 al frente
				51, {synthiGME.guiSC.panels[2].window.front; this.focus(2)}, // Tecla 3: Panel 3 al frente
				52, {synthiGME.guiSC.panels[3].window.front; this.focus(3)}, // Tecla 4: Panel 4 al frente
				53, {synthiGME.guiSC.panels[4].window.front; this.focus(4)}, // Tecla 5: Panel 5 al frente
				54, {synthiGME.guiSC.panels[5].window.front; this.focus(5)}, // Tecla 6: Panel 6 al frente
				55, {synthiGME.guiSC.panels[6].window.front; this.focus(6)}, // Tecla 7: Panel 7 al frente
				79, {// Tecla O: Todos los Paneles a posición y tamaño original
					synthiGME.guiSC.panels.do({|panel|
						panel.goToOrigin
					})
				},
				111, {
					if(mod.isCtrl, { // Ctrl + O. Establece un nuevo origen de todas las ventanas
						synthiGME.guiSC.panels.do({|panel|
							panel.saveOrigin;
						})
					}, {// Tecla o: Panel a posición y tamaño original
						this.goToOriginFocusedPanel
					})
				},
				101, { // Tecla e: Hace visibles o invisibles todos los nodos dependiendo de si son posibles de usar en el SynthiGME
					synthiGME.guiSC.panels[4].enableNodes;
					synthiGME.guiSC.panels[5].enableNodes;
				},
				99, { // Tecla Ctrl+C: Para cerrar la aplicación.
					if (mod.isCtrl, { // Comprueba si está el modificador "Control"
						synthiGME.close;
					})
				},
				104, { // Tecla h: (help) ayuda con los atajos de teclado
					synthiGME.guiSC.makeHelp();
				},
			)
		}
	}

}