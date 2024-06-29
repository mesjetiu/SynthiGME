SGME_TooltipHandler {
    var view, tooltipWindow, tooltipText, hideTooltipTask;

    *new { |v|
        ^super.new.init(v);
    }

    init { |v|
        view = v;
        this.setupTooltip;
        ^this;
    }

    setupTooltip {
        view.mouseEnterAction = {
            "Mouse entered knob.".postln;
            this.updateTooltipPosition;
        };

        view.mouseLeaveAction = {
            "Mouse left knob.".postln;
            hideTooltipTask = Task({
                0.1.wait;
                {this.hideTooltip.value}.defer;
            }).play;
        };

        view.action = {
            this.updateTooltipPosition;
        };
    }

    updateTooltipPosition {
        var x, y, knobPos, windowBounds;

        // Obtener la posición absoluta del knob
        knobPos = view.absoluteBounds;

        // Obtener los límites de la pantalla
        windowBounds = Window.availableBounds;

        // Calcular la posición del tooltip basado en la posición absoluta del knob
        x = knobPos.left + (knobPos.width / 2);
        y = knobPos.top + knobPos.height;

        // Ajustar la coordenada y para invertir el comportamiento si es necesario
        y = windowBounds.height - y;

        // Asegurarse de que el tooltip no se salga de los límites de la ventana
        if (x + 100 > windowBounds.right) {  // Ajuste basado en el ancho del tooltip
            x = windowBounds.right - 100;
        };
        if (y + 20 > windowBounds.bottom) {  // Ajuste basado en el alto del tooltip
            y = windowBounds.bottom - 20;
        };
        if (y < windowBounds.top) {  // Asegurarse de que el tooltip no se salga por arriba
            y = windowBounds.top;
        };

        ("Updated tooltip position: " ++ x ++ ", " ++ y).postln;
        this.showTooltip(x, y, "Valor actual: " ++ view.value.round(0.01).asString("%.2f"));
    }

    showTooltip { |x, y, text|
		var rect = Rect(x, y, text.size * 6, 20);
        hideTooltipTask.notNil.if({ hideTooltipTask.stop });  // Detener cualquier tarea de ocultar tooltip en curso
        ("Attempting to show tooltip at: " ++ x ++ ", " ++ y).postln;
        tooltipWindow.isNil.not.if({
            "Tooltip exists, updating position...".postln;
            tooltipWindow.bounds = rect;
            tooltipText.string = text;
            tooltipWindow.front;
        }, {
            "Creating new tooltip window...".postln;
            tooltipWindow = Window("Tooltip", rect, resizable: false, border: false)
                .alwaysOnTop_(true)
                .drawFunc_({
                    Pen.fillColor = Color.red;  // Color de fondo rojo para asegurar que se ve
                    Pen.fillRect(tooltipWindow.bounds);
                });
            tooltipText = StaticText(tooltipWindow, Rect(5, 2, text.size * 10, 16))
                .string_(text)
                .align_(\left)
                .background_(Color.grey(0.9))
                .font_(Font("Helvetica", 12));
            tooltipWindow.front;
        });
    }

    hideTooltip {
        if (tooltipWindow.notNil) {
            "Closing tooltip...".postln;
            tooltipWindow.close;
            tooltipWindow = nil;
            tooltipText = nil;
        }
    }

    updateTooltip { |val|
        if (tooltipWindow.notNil) {
			var string = "Valor actual: " ++ val.round(0.01).asString("%.2f");
            ("Updating tooltip text to: " ++ string).postln;
            tooltipText.string = string;
        }
    }
}


