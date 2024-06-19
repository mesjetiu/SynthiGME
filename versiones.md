## Version 1.9 

### Importantes cambios
- Aplicación funcionando como Quark y como Extension de SuperCollider.
- Optimizaciones en eficiencia y uso de memoria.
- Mayor unidad e integración entre las diferentes ventanas.
- Creada grabadora de eventos con marcas de tiempo.
- Ejecutable binario para Linux.
- Creada ventana de inicio o bienvenida al cargar la aplicación (splash window).
- Cada instancia de SynthiGME tiene su propio servidor de audio por defecto.


### Bugs corregidos
- Funcionamiento de + y - para cambiar de tamaño las ventanas.
- Problema de tamaño de paneles 5 y 6 por defecto.
- Cambio de patch más seguro por defecto (evitando artefactos).


### Otras mejoras
- Script de Linux mejorado y funcionando.
- Mejoras de escritura de código.
- Se pueden crear grupos en la LAN añadiendo un mismo prefijo a los mensajes OSC.
- Menú contextual disponible en todas las ventanas.
- Modo oscuro por defecto en las ventanas.
- Creada ventana "About" o "Acerca de...".
- Optimización de guardado de patches.
- Los nodos de Panel 5 y 6 aceptan valores intermedios entre 0 y 1, simulando la resistencia de los pines.
- Mayor seguridad y más comprobaciones en la ejecución de diálogos de usuario.
- Creación de algunos patchs de ejemplo.

## Version 1.8.0 

### Importantes cambios
- Guardar y cargar archivos con patches.
- Mejor gestión de la salida del programa con diálogo.
- Parpadeo de los mandos al variar su valor.
- Módulo de osciloscopio creado (funcional, sin características modificables).
- Comunicación OSC en red local de forma permanente y distribuida.
- Creada ventana Post Window para ver los mensajes fuera de la IDE de SC (solo funcional por ahora);

### Bugs corregidos
- Los paneles no tomaban foco al pulsar su atajo (1-7).
- Otros bugs encontrados en el proceso.
- Manejo de outputs al sistema en el innicio.

### Otras mejoras
- Reorganización de código para mayor claridad.
- Mutear y desmutear con el atajo "m".
- Perilla de selección de envolvente es movible con el ratón.
- Textos dinámicos en menú contextual.

## Version 1.7.0 
2024/05/12

### Importantes cambios
- Corregido problema de cierre al inicio en Windows. Los nodos de los patchbays se han repartido en dos views en lugar de una sola.
- Se pueden guardar patches (estados del Synthi) en archivos y recuperarse.
- La interfaz gráfica siempre se inicia.
- La interfaz gráfica se inicia solo si el servidor ha iniciado correctamente.

### Bugs corregidos
- Uso de "/" y "\" en paths en función del sistema operativo.
- Error en la distribución del canal de salida 1 en derecho e izquierdo.

### Otras mejoras
- Reorganización y claridad en los mensajes de salida en Post window.

## Version 1.6.0

### Importantes cambios y mejoras
- Corregido problema de arranque en Windows (modificada estructura de paneles de patch)
- Número de canales de entrada y salida elegibles por el usuario al arranque. 2 salidas por defecto.
- Mejoras de eficiencia y memoria en Node y paneles de patch.


## Otras mejoras
- Cambiados métodos descontinuados (deprecated).
- Imágenes de Node más ligeras.
- Apertura de puerto 9000 para OSC.
- Cualquier cambio en parámetro se devuelve en Post window como texto OSC.
- Creado atajo "m" para mutear y desmutear el sonido global.

## Bugs corregidos
- Corregida barra inclinada de directorio en paths distinta para Unix y Windows (/ y \).
- Correcciones de atajos de teclado.

## Version 1.5.0
2024/04/29

### Nuevas implementaciones
- Modulación DADSR de "Gated"
- Módulo External tratment Returns
- Módulo de Reverb

### Mejoras
- SlewLimiter
- Suavizado de algunos synths
- Mejoras en la comunicación con el servidor de audio

### Otros
- Actualizaciones para SuperCollider 3.13.0
- Correcciones de bugs
	+ Ventana de atajos no siempre visible
	+ Atajos de teclado en unicode