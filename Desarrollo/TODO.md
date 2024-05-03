# Implementar:
- Hacer modo "Verbose" que muestre en Post Window todos los mensajes OSC ejecutados.
- Guardado de patches (autoguardado, archivo de guardado (JSON?), selección de patche...)
    - Guardar el estado actual:
	    + Con SynthiGME.getState() que estaba hecho pero hay que construirlo y testearlo.
	    + La variable oscRecievedMessages almacena el último mensaje de cada variable de la GUI. Se puede revertir a un estado anterior con él.
	    + Probar con todos los módulos.
	    + Crear un diccionario con estado inicial.
	    + Guardar en archivo y recuperar un estado (como un array que se agrupa en pares key-value).
	    + Guardar estados automáticamente antes de salir o cada cierto tiempo, y poder volver a estado anterior al reiniciar.
    - Grabar comandos OSC en el tiempo: guardar una lista de eventos. Cada evento consistirá en el comando OSC recibido más la marca temporal. Todo puede después reproducirse (velocidad, sentido variables.
- Módulo Joystick.
- Módulo Osciloscopio.
- Send outputs (pensaba que estaban hechos, pero no aparecen en en panel de audio).
- Poner variables relevantes de módulos en configuración.
- Hacer el archivo de configuración en JSON.
- Crear hints al poner el ratón sobre un view.
- Crear tabla de OSC actualizada y accesible.
- Ver si se pueden hacer las ventanas resizables. Actualmente no lo están. Pero hay que ver si es compatible con los atajos actuales para Zoom.

# Bugs y mejoras:
- Comprobar qué servidor usa por defecto, porque no se ve en los controles de SCIDE
- En el documento de creación de nuevos módulos, añadir la declaración de las variables en SynthiGME.getState()
- Módulo SGME_GUINode: refactorizar sin ifs anidados. Probar con case o switch.

# Ideas a pensar:
- Threads. uso en Supercollider? puede aliviar la CPU?
    - ver artículo sobre Supernova: https://madskjeldgaard.dk/posts/supernova-intro/
- Rueda de envelope hacer que sea intuitivo
- ¿poner todas las las salidas por defecto? (quizás dejar solo dos por defecto por si algún sistema operativo no puede lidiar con todo ello correctamente).