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


SGME_Button : Button {

	var <blinkView = nil;
	var <tooltipHandler;
	var <tooltip; // true o false
	//*********************************************************************************************

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(Button);
		Class.initClassTree(Blink_view);
		Class.initClassTree(SGME_TooltipHandler);
	}

	*new {|parent, bounds, min = 0, max = 1, funcParam = nil, tooltipEnable = true|
		var instance = super.new(parent, bounds);
		instance.init(min, max, tooltipEnable, funcParam);
		^instance
	}

	init {|min, max, tooltipEnable, funcParam|
		blinkView = Blink_view(this, 1, 0.1);
		tooltip = tooltipEnable;
		if (tooltip) {
			tooltipHandler = SGME_TooltipHandler.new(this, min, max, funcParam, prefix: "Estado:");
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
