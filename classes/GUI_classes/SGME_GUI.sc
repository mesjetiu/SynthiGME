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

SGME_GUI {

	classvar settings;

	var <mainWindow; // Ventana principal para abrir y cerrar los módulos

	var <parameterViews; // Dictionary con todas las views y claves para OSC
	var <defaultSizes; // Array con todos los tamaños por defecto

	var <panels;

	var <helpWindow; // Ventana de ayuda de atajos de teclado

	//var installedPath; // Dirección absoluta de instalación del Quark.
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
		Class.initClassTree(SGME_GUIHelp);
		Class.initClassTree(SGME_GUIAbout);
	}

	*new {arg synthi, postWin;
		^super.new.init(synthi, postWin);
	}


	// Métodos de instancia ******************************************************************

	init {|synthi, postWin|
		parameterViews = Dictionary.new;
		//installedPath = Quarks.quarkNameAsLocalPath("SynthiGME"); // quizás equivalente a Quark("SynthiGME").localPath

		if (postWin) {
			MessageRedirector.createWindow(synthi);
			MessageRedirector.window.alwaysOnTop = true;
			MessageRedirector.window.alwaysOnTop = false;
		};
		panels = [];
		panels = panels.add(SGME_GUIPanel1(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel2(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel3(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel4(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel5(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel6(synthi, parameterViews));
		panels = panels.add(SGME_GUIPanel7(synthi, parameterViews));

		helpWindow = SGME_GUIHelp(synthi);

		running = false;
	}

	makeWindow {
		var waitTime = 0.1;
	//	Window.closeAll; // Cierra todas las ventanas

		Routine({
			panels.do({|panel|
				panel.makeWindow;
				while({panel.window.visible == false}, {wait(waitTime)});
			});
			//helpWindow.makeWindow;
			panels[0].window.front; // Ponemos al frente al primer panel.
			panels[0].focus(0); // damos el foco al primer panel.

			running = true;

		}).play(AppClock);
	}

	closeWindows {
		panels.do({|panel|
			panel.window.close;
		});
		helpWindow.close;
		"SynthiGME terminado".sgmePostln;
	}

	frontWindows {
		panels.do({|panel|
			panel.window.front;
		})
	}
}