# ui_config.py
import os
import yaml
from config import CONFIG_DIR, save_config  # Añadir save_config a la importación

def update_config_impl(self_instance, key, value):
    """Actualiza el archivo de configuración YAML cuando se cambia un valor."""
    if key in self_instance.config['synthigme']:
        if self_instance.config['synthigme'][key] != value:
            self_instance.config['synthigme'][key] = value
            save_config(self_instance.config)
            # Mostrar el mensaje de advertencia
            self_instance.config_message.config(
                text="Los cambios tendrán efecto la próxima vez que se inicie SynthiGME."
            )
        else:
            self_instance.config_message.config(text="")
    else:
        if self_instance.config.get(key) != value:
            self_instance.config[key] = value
            save_config(self_instance.config)
            # Mostrar el mensaje de advertencia
            self_instance.config_message.config(
                text="Los cambios tendrán efecto la próxima vez que se inicie SynthiGME."
            )
        else:
            self_instance.config_message.config(text="")

def restore_defaults_impl(self_instance):
    """Restaura los valores por defecto desde el archivo YAML de configuración por defecto."""
    default_config_file = os.path.join(CONFIG_DIR, "synthigme_config_default.yaml")
    try:
        with open(default_config_file, "r", encoding="utf-8") as file:
            default_config = yaml.safe_load(file)
        self_instance.config = default_config
        save_config(self_instance.config)
        self_instance.create_options_widgets()  # Actualizar los widgets con los valores por defecto
        self_instance.config_message.config(
            text="Valores por defecto restaurados. Reinicie SynthiGME para aplicar los cambios.",
            fg="green"
        )
    except Exception as e:
        self_instance.config_message.config(
            text=f"Error al restaurar los valores por defecto: {e}",
            fg="red"
        )