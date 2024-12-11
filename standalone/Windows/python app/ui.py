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

SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, ".SuperCollider")
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang.exe")
SCLANG_CONFIG = os.path.join(SCRIPT_DIR, "Config", "sclang_conf.yaml")


class SynthiGMEApp:
    """Interfaz gráfica para la aplicación SynthiGME con Tkinter."""
    def __init__(self, root):
        self.root = root
        self.root.title("SynthiGME")
        self.root.geometry("533x600")  # Cambiar el ancho a 2/3 del tamaño original (800 * 2/3 = 533)

        # Cargar configuración
        self.config = load_config()
        self.initial_config = self.config.copy()  # Guardar la configuración inicial
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
            "Opciones": {"frame": ttk.Frame(self.notebook), "variable": BooleanVar(value=True)},
        }

        # Añadir pestañas al notebook
        for name, data in self.tabs.items():
            self.notebook.add(data["frame"], text=name)

        # Crear widgets de consola en su pestaña
        self.create_console_widgets()

        # Crear widgets de configuración en su pestaña
        self.create_options_widgets()

        # Crear menú principal
        self.create_menu()

        # Habilitar el movimiento de pestañas
        self.enable_tab_dragging()

        self.process = None
        self.stop_event = threading.Event()

        # Manejar el evento de cierre de la ventana principal
        self.close_attempted = False
        self.double_click_timer = None
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

        # Mostrar la información del programa en la consola
        self.show_program_info()

        # Enlazar el doble clic en el botón de cierre
        self.root.bind("<Double-Button-1>", self.on_double_click_close)


    def build_synthigme_command(self):
        """Construye el comando SynthiGME() a partir de la configuración."""
        params = self.config['synthigme']
        param_str = ", ".join(f"{key}: {value}" for key, value in params.items())
        command = f"SynthiGME({param_str}, standalone: true)"
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

        # Menú Ayuda
        help_menu = Menu(menu_bar, tearoff=0)
        help_menu.add_command(label="Acerca de", command=self.show_about)
        menu_bar.add_cascade(label="Ayuda", menu=help_menu)

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

    def create_config_widgets(self):
        """Crea los widgets de configuración en la pestaña 'Inicio'."""
        frame = self.tabs["Inicio"]["frame"]
        row = 0

        # Añadir un título en la pestaña
        tk.Label(frame, text="Configuración de Inicio", font=("Helvetica", 16)).grid(row=row, column=0, columnspan=2, padx=5, pady=10)
        row += 1

        # Añadir la opción autoStart
        tk.Label(frame, text="Abrir Synthi GME automáticamente al inicio").grid(row=row, column=0, padx=5, pady=5, sticky=tk.W)
        auto_start_var = BooleanVar(value=self.config.get('autoStart', 'false').lower() == 'true')
        auto_start_widget = tk.Checkbutton(frame, variable=auto_start_var, onvalue=True, offvalue=False, command=lambda: self.update_config('autoStart', "true" if auto_start_var.get() else "false"))
        auto_start_widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
        row += 1

        for key, value in self.config['synthigme'].items():
            tk.Label(frame, text=key).grid(row=row, column=0, padx=5, pady=5, sticky=tk.W)

            if key == "server":
                var = StringVar(value="default" if value == "s" else "new")
                widget = ttk.Combobox(frame, textvariable=var, values=["default", "new"])
                widget.bind("<<ComboboxSelected>>", lambda e, k=key, v=var: self.update_config(k, "s" if v.get() == "default" else "nil"))
            elif isinstance(value, str) and value.lower() in ["true", "false"]:
                var = BooleanVar(value=value.lower() == "true")
                widget = tk.Checkbutton(frame, variable=var, onvalue=True, offvalue=False, command=lambda k=key, v=var: self.update_config(k, "true" if v.get() else "false"))
            elif isinstance(value, (int, float)):
                var = IntVar(value=value)
                widget = tk.Entry(frame, textvariable=var)
                widget.bind("<FocusOut>", lambda e, k=key, v=var: self.update_config(k, v.get()))
            else:
                var = StringVar(value=str(value))
                widget = tk.Entry(frame, textvariable=var)
                widget.bind("<FocusOut>", lambda e, k=key, v=var: self.update_config(k, v.get()))

            widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
            row += 1

        # Añadir una etiqueta para mostrar el mensaje de advertencia
        self.config_message = tk.Label(frame, text="", fg="red")
        self.config_message.grid(row=row, column=0, columnspan=2, padx=5, pady=5, sticky=tk.W)

    def create_options_widgets(self):
        """Crea los widgets de configuración en la pestaña 'Opciones'."""
        frame = self.tabs["Opciones"]["frame"]
        row = 0

        # Añadir un título en la pestaña
        tk.Label(frame, text="Opciones", font=("Helvetica", 16)).grid(row=row, column=0, columnspan=2, padx=5, pady=10)
        row += 1

        # Añadir una advertencia
        tk.Label(frame, text="Advertencia: No modifique estos valores a menos que sepa lo que está haciendo.", fg="red").grid(row=row, column=0, columnspan=2, padx=5, pady=5)
        row += 1

        # Añadir la opción autoStart
        tk.Label(frame, text="Abrir Synthi GME automáticamente al inicio").grid(row=row, column=0, padx=5, pady=5, sticky=tk.W)
        auto_start_var = BooleanVar(value=self.config.get('autoStart', 'false').lower() == 'true')
        auto_start_widget = tk.Checkbutton(frame, variable=auto_start_var, onvalue=True, offvalue=False, command=lambda: self.update_config('autoStart', "true" if auto_start_var.get() else "false"))
        auto_start_widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
        row += 1

        for key, value in self.config['synthigme'].items():
            tk.Label(frame, text=key).grid(row=row, column=0, padx=5, pady=5, sticky=tk.W)

            if key == "server":
                var = StringVar(value="default" if value == "s" else "new")
                widget = ttk.Combobox(frame, textvariable=var, values=["default", "new"])
                widget.bind("<<ComboboxSelected>>", lambda e, k=key, v=var: self.update_config(k, "s" if v.get() == "default" else "nil"))
            elif isinstance(value, str) and value.lower() in ["true", "false"]:
                var = BooleanVar(value=value.lower() == "true")
                widget = tk.Checkbutton(frame, variable=var, onvalue=True, offvalue=False, command=lambda k=key, v=var: self.update_config(k, "true" if v.get() else "false"))
            elif isinstance(value, (int, float)):
                var = IntVar(value=value)
                widget = tk.Entry(frame, textvariable=var)
                widget.bind("<FocusOut>", lambda e, k=key, v=var: self.update_config(k, v.get()))
            else:
                var = StringVar(value=str(value))
                widget = tk.Entry(frame, textvariable=var)
                widget.bind("<FocusOut>", lambda e, k=key, v=var: self.update_config(k, v.get()))

            widget.grid(row=row, column=1, padx=5, pady=5, sticky=tk.W)
            row += 1

        # Añadir una etiqueta para mostrar el mensaje de advertencia
        self.config_message = tk.Label(frame, text="", fg="red")
        self.config_message.grid(row=row, column=0, columnspan=2, padx=5, pady=5, sticky=tk.W)
        row += 1

        # Añadir un botón para restaurar los valores por defecto
        tk.Button(frame, text="Restaurar valores por defecto", command=self.restore_defaults).grid(row=row, column=0, columnspan=2, padx=5, pady=10)

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
        if not self.root.winfo_exists():
            return  # Si la ventana principal ha sido destruida, no hacer nada

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
        self.append_output("Compilación completada.", "green_ready")
        if self.process and self.process.stdin:
            if self.config.get('autoStart', 'false').lower() == 'true':
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


# En la clase SynthiGMEApp, reemplaza el método on_close con el siguiente:

    def on_close(self):
        """Lógica para cerrar la ventana y finalizar el proceso sclang."""
        if self.double_click_timer:
            self.root.after_cancel(self.double_click_timer)
            self.double_click_timer = None
            self.confirm_force_close()
        else:
            self.double_click_timer = self.root.after(2000, self.reset_close_attempt)
            self.close_attempted = True
            log_message = "Intentando cerrar SynthiGME de forma ordenada..."
            self.append_output(log_message, "light_goldenrod3")  # Registrar en consola
            if self.log_file_handle:
                self.log_file_handle.write(log_message + "\n")  # Registrar en log
                self.log_file_handle.flush()

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

    def reset_close_attempt(self):
        """Resetea el intento de cierre."""
        self.close_attempted = False
        self.double_click_timer = None

    def confirm_force_close(self):
        """Confirma el cierre forzado con el usuario."""
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

    def on_double_click_close(self, event):
        """Lógica para manejar el doble clic en el botón de cierre."""
        self.close_attempted = True
        self.on_close()

    def show_program_info(self):
        """Muestra la información del programa en la consola."""
        version = get_version()
        program_info = [
            "==== Synthi GME ====",
            f"Versión: {version}",
            "Autor: Carlos Arturo Guerra Parra",
            "Contacto: carlosarturoguerra@gmail.com",
            "",
            "Synthi GME es un software libre distribuido bajo la Licencia Pública General de GNU.",
            "Copyright 2024."
            "====================",
            ""
        ]

        # Mostrar el encabezado en la consola
        for line in program_info:
            self.append_output(line, "gold3")

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

