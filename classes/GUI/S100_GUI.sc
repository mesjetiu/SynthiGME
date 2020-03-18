S100_GUI {

	classvar settings;

	var <mainWindow; // Ventana principal para abrir y cerrar los módulos

	var <parameterViews; // Dictionary con todas las views y claves para OSC
	var <defaultSizes; // Array con todos los tamaños por defecto

	var <panels;

	var installedPath; // Dirección absoluta de instalación del Quark.
	var <running; // Es true cuando se enciende la GUI. Sirve de semáforo para enviar o no mensajes desde fuera.



	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;


	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(S100_GUIPanel);
		Class.initClassTree(S100_GUIPanel1);
		Class.initClassTree(S100_GUIPanel2);
		Class.initClassTree(S100_GUIPanel3);
		Class.initClassTree(S100_GUIPanel4);
		Class.initClassTree(S100_GUIPanel5);
		Class.initClassTree(S100_GUIPanel6);
		Class.initClassTree(S100_GUIPanel7);
	}

	*new {arg synthi;
		^super.new.init(synthi);
	}


	// Métodos de instancia ******************************************************************

	init {|synthi|
		parameterViews = Dictionary.new;
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];

		panels = [];
		panels = panels.add(S100_GUIPanel1(synthi, parameterViews));
		panels = panels.add(S100_GUIPanel2(synthi, parameterViews));
		panels = panels.add(S100_GUIPanel3(synthi, parameterViews));
		panels = panels.add(S100_GUIPanel4(synthi, parameterViews));
		panels = panels.add(S100_GUIPanel5(synthi, parameterViews));
		panels = panels.add(S100_GUIPanel6(synthi, parameterViews));
		panels = panels.add(S100_GUIPanel7(synthi, parameterViews));

		running = false;
	}

	makeWindow {
		Window.closeAll; // Cierra todas las ventanas
		panels.do({|panel|
			panel.makeWindow
		});

		panels[0].hasFocus = true; // damos el foco al primer panel.
		panels[0].window.front; // Ponemos al frente al primer panel.

		running = true;
	}

	closeWindows {
		panels.do({|panel|
			panel.window.close;
		})
	}

	frontWindows {
		panels.do({|panel|
			panel.window.front;
		})
	}
}