# ui_options.py
import tkinter as tk
from tkinter import ttk, BooleanVar, StringVar, IntVar
from ui_config import restore_defaults_impl

def create_options_widgets_impl(frame, config, update_config, config_message, device_list, app_instance):  # Add app_instance
    """Implementación de los widgets de configuración en la pestaña 'Opciones'."""
    row = 0

    # Título
    tk.Label(frame, text="Opciones", font=("Helvetica", 16)).grid(
        row=row, column=0, columnspan=2, padx=5, pady=10
    )
    row += 1

    # Advertencia
    tk.Label(
        frame, 
        text="Advertencia: No modifique estos valores a menos que sepa lo que está haciendo.", 
        fg="red"
    ).grid(row=row, column=0, columnspan=2, padx=5, pady=5)
    row += 1

    # AutoStart option
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

    # SynthiGME options
    for key, value in config['synthigme'].items():
        tk.Label(frame, text=key).grid(row=row, column=0, padx=5, pady=5, sticky=tk.W)

        if key == "server":
            var = StringVar(value="default" if value == "s" else "new")
            widget = ttk.Combobox(frame, textvariable=var, values=["default", "new"])
            widget.bind("<<ComboboxSelected>>", 
                lambda e, k=key, v=var: update_config(k, "s" if v.get() == "default" else "nil"))
            widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
        elif key in ["deviceIn", "deviceOut"]:
            var = StringVar(value="default" if value == "nil" else value)
            combobox = ttk.Combobox(
                frame, 
                textvariable=var, 
                values=device_list, 
                state='readonly', 
                name=f"{key}_combobox", 
                width=30
            )
            combobox.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
            def on_device_select(event, k=key, v=var):
                selection = v.get()
                update_config(k, "nil" if selection == "default" else selection)
            combobox.bind("<<ComboboxSelected>>", on_device_select)
        elif isinstance(value, str) and value.lower() in ["true", "false"]:
            var = BooleanVar(value=value.lower() == "true")
            widget = tk.Checkbutton(
                frame, 
                variable=var, 
                onvalue=True, 
                offvalue=False,
                command=lambda k=key, v=var: update_config(k, "true" if v.get() else "false")
            )
            widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
        elif isinstance(value, (int, float)):
            var = IntVar(value=value)
            widget = tk.Entry(frame, textvariable=var)
            widget.bind("<FocusOut>", 
                lambda e, k=key, v=var: update_config(k, v.get()))
            widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
        else:
            var = StringVar(value=str(value))
            widget = tk.Entry(frame, textvariable=var)
            widget.bind("<FocusOut>", 
                lambda e, k=key, v=var: update_config(k, v.get()))
            widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)

        row += 1

    # Añadir el botón de restaurar valores por defecto al final
    tk.Button(
        frame,
        text="Restaurar valores por defecto",
        command=lambda: restore_defaults_impl(app_instance),  # Pass app_instance instead of frame.master.master
        fg="red"
    ).grid(row=row, column=0, columnspan=2, pady=20)
    row += 1

    # El mensaje de configuración va al final
    config_message.grid(row=row, column=0, columnspan=2, padx=5, pady=5)