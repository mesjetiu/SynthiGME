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
		// Lo primero de todo, se crea la ventana que será padre de todos los "views"
		//	window = Window("EMS Synthi 100", rectWindow, false, true, scroll: true);
		//	window.background = blackForniture;
		//	defaultSizes = defaultSizes.add([window, window.bounds]);

		/*		window.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
		char.postln;
		keycode.postln;
		if(keycode==65451, {this.resize(1.3)}); // '+'
		if(keycode==65453, {this.resize(0.7)}); // '-'
		if(keycode==65450, {this.resetSize}); // '*'
		};

		window.view.mouseWheelAction = {|view, x, y, modifiers, xDelta, yDelta|
		[x,y,xDelta,yDelta].postln;
		if(yDelta > 0, {this.resize(1.05 ** (yDelta/15))});
		if(yDelta < 0, {this.resize(0.95 ** ((yDelta).abs/15))});
		};
		*/


		//  this.makePanel1(compositeView);
		//	this.makePanel2(compositeView);
		//	this.makePanel3(compositeView);
		//	this.makePanel4(compositeView);
		//	this.makePanel5(compositeView);
		//	this.makePanel6(compositeView);

		//	this.resize(widthRealScreen/widthScreen);
		//	this.resize2(0.5);
		//	window.front;

		panels.do({|panel|
			panel.makeWindow
		});

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