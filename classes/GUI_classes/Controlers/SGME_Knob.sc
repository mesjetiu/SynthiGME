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
	var <tooltipHandler;
	var <tooltip; // true o false

	*initClass {
		Class.initClassTree(Knob);
		Class.initClassTree(Blink_view);
		Class.initClassTree(SGME_TooltipHandler);
	}

	*new {|parent, bounds, min = 0, max = 10, tooltipEnable = true|
		var instance = super.new(parent, bounds);
		instance.init(min, max, tooltipEnable);
		^instance
	}

	init {|min, max, tooltipEnable|
		blinkView = Blink_view(this, 1, 0.1);
		tooltip = tooltipEnable;
		if (tooltip) {
			tooltipHandler = SGME_TooltipHandler.new(this, min, max);
		}
	}

	value_ {|val|
		super.value = val;
		blinkView.blink;
		if (tooltip) {
			tooltipHandler.updateTooltip(val);
		}
	}
}
