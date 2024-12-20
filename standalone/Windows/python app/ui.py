import threading
import os
import tkinter as tk  # Importar tkinter como tk
from tkinter import BooleanVar
from tkinter import ttk  # Para usar Notebook (pestañas)
from datetime import datetime
from config import SCRIPT_DIR, load_config, get_version
from logger import write_log_header, LOG_DIR
from ui_colors import detect_color, configure_tags
from ui_config import update_config_impl, restore_defaults_impl
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
    force_exit,
    close_synthigme
)
from ui_menu import create_menu_impl, show_about_impl
from ui_tabs import (
    enable_tab_dragging_impl,
    on_tab_drag_start_impl,
    on_tab_drag_motion_impl,
    toggle_tab_impl
)

SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, ".SuperCollider")
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang.exe")
SCLANG_CONFIG = os.path.join(SCRIPT_DIR, "Config", "sclang_conf.yaml")


class SynthiGMEApp:
    """Interfaz gráfica para la aplicación SynthiGME con Tkinter."""
    def __init__(self, root, sclang_process):
        self.root = root
        version = get_version()
        self.root.title(f"Synthi GME v{version}")
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
        self.synthi_running = False  # Inicializa el estado de SynthiGME
        self.process = sclang_process  # Use the existing sclang process
        self.device_list = []  # Initialize device_list before calling create_options_widgets
        self.fetching_devices = False  # Flag to indicate if fetching devices
        self.processed_lines = set()  # Track processed lines to avoid duplicates
        self.ignored_errors = []  # Initialize ignored_errors list
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

        self.close_synthigme = lambda on_complete=None: close_synthigme(self, on_complete)

    def build_synthigme_command(self):
        return build_synthigme_command(self)

    def create_menu(self):
        create_menu_impl(self)

    def start_synthigme(self):
        start_synthigme(self)

    def toggle_tab(self, tab_name):
        toggle_tab_impl(self, tab_name)

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

    def create_options_widgets(self):
        """Crea los widgets de configuración en la pestaña 'Opciones'."""
        create_options_widgets_impl(self)

    def update_config(self, key, value):
        update_config_impl(self, key, value)

    def restore_defaults(self):
        restore_defaults_impl(self)

    def enable_tab_dragging(self):
        enable_tab_dragging_impl(self)

    def on_tab_drag_start(self, event):
        on_tab_drag_start_impl(self, event)

    def on_tab_drag_motion(self, event):
        on_tab_drag_motion_impl(self, event)

    def append_output(self, text, color="bright_black"):
        append_output(self, text, color)

    def read_sclang_output(self):
        read_sclang_output(self)

    def reset_close_attempt(self):
        reset_close_attempt(self)

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

    def show_program_info(self):
        show_program_info(self)

    def show_about(self):
        show_about_impl(self)

    def detect_color(self, text):
        return detect_color(text)
    
    def fetch_device_list(self):
        fetch_device_list(self)

    def update_device_comboboxes(self):
        update_device_comboboxes(self)