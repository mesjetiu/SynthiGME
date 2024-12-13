def detect_color(text):
    """Detecta el color apropiado basado en el contenido del texto."""
    if "==== SynthiGME Log ====" in text or "==== Log Output ====" in text:
        return "gold3"
    elif "compiling" in text or "compile done" in text:
        return "light_slate_blue"
    elif "WARNING:" in text:
        return "sandy_brown"
    elif "FAILURE" in text or "not found" in text:
        return "light_coral"
    elif "Buscando puertos" in text or "Conexión de salida" in text or "Número de canales" in text:
        return "olive_drab1"
    elif "[local]:" in text:
        return "light_cyan"
    elif "*** SynthiGME" in text and "en ejecución ***" in text:
        return "green_ready"
    elif "SuperCollider 3 server ready." in text or "Cerrando Synthi GME..." in text:
        return "light_goldenrod3"
    else:
        return "bright_black"

def configure_tags(output_area):
    """Configura etiquetas de colores para el área de texto."""
    colors = {
        "gold3": "#ffd700",
        "sandy_brown": "#f4a460",
        "light_coral": "#f08080",
        "olive_drab1": "#c0ff00",
        "light_goldenrod3": "#cdbd9c",
        "light_slate_blue": "#8470ff",
        "bright_black": "#808080",
        "light_cyan": "#00e5ee",
        "green_ready": "#32cd32",
    }
    for name, color in colors.items():
        output_area.tag_configure(name, foreground=color)