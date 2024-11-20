+ Platform {


	initPlatform {
		// Obtenemos el path superior a la carpeta de SC:
		var path_general = PathName(Platform.resourceDir).pathOnly;
		// Método original sin cambios
		classLibraryDir = thisMethod.filenameSymbol.asString.dirname.dirname;
		helpDir = thisMethod.filenameSymbol.asString.dirname.dirname.dirname ++ "/Help";
		features = IdentityDictionary.new;
		// Directorio de grabaciones en "Recordings"
		recordingsDir = path_general +/+ "Recordings";
	}

	systemAppSupportDir {
		^Platform.resourceDir
	}

	userAppSupportDir {
		^Platform.resourceDir
	}

	systemExtensionDir {
		^Platform.resourceDir
	}
	*systemExtensionDir { ^Platform.resourceDir }

	userExtensionDir {
		^Platform.resourceDir
	}
	*userExtensionDir { ^Platform.resourceDir }

	userConfigDir {
		^Platform.resourceDir
	}
	*userConfigDir { ^Platform.resourceDir }
}