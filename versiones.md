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