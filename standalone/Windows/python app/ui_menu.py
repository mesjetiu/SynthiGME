# ui_menu.py
import tkinter as tk
from tkinter import Menu, Frame
from config import get_version

def create_menu_impl(self_instance):
    """Crea el menú principal de la aplicación."""
    menu_bar = Menu(self_instance.root)

    # Menú Archivo
    file_menu = Menu(menu_bar, tearoff=0)
    file_menu.add_command(label="Cerrar", command=self_instance.on_close)
    menu_bar.add_cascade(label="Archivo", menu=file_menu)

    # Menú Ver
    self_instance.view_menu = Menu(menu_bar, tearoff=0)
    for tab_name, tab_data in self_instance.tabs.items():
        self_instance.view_menu.add_checkbutton(
            label=tab_name,
            variable=tab_data["variable"],
            command=lambda name=tab_name: self_instance.toggle_tab(name),
        )
    menu_bar.add_cascade(label="Ver", menu=self_instance.view_menu)

    # Menú Synthi
    self_instance.synthi_menu = Menu(menu_bar, tearoff=0)
    self_instance.synthi_menu.add_command(label="Iniciar", command=self_instance.start_synthigme, state="normal")
    menu_bar.add_cascade(label="Synthi", menu=self_instance.synthi_menu)

    # Menú Ayuda
    help_menu = Menu(menu_bar, tearoff=0)
    help_menu.add_command(label="Acerca de", command=self_instance.show_about)
    menu_bar.add_cascade(label="Ayuda", menu=help_menu)

    self_instance.root.config(menu=menu_bar)

def show_about_impl(self_instance):
    """Muestra una ventana con la información 'Acerca de'."""
    about_window = tk.Toplevel(self_instance.root)
    about_window.title("Acerca de SynthiGME")
    about_window.geometry("400x300")

    version = get_version()
    about_info = [
        "==== SynthiGME ====",
        f"Versión: {version}",
        "Autor: Carlos Arturo Guerra Parra",
        "Contacto: carlosarturoguerra@gmail.com",
        "",
        "SynthiGME es un software libre distribuido bajo la",
        "Licencia Pública General de GNU.",
        "====================",
    ]

    # Crear un frame para el contenido
    frame = Frame(about_window, padx=10, pady=10)
    frame.pack(fill=tk.BOTH, expand=True)

    # Añadir la información al frame
    for line in about_info:
        tk.Label(frame, text=line, justify=tk.LEFT, anchor="w").pack(fill=tk.X)

    # Botón para cerrar la ventana
    tk.Button(frame, text="Cerrar", command=about_window.destroy).pack(pady=10)