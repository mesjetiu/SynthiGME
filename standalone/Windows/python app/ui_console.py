# ui_console.py
import tkinter as tk
from tkinter.scrolledtext import ScrolledText

def create_console_widgets_impl(frame, console_content, send_command_callback):
    """Implementación de los widgets de la consola."""
    # Área de texto para la salida
    output_area = ScrolledText(
        frame, 
        wrap=tk.WORD, 
        font=("Courier", 12), 
        bg="black", 
        fg="white"
    )
    output_area.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
    output_area.configure(state="disabled")

    # Área de entrada para comandos
    input_area = tk.Entry(
        frame, 
        font=("Courier", 12), 
        bg="black", 
        fg="white", 
        insertbackground="white"
    )
    input_area.pack(fill=tk.X, padx=5, pady=5)
    input_area.bind("<Return>", send_command_callback)

    # Restaurar contenido previo
    if console_content:
        output_area.configure(state="normal")
        output_area.insert(tk.END, console_content)
        output_area.configure(state="disabled")

    return output_area, input_area