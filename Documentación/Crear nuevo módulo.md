1. Crear una nueva rama para desarrollo del módulo.

1. Crear la clase dummy. 
	- Copiar una clase existente con un comportamiento similar en cuanto a entradas y salidas. 
	- Adaptar nombre, nombre del synth, entradas y salidas, métodos.
	- Crear un bypass en el synth (para probar más adelante)
	Comprobar que compila.

2. Editar SynthiGME.sc
	- Añadir variable modul[...] al principio de la clase.
	- Añadir línea en *initClass.
	- Añadir declaración de [modul].addSynthDef en init.
	- En run():
		- Sumar uno a splashWindow.numSteps, al principio del método.
		Añadir instanciación de módulo en server.waitForBoot().
		- Arrancar synths de módulo.
	- En este punto debería poder compilar y ejecutarse sin fallos.
	
3. Editar SGME_PatchbayAudio (si procede, y hacer lo mismo con SGME_PatchVoltage):
	- Añadir módulo en ordenateIntputsOutputs() con el número que corresponde en el panel tanto de input como de output (si los hubiera)
	- Añadir entrada del módulo en connect().

4. Editar SynthiGME_run.sc
	- En run():
		- Añadir entrada del módulo en "conexiones de entrada y salida de cada módulo en el patchbay de audio/voltage".

5. Añadir mandos en el panel correspondiente, con los códigos OSC de cada parámetro.

6. Editar "synthiGME_setParameterOSC:
	- Añadir entrada del módulo con código OSC.
	- Forzar "level = 1" (si procede) en el módulo para hacer bypass.
	- En este punto debería poder compilar, ejecutarse sin fallos. Debería poder utilizarse el módulo aunque sea dummy.
	
7. Editar forbidenRows y forbidenColumns en makeNodeTable() y makeRow() en Panel 5 y Panel 6 según corresponda para que estén habilitados los nodos.
	
8. Editar SGME_PatchbayVoltage (si procede): (hacer lo equivalente de SGME_PatchbayAudio) y editar SynthiGME del mismo modo. Debería compilar y poder ser conectado en Voltage (desde el punto 4).

9. Añadir views al panel correspondiente. Copiar alguno ya hecho y adaptarlo.

10. Añadir las variables en SynthiGME.getFullState() para poder ser recuperadas.
	
