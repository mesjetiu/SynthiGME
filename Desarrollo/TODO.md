# Implementar:
- Guardado de patches (autoguardado, archivo de guardado (JSON?), selección de patche...)
    - Guardar el estado actual: actualizar un diccionario con todos los comandos OSC recibidos pero que guarde el último valor cada vez.
    - Grabar comandos OSC en el tiempo: guardar una lista de eventos. Cada evento consistirá en el comando OSC recibido más la marca temporal. Todo puede después reproducirse (velocidad, sentido variables.
- Módulo Joystick.
- Módulo Osciloscopio.
- Send outputs (pensaba que estaban hechos, pero no aparecen en en panel de audio).
- Poner variables relevantes de módulos en configuración.
- Convertir archivo a JSON?
- Crear hints al poner el ratón sobre un view.
- Crear tabla de OSC actualizada y accesible.
- Ver si se pueden hacer las ventanas resizables. Actualmente no lo están. Pero hay que ver si es compatible con los atajos actuales para Zoom.

# Bugs y mejoras:
- Comprobar qué servidor usa por defecto, porque no se ve en los controles de SCIDE

# Ideas a pensar:
- Threads. uso en Supercollider? puede aliviar la CPU?
    - ver artículo sobre Supernova: https://madskjeldgaard.dk/posts/supernova-intro/
- Rueda de envelope hacer que sea intuitivo
- ¿poner todas las las salidas por defecto? (quizás dejar solo dos por defecto por si algún sistema operativo no puede lidiar con todo ello correctamente).