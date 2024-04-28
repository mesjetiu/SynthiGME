# Synthi GME
Pincha en la imagen para ver una demostración:

[![Alt text](https://img.youtube.com/vi/EBUEooWrfBE/0.jpg)](https://www.youtube.com/watch?v=EBUEooWrfBE)

## Sobre el Quark

Este repositorio contiene un Quark de SuperCollider, consistente en un emulador del sintetizador analógico EMS Synthi 100 del Gabinete de Música Electroacústica de Cuenca.
Esta aplicación forma parte del Trabajo de Fin de Máster en "Arte Sonoro" del autor, en la Facultad de Bellas Artes de la Universidad de Barcelona, en el 2019/2020, bajo la dirección de [José Manuel Berenguer Alarcón](http://www.sonoscop.net/jmb/).

El nombre de la aplicación, "GME", tiene un significado recursivo: "GME Modular Emulator", al tiempo que se refiere al Gabinete de Música Electroacústica de Cuenca.

Las fotografías de los paneles del GUI de la aplicación, son del Synthi 100 del GME de Cuenca.

Esta herramienta tiene primeramente un fin pedagógico. Quien quiera puede ver su código, mejorarlo, aprender de él, Conocer el funcionamiento básico de un sintetizador modular y, en concreto, el coloso Synthi 100 de EMS.


## Instalación

La aplicación está escrita en [SuperCollider](https://supercollider.github.io/download). Los requisitos previos a la instalación son los siguientes:

* [SuperCollider](https://supercollider.github.io/download).
* [git](https://git-scm.com/downloads) para poder descargar el Quark.

### Instalación desde el IDE de SuperCollider

Para instalar el Quark de Synth GME, ejecutar en SuperCollider IDE:

	// Instala el Quark desde el repositorio remoto:
    Quarks.install("SynthiGME");
    
    // Recompila las clases para poder utilizarlas (también con Ctrl+Shift+L)
    thisProcess.recompile;
    
    // Si se quiere desinstalar:
    Quarks.uninstall("SynthiGME");


### Ejecutar la aplicación desde el IDE de SuperCollider


	// Se instancia la clase y se llama al método "run" para arrancar.
    ~synthi = SynthiGME().run;
    
    // Para cerrar la aplicación
    ~synthi.close;

Una vez abierta la aplicación, aparecerán un conjunto de ventanas que representan los diversos paneles del Synthi 100, y todo estará listo para crear sonidos.
¡Disfruta!

