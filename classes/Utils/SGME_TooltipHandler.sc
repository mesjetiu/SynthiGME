SGME_TooltipHandler {
	var <view, <tooltipWindow, <tooltipText, hideTooltipTask, funcParam;

	*new { |view, min = 0, max = 10, funcParam = nil|
		^super.new.init(view, min, max, funcParam);
	}

	init { |v, min, max, function|
		view = v;
		if (function.isNil) {
			funcParam = {|v| v.linlin(0, 1, min, max)};
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
				0.1.wait;
				{ this.hideTooltip }.defer;
			}).play;
		};

		view.action = {
			this.updateTooltipPosition;
		};
	}

	updateTooltipPosition {
		var x, y, knobPos, windowBounds, value;

		knobPos = view.absoluteBounds;
		windowBounds = Window.availableBounds;

		x = knobPos.left + (knobPos.width / 2);
		y = knobPos.top + knobPos.height;
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
		value = funcParam.value(view.value).round(0.01).asString("%.2f");
		this.showTooltip(x, y, "Valor actual: " ++ value);
	}

	showTooltip { |x, y, text|
		var rect = Rect(x, y, text.size * 6, 20);
		hideTooltipTask.notNil.if({ hideTooltipTask.stop });
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
			var string = "Valor actual: " ++ funcParam.value(view.value).round(0.01).asString("%.2f");
			("Updating tooltip text to: " ++ string).postln;
			tooltipText.string = string;
		}
	}
}
