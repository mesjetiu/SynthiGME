# Synthi GME

## Sobre el Quark

Este repositorio contiene un Quark de SuperCollider, consistente en un emulador del sintetizador analógico EMS Synthi 100 dell Gabinete de Música Electroacústica de Cuenca.
Esta aplicación forma parte del Trabajo de Fin de Máster en "Arte Sonoro" del autor, en la Facultad de Bellas Artes de la Universidad de Barcelona, en el 2019/2020, bajo la dirección de [José Manuel Berenguér Alarcón](http://www.sonoscop.net/jmb/).

El nombre de la aplicación, "GME", tiene un significado recursivo: "GME Modular Emulator", haciendo un claro guiño al Gabinete de Música Electroacústica de Cuenca. Esta recursividad puede leerse como la imposibilidad de emular de manera fiel un sintetizador analógico, como es el del GME, por medios digitales, por más que se itere el trabajo del programador.  

Las fotografías de los paneles del GUI de la aplicación, son del Synthi 100 del GME de Cuenca, al cual he podido acceder en diversas ocasiones los últimos meses gracias a la gentileza y generosidad de [Julio Sanz Vázquez](https://www.facebook.com/juliosanzvaz) y [Sylvia Molina Muro](https://www.facebook.com/sylvia.mmuro).

Esta herramienta tiene primeramente un fin pedagógico. Quien quiera puede ver su código, mejorarlo, aprender de él, Conocer el funcionamiento básico de un sintetizador modular y, en concreto, el coloso Synthi 100 de EMS.

Olaf. 2009/12/23

## Instalación

La aplicación está escrita en [SuperCollider](https://github.com/supercollider/supercollider). Los requisitos previos a la instalación son los siguientes:

* [SuperCollider](https://github.com/supercollider/supercollider).
* [SC3-plugins](https://github.com/supercollider/sc3-plugins). Ciertos UGens son de este paquete.
* [git](https://git-scm.com/) para poder descargar el Quark.

### Instalación desde el IDE de SuperCollider

Para instalar el Quark de Synth GME, ejecutar en SuperCollider IDE:

	// Instala el Quark desde el repositorio remoto:
    Quarks.install("https://github.com/mesjetiu/SynthiGME.git");
    // Recompila las clases para poder utilizarlas (también con Ctrl+Shift+L)
    thisProcess.recompile;

### Instalación desde la línea de comandos del sistema

Otro modo de instalar el Quark es a través de la línea de comandos del sistema. Descargar el archivo ["install_SGME.scd"](https://github.com/mesjetiu/SynthiGME/blob/master/installation/install_SGME.scd) del repositorio, y ejecutar la siguiente instrucción:

    sclang ./install_SGME.scd



## Ejecutar la aplicación

Cuando se ejecuta Synthi GME no es posible usar más clases en la misma sesión de SuperCollider. Incluso el servidor de sonido es reiniciado cuando se abre la aplicación, ya que es necesario una configuración ad hoc para una correcta ejecución.

Del mismo modo que en la instalación, existen dos modos de ejecutar la aplicación: desde el IDE de Supercollider (scide) o desde una terminal de comandos del sistema.



### Ejecutar la aplicación desde el IDE de SuperCollider


	// Se instancia la clase y se llama al método "run".
    ~synthi = SynthiGME().run;
    
    // Para cerrar la aplicación
    ~synthi.close;

Una vez abierta la aplicación, aparecerán un conjunto de ventanas que representan los diversos paneles del Synthi 100, y todo estará listo para crear sonidos.

