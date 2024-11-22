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
Platform.userConfigDir // Carpeta de configuración del usuario (dentro de carpeta del usuario)
Platform.userAppSupportDir // Carpeta de soporte del usuario (detro de carpeta del usuario)
Platform.userExtensionDir // Carpeta de 'Extensions' del usuario (dentro de carpeta de soporte)
Platform.recordingsDir // Carpeta de 'Recordings' del usuario (dentro de carpeta del usuario)
```

Para empaquetar una versión portable de SuperCollider + SynthiGME, es necesario "redirigir" estas direcciones de archivos, de forma que esta versión se autoreferencie y no entre en conflicto con una eventual instalación de SuperCollider. Se ha comprobado que la carpeta de instalación referenciada ```Platform.resourceDir``` es siempre la carpeta donde se encuentra el ejecutable ```sclang```, independientemente de que SuperCollider haya sido instalado o simplemente copiado manualmente. Lo mismo ocurre con ```Platform.classLibraryDir```, que también depende de la situación actual de ```sclang```.

La solución pasa por extender la clase ```Platform```, pero, aun así, ```sclang``` parece buscar estas carpetas preguntando al sistema operativo antes de compilar la propia clase ```Platform```, con lo que esta aproximación no sirve para cambiar las rutas que ```sclang``` compila al inicio.

## Estructura de directorios de SynthiGME standalone

La estructura de directorios base que construiremos es la siguiente:

```
SynthiGME_standalone/
|
----Recordings/
----SuperCollider/
|   |
|   ----SCClassLibrary/
|       |
|       ----SynthiGME/
----Support/
|   |
|   ----System_support/
|   ----User_support/
|   ----User_config/
|       |
|       ----startup.scd (opcional)
----SynthiGME.exe
```


Los directorios ```Recordings```, ```Support``` y los que penden de este, son creados automáticamente por ```sclang``` una vez quede configurado el archivo de configuración ```sclang_config.yaml``` (ver más abajo).

El directorio de Supercollider se ha de extraer de ```Program Files```o similar en Windows, lugar donde ha sido instalado SuperCollider con su instalador. Este directorio contiene todos los ejecutables, librerías y clases necesarias para hacer funcionar SuperCollider. Una vez aislado, SuperCollider puede ser eliminado completamente del sistema sin ningún peligro para nuestra compilación de SynthiGME standalone.


## Actualización de paths en Platform

Las direcciones a las que se apunta en Platform pueden ser modificadas para cada sistema operativo, modificando los archivos de las clases ```Platform``` (```Platform.sc```) y las clases herederas correspondientes al sistema operativo. Todos estos archivos están en la carpeta raiz de SuperCollider, en ```/SCClassLibrary/Platform/```.

Añadir las extensiones correspondientes de las clases implicadas:
```
// Archivos a añadir en el caso de Windows:

Platform_extension.sc
WindowsPlatform_extension.sc  
```
En el código fuente de ```SynthiGME``` estos archivos se encuentran en el directorio ```standalone```con la extensión ```.scd```para que no sean compilados por accidente en un sistema operativo distinto o en contextos diferentes al modo standalone. Han de ser renombrados con la extensión ```.sc```al utilizarlos en una compilación concreta.

## Incluir fuentes de SynthiGME en la librería de clases de SuperCollider

La carpeta de ```SynthiGME```, en lugar de estar ubicada en quarks descargados o en extensions, ha de ser incluida en el directorio de la librería de clases incluida en la compliación de SuperCollider.

## Creación de sclang_config.yaml

El archivo ```sclang_config.yaml``` puede ser editado para activar y desactivar diversos paths de compilación al arranque de ```sclang```.
En primer lugar es necesario excluir los paths por defecto de compilación:

```
excludeDefaultPaths: true
```

De este modo ```sclang``` no compilará ningun quark ni extensión que eventualmente el usuario tenga instalados en su sistema.

Desde ahí, ahora se pueden incluir nuevos paths. En nuestro caso, sólo incluiremos el path de la librería de clases incluidas en SuperCollider:
```
includePaths:
    -   ./SCClassLibrary
```

El archivo ```sclang_config.yaml```ha de estar incluido en el directorio de SynthiGME, de forma que pueda ser arrancado ```sclang``` con el comando:

```
./sclang -l ./SCClassLibrary/SynthiGME/standalone/sclang_config.yaml
```

Así, sclang compilará únicamente la librería de clases, incluyendo SynthiGME, que está en su interior. Nótese que para que funcione correctamente el direccionamiento del YAML al ejecutar ```sclang```, ha de ser ejecutado desde el directorio donde este se encuentra. Si no, habría que pasarle una dirección absoluta para el archivo YAML.

## Ejecutable de arranque de la aplicación

Se creará un ejecutable (en Python, aún por implementar) que detecte la dirección absoluta de su propia ubicación. Dicho ejecutable se encargará de abrir un proceso en el sistema operativo con ```sclang``` pasándole como argumento la dirección exacta del ```sclang_config.yaml```. 

El resultado será una aplicación portable, sin necesidad de instalación, sin interacción con evetuales instalaciones de SuperCollider en el mismo equipo y enteramente distribuible.

## Limpieza del directorio de SuperCollider

Puesto que solo se usa SuperCollider para ejecutar Synthi GME, se pueden eliminar ciertos archivos y directorios inútiles que no alteran la funcionalidad, de forma que se aligera el tamaño general del paquete completo.