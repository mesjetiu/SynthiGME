S100_GUI {

	classvar settings;

	var <window;
	var windowSize; // Rect, tamaño y coordenadas por defecto (para reiniciar)
	var <allViews; // Array con todos los views que componen la ventana (excepto la ventana misma, de la variable "window"). Se usará para cambiar el tamaño de todos los elementos al mismo tiempo.
	var allSizeViews; // Array que almacenará todos los Rect de todos los Views de la ventana. Serán los tamaños por defecto, para reiniciar.
	var rectWindow; // Posición y tamaño de la ventana.
	var proportion; // Proporción de la ventana
	var widthScreen; // Anchura de la pantalla del ordenador

	*new {arg create = false;
		^super.new.init(create);
	}


	// Métodos de instancia ******************************************************************

	init {
		//	widthScreen = Window.screenBounds.width; // tamaño de la pantalla del ordenador
		widthScreen = 700; // solo para pruebas, por comodidad.
		proportion = [16,9]; // Proporciones de la ventana
		rectWindow = Rect(0, 0, widthScreen, (widthScreen * proportion[1]) / proportion[0]);
		allViews = [];
	}

	makeWindow {
		var vLayout, hUpLayout, hDownLayout, pannelsUp, pannelsDown;

		// Lo primero de todo, se crea la ventana que será padre de todos los "views"
		window = Window("EMS Synthi 100", rectWindow, false, true, scroll: true);
		windowSize = window.bounds;

		// Todos los paneles de arriba (pruebas)
		pannelsUp = 4.collect({
			var pannel = this.makePannel1(
				parent: window,
				width: rectWindow.width/4,
				height: rectWindow.width/4,
			);
		//	allViews.add(pannel);
			pannel;
		});

		// Todos los paneles de abajo (pruebas)
		pannelsDown = 4.collect({
			var pannel = this.makePannel1(
				parent: window,
				width: rectWindow.width/4,
				height: rectWindow.width/4,
			);
		//	allViews.add(pannel);
			pannel;
		});


		// Layout horizontal superior
		hUpLayout = HLayout(*pannelsUp);
	//	allViews.add(hUpLayout);

		// Layout horizontal inferior
		hDownLayout = HLayout(*pannelsDown);
	//	allViews.add(hDownLayout);

		// Layout vertical (contiene ambas filas horizontales)
		vLayout = VLayout(hUpLayout, hDownLayout);
	//	allViews.add(vLayout);

		vLayout.setStretch(0, 4);
		vLayout.setStretch(1, 5);

		hDownLayout.setStretch(0, 5);
		hDownLayout.setStretch(1, 1);
		hDownLayout.setStretch(2, 5);
		hDownLayout.setStretch(3, 5);

		window.layout_(vLayout);


		// Se almacenan todos los Rect de todos los Views para saber el tamaño por defecto y poder resetear.
	//	allSizeViews = allViews.collect({|v| v.bounds});
		window.front;
	}

	makePannel1 {arg parent, width, height;
		var compositeView;
		height = width; // en principio, un cuadrado perfecto
		compositeView = CompositeView(parent: parent, bounds: Rect(0, 0, width, height)).background_(Color.rand);
		^compositeView;
	}

	resize {|factor|
		window.bounds = Rect(
			left: window.bounds.left,
			top: window.bounds.top,
			width: window.bounds.width * factor,
			height: window.bounds.height * factor,
		);
		allViews.do({|view|
			view.bounds = Rect(
				left: view.bounds.left * factor,
				top: view.bounds.top * factor,
				width: view.bounds.width * factor,
				height: view.bounds.height * factor,
			);
		})
	}

	resetSize {
		window.bounds = windowSize;
		allViews.do({|view, i|
			view.bounds = allSizeViews[i];
		})
	}
}


