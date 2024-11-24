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

SGME_Path {
	classvar <appName = "SynthiGME";
	classvar <rootPath = nil;
	classvar <imagesPath = nil;

	*initPath {
		rootPath = SGME_Path.getAppPath;
		imagesPath = rootPath.asString +/+ "classes" +/+ "GUI_classes" +/+ "images";
	}


	*isQuarkInstalled { |quarkName|
		if (quarkName.isNil) {quarkName = appName};
		^Quarks.isInstalled(quarkName)
	}

	// No se llama esta función en principio
	*isExtensionInstalled { |extensionName|
		var userExtensions, systemExtensions;
		if (extensionName.isNil) {extensionName = appName};
		userExtensions = Platform.userExtensionDir +/+ extensionName +/+ "SynthiGME.quark";
		systemExtensions = Platform.systemExtensionDir +/+ extensionName +/+ "SynthiGME.quark";

		^(File.exists(userExtensions) ||  File.exists(systemExtensions))
	}

	*getAppPath { |name|
		if (name.isNil) {name = appName};
		/*
		if (SGME_Path.isQuarkInstalled(name)) {
			var quarkPath = Quarks.quarkNameAsLocalPath(name);
			"Quark encontrado en: %".format(quarkPath).postln;
			^quarkPath
		} { // Si no es Quark, entonces es extensión
			//if (SynthiGME.isExtensionInstalled.(name)) {
			var userExtensionPath = Platform.userExtensionDir +/+ name;
			var systemExtensionPath = Platform.systemExtensionDir +/+ name;
			var extensionPath = if (File.exists(userExtensionPath +/+ "SynthiGME.quark")) {
				userExtensionPath
			} {
				systemExtensionPath
			};
			"Extensión encontrada en: %".format(extensionPath).postln;
			^extensionPath
			/*} {
			"Ni Quark ni Extensión encontrados con el nombre: %".format(name).postln;
			nil
			}*/
		}
		*/

		switch (true)
		// Si está integrado en el directorio SCClassLibrary, donde están todas las clases de SuperCollider (no Quarks, no Extensions).
		{File.exists(Platform.classLibraryDir +/+ name +/+ "SynthiGME.quark")} {
			var libraryPath = Platform.classLibraryDir +/+ name;
			("Path = " + libraryPath).postln;
			//SynthiGME.instance.executionMode = "standalone";
			^libraryPath;
		}
		// Si está integrado en otro directorio al mismo nivel que SuperCollider. Es modo standalone también. No es ni extensión ni quark
/*		{
			var path = PathName(PathName(Platform.classLibraryDir).pathOnly).pathOnly;
			File.exists(path +/+ name +/+ "SynthiGME.quark")} {
			var synthiPath = PathName(PathName(Platform.classLibraryDir).pathOnly).pathOnly +/+ name;
			("Path = " + synthiPath).postln;
			SynthiGME.instance.executionMode = "standalone";
			^synthiPath;
		}
		*/
		// Si es un Quark
		{SGME_Path.isQuarkInstalled(name)} {
			var quarkPath = Quarks.quarkNameAsLocalPath(name);
			^quarkPath;
		}
		// Si no es nada de lo anterior, ha de ser una extensión
		{
			var userExtensionPath = Platform.userExtensionDir +/+ name;
			var systemExtensionPath = Platform.systemExtensionDir +/+ name;
			var extensionPath = if (File.exists(userExtensionPath +/+ "SynthiGME.quark")) {
				userExtensionPath
			} {
				systemExtensionPath
			};
			^extensionPath
		}
	}
}