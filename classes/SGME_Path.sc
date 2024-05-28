SGME_Path {
	classvar <appName = "SynthiGME";
	classvar <rootPath = nil;
	classvar <imagesPath = nil;

	*initClass {
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
	}
}