
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


// Instalar SynthiGME desde directorio local
(
Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/SynthiGME");
thisProcess.recompile;
)

// Desinstalar SynthiGME (versión de directorio local)
(
Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/SynthiGME");
Quarks.clear;
thisProcess.recompile;
)

