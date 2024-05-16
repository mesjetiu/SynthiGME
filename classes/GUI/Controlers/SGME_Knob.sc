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

	//*********************************************************************************************

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(Knob);
		Class.initClassTree(Blink_view);
	}

	*new {|parent, bounds|
		var instance = super.new(parent, bounds);
		instance.init();
		^instance
	}

	init {
		blinkView = Blink_view(this);
	}

	value_ {|value|
		super.value = value;
		this.blinkView.blink;
	}
}
