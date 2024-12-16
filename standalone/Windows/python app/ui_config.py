# ui_config.py
import os
import yaml
import tkinter as tk
from tkinter import BooleanVar
from config import CONFIG_DIR, save_config  # Añadir save_config a la importación

def create_config_widgets_impl(frame, tabs, config, update_config):
    """Implementación de los widgets de configuración."""
    row = 0

    # Añadir un título en la pestaña
    tk.Label(frame, text="Configuración de Inicio", font=("Helvetica", 16)).grid(
        row=row, column=0, columnspan=2, padx=5, pady=10
    )
    row += 1

    # Añadir la opción autoStart
    tk.Label(frame, text="Abrir Synthi GME automáticamente al inicio").grid(
        row=row, column=0, padx=5, pady=5, sticky=tk.W
    )
    auto_start_var = BooleanVar(value=config.get('autoStart', 'false').lower() == 'true')
    auto_start_widget = tk.Checkbutton(
        frame, 
        variable=auto_start_var, 
        onvalue=True, 
        offvalue=False,
        command=lambda: update_config('autoStart', "true" if auto_start_var.get() else "false")
    )
    auto_start_widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
    row += 1

    # Configuración de SynthiGME
    for key, value in config['synthigme'].items():
        tk.Label(frame, text=key).grid(row=row, column=0, padx=5, pady=5, sticky=tk.W)
        if isinstance(value, bool):
            var = BooleanVar(value=value)
            widget = tk.Checkbutton(
                frame, 
                variable=var,
                command=lambda k=key, v=var: update_config(k, v.get())
            )
        else:
            var = tk.StringVar(value=str(value))
            widget = tk.Entry(frame, textvariable=var)
            widget.bind('<FocusOut>', 
                       lambda e, k=key, v=var: update_config(k, v.get()))
        widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
        row += 1

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