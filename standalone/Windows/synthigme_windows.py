import subprocess
import threading
import os
import sys
import platform
import psutil
from datetime import datetime
import traceback
import tkinter as tk
from tkinter.scrolledtext import ScrolledText
from tkinter import Menu, BooleanVar

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


class TkinterTerminal:
    """Interfaz gráfica para emular una terminal con Tkinter."""
    def __init__(self, root):
        self.root = root
        self.root.title("SynthiGME")
        self.root.geometry("800x600")

        # Variables para el log
        self.log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
        write_log_header(self.log_file)
        self.log_file_handle = None

        # Variable para el contenido de la consola
        self.console_content = ""

        # Variable para el estado de la consola
        self.show_console = BooleanVar(value=True)

        # Crear menú principal
        self.create_menu()

        # Consola incrustada
        self.create_console_widgets()

        self.process = None
        self.stop_event = threading.Event()

        # Manejar el evento de cierre de la ventana principal
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

    def create_menu(self):
        """Crea el menú principal de la aplicación."""
        menu_bar = Menu(self.root)

        # Menú Archivo
        file_menu = Menu(menu_bar, tearoff=0)
        file_menu.add_command(label="Cerrar", command=self.on_close)
        menu_bar.add_cascade(label="Archivo", menu=file_menu)

        # Menú Ver
        view_menu = Menu(menu_bar, tearoff=0)
        view_menu.add_checkbutton(label="Ver consola", variable=self.show_console, command=self.toggle_console)
        menu_bar.add_cascade(label="Ver", menu=view_menu)

        self.root.config(menu=menu_bar)

    def create_console_widgets(self):
        """Crea los widgets de la consola incrustada."""
        self.console_frame = tk.Frame(self.root)
        self.console_frame.pack(fill=tk.BOTH, expand=True)

        # Área de texto para la salida
        self.output_area = ScrolledText(self.console_frame, wrap=tk.WORD, font=("Courier", 12), bg="black", fg="white")
        self.output_area.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        self.output_area.configure(state="disabled")

        # Área de entrada para comandos
        self.input_area = tk.Entry(self.console_frame, font=("Courier", 12), bg="black", fg="white", insertbackground="white")
        self.input_area.pack(fill=tk.X, padx=5, pady=5)
        self.input_area.bind("<Return>", self.send_command)

        # Restaurar contenido previo
        if self.console_content:
            self.append_output(self.console_content, "bright_black")

    def toggle_console(self):
        """Alterna la visibilidad de la consola incrustada."""
        if self.show_console.get():
            self.console_frame.pack(fill=tk.BOTH, expand=True)
        else:
            self.console_frame.pack_forget()

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
        }
        for name, color in colors.items():
            self.output_area.tag_configure(name, foreground=color)

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

    def send_command(self, event=None):
        """Envía un comando al proceso sclang."""
        if self.process and self.process.stdin:
            command = self.input_area.get().strip()
            if command.lower() in ["exit", "quit"]:
                self.on_close()
            else:
                self.process.stdin.write(command + "\n")
                self.process.stdin.flush()
            self.input_area.delete(0, tk.END)

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
                bufsize=1,  # Desbufferizado de línea
                universal_newlines=True,  # Leer como texto, no binario
                encoding="utf-8",
                errors="replace",
                creationflags=subprocess.CREATE_NO_WINDOW,  # No mostrar consola de Windows
            )

            # Abrir el archivo de log en modo append
            self.log_file_handle = open(self.log_file, "a", encoding="utf-8")

            # Crear un hilo para procesar la salida del proceso
            threading.Thread(target=self.read_sclang_output, daemon=True).start()
        except Exception as e:
            self.append_output(f"Error al iniciar sclang: {e}", "light_coral")
            traceback.print_exc()

    def read_sclang_output(self):
        """Lee la salida de sclang y la registra en el archivo de log."""
        try:
            while not self.stop_event.is_set() and self.process:
                output = self.process.stdout.readline()
                if output:
                    self.append_output(output.strip(), self.detect_color(output.strip()))
                if self.process.poll() is not None:
                    break
        except Exception as e:
            self.append_output(f"Error leyendo salida de sclang: {e}", "light_coral")
        finally:
            if self.log_file_handle:
                self.log_file_handle.close()

    def on_close(self):
        """Lógica para cerrar la ventana y finalizar el proceso sclang."""
        self.append_output("Cerrando Synthi GME...", "light_goldenrod3")
        if self.process:
            self.stop_event.set()
            self.process.terminate()
            try:
                self.process.wait(timeout=2)
            except subprocess.TimeoutExpired:
                self.process.kill()
        self.root.destroy()


if __name__ == "__main__":
    root = tk.Tk()
    terminal = TkinterTerminal(root)
    terminal.start_sclang()
    root.mainloop()
