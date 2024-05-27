// Comprobar con (ya está implementado pero no testeado en otros SO)
SynthiGME.isExtensionInstalled
SynthiGME.isQuarkInstalled
SynthiGME.getAppPath


(
~checkQuarkInstalled = { |quarkName|
	Quarks.installed.any ({ |quark|
        quark.name == quarkName
    })
};
)


~checkQuarkInstalled.("SynthiGME")


// Ejemplo de uso
(
if (~checkQuarkInstalled.value("SynthiGME")) {
    "El quark SynthiGME está instalado.".postln;
} {
    "El quark SynthiGME no está instalado.".postln;
};
)



////////////////////////////////////////



(
~isQuarkInstalled = { |quarkName|
    Quarks.installed.any { |quark|
        quark.name == quarkName
    }
};

~isExtensionInstalled = { |extensionName|
	var userExtensions = Platform.userExtensionDir +/+ extensionName +/+ "SynthiGME.quark";
    var systemExtensions = Platform.systemExtensionDir +/+ extensionName +/+ "SynthiGME.quark";

    File.exists(userExtensions).postln ||  File.exists(systemExtensions).postln;
};
)

~isExtensionInstalled.("MiClase")
~isQuarkInstalled.("MiClase")
// hasta aquí funciona

(
~getResourcePath = { |name|
    if (~isQuarkInstalled.(name)) {
        var quarkPath = Quarks.quarkNameAsLocalPath(name);
        "Quark encontrado en: %".format(quarkPath).postln;
        quarkPath
    } {
        if (~isExtensionInstalled.(name)) {
            var userExtensionPath = Platform.userExtensionDir +/+ name;
            var systemExtensionPath = Platform.systemExtensionDir +/+ name;
			var extensionPath = if (File.exists(userExtensionPath +/+ "SynthiGME.quark")) {
                userExtensionPath
            } {
                systemExtensionPath
            };
            "Extensión encontrada en: %".format(extensionPath).postln;
            extensionPath
        } {
            "Ni Quark ni Extensión encontrados con el nombre: %".format(name).postln;
            nil
        }
    }
};

)

// Ejemplo de uso
~getResourcePath.value("SynthiGME");
~getResourcePath.value("MiClas");
