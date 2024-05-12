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


SGME_Connectable {
	// Clase abstracta. Contiene variables y métodos para llevar el conteo de conexiones en las matrices (patchbay). Los módulos que se puden conectar por medio de la matriz, heredan de esta clase. Cada vez que se haga o deshaga una conexión se se hace crecer o decrecer el recuento.

	var <inCount = 0;
	var <outCount = 0;

	inPlusOne {arg bool = true;
		if (bool == true, {
			inCount = inCount + 1;
		}, {
			inCount = inCount - 1;
		});
		this.synthRun;
	}

	outPlusOne {arg bool = true;
		if (bool == true, {
			outCount = outCount + 1;
		}, {
			outCount = outCount - 1;
		});
		this.synthRun;
	}
	synthRun {} // para sobrescribir
}

