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

SGME_GUI {

	classvar settings;

	var <mainWindow; // Ventana principal para abrir y cerrar los módulos

	var <parameterViews; // Dictionary con todas las views y claves para OSC
	var <defaultSizes; // Array con todos los tamaños por defecto

	var <panels;

	var <helpWindow; // Ventana de ayuda de atajos de teclado

	var installedPath; // Dirección absoluta de instalación del Quark.
	var <running; // Es true cuando se enciende la GUI. Sirve de semáforo para enviar o no mensajes desde fuera.



	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;


	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(SGME_GUIPanel);
		Class.initClassTree(SGME_GUIPanel1);
		Class.initClassTree(SGME_GUIPanel2);
		Class.initClassTree(SGME_GUIPanel3);
		Class.initClassTree(SGME_GUIPanel4);
		Class.initClassTree(SGME_GUIPanel5);
		Class.initClassTree(SGME_GUIPanel6);
		Class.initClassTree(SGME_GUIPanel7);
	}

	*new {arg synthi;
		^super.new.init(synthi);
	}


	// Métodos de instancia ******************************************************************

	init {|synthi|
		parameterViews = Dictionary.new;
		installedPath = Quarks.quarkNameAsLocalPath("SynthiGME");

		panels = [];
		panels = panels.add(SGME_GUIPanel1(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel2(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel3(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel4(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel5(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel6(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel7(synthi, parameterViews));

		running = false;
	}

	makeWindow {
		Window.closeAll; // Cierra todas las ventanas
		panels.do({|panel|
			panel.makeWindow
		});

		panels[0].hasFocus = true; // damos el foco al primer panel.
		panels[0].window.front; // Ponemos al frente al primer panel.
		this.makeHelp;

		running = true;
	}

	// Crea una ventana con la información de atajos de teclado
	makeHelp {
		if (helpWindow == nil,
			{
				var row, allTexts;
				helpWindow = Window.new("Atajos de teclado", Rect(200,200,255,800), resizable: false).userCanClose_(false);
				row = VLayoutView.new(helpWindow, Rect(0, 0, 255, 800)); //cada una de las filas

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

				helpWindow.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
					keycode.switch(
						104, { // Tecla h: (help) ayuda con los atajos de teclado
							this.makeHelp();
						},
					);
				};

				helpWindow.front;
			}, {
				helpWindow.close;
				helpWindow = nil;
		});
	}


	closeWindows {
		panels.do({|panel|
			panel.window.close;
		});
		helpWindow.close;
	}

	frontWindows {
		panels.do({|panel|
			panel.window.front;
		})
	}
}