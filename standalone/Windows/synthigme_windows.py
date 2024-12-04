"""
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
"""

import subprocess
import threading
import os
import sys
import platform
import psutil
from datetime import datetime
import traceback
import tkinter as tk
from tkinter import Menu, BooleanVar
from tkinter.scrolledtext import ScrolledText
from tkinter import ttk  # Para usar Notebook (pestañas)
import tkinter.messagebox as mb
import yaml


# Detectar el directorio donde se encuentra el script o el ejecutable
if getattr(sys, 'frozen', False):  # Si está empaquetado como .exe
    SCRIPT_DIR = os.path.dirname(sys.executable)  # Directorio del ejecutable
else:
    SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))  # Directorio del script Python

# Configurar rutas basadas en SCRIPT_DIR
SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, ".SuperCollider")
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang.exe")
CONFIG_DIR = os.path.join(SCRIPT_DIR, "Config")
SCLANG_CONFIG = os.path.join(CONFIG_DIR, "sclang_conf.yaml")
LOG_DIR = os.path.join(SCRIPT_DIR, "PostWindow_Logs")
VERSION_FILE = os.path.join(SCRIPT_DIR, ".Extensions", "SynthiGME", "version")

# Asegurar que el directorio de logs existe
os.makedirs(LOG_DIR, exist_ok=True)


def get_version():
    """Obtiene la versión de SynthiGME desde el archivo SynthiGME/version."""
    try:
        with open(VERSION_FILE, "r", encoding="utf-8") as version_file:
            return version_file.read().strip()
    except Exception:
        return "unknown (version file not found)"


def get_system_info():
    """Obtiene información relevante del sistema para el encabezado del log."""
    system_info = {
        "Timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "SynthiGME Version": get_version(),
        "OS": platform.system(),
        "OS Version": platform.version(),
        "Architecture": platform.architecture()[0],
        "Processor": platform.processor(),
        "CPU Cores (Logical)": psutil.cpu_count(logical=True),
        "CPU Cores (Physical)": psutil.cpu_count(logical=False),
        "Total RAM (GB)": round(psutil.virtual_memory().total / (1024 ** 3), 2),
    }
    return system_info


def write_log_header(log_file):
    """Escribe un encabezado con información del sistema en el archivo de log."""
    system_info = get_system_info()
    with open(log_file, "w", encoding="utf-8") as log:
        log.write("==== SynthiGME Log ====\n")
        log.write("Session Details:\n")
        for key, value in system_info.items():
            log.write(f"{key}: {value}\n")
        log.write("\n==== Log Output ====\n")
        log.flush()


def load_config():
    """Carga la configuración desde el archivo YAML."""
    config_file = os.path.join(CONFIG_DIR, "synthigme_config.yaml")
    with open(config_file, "r", encoding="utf-8") as file:
        return yaml.safe_load(file)


class TkinterTerminal:
    """Interfaz gráfica para emular una terminal con Tkinter."""
    def __init__(self, root):
        self.root = root
        self.root.title("SynthiGME")
        self.root.geometry("800x600")

        # Cargar configuración
        self.config = load_config()
        self.post_compilation_command = self.build_synthigme_command()

        # Variables para el log
        self.log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
        write_log_header(self.log_file)
        self.log_file_handle = None

        # Variable para el contenido de la consola
        self.console_content = ""

        # Crear la estructura de pestañas antes de definir las pestañas
        self.notebook = ttk.Notebook(self.root)
        self.notebook.pack(fill=tk.BOTH, expand=True)

        # Diccionario para gestionar pestañas
        self.tabs = {
            "Consola": {"frame": ttk.Frame(self.notebook), "variable": BooleanVar(value=True)},
            "Pestaña 1": {"frame": ttk.Frame(self.notebook), "variable": BooleanVar(value=True)},
            "Pestaña 2": {"frame": ttk.Frame(self.notebook), "variable": BooleanVar(value=True)},
        }

        # Añadir pestañas al notebook
        for name, data in self.tabs.items():
            self.notebook.add(data["frame"], text=name)

        # Crear widgets de consola en su pestaña
        self.create_console_widgets()

        # Crear menú principal
        self.create_menu()

        # Habilitar el movimiento de pestañas
        self.enable_tab_dragging()

        self.process = None
        self.stop_event = threading.Event()

        # Manejar el evento de cierre de la ventana principal
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

    def build_synthigme_command(self):
        """Construye el comando SynthiGME() a partir de la configuración."""
        params = self.config['synthigme']
        param_str = ", ".join(f"{key}: {str(value).lower() if isinstance(value, bool) else value}" for key, value in params.items())
        command = f"SynthiGME({param_str})"
        return command

    def create_menu(self):
        """Crea el menú principal de la aplicación."""
        menu_bar = Menu(self.root)

        # Menú Archivo
        file_menu = Menu(menu_bar, tearoff=0)
        file_menu.add_command(label="Cerrar", command=self.on_close)
        menu_bar.add_cascade(label="Archivo", menu=file_menu)

        # Menú Ver
        self.view_menu = Menu(menu_bar, tearoff=0)
        for tab_name, tab_data in self.tabs.items():
            self.view_menu.add_checkbutton(
                label=tab_name,
                variable=tab_data["variable"],
                command=lambda name=tab_name: self.toggle_tab(name),
            )
        menu_bar.add_cascade(label="Ver", menu=self.view_menu)

        self.root.config(menu=menu_bar)

    def toggle_tab(self, tab_name):
        """Abre o cierra la pestaña según el estado de la variable."""
        tab_data = self.tabs[tab_name]
        if tab_data["variable"].get():
            self.notebook.add(tab_data["frame"], text=tab_name)
        else:
            self.notebook.forget(tab_data["frame"])

    def create_console_widgets(self):
        """Crea los widgets de la consola en la pestaña 'Consola'."""
        # Área de texto para la salida
        self.output_area = ScrolledText(self.tabs["Consola"]["frame"], wrap=tk.WORD, font=("Courier", 12), bg="black", fg="white")
        self.output_area.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        self.output_area.configure(state="disabled")

        # Área de entrada para comandos
        self.input_area = tk.Entry(self.tabs["Consola"]["frame"], font=("Courier", 12), bg="black", fg="white", insertbackground="white")
        self.input_area.pack(fill=tk.X, padx=5, pady=5)
        self.input_area.bind("<Return>", self.send_command)

        # Restaurar contenido previo
        if self.console_content:
            self.append_output(self.console_content, "bright_black")

        # Configurar etiquetas de colores
        self.configure_tags()

    def configure_tags(self):
        """Configura etiquetas de colores para el área de texto."""
        colors = {
            "gold3": "#ffd700",
            "sandy_brown": "#f4a460",
            "light_coral": "#f08080",
            "olive_drab1": "#c0ff00",
            "light_goldenrod3": "#cdbd9c",
            "light_slate_blue": "#8470ff",
            "bright_black": "#808080",
            "light_cyan": "#00e5ee",
            "green_ready": "#32cd32",  # Nuevo color para indicar SynthiGME listo
        }
        for name, color in colors.items():
            self.output_area.tag_configure(name, foreground=color)

    def enable_tab_dragging(self):
        """Habilita el movimiento de pestañas mediante arrastrar y soltar."""
        self.notebook.bind("<ButtonPress-1>", self.on_tab_drag_start)
        self.notebook.bind("<B1-Motion>", self.on_tab_drag_motion)

    def on_tab_drag_start(self, event):
        """Inicio del evento de arrastre de una pestaña."""
        self.drag_start_index = self.notebook.index("@%d,%d" % (event.x, event.y))

    def on_tab_drag_motion(self, event):
        """Mueve una pestaña mientras el usuario la arrastra."""
        try:
            current_index = self.notebook.index("@%d,%d" % (event.x, event.y))
            if current_index != self.drag_start_index:
                self.notebook.insert(current_index, self.notebook.tabs()[self.drag_start_index])
                self.drag_start_index = current_index
        except tk.TclError:
            pass  # Ignorar si el cursor está fuera de las pestañas

    def detect_color(self, text):
        """Detecta el color apropiado basado en el contenido del texto."""
        if "==== SynthiGME Log ====" in text or "==== Log Output ====" in text:
            return "gold3"
        elif "compiling" in text or "compile done" in text:
            return "light_slate_blue"
        elif "WARNING:" in text:
            return "sandy_brown"
        elif "FAILURE" in text or "not found" in text:
            return "light_coral"
        elif "Buscando puertos" in text or "Conexión de salida" in text or "Número de canales" in text:
            return "olive_drab1"
        elif "[local]:" in text:
            return "light_cyan"
        elif "*** SynthiGME" in text and "en ejecución ***" in text:  # Línea específica
            return "green_ready"
        elif "SuperCollider 3 server ready." in text or "Cerrando Synthi GME..." in text:
            return "light_goldenrod3"
        else:
            return "bright_black"

    def append_output(self, text, color="bright_black"):
        """Añade texto al área de salida con un color específico y guarda en el log."""
        self.console_content += text + "\n"  # Guardar en el estado interno
        self.output_area.configure(state="normal")
        self.output_area.insert(tk.END, text + "\n", color)
        self.output_area.see(tk.END)
        self.output_area.configure(state="disabled")

        # Guardar en el log
        if self.log_file_handle:
            self.log_file_handle.write(text + "\n")
            self.log_file_handle.flush()
            
    def start_sclang(self):
        """Inicia el proceso de sclang y redirige la salida."""
        try:
            if not os.path.isfile(SCLANG_EXECUTABLE):
                raise FileNotFoundError(f"sclang executable not found at {SCLANG_EXECUTABLE}.")

            os.chdir(SUPER_COLLIDER_DIR)

            # Iniciar el proceso con configuración para capturar toda la salida
            self.process = subprocess.Popen(
                [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG],
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                bufsize=1,
                universal_newlines=True,
                encoding="utf-8",
                errors="replace",
                creationflags=subprocess.CREATE_NO_WINDOW,
            )

            # Abrir el archivo de log en modo append
            self.log_file_handle = open(self.log_file, "a", encoding="utf-8")

            # Crear un hilo para procesar la salida del proceso
            threading.Thread(target=self.read_sclang_output, daemon=True).start()
        except Exception as e:
            self.append_output(f"Error al iniciar sclang: {e}", "light_coral")
            traceback.print_exc()

    def read_sclang_output(self):
        """Lee la salida de sclang, detecta comandos y registra en el archivo de log."""
        try:
            while not self.stop_event.is_set() and self.process:
                output = self.process.stdout.readline()
                if output:
                    self.process_command(output.strip())
                    self.append_output(output.strip(), self.detect_color(output.strip()))

                    # Detectar finalización de compilación
                    if "*** Welcome to SuperCollider" in output:
                        self.on_compilation_complete()

                if self.process.poll() is not None:  # Si el proceso ha terminado
                    break
        except Exception as e:
            self.append_output(f"Error leyendo salida de sclang: {e}", "light_coral")
        finally:
            if self.log_file_handle:
                self.log_file_handle.close()

    def on_compilation_complete(self):
        """Ejecuta un comando arbitrario cuando la compilación ha terminado."""
        self.append_output("Compilación completada. Ejecutando comando arbitrario...", "green_ready")
        if self.process and self.process.stdin:
            self.process.stdin.write(f'{self.post_compilation_command};\n')
            self.process.stdin.flush()

    def send_command(self, event=None):
        """Envía un comando al proceso sclang."""
        if self.process and self.process.stdin:
            command = self.input_area.get().strip()
            if command.lower() in ["exit", "quit"]:
                # Envía el comando a SynthiGME para que gestione el cierre
                self.process.stdin.write("SynthiGME.instance.close\n")
                self.process.stdin.flush()
            elif command.lower() == "force_exit":
                self.on_close()  # Llama directamente al cierre forzado
            else:
                self.process.stdin.write(command + "\n")
                self.process.stdin.flush()
            self.input_area.delete(0, tk.END)

    def process_command(self, text):
        """Procesa comandos específicos enviados desde sclang."""
        if text.startswith("command: "):
            command = text[len("command: "):].strip()
            self.append_output(f"Comando recibido: {command}", "light_cyan")
            if command == "exit":
                self.append_output("Recibido comando 'exit'. Cerrando la aplicación...", "light_goldenrod3")
                self.on_close()
            elif command == "force_exit":
                self.append_output("Recibido comando 'force_exit'. Forzando cierre...", "light_coral")
                self.force_exit()  # Llama a la nueva función de cierre inmediato
            else:
                self.append_output(f"Comando desconocido: {command}", "sandy_brown")


    def force_exit(self):
        """Cierra la aplicación de forma inmediata."""
        self.append_output("Forzando el cierre de Synthi GME...", "light_coral")
        if self.process:
            self.stop_event.set()
            try:
                self.process.terminate()
                self.process.wait(timeout=2)
            except subprocess.TimeoutExpired:
                self.process.kill()
        self.root.destroy()


    def on_close(self):
        """Lógica para cerrar la ventana y finalizar el proceso sclang."""
        if hasattr(self, 'close_attempted') and self.close_attempted:
            # Mostrar ventana modal para confirmar el cierre forzado
            log_message = "Confirmación de cierre: El proceso no ha respondido. Preguntando al usuario si desea forzar el cierre."
            self.append_output(log_message, "bright_black")  # Registrar en consola
            if self.log_file_handle:
                self.log_file_handle.write(log_message + "\n")  # Registrar en log
                self.log_file_handle.flush()

            if mb.askyesno("Confirmación de cierre", "El proceso no ha respondido. ¿Deseas forzar el cierre?"):
                log_message = "Respuesta del usuario: Sí. Procediendo con el cierre forzado."
                self.append_output(log_message, "light_coral")  # Registrar en consola
                if self.log_file_handle:
                    self.log_file_handle.write(log_message + "\n")  # Registrar en log
                    self.log_file_handle.flush()
                self.force_exit()
            else:
                log_message = "Respuesta del usuario: No. Cancelando el cierre forzado."
                self.append_output(log_message, "sandy_brown")  # Registrar en consola
                if self.log_file_handle:
                    self.log_file_handle.write(log_message + "\n")  # Registrar en log
                    self.log_file_handle.flush()
        else:
            log_message = "Intentando cerrar SynthiGME de forma ordenada..."
            self.append_output(log_message, "light_goldenrod3")  # Registrar en consola
            if self.log_file_handle:
                self.log_file_handle.write(log_message + "\n")  # Registrar en log
                self.log_file_handle.flush()

            self.close_attempted = True  # Marcar que el intento de cerrar se realizó
            if self.process and self.process.stdin:
                try:
                    # Enviar el comando 'exit' para que SynthiGME gestione el cierre
                    self.process.stdin.write("SynthiGME.instance.close\n")
                    self.process.stdin.flush()

                    log_message = "Comando enviado a sclang: SynthiGME.instance.close"
                    self.append_output(log_message, "light_cyan")  # Registrar en consola
                    if self.log_file_handle:
                        self.log_file_handle.write(log_message + "\n")  # Registrar en log
                        self.log_file_handle.flush()

                except Exception as e:
                    log_message = f"Error al enviar comando 'exit': {e}"
                    self.append_output(log_message, "light_coral")  # Registrar en consola
                    if self.log_file_handle:
                        self.log_file_handle.write(log_message + "\n")  # Registrar en log
                        self.log_file_handle.flush()

if __name__ == "__main__":
    root = tk.Tk()
    terminal = TkinterTerminal(root)
    terminal.start_sclang()
    root.mainloop()
