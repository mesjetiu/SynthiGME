Quarks.installed[7].name == "SynthiGME"



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
