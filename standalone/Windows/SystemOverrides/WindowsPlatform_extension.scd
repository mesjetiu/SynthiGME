+ WindowsPlatform {

	initPlatform {
		var path_general;
		super.initPlatform;
		// Obtenemos el path superior a la carpeta de SC:
		path_general = PathName(Platform.resourceDir).pathOnly;
		// Directorio de grabaciones en "Recordings"
		recordingsDir = path_general +/+ "Recordings";
	}

	// Con estos overrides se consigue que SC no busque en archivos del sistema y del usuario, ni sobrescriba ninguna configuración previa de SC en la máquina.
	systemAppSupportDir {
		var path_general = PathName(Platform.resourceDir).pathOnly;
		^path_general +/+ "Support" +/+ "System_support";
	}
	userAppSupportDir {
		var path_general = PathName(Platform.resourceDir).pathOnly;
		^path_general +/+ "Support" +/+ "User_support";
	}

	// Ubicación de "startup.scd"
	userConfigDir {
		var path_general = PathName(Platform.resourceDir).pathOnly;
		^path_general +/+ "Support" +/+ "User_config";
	}
}