import subprocess
import threading
import os
import traceback
import yaml
import tkinter as tk  # Importar tkinter como tk
from tkinter import Menu, BooleanVar, StringVar, IntVar
from tkinter.scrolledtext import ScrolledText
from tkinter import ttk  # Para usar Notebook (pestañas)
import tkinter.messagebox as mb
from datetime import datetime
from config import SCRIPT_DIR, CONFIG_DIR, load_config, save_config, get_version
from logger import write_log_header, LOG_DIR
from ui_colors import detect_color, configure_tags
from ui_config import create_config_widgets_impl
from ui_events import send_command, on_close, confirm_force_close, reset_close_attempt
from ui_options import create_options_widgets_impl
from ui_console import create_console_widgets_impl
from ui_output import append_output, show_program_info
from ui_process import (
    read_sclang_output, 
    on_compilation_complete,
    build_synthigme_command,
    start_synthigme,
    fetch_device_list,
    process_command,
    update_device_comboboxes,
    force_exit
)

SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, ".SuperCollider")
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang.exe")
SCLANG_CONFIG = os.path.join(SCRIPT_DIR, "Config", "sclang_conf.yaml")


class SynthiGMEApp:
    """Interfaz gráfica para la aplicación SynthiGME con Tkinter."""
    def __init__(self, root, sclang_process):
        self.root = root
        self.root.title("Synthi GME")
        self.root.geometry("533x600")  # Cambiar el ancho a 2/3 del tamaño original (800 * 2/3 = 533)

        # Inicializar variables para el cierre
        self.close_attempted = False
        self.double_click_timer = None

        # Manejar el evento de cierre de la ventana principal
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

        # Cargar configuración
        self.config = load_config()
        self.initial_config = self.config.copy()  # Guardar la configuración inicial
        self.post_compilation_command = self.build_synthigme_command()

        # Variables para el log
        self.log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
        # Open the log file and store the handle
        self.log_file_handle = open(self.log_file, "a", encoding="utf-8")  # Changed to append mode
        write_log_header(self.log_file)

        # Variable para el contenido de la consola
        self.console_content = ""

        # Crear la estructura de pestañas antes de definir las pestañas
        self.notebook = ttk.Notebook(self.root)
        self.notebook.pack(fill=tk.BOTH, expand=True)

        # Diccionario para gestionar pestañas
        self.tabs = {
            "Consola": {"frame": ttk.Frame(self.notebook), "variable": BooleanVar(value=True)},
            "Opciones": {"frame": ttk.Frame(self.notebook), "variable": BooleanVar(value=True)},
        }

        # Añadir pestañas al notebook
        for name, data in self.tabs.items():
            self.notebook.add(data["frame"], text=name)

        # Crear widgets de consola en su pestaña
        self.create_console_widgets()

        # Crear widgets de configuración en su pestaña
        self.synthi_started = False  # Track if SynthiGME has been started
        self.process = sclang_process  # Use the existing sclang process
        self.device_list = []  # Initialize device_list before calling create_options_widgets
        self.fetching_devices = False  # Flag to indicate if fetching devices
        self.processed_lines = set()  # Track processed lines to avoid duplicates
        self.create_options_widgets()

        # Crear menú principal
        self.create_menu()

        # Habilitar el movimiento de pestañas
        self.enable_tab_dragging()

        self.stop_event = threading.Event()

        # Mostrar la información del programa en la consola
        self.show_program_info()

        # Start reading sclang output
        threading.Thread(target=self.read_sclang_output, daemon=True).start()

    def build_synthigme_command(self):
        return build_synthigme_command(self)

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

        # Menú Synthi
        self.synthi_menu = Menu(menu_bar, tearoff=0)
        self.synthi_menu.add_command(label="Iniciar", command=self.start_synthigme, state="normal")
        menu_bar.add_cascade(label="Synthi", menu=self.synthi_menu)

        # Menú Ayuda
        help_menu = Menu(menu_bar, tearoff=0)
        help_menu.add_command(label="Acerca de", command=self.show_about)
        menu_bar.add_cascade(label="Ayuda", menu=help_menu)

        self.root.config(menu=menu_bar)

    def start_synthigme(self):
        start_synthigme(self)

    def toggle_tab(self, tab_name):
        """Abre o cierra la pestaña según el estado de la variable."""
        tab_data = self.tabs[tab_name]
        if tab_data["variable"].get():
            self.notebook.add(tab_data["frame"], text=tab_name)
        else:
            self.notebook.forget(tab_data["frame"])

    def create_console_widgets(self):
        """Crea los widgets de la consola en la pestaña 'Consola'."""
        frame = self.tabs["Consola"]["frame"]
        self.output_area, self.input_area = create_console_widgets_impl(
            frame,
            self.console_content,
            self.send_command
        )
        
        # Configurar etiquetas de colores
        self.configure_tags()

    def configure_tags(self):
        configure_tags(self.output_area)

    def create_config_widgets(self):
        """Crea los widgets de configuración en la pestaña 'Inicio'."""
        frame = self.tabs["Inicio"]["frame"]
        create_config_widgets_impl(frame, self.tabs, self.config, self.update_config)

    def create_options_widgets(self):
        """Crea los widgets de configuración en la pestaña 'Opciones'."""
        frame = self.tabs["Opciones"]["frame"]
        self.config_message = tk.Label(frame, text="", fg="red")  # Create label first
        create_options_widgets_impl(frame, self.config, self.update_config, self.config_message, self.device_list)

    def update_config(self, key, value):
        """Actualiza el archivo de configuración YAML cuando se cambia un valor."""
        if key in self.config['synthigme']:
            if self.config['synthigme'][key] != value:
                self.config['synthigme'][key] = value
                save_config(self.config)
                # Mostrar el mensaje de advertencia
                self.config_message.config(text="Los cambios tendrán efecto la próxima vez que se inicie SynthiGME.")
            else:
                self.config_message.config(text="")
        else:
            if self.config.get(key) != value:
                self.config[key] = value
                save_config(self.config)
                # Mostrar el mensaje de advertencia
                self.config_message.config(text="Los cambios tendrán efecto la próxima vez que se inicie SynthiGME.")
            else:
                self.config_message.config(text="")

    def restore_defaults(self):
        """Restaura los valores por defecto desde el archivo YAML de configuración por defecto."""
        default_config_file = os.path.join(CONFIG_DIR, "synthigme_config_default.yaml")
        try:
            with open(default_config_file, "r", encoding="utf-8") as file:
                default_config = yaml.safe_load(file)
            self.config = default_config
            save_config(self.config)
            self.create_options_widgets()  # Actualizar los widgets con los valores por defecto
            self.config_message.config(text="Valores por defecto restaurados. Reinicie SynthiGME para aplicar los cambios.", fg="green")
        except Exception as e:
            self.config_message.config(text=f"Error al restaurar los valores por defecto: {e}", fg="red")

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
        return detect_color(text)

    def append_output(self, text, color="bright_black"):
        append_output(self, text, color)

    def read_sclang_output(self):
        read_sclang_output(self)

    def on_compilation_complete(self):
        on_compilation_complete(self)

    def send_command(self, event=None):
        send_command(self, event)
    
    def process_command(self, text):
        process_command(self, text)
    
    def on_close(self):
        on_close(self)
    
    def confirm_force_close(self):
        confirm_force_close(self)

    def force_exit(self):
        force_exit(self)

    def fetch_device_list(self):
        fetch_device_list(self)

    def update_device_comboboxes(self):
        update_device_comboboxes(self)


# En la clase SynthiGMEApp, reemplaza el método on_close con el siguiente:

    def show_program_info(self):
        show_program_info(self)

    def show_about(self):
        """Muestra una ventana con la información 'Acerca de'."""
        about_window = tk.Toplevel(self.root)
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
        frame = tk.Frame(about_window, padx=10, pady=10)
        frame.pack(fill=tk.BOTH, expand=True)

        # Añadir la información al frame
        for line in about_info:
            tk.Label(frame, text=line, justify=tk.LEFT, anchor="w").pack(fill=tk.X)

        # Botón para cerrar la ventana
        tk.Button(frame, text="Cerrar", command=about_window.destroy).pack(pady=10)

    def reset_close_attempt(self):
        reset_close_attempt(self)

