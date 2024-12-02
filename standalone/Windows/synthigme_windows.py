# Este archivo es parte de SynthiGME.

# SynthiGME es software libre: puedes redistribuirlo y/o modificarlo
# bajo los términos de la Licencia Pública General de GNU publicada por
# la Free Software Foundation, ya sea la versión 3 de la Licencia o
# (a tu elección) cualquier versión posterior.

# SynthiGME se distribuye con la esperanza de que sea útil,
# pero SIN NINGUNA GARANTÍA; ni siquiera la garantía implícita de
# COMERCIABILIDAD o ADECUACIÓN A UN PROPÓSITO PARTICULAR.
# Consulta la Licencia Pública General de GNU para más detalles.

# Deberías haber recibido una copia de la Licencia Pública General de GNU
# junto con SynthiGME. Si no, consulta <https://www.gnu.org/licenses/>.

# Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>

import subprocess
import threading
import os
import sys
import traceback
import tkinter as tk
from tkinter.scrolledtext import ScrolledText
from tkinter import Menu
from art import text2art  # Paquete para generar banners ASCII

# Detectar el directorio donde se encuentra el script o el ejecutable
if getattr(sys, 'frozen', False):  # Si está empaquetado como .exe
    SCRIPT_DIR = os.path.dirname(sys.executable)  # Directorio del ejecutable
else:
    SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))  # Directorio del script Python

# Configurar rutas basadas en SCRIPT_DIR
SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, ".SuperCollider")  # Ruta a .SuperCollider/
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang.exe")
CONFIG_DIR = os.path.join(SCRIPT_DIR, "Config")  # Ruta a directorio de configuración
SCLANG_CONFIG = os.path.join(CONFIG_DIR, "sclang_conf.yaml")  # Configuración de SC

class TkinterTerminal:
    """Interfaz gráfica para emular una terminal con Tkinter."""
    def __init__(self, root):
        self.root = root
        self.root.title("SynthiGME Terminal")
        self.root.geometry("800x600")

        # Crear menú principal
        self.create_menu()

        # Área de texto para la salida
        self.output_area = ScrolledText(root, wrap=tk.WORD, font=("Courier", 12), bg="black", fg="white")
        self.output_area.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        self.output_area.configure(state="disabled")

        # Área de entrada para comandos
        self.input_area = tk.Entry(root, font=("Courier", 12), bg="black", fg="white", insertbackground="white")
        self.input_area.pack(fill=tk.X, padx=5, pady=5)
        self.input_area.bind("<Return>", self.send_command)

        self.colors = {
            "gold3": "#ffd700",
            "sandy_brown": "#f4a460",
            "light_coral": "#f08080",
            "olive_drab1": "#c0ff00",
            "light_goldenrod3": "#cdbd9c",
            "light_slate_blue": "#8470ff",
            "bright_black": "#808080",
        }

        self.process = None
        self.stop_event = threading.Event()

        # Manejar el evento de cierre de la ventana
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

        # Configurar colores
        self.configure_tags()

        # Mostrar banner ASCII
        self.show_ascii_banner()

    def create_menu(self):
        """Crea el menú principal de la aplicación."""
        menu_bar = Menu(self.root)

        # Menú Archivo
        file_menu = Menu(menu_bar, tearoff=0)
        file_menu.add_command(label="Cerrar", command=self.on_close)
        menu_bar.add_cascade(label="Archivo", menu=file_menu)

        # Menú Ver
        view_menu = Menu(menu_bar, tearoff=0)
        view_menu.add_command(label="Próximamente...")  # Espacio para expandir
        menu_bar.add_cascade(label="Ver", menu=view_menu)

        # Menú Herramientas
        tools_menu = Menu(menu_bar, tearoff=0)
        tools_menu.add_command(label="Próximamente...")  # Espacio para expandir
        menu_bar.add_cascade(label="Herramientas", menu=tools_menu)

        self.root.config(menu=menu_bar)

    def configure_tags(self):
        """Configura etiquetas de colores para el área de texto."""
        for name, color in self.colors.items():
            self.output_area.tag_configure(name, foreground=color)

    def detect_color(self, text):
        """Detecta el color apropiado basado en el contenido del texto."""
        if "==== SynthiGME Log ====" in text or "==== Log Output ====" in text:
            return "gold3"
        elif "WARNING:" in text:
            return "sandy_brown"
        elif "FAILURE IN SERVER" in text or ("Node" in text and "not found" in text):
            return "light_coral"
        elif "SuperCollider 3 server ready." in text or "*** Synthi GME" in text:
            return "olive_drab1"
        elif "compiling" in text or "Arrancando servidor" in text or "Booting server" in text:
            return "light_goldenrod3"
        elif "Device options:" in text or "Conexión de salida stereo" in text:
            return "light_slate_blue"
        else:
            return "bright_black"

    def append_output(self, text, color="bright_black"):
        """Añade texto al área de salida con un color específico."""
        self.output_area.configure(state="normal")
        self.output_area.insert(tk.END, text + "\n", color)
        self.output_area.see(tk.END)
        self.output_area.configure(state="disabled")

    def send_command(self, event=None):
        """Envía un comando al proceso sclang."""
        if self.process and self.process.stdin:
            command = self.input_area.get().strip()
            if command.lower() in ["exit", "quit"]:
                self.on_close()  # Cierra la aplicación
            else:
                self.process.stdin.write(command + "\n")
                self.process.stdin.flush()
            self.input_area.delete(0, tk.END)

    def start_sclang(self):
        """Inicia el proceso de sclang y redirige la salida."""
        try:
            if not os.path.isfile(SCLANG_EXECUTABLE):
                raise FileNotFoundError(f"sclang executable not found at {SCLANG_EXECUTABLE}. "
                                        "Ensure the SuperCollider directory contains sclang.")

            os.chdir(SUPER_COLLIDER_DIR)
            self.process = subprocess.Popen(
                [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG],
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                encoding="utf-8",
                errors="replace",
                creationflags=subprocess.CREATE_NO_WINDOW  # No muestra consola de Windows
            )
            threading.Thread(target=self.read_sclang_output, daemon=True).start()
        except Exception as e:
            self.append_output(f"Error al iniciar sclang: {e}", "light_coral")
            traceback.print_exc()

    def read_sclang_output(self):
        """Lee la salida de sclang y la muestra en el área de texto."""
        while not self.stop_event.is_set() and self.process:
            try:
                output = self.process.stdout.readline()
                if output:
                    color = self.detect_color(output.strip())
                    self.append_output(output.strip(), color)
                if self.process.poll() is not None:
                    break
            except Exception as e:
                self.append_output(f"Error leyendo salida de sclang: {e}", "light_coral")
                break

    def on_close(self):
        """Lógica para cerrar la ventana y finalizar el proceso sclang."""
        self.append_output("Cerrando Synthi GME...", "light_goldenrod3")
        if self.process:
            self.stop_event.set()  # Detener la lectura del proceso
            self.process.terminate()  # Terminar el proceso sclang
            try:
                self.process.wait(timeout=2)  # Esperar a que termine
            except subprocess.TimeoutExpired:
                self.process.kill()  # Forzar cierre si no responde
        self.root.destroy()  # Cierra la ventana principal

    def show_ascii_banner(self):
        """Muestra un banner ASCII dinámico al inicio."""
        banner = text2art("Synthi GME", font="slant")  # Genera el texto ASCII
        self.append_output(banner, "olive_drab1")


if __name__ == "__main__":
    root = tk.Tk()
    terminal = TkinterTerminal(root)
    terminal.start_sclang()
    root.mainloop()
