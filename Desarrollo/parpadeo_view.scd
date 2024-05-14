(
var window, slider, defaultColor, blinkColor1, blinkColor2, blinkRate, blinkDuration, isBlinking, currentColor;

// Configuración de colores y parpadeo
defaultColor = Color.blue(alpha: 0.6); // Color predeterminado del slider
blinkColor1 = Color.red(alpha: 0.8); // Primer color de parpadeo
blinkColor2 = Color.green(alpha: 0.8); // Segundo color de parpadeo
blinkRate = 0.1; // Tiempo entre cambios de estado en el parpadeo
blinkDuration = 1.0; // Duración total del parpadeo
isBlinking = false; // Indicador de si el parpadeo está activo
currentColor = defaultColor; // Variable para rastrear el color actual de forma local

// Crear ventana y slider
window = Window("Test de Parpadeo", Rect(100, 100, 300, 100)).front;
slider = Slider(window, Rect(10, 10, 280, 20))
    .background_(defaultColor)
    .action_({
        if (isBlinking.not) { // Verificar si ya está parpadeando
            isBlinking = true;
            fork {
                var startTime = Main.elapsedTime;
                var endTime = startTime + blinkDuration;
                while({ Main.elapsedTime < endTime }) {
                    defer {
                        // Alternar colores de forma segura utilizando la variable local
                        if (currentColor == blinkColor1) {
                            slider.background = blinkColor2;
                            currentColor = blinkColor2; // Actualizar el estado local
                            "Changing to blinkColor2".postln;
                        } {
                            slider.background = blinkColor1;
                            currentColor = blinkColor1; // Actualizar el estado local
                            "Changing to blinkColor1".postln;
                        }
                    };
                    (blinkRate).wait; // Esperar el tiempo definido antes de cambiar de nuevo
                };
                defer { slider.background = defaultColor; currentColor = defaultColor; }; // Restablecer el color original de forma segura
                isBlinking = false;
            };
        }
    });

window.onClose = {
    if (isBlinking) {
        defer { slider.background = defaultColor; currentColor = defaultColor; }; // Asegurar que el color base se restaure de forma segura
    };
    window.close;
};
)
