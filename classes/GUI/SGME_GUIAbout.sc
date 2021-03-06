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

Copyright 2020 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/


// Ventana "Acerca de Synthi GME" (Por hacer)
SGME_GUIAbout{

	var <window;
	var <synthiGME;

	//*********************************************************************************************

	*initClass {
		// Inicializa otras clases antes de esta
		// Class.initClassTree(SGME_GUIShortcuts);
	}

	*new {|synthi|
		^super.new.init(synthi);
	}

	init {|synthi|
		synthiGME = synthi;
	}

	// Crea una ventana con la información de atajos de teclado
	makeWindow {

	}

}
