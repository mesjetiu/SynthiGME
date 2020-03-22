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