S100_GUI {

	classvar settings;

	var synthi100;

	var <parameterViews; // Dictionary con todas las views y claves para OSC
	var <defaultSizes; // Array con todos los tamaños por defecto
	var <synthi100; // instancia de Synthi100 para callback

	var <pannels;


	var installedPath; // Dirección absoluta de instalación del Quark.
	var <running; // Es true cuando se enciende la GUI. Sirve de semáforo para enviar o no mensajes desde fuera.



	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;

	// Al hacer click sobre determinadas views se activa la rutina esperando un segundo click.
	var click;
	var timeDoubleClick = 0.5;

	*new {arg synthi;
		^super.new.init(synthi);
	}


	// Métodos de instancia ******************************************************************

	init {|synthi|
		parameterViews = Dictionary.new;
		synthi100 = synthi;
		installedPath = Quarks.installedPaths.select({|path| "Synthi100".matchRegexp(path)})[0];

		pannels = [];
		pannels = pannels.add(S100_GUIPannel1(synthi100, parameterViews));
		pannels = pannels.add(S100_GUIPannel2(synthi100, parameterViews));
		pannels = pannels.add(S100_GUIPannel3(synthi100, parameterViews));
		pannels = pannels.add(S100_GUIPannel4(synthi100, parameterViews));
		pannels = pannels.add(S100_GUIPannel5(synthi100, parameterViews));
		pannels = pannels.add(S100_GUIPannel6(synthi100, parameterViews));
		pannels = pannels.add(S100_GUIPannel7(synthi100, parameterViews));

		click = false;
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
}