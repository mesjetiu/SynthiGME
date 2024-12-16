# ui_output.py
import tkinter as tk
from datetime import datetime
from ui_colors import detect_color
from config import get_version  # Añadir esta importación

def append_output(self_instance, text, color="bright_black"):
    """Añade textom al área de salidae con un color específico y guarda en el log."""
    if not self_instance.root.winfo_exists():
        return  # Sir la ventana principal ha sido destruida, no hacer nada

    if text.strip() and text not in self_instance.processed_lines:  
        self_instance.processed_lines.add(text)
        self_instance.console_content += text + "\n"  
        
        # Update UI
        self_instance.output_area.configure(state="normal")
        self_instance.output_area.insert(tk.END, text + "\n", color)
        self_instance.output_area.see(tk.END)
        self_instance.output_area.configure(state="disabled")

        # Write to log file
        try:
            if self_instance.log_file_handle and not self_instance.log_file_handle.closed:
                self_instance.log_file_handle.write(text + "\n")
                self_instance.log_file_handle.flush()
        except Exception as e:
            print(f"Error writing to log file: {e}")

def show_program_info(self_instance):
    """Muestra la información del programa en la consola."""
    version = get_version()  # Usar directamente get_version, no self_instance.get_version
    program_info = [
        "==== Synthi GME ====",
        f"Versión: {version}",
        "Autor: Carlos Arturo Guerra Parra",
        "Contacto: carlosarturoguerra@gmail.com",
        "",
        "Synthi GME es un software libre distribuido bajo la Licencia Pública General de GNU.",
        "Copyright 2024.",
        "====================",
        ""
    ]

    for line in program_info:
        append_output(self_instance, line, "gold3")