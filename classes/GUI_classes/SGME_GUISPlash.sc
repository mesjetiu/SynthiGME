
SGME_SplashWindow  {

	var window = nil,
	<>numSteps = 10,
	<>progressBarView = nil,
	actualProgress = 0;

	*new {
		^super.new.init();
	}

	init {

	}

	// Crear la ventana splash
	showSplashWindow {
		var color, availableBounds, windowWidth, windowHeight, windowX, windowY, rect, compositeView, imagesPath, image;

		color = Color.blue;
		// Obtener las dimensiones disponibles de la pantalla
		availableBounds = Window.availableBounds;

		// Definir el tamaño de la ventana
		windowWidth = 400 * 1.5;
		windowHeight = 300 * 1.5;

		// Calcular la posición para centrar la ventana dentro de los límites disponibles
		windowX = (availableBounds.width - windowWidth) / 2 + availableBounds.left;
		windowY = (availableBounds.height - windowHeight) / 2 + availableBounds.top;

		// Crear una ventana con tamaño y posición centrados, sin bordes ni botones, y no redimensionable
		rect = Rect(windowX, windowY, windowWidth, windowHeight);
		window = Window("Cargando...", rect, border: false, resizable: false);
		rect = Rect(
			left: 0,
			top: 0,
			width: windowWidth,
			height: windowHeight,
		);
		compositeView = CompositeView(window, rect);
		imagesPath = SGME_Path.imagesPath;
		image = Image(imagesPath +/+ "panels" +/+ "splash");
		compositeView.setBackgroundImage(image,10);

		// Ajustar las propiedades del TopView para quitar bordes y botones
		//window.view.decorator = FlowLayout(window.view.bounds);

		// Establecer el color de fondo de la ventana
		//window.view.background = color;

		window.front;
		//window.alwaysOnTop_(true);

		// Crear una vista personalizada para la barra de progreso
		progressBarView = UserView(window, Rect(50, windowHeight - 50, windowWidth - 100, 20))
		.background_(Color(0.8, 0.8, 0.8)) // Fondo de la barra de progreso
		.drawFunc_({ |view|
			var progress = view.getProperty(\progress, 0);
			Pen.color = Color.blue;
			Pen.fillRect(Rect(0, 0, view.bounds.width * progress, view.bounds.height));
		});

		progressBarView.setProperty(\progress, actualProgress); // Valor inicial de la barra de progreso
		progressBarView.refresh;

		// Mostrar la ventana
	//	window.onClose = { window.close };
	}

	close {
		window.close
	}


	progress {
		actualProgress = actualProgress + (1/numSteps);
		progressBarView.setProperty(\progress, actualProgress);
		progressBarView.refresh;
		if (actualProgress >= 1) {this.close}
	}



	// Simular la carga del sistema (reemplaza esto con tu código de carga real)
	/*progressRoutine = Routine({
	waitTime.do { |i|
	0.1.wait; // Simular un pequeño retraso en la carga
	{
	var progress = (i + 1) / waitTime; // Calcular el progreso
	progressBarView.setProperty(\progress, progress); // Actualizar el progreso
	progressBarView.refresh;
	}.defer;
	};
	{ splashWindow.close }.defer; // Cerrar la ventana splash
	}).play;*/

}


