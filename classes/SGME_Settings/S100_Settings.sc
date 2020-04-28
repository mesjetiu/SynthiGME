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

SGME_Settings {
	classvar settingsDictionary = nil;


	// Cuando se llama a esta función se devuelve un diccionario con las configuraciones. Solo se crea una vez.
	*get {
		if(settingsDictionary == nil, {
			this.readSettings;
		})
		^settingsDictionary;
	}

	// Se puede forzar la relectura de configuraciones desde fuera.
	*readSettings {
		settingsDictionary = Dictionary.newFrom(SGME_Settings.settings);
	}
}