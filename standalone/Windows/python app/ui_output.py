# ui_output.py
import tkinter as tk
from datetime import datetime
from config import get_version  # Añadir esta importación

def append_output(self_instance, text, color="bright_black"):
    """Añade texto al área de salida con un color específico y guarda en el log."""
    if not self_instance.root.winfo_exists():
        return
        
    # Textos que queremos colorear específicamente
    highlight_phrases = {
        "Device list fetched:": "olive_drab1",
        "Updated deviceIn combobox with devices:": "light_slate_blue",
        "Updated deviceOut combobox with devices:": "light_slate_blue"
    }
    
    # Si el texto contiene alguna de las frases clave
    for phrase, phrase_color in highlight_phrases.items():
        if phrase in text:
            # Separar la frase de la lista de dispositivos
            parts = text.split(":", 1)
            if len(parts) == 2:
                # Insertar la frase con color
                self_instance.output_area.configure(state="normal")
                self_instance.output_area.insert(tk.END, f"{parts[0]}:", phrase_color)
                # Insertar la lista de dispositivos con el color por defecto
                self_instance.output_area.insert(tk.END, f"{parts[1]}\n", "bright_black")
                self_instance.output_area.configure(state="disabled")
                self_instance.console_content += text + "\n"
                return

    # Añadir espacio extra antes de ciertos mensajes
    if any(msg in text for msg in ["Device list fetched:", "Updated deviceIn", "Updated deviceOut"]):
        self_instance.console_content += "\n"
        self_instance.output_area.configure(state="normal")
        self_instance.output_area.insert(tk.END, "\n")
        self_instance.output_area.configure(state="disabled")

    # Para el resto del texto, mantener el comportamiento normal
    if text.strip() and text not in self_instance.processed_lines:  
        self_instance.processed_lines.add(text)  # Cambiado de append a add
        self_instance.console_content += text + "\n"  
        
        # Update UI
        self_instance.output_area.configure(state="normal")
        self_instance.output_area.insert(tk.END, text + "\n", color)
        self_instance.output_area.see(tk.END)
        self_instance.output_area.configure(state="disabled")

        # Write to log file with timestamp
        try:
            if self_instance.log_file_handle and not self_instance.log_file_handle.closed:
                timestamp = datetime.now().strftime("[%H:%M:%S]")
                self_instance.log_file_handle.write(f"{timestamp} {text}\n")
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