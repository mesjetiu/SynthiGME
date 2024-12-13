# ui_config.py
import tkinter as tk
from tkinter import BooleanVar

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
        # ... resto de la implementación para cada parámetro