S100_GUI {

	classvar settings;

	var <mainWindow; // Ventana principal para abrir y cerrar los módulos

	var <parameterViews; // Dictionary con todas las views y claves para OSC
	var <defaultSizes; // Array con todos los tamaños por defecto

	var <pannels;

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

		pannels = [];
		pannels = pannels.add(S100_GUIPannel1(synthi, parameterViews));
		pannels = pannels.add(S100_GUIPannel2(synthi, parameterViews));
		pannels = pannels.add(S100_GUIPannel3(synthi, parameterViews));
		pannels = pannels.add(S100_GUIPannel4(synthi, parameterViews));
		pannels = pannels.add(S100_GUIPannel5(synthi, parameterViews));
		pannels = pannels.add(S100_GUIPannel6(synthi, parameterViews));
		pannels = pannels.add(S100_GUIPannel7(synthi, parameterViews));

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


		//  this.makePannel1(compositeView);
		//	this.makePannel2(compositeView);
		//	this.makePannel3(compositeView);
		//	this.makePannel4(compositeView);
		//	this.makePannel5(compositeView);
		//	this.makePannel6(compositeView);

		//	this.resize(widthRealScreen/widthScreen);
		//	this.resize2(0.5);
		//	window.front;

		pannels.do({|pannel|
			pannel.makeWindow
		});

		running = true;
	}

	closeWindows {
		pannels.do({|pannel|
			pannel.window.close;
		})
	}

	frontWindows {
		pannels.do({|pannel|
			pannel.window.front;
		})
	}
}