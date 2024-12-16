# ui_tabs.py
import tkinter as tk
from tkinter import ttk

def enable_tab_dragging_impl(self_instance):
    """Habilita el movimiento de pestañas mediante arrastrar y soltar."""
    self_instance.notebook.bind("<ButtonPress-1>", self_instance.on_tab_drag_start)
    self_instance.notebook.bind("<B1-Motion>", self_instance.on_tab_drag_motion)

def on_tab_drag_start_impl(self_instance, event):
    """Inicio del evento de arrastre de una pestaña."""
    self_instance.drag_start_index = self_instance.notebook.index("@%d,%d" % (event.x, event.y))

def on_tab_drag_motion_impl(self_instance, event):
    """Mueve una pestaña mientras el usuario la arrastra."""
    try:
        current_index = self_instance.notebook.index("@%d,%d" % (event.x, event.y))
        if current_index != self_instance.drag_start_index:
            self_instance.notebook.insert(current_index, self_instance.notebook.tabs()[self_instance.drag_start_index])
            self_instance.drag_start_index = current_index
    except tk.TclError:
        pass  # Ignorar si el cursor está fuera de las pestañas

def toggle_tab_impl(self_instance, tab_name):
    """Abre o cierra la pestaña según el estado de la variable."""
    tab_data = self_instance.tabs[tab_name]
    if tab_data["variable"].get():
        self_instance.notebook.add(tab_data["frame"], text=tab_name)
    else:
        self_instance.notebook.forget(tab_data["frame"])