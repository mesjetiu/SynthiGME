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

/*
Para instalar el Quark Synthi GME es necesario tener Git en el sistema.
Synthi GME utiliza extensiones de SC3-plugins. Si no se instalan no funcionará.
*/



// Instalar SynthiGME desde GitHub
(
Quarks.install("https://github.com/mesjetiu/SynthiGME.git");
thisProcess.recompile;
)

// Desinstalar SynthiGME (versión de GitHub)
(
Quarks.uninstall("https://github.com/mesjetiu/SynthiGME.git");
Quarks.clear;
thisProcess.recompile;
)

// Ejecutar SynthiGME (comando recomendado): Atención: Se reiniciará el servidor de sonido.
o = SynthiGME().run

// Ejecutar y sincronizar otros dispositivos (experimental)
o = SynthiGME().run.pairDevice

// Cerrar
o.close