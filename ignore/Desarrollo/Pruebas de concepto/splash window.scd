(
~showSplashWindow = { |color, waitTime|
    var splashWindow, progressBarView, progressRoutine;

    // Crear la ventana splash
    splashWindow = {
        // Obtener las dimensiones disponibles de la pantalla
        var availableBounds = Window.availableBounds;

        // Definir el tamaño de la ventana
        var windowWidth = 400 * 1.5;
        var windowHeight = 300 * 1.5;

        // Calcular la posición para centrar la ventana dentro de los límites disponibles
        var windowX = (availableBounds.width - windowWidth) / 2 + availableBounds.left;
        var windowY = (availableBounds.height - windowHeight) / 2 + availableBounds.top;

        // Crear una ventana con tamaño y posición centrados, sin bordes ni botones, y no redimensionable
        var window = Window("Cargando...", Rect(windowX, windowY, windowWidth, windowHeight), border: false, resizable: false)
            .front
            .alwaysOnTop_(true);

        // Ajustar las propiedades del TopView para quitar bordes y botones
        window.view.decorator = FlowLayout(window.view.bounds);

        // Establecer el color de fondo de la ventana
        window.view.background = color;

        // Crear una vista personalizada para la barra de progreso
        progressBarView = UserView(window, Rect(50, windowHeight - 50, windowWidth - 100, 20))
            .background_(Color(0.8, 0.8, 0.8)) // Fondo de la barra de progreso
            .drawFunc_({ |view|
                var progress = view.getProperty(\progress, 0);
                Pen.color = Color.green;
                Pen.fillRect(Rect(0, 0, view.bounds.width * progress, view.bounds.height));
            });

        progressBarView.setProperty(\progress, 0); // Valor inicial de la barra de progreso

        // Mostrar la ventana
        window.onClose = { window.close };

        window;
    }.value;

    // Simular la carga del sistema (reemplaza esto con tu código de carga real)
    progressRoutine = Routine({
        waitTime.do { |i|
            0.1.wait; // Simular un pequeño retraso en la carga
            {
                var progress = (i + 1) / waitTime; // Calcular el progreso
                progressBarView.setProperty(\progress, progress); // Actualizar el progreso
                progressBarView.refresh;
            }.defer;
        };
        { splashWindow.close }.defer; // Cerrar la ventana splash
    }).play;
};

// Llamar a la función para mostrar el splash
~showSplashWindow.value(Color(0.5, 0.5, 0.5), 80); // Color gris y espera de 8 segundos
)
