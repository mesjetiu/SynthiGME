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