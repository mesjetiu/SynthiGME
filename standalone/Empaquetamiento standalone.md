# Empaquetamiento de aplicación standalone

## Objetivo y problemática

El objetivo es construir una rutina que permita empaquetar SynthiGME junto con su framework SuperCollider en una sola carpeta portable. De cara al usuario existe así una sola aplicación, SynthiGME, y no ha de entrar en conflicto con ninguna eventual instalación de SuperCollider ni con la existencia de otras versiones de SynthiGME en el mismo equipo. 

SynthiGME fue creada como una "extension" o "quark" en SuperCollider, lo que significa que para hacerlo funcionar es necesario instalar SuperCollider y posteriormente descargar SynthiGME. El proceso de instalación de este último consiste en introducirlo en la carpeta de "extensions" del usuario o del sistema, lo cual ofrece una fricción extra, ya que no es evidente dónde sitúa SuperCollider dichas carpetas. Adicionalmente, para instalarlo y mantenerlo como quark es necesario instalar Git, lo cual suma complejidad al sistema. Se ha comprobado que el sistema de mantenimiento de quarks (instalación, actualización...) integrado en SuperCollider no está exento de fallos.

Además, la ejecución de las versiones del modo quark o extension, requiere abrir SuperCollider y ejecutar una línea de código, lo cual constituye más fricción para el usuario.

SuperCollider no tiene una versión portable o standalone que permita fácilmente ser aislada de su instalación en el sistema, ya que crea carpetas en el sistema a las cuales accede al arrancar ```sclang``` . Estas carpetas pueden contener archivos de configuración, extensiones, quarks, etc.:
```
Platform.systemAppSupportDir // Carpeta de instalación
Platform.resourceDir  // Carpeta donde se encuentra sclang (puede o no coincidir con carpeta de instalación)
Platform.classLibraryDir // Carpeta 'SCClassLibrary' (dentro de carpeta de resourceDir)
Platform.systemExtensionDir // carpeta 'Extensions' en carpeta de instalación
Platform.userHomeDir // Carpeta del usuario
Platform.userConfigDir // Carpeta de configuración del usuario (dentro de carpeta del usuario)
Platform.userAppSupportDir // Carpeta de soporte del usuario (detro de carpeta del usuario)
Platform.userExtensionDir // Carpeta de 'Extensions' del usuario (dentro de carpeta de soporte)
Platform.recordingsDir // Carpeta de 'Recordings' del usuario (dentro de carpeta del usuario)
```

Para empaquetar una versión portable de SuperCollider + SynthiGME, es necesario "redirigir" estas direcciones de archivos, de forma que esta versión se autoreferencie y no entre en conflicto con una eventual instalación de SuperCollider. Se ha comprobado que la carpeta de instalación referenciada ```Platform.resourceDir``` es siempre la carpeta donde se encuentra el ejecutable ```sclang```, independientemente de que SuperCollider haya sido instalado o simplemente copiado manualmente. Lo mismo ocurre con ```Platform.classLibraryDir```, que también depende de la situación actual de ```sclang```.


## Actualización de paths en Platform

Las direcciones a las que se apunta en Platform pueden ser modificadas para cada sistema operativo, modificando los archivos de las clases ```Platform``` (```Platform.sc```) y las clases herederas correspondientes al sistema operativo. Todos estos archivos están en la carpeta raiz de SuperCollider, en ```/SCClassLibrary/Platform/```.

Añadir las extensiones correspondientes de las clases implicadas en sus carpetas:
```
// Archivos a añadir en el caso de Windows:

Platform_extension.sc
/windows/WindowsPlatform_extension.sc  
```