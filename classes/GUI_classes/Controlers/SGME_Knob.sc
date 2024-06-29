/*
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_Knob : Knob {
    var <blinkView = nil;
    var tooltipWindow, tooltipText, hideTooltipTask;

    *initClass {
        Class.initClassTree(Knob);
        Class.initClassTree(Blink_view);
    }

    *new {|parent, bounds|
        var instance = super.new(parent, bounds);
        instance.init();
        ^instance
    }

    init {
        blinkView = Blink_view(this, 1, 0.1);
        this.setupTooltip;
    }

    value_ {|val|
        super.value = val;
        blinkView.blink;
        this.updateTooltip("Valor actual: " ++ val.round(0.01).asString("%.2f"));
    }

    setupTooltip {
        this.mouseEnterAction_({
            "Mouse entered knob.".postln;
            this.updateTooltipPosition;
        });

        this.mouseLeaveAction_({
            "Mouse left knob.".postln;
            hideTooltipTask = Task({
                0.1.wait;
                {this.hideTooltip.value}.defer;
            }).play;
        });

        this.action_({
            this.updateTooltipPosition;
        });
    }

    updateTooltipPosition {
        var x, y, knobPos, windowBounds;

        // Obtener la posición absoluta del knob
        knobPos = this.absoluteBounds;

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
        this.showTooltip(x, y, "Valor actual: " ++ this.value.round(0.01).asString("%.2f"));
    }

    showTooltip { |x, y, text|
        hideTooltipTask.notNil.if({ hideTooltipTask.stop });  // Detener cualquier tarea de ocultar tooltip en curso
        ("Attempting to show tooltip at: " ++ x ++ ", " ++ y).postln;
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
                    Pen.fillColor = Color.red;  // Color de fondo rojo para asegurar que se ve
                    Pen.fillRect(tooltipWindow.bounds);
                });
            tooltipText = StaticText(tooltipWindow, Rect(5, 2, text.size * 8 + 10, 16))
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

    updateTooltip { |text|
        if (tooltipWindow.notNil) {
            ("Updating tooltip text to: " ++ text).postln;
            tooltipText.string = text;
        }
    }
}
