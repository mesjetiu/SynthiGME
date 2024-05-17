# Implementar:
- Módulo Osciloscopio (en proceso)
	+ Creado hasta unión de patchbay audio. Falta:
		* Unir a patchbay voltage.
		* Añadir controles y knobs (ScopeView tiene star() y stop(), útil).
		* Depurar el synth, el uso del buffer, del bus... sobran cosas.
		* Retocar colores y apariencia final.
- Módulo External Treatmen Device: terminar. Está hecha la GUI.
- Módulo Envelope Followers: terminar. Está hecha la GUI.
- Explorar y mejorar la conectividad entre varios SynthiGME y otros dipositivos OSC.
- Grabar comandos OSC en el tiempo: guardar una lista de eventos. Cada evento consistirá en el comando OSC recibido más la marca temporal. Todo puede después reproducirse (velocidad, sentido variables).
- Archivo de configuración personal de usuario (para recordar paths y otras variables personales).
- Módulo Joystick.
- Send outputs (pensaba que estaban hechos, pero no aparecen en en panel de audio).
- Poner variables relevantes de módulos en configuración.
- Hacer el archivo de configuración en JSON.
- Crear hints al poner el ratón sobre un view.
- Crear tabla de OSC actualizada y accesible.
- Ver si se pueden hacer las ventanas resizables. Actualmente no lo están. Pero hay que ver si es compatible con los atajos actuales para Zoom.

# Bugs y mejoras:
- Evitar ejecuciones circulares de elementos de la GUI como ecos...
- Limpiar variables no utilizadas en la clase principal y algunos de sus valores en settings.
- Comprobar qué servidor usa por defecto, porque no se ve en los controles de SCIDE
- En el documento de creación de nuevos módulos, añadir la declaración de las variables en SynthiGME.getState() (si finalmente se usa esta función)

# Ideas a pensar:
- Threads. uso en Supercollider? puede aliviar la CPU?
    - ver artículo sobre Supernova: https://madskjeldgaard.dk/posts/supernova-intro/
- Rueda de envelope hacer que sea intuitivo
- ¿poner todas las las salidas por defecto? (quizás dejar solo dos por defecto por si algún sistema operativo no puede lidiar con todo ello correctamente).