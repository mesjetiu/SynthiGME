S100_GUI {

	classvar settings;

	var <window;
	var views; // Array con todos los views que componen la ventana (excepto la ventana: en la variable "window"). Se usará para cambiar el tamaño de todos los elementos al mismo tiempo.
	var rectWindow; // Posición y tamaño de la ventana.

	*new {arg create = false;
		^super.new.init(create);
	}


	// Métodos de instancia ******************************************************************

	init {
	}

	makeWindow {
		// Cálculos iniciales
		var proportion = [16,9]; // Proporciones de la ventana
		var widthScreen = Window.screenBounds.width; // tamaño de la pantalla del ordenador
		rectWindow = Rect(0, 0, widthScreen, (widthScreen * proportion[1]) / proportion[0]);

		// Lo primero de todo, se crea la ventana que será padre de todos los "views"
		window = Window("EMS Synthi 100", rectWindow, false, true, scroll: true);

		// Se crea el panel número 1
		this.makePannel1(
			parent: window,
			width: rectWindow.width/4,
			height: rectWindow.width/4,
		);

		window.front;
	}

	makePannel1 {arg parent, width, height;
		var compositeView;
		height = width; // en principio, un cuadrado perfecto
		^CompositeView(parent, Rect(0, 0, width, height)).background_(Color.rand);
	}
}


