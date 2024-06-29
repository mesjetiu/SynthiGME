/// Hints para knobs


(
var window, knob, tooltipWindow, tooltipText, showTooltip, hideTooltip, updateTooltip, hideTooltipTask;

// Crear la ventana principal
window = Window.new("Test Window", Rect(100, 100, 300, 100))
    .front
    .acceptsMouseOver_(true);  // Asegurar que la ventana acepta eventos mouseOver

// Función para mostrar el tooltip
showTooltip = {
    arg x, y, text;
    hideTooltipTask.notNil.if({ hideTooltipTask.stop });  // Detener cualquier tarea de ocultar tooltip en curso
    "Attempting to show tooltip...".postln;
    tooltipWindow.isNil.not.if({
        "Tooltip exists, updating position...".postln;
        tooltipWindow.bounds = Rect(x, y, text.size * 8 + 20, 20);
        tooltipText.string = text;
        tooltipWindow.front;
    }, {
        "Creating new tooltip window...".postln;
        tooltipWindow = Window("Tooltip", Rect(x, y, text.size * 8 + 20, 20), resizable: false, border: false)
            .alwaysOnTop_(true)
            .drawFunc_({
                Pen.fillColor = Color.white;
                Pen.fillRect(tooltipWindow.bounds);
            });
        tooltipText = StaticText(tooltipWindow, Rect(5, 2, text.size * 8 + 10, 16))
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
        tooltipText = nil;
    };
};

// Crear un knob
knob = Knob(window, Rect(10, 10, 280, 20))
    .value_(0.5) // Valor inicial del knob
    .action_({
        var x, y;
        x = window.bounds.left + knob.bounds.left + (knob.bounds.width / 2);
        y = window.bounds.top + knob.bounds.top + knob.bounds.height;
        if (tooltipWindow.notNil) {
            tooltipText.string = "Valor actual: " ++ knob.value.round(0.01).asString("%.2f");
        } {
            showTooltip.value(x, y, "Valor actual: " ++ knob.value.round(0.01).asString("%.2f"));
        }
    });

// Asignar eventos de ratón al knob
knob.mouseOverAction_({
    var x, y;
    x = window.bounds.left + knob.bounds.left + (knob.bounds.width / 2);
    y = window.bounds.top + knob.bounds.top + knob.bounds.height;
    showTooltip.value(x, y, "Valor actual: " ++ knob.value.round(0.01).asString("%.2f"));
});

knob.mouseLeaveAction_({
    "Mouse left knob.".postln;
    hideTooltipTask = Task({
        2.wait;
        {hideTooltip.value}.defer;
    }).play;
});

// Mostrar la ventana
window.onClose_({ hideTooltip.value; });
window.front;
)
