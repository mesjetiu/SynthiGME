// Usar esta extensión sólo en caso de crear apliación standalone


+ WindowsPlatform {

	initPlatform {
		var path_general;
		super.initPlatform;
		// Obtenemos el path superior a la carpeta de SC:
		path_general = PathName(Platform.resourceDir).pathOnly;
		// Directorio de grabaciones en "Recordings"
		recordingsDir = path_general +/+ "Recordings" +/+ "Audio";
	}

	// Con estos overrides se consigue que SC no busque en archivos del sistema y del usuario, ni sobrescriba ninguna configuración previa de SC en la máquina.
	systemAppSupportDir {
		/*var path_general = PathName(Platform.resourceDir).pathOnly;
		^path_general +/+ ".SC_Support" +/+ "System_support";*/
		^Platform.resourceDir +/+ "SC_Support" +/+ "System_support";
	}
	userAppSupportDir {
		/*var path_general = PathName(Platform.resourceDir).pathOnly;
		^path_general +/+ ".SC_Support" +/+ "User_support";*/
		^Platform.resourceDir +/+ "SC_Support" +/+ "User_support";
	}

	// Ubicación de "startup.scd"
	userConfigDir {
		var path_general = PathName(Platform.resourceDir).pathOnly;
		^path_general +/+ "Config";
	}

	// Ubicación de "extensions", donde se ubicará SynthiGME
	userExtensionDir {
		var path_general = PathName(Platform.resourceDir).pathOnly;
		^path_general +/+ ".Extensions";
	}
	//*userExtensionDir { ^thisProcess.platform.userExtensionDir }

}