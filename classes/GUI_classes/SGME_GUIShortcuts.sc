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


// De esta clase heredan todas las clases de ventanas que aceptan los atajos de teclado de la aplicación. Simplemente añade una función para aplicar a las ventanas, con los atajos de teclado.
SGME_GUIShortcuts {

	*makeShortcuts {|objectCaller, win, synthiGME|
		win.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
			var factor = 2;
			//keycode.sgmePostln;
			//[char, unicode, keycode, key, mod].sgmePostln;

			unicode.switch( // usamos unicode
				118, {// "v" Activa y desactiva la visibilidad de los mandos de la ventana en foco.
					synthiGME.guiSC.panels.do({|panel|
						panel.conmuteVisibility
					})
				},
				43, {objectCaller.resizeFocusedPanel(factor)}, // +
				45, {objectCaller.resizeFocusedPanel(1/factor)}, // -
				102, {
					synthiGME.guiSC.frontWindows;
					MessageRedirector.getInstance.goFront;
					synthiGME.eventRecorder.goFront;
				}, // f (front) Todas las ventanas al frente
				49, {synthiGME.guiSC.panels[0].window.front; synthiGME.guiSC.panels[0].focus(0); synthiGME.guiSC.panels[0].goFront;}, // Tecla 1: Panel 1 al frente
				50, {synthiGME.guiSC.panels[1].window.front; synthiGME.guiSC.panels[1].focus(1); synthiGME.guiSC.panels[1].goFront;}, // Tecla 2: Panel 2 al frente
				51, {synthiGME.guiSC.panels[2].window.front; synthiGME.guiSC.panels[2].focus(2); synthiGME.guiSC.panels[2].goFront;}, // Tecla 3: Panel 3 al frente
				52, {synthiGME.guiSC.panels[3].window.front; synthiGME.guiSC.panels[3].focus(3); synthiGME.guiSC.panels[3].goFront;}, // Tecla 4: Panel 4 al frente
				53, {synthiGME.guiSC.panels[4].window.front; synthiGME.guiSC.panels[4].focus(4); synthiGME.guiSC.panels[4].goFront;}, // Tecla 5: Panel 5 al frente
				54, {synthiGME.guiSC.panels[5].window.front; synthiGME.guiSC.panels[5].focus(5); synthiGME.guiSC.panels[5].goFront;}, // Tecla 6: Panel 6 al frente
				55, {synthiGME.guiSC.panels[6].window.front; synthiGME.guiSC.panels[6].focus(6); synthiGME.guiSC.panels[6].goFront;}, // Tecla 7: Panel 7 al frente
				79, {// Shift + O: Todos los Paneles a posición y tamaño original
					synthiGME.guiSC.panels.do({|panel|
						panel.goToOrigin
					})
				},
				116, {// t: Activa y desactiva los tooltip del ratón
					if (SGME_TooltipHandler.enabled) {SGME_TooltipHandler.enabled = false}
					{SGME_TooltipHandler.enabled = true}
				},
				/*15, {// Ctrl + O. Establece un nuevo origen de todas las ventanas
				synthiGME.guiSC.panels.do({|panel|
				panel.saveOrigin;
				})
				},*/ // Funciona pero lo dejamos fuera en este momento
				111, {// Tecla o: Todos los Paneles a posición y tamaño original
					synthiGME.guiSC.panels.do({|panel|
						panel.goToOrigin
					})
					// (antiguo)Tecla o: Panel a posición y tamaño original
					//	this.goToOriginFocusedPanel
				},
				/*18, {// Tecla Ctrl + R: Comenzar a grabar o terminar de grabar
					if (synthiGME.server.isRecording) {
						synthiGME.server.stopRecording;
					} {
						synthiGME.server.record;
					}
				},*/
				19, {// Tecla Ctrl + S: Guardar el patch actual
					synthiGME.saveStateGUI();
				},
				15, {// Ctrl + O. Abrir un patch existente
					synthiGME.loadStateGUI;
				},


				/* No se ejecuta enableNodes porque de hecho los nodos no implementados no están dibujados.
				101, { // Tecla e: Hace visibles o invisibles todos los nodos dependiendo de si son posibles de usar en el SynthiGME
				synthiGME.guiSC.panels[4].enableNodes;
				synthiGME.guiSC.panels[5].enableNodes;
				},
				*/


				3, { // Tecla Ctrl+C: Para cerrar la aplicación.
					synthiGME.close;
				},
				104, { // Tecla h: (help) ayuda con los atajos de teclado
					synthiGME.guiSC.helpWindow.conmuteVisibility;
				},
				109, { // Tecla m: (mute) mutea y desmutea
					if (synthiGME.server.volume.isMuted,
						{synthiGME.server.volume.unmute},
						{synthiGME.server.volume.mute}
					)
				},
			)
		}
	}

}