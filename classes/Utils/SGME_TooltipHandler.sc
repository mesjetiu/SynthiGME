SGME_TooltipHandler {
	var <view, <tooltipWindow, <tooltipText, hideTooltipTask, funcParam, prefix;
	var makeTextFunc = nil; // función dada externamente para crear el mensaje el tooltip
	var offsetLeft, offsetTop;
	var tooltipClosable = true; // bandera en false cuando el ratón está sobre el tooltip. Para evitar bucles infinitos de abrir y cerrar.

	classvar <>enabled = true;

	*new { |view, min = 0, max = 10, funcParam = nil, offLeft = 0, offTop = 0, prefix = "Valor:", funcMakeText = nil|
		^super.new.init(view, min, max, funcParam, offLeft, offTop, prefix, funcMakeText);
	}

	init { |v, min, max, function, offLeft, offTop, pref, makeText|
		view = v;
		offsetLeft = offLeft;
		offsetTop = offTop;
		prefix = pref;
		makeTextFunc = makeText;
		if (function.isNil) {
			funcParam = {|v| v.linlin(0, 1, min, max).round(0.01)};
		} {
			funcParam = function;
		};
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
				0.wait;
				while {tooltipClosable == false} {0.001.wait};
				{ this.hideTooltip }.defer;
			}).play;
		};

		view.action = {
			this.updateTooltipPosition;
		};
	}

	updateTooltipPosition {
		var x, y, knobPos, windowBounds, value;

		if (SGME_TooltipHandler.enabled.not) {^this};

		knobPos = view.absoluteBounds;
		windowBounds = Window.availableBounds;

		x = knobPos.left + (knobPos.width / 2) + offsetLeft;
		y = knobPos.top + knobPos.height + offsetTop;
		y = windowBounds.height - y;

		if (x + 100 > windowBounds.right) {
			x = windowBounds.right - 100;
		};
		if (y + 20 > windowBounds.bottom) {
			y = windowBounds.bottom - 20;
		};
		if (y < windowBounds.top) {
			y = windowBounds.top;
		};

		("Updated tooltip position: " ++ x ++ ", " ++ y).postln;
		//value = funcParam.value(view.value).asString("%.2f");
		this.showTooltip(x, y);
	}

	makeText {
		if (makeTextFunc.isNil.not) {^makeTextFunc.()} {
			var value = funcParam.value(view.value).asString("%.2f");
			^(prefix + value);
		}
	}

	showTooltip { |x, y|
		var text = this.makeText;
		var rect = Rect(x, y, (text.size+1) * 6, 20);
		hideTooltipTask.notNil.if({ hideTooltipTask.stop });

		if (SGME_TooltipHandler.enabled.not) {^this};

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
				Pen.fillColor = Color.red;
				Pen.fillRect(tooltipWindow.bounds);
			});

			tooltipWindow.view.mouseEnterAction = {
				tooltipClosable = false;
			};
			tooltipWindow.view.mouseLeaveAction = {
				tooltipClosable = true;
			};

			tooltipText = StaticText(tooltipWindow, Rect(5, 2, (text.size) * 10, 16))
			.string_(text)
			.align_(\left)
			.background_(Color.grey(0.9))
			.font_(Font("Helvetica", 12));

			SGME_GUIShortcuts.makeShortcuts(SynthiGME.instance.guiSC.panels[0], tooltipWindow, SynthiGME.instance);
			// se le pasa un panel para que actúe con los shortcuts propios de los paneles, no del tooltip.

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
		if (SGME_TooltipHandler.enabled.not) {^this};
		if (tooltipWindow.notNil) {
			var string = this.makeText; //prefix + funcParam.value(view.value).asString("%.2f");
			("Updating tooltip text to: " ++ string).postln;
			tooltipText.string = string;
		}
	}
}
