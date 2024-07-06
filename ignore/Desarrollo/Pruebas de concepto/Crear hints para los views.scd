(
var window, button, tooltipWindow, showTooltip, hideTooltip;

// Crear la ventana principal
window = Window.new("Test Window", Rect(100, 100, 300, 100))
    .front
    .acceptsMouseOver_(true);  // Asegurar que la ventana acepta eventos mouseOver

// Función para mostrar el tooltip
showTooltip = {
    arg x, y, text;
    "Attempting to show tooltip...".postln;
    tooltipWindow.notNil.if({
        "Tooltip exists, updating position...".postln;
        tooltipWindow.bounds = Rect(x, y, text.size * 8 + 20, 20);
        tooltipWindow.front;
    }, {
        "Creating new tooltip window...".postln;
        tooltipWindow = Window("Tooltip", Rect(x, y, text.size * 8 + 20, 20), resizable:  false, border: false)
        .alwaysOnTop_(true)
        .drawFunc_({
            Pen.fillColor = Color.white;
            Pen.fillRect(tooltipWindow.bounds);
        });
        StaticText(tooltipWindow, Rect(5, 2, text.size * 8 + 10, 16))
        .string_(text)
        .align_(\left)
        .background_(Color.grey(0.9))
        .font_(Font("Helvetica", 12));
        tooltipWindow.front;
    });
};

// Función para ocultar el tooltip
hideTooltip = {
    if (tooltipWindow.notNil) {
        "Closing tooltip...".postln;
        tooltipWindow.close;
        tooltipWindow = nil;
    };
};

// Crear un botón
button = Button(window, Rect(10, 10, 280, 20))
.states_([["Hover for tooltip"]])
.mouseOverAction_({
	var x, y;
    "Mouse entered button.".postln;
    x = window.bounds.left + button.bounds.left + (button.bounds.width / 2);
    y = window.bounds.top + button.bounds.top + button.bounds.height;
    showTooltip.value(x, y, "Este es el hint del botón.");
})
.mouseLeaveAction_({
    "Mouse left button.".postln;
    hideTooltip.value;
});

// Mostrar la ventana
window.onClose_({ hideTooltip.value; });
window.front;
)
