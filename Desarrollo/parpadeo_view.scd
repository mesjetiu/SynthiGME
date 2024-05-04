// parpadeo de una view al modificarla.
// discusión en https://chat.openai.com/share/5df233ef-65b2-4e36-af12-1a98a15d428f

(
var window, slider, baseColor, activeColor, blinkRate, blinkDuration, isBlinking, currentColor;

// Configuración de colores y parpadeo
baseColor = Color.grey(0.9); // Color normal
activeColor = Color.red(alpha: 0.8); // Color cuando está activo
blinkRate = 0.1; // Tiempo entre cambios de estado en el parpadeo
blinkDuration = 1.0; // Duración total del parpadeo
isBlinking = false; // Indicador de si el parpadeo está activo
currentColor = baseColor; // Variable para rastrear el color actual de forma local

// Crear ventana y slider
window = Window("Test de Parpadeo", Rect(100, 100, 300, 100)).front;
slider = Slider(window, Rect(10, 10, 280, 20))
    .background_(baseColor)
    .action_({
        if (isBlinking.not) { // Verificar si ya está parpadeando
            isBlinking = true;
            fork {
                var startTime = Main.elapsedTime;
                var endTime = startTime + blinkDuration;
                while({ Main.elapsedTime < endTime }) {
                    defer {
                        // Alternar colores de forma segura utilizando la variable local
                        if (currentColor == baseColor) {
                            slider.background = activeColor;
                            currentColor = activeColor; // Actualizar el estado local
                            "Changing to activeColor".postln;
                        } {
                            slider.background = baseColor;
                            currentColor = baseColor; // Actualizar el estado local
                            "Changing to baseColor".postln;
                        }
                    };
                    (blinkRate).wait; // Esperar el tiempo definido antes de cambiar de nuevo
                };
                defer { slider.background = baseColor; currentColor = baseColor; }; // Restablecer el color original de forma segura
                isBlinking = false;
            };
        }
    });

window.onClose = {
    if (isBlinking) {
        defer { slider.background = baseColor; currentColor = baseColor; }; // Asegurar que el color base se restaure de forma segura
    };
    window.close;
};
)
