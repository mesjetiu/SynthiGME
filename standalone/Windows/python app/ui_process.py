# ui_process.py
import subprocess
import tkinter.messagebox as mb
import tkinter as tk
from config import ICON_PATH

def read_sclang_output(self_instance):
    """Lee la salida de sclang, detecta comandos y registra en el archivo de log."""
    try:
        while not self_instance.stop_event.is_set() and self_instance.process:
            output = self_instance.process.stdout.readline()
            if output:
                self_instance.process_command(output.strip())
                self_instance.append_output(output.strip(), self_instance.detect_color(output.strip()))

                # Detectar finalización de compilación
                if "*** Welcome to SuperCollider" in output:
                    on_compilation_complete(self_instance)

            if self_instance.process.poll() is not None:
                break
    except Exception as e:
        self_instance.append_output(f"Error leyendo salida de sclang: {e}", "light_coral")
    finally:
        if self_instance.log_file_handle:
            self_instance.log_file_handle.close()

def on_compilation_complete(self_instance):
    """Ejecuta acciones cuando la compilación ha terminado."""
    self_instance.append_output("Compilación completada.", "green_ready")
    self_instance.device_list = []
    fetch_device_list(self_instance)
    if self_instance.config.get('autoStart', 'false').lower() == 'true':
        start_synthigme(self_instance)

def build_synthigme_command(self_instance):
    """Construye el comando SynthiGME() a partir de la configuración."""
    params = self_instance.config['synthigme']
    param_list = []
    for key, value in params.items():
        if key in ['server', 'deviceIn', 'deviceOut']:
            if value == 'nil':
                param_list.append(f"{key}: {value}")
            else:
                param_list.append(f'{key}: "{value}"')
        elif isinstance(value, str) and value.lower() in ['true', 'false']:
            param_list.append(f"{key}: {value.lower()}")
        else:
            param_list.append(f"{key}: {value}")
    param_str = ", ".join(param_list)
    return f"SynthiGME({param_str}, standalone: true)"

def start_synthigme(self_instance):
    """Inicia SynthiGME con los parámetros configurados."""
    if not self_instance.synthi_started:
        self_instance.synthi_started = True
        self_instance.synthi_menu.entryconfig("Iniciar", state="disabled")
        self_instance.append_output("SynthiGME iniciado.", "green_ready")
        if self_instance.process and self_instance.process.stdin:
            self_instance.process.stdin.write(f'{build_synthigme_command(self_instance)};\n')
            self_instance.process.stdin.flush()

def close_synthigme(self_instance, on_complete=None):
    """Cierra la instancia de SynthiGME.
    
    Args:
        self_instance: Instancia de la aplicación
        on_complete: Código sclang a ejecutar al completar el cierre (str, opcional)
    """
    if hasattr(self_instance, 'synthi_running') and self_instance.synthi_running:
        if self_instance.process and self_instance.process.stdin:
            on_complete_str = f"onComplete: {{{on_complete}}}" if on_complete else ""
            command = f"SynthiGME.instance.close({on_complete_str})\n"
            self_instance.process.stdin.write(command)
            self_instance.process.stdin.flush()
            self_instance.append_output("Enviado comando para cerrar SynthiGME", "light_cyan")
    else:
        self_instance.force_exit()
        
def fetch_device_list(self_instance):
    """Envía un comando a sclang para obtener la lista de dispositivos."""
    if self_instance.process and self_instance.process.stdin:
        self_instance.append_output("Fetching device list...", "light_cyan")
        self_instance.fetching_devices = True
        self_instance.device_list = []
        self_instance.process.stdin.write("ServerOptions.devices.do{|device| device.postln}; nil\n")
        self_instance.process.stdin.flush()

def update_device_comboboxes(self_instance):
    """Actualiza los comboboxes de dispositivos con la lista de dispositivos."""
    for key in ["deviceIn", "deviceOut"]:
        combobox = self_instance.tabs["Opciones"]["frame"].children.get(f"{key}_combobox")
        if combobox:
            combobox["values"] = self_instance.device_list
            self_instance.append_output(f"Updated {key} combobox with devices: {self_instance.device_list}", "light_cyan")

def force_exit(self_instance):
    """Cierra la aplicación de forma inmediata."""
    self_instance.append_output("Forzando el cierre de Synthi GME...", "light_coral")
    if self_instance.process:
        self_instance.stop_event.set()
        try:
            self_instance.process.terminate()
            self_instance.process.wait(timeout=2)
        except subprocess.TimeoutExpired:
            self_instance.process.kill()
    
    if self_instance.log_file_handle and not self_instance.log_file_handle.closed:
        self_instance.log_file_handle.close()
    
    self_instance.root.destroy()

def process_command(self_instance, text):
    if text.startswith("ERROR: "):
        error_message = text
        if error_message in self_instance.ignored_errors:
            return True

        dialog = tk.Toplevel(self_instance.root)
        dialog.title("Error en SuperCollider")
        dialog.grab_set()
        dialog.iconbitmap(ICON_PATH)
        
        # Make dialog resizable and set min/max width
        dialog.resizable(True, False)  # Allow horizontal resize only
        dialog.minsize(400, 0)
        dialog.maxsize(800, 1000)
        
        # Main container with padding
        main_frame = tk.Frame(dialog, padx=20, pady=15)
        main_frame.pack(fill=tk.BOTH, expand=True)
        
        # Error message with automatic wrapping
        message_label = tk.Label(
            main_frame, 
            text=error_message,
            fg="red",
            wraplength=700,  # Will wrap before dialog's maxsize
            justify=tk.LEFT,
            anchor="w"
        )
        message_label.pack(fill=tk.BOTH, expand=True, pady=(0, 20))
        
        # Buttons frame at bottom
        button_frame = tk.Frame(main_frame)
        button_frame.pack(side=tk.BOTTOM, fill=tk.X, pady=(10, 0))
        
        def cerrar_app():
            self_instance.append_output("Cerrando SynthiGME...", "light_coral")
            self_instance.force_exit()
            dialog.destroy()
        
        def ignorar_error():
            dialog.destroy()
        
        def ignorar_siempre():
            self_instance.ignored_errors.append(error_message)
            dialog.destroy()
        
        # Create buttons with consistent width
        tk.Button(button_frame, text="Cerrar aplicación", command=cerrar_app, width=15).pack(side=tk.LEFT, padx=5)
        tk.Button(button_frame, text="Ignorar este error", command=ignorar_error, width=15).pack(side=tk.LEFT, padx=5)
        tk.Button(button_frame, text="Ignorar siempre", command=ignorar_siempre, width=15).pack(side=tk.LEFT, padx=5)
        
        # Center dialog on screen after it's been created
        dialog.update_idletasks()
        width = dialog.winfo_width()
        height = dialog.winfo_height()
        x = (dialog.winfo_screenwidth() // 2) - (width // 2)
        y = (dialog.winfo_screenheight() // 2) - (height // 2)
        dialog.geometry(f'+{x}+{y}')
        
        dialog.wait_window()
        return True
        
        tk.Button(button_frame, text="Cerrar aplicación", command=cerrar_app).grid(row=0, column=0, padx=5)
        tk.Button(button_frame, text="Ignorar este error", command=ignorar_error).grid(row=0, column=1, padx=5)
        tk.Button(button_frame, text="Ignorar siempre este error", command=ignorar_siempre).grid(row=0, column=2, padx=5)
        
        dialog.wait_window()
        return True
    
    # Detectar cuando SynthiGME está en ejecución
    if "*** SynthiGME" in text and "en ejecución ***" in text:
        self_instance.synthi_running = True
        return True
    
    # Detectar comandos que comienzan con "command:"
    if text.startswith("command: "):
        command = text[len("command: "):].strip()
        if command == "force_exit":
            self_instance.append_output("Recibido comando 'force_exit'. Forzando cierre...", "light_coral")
            self_instance.force_exit()
        else:
            self_instance.append_output(f"Comando desconocido: {command}", "sandy_brown")
        return True
    
    # Procesar otros textos
    if self_instance.fetching_devices:
        if text.startswith("-> nil"):
            self_instance.device_list.append("default")
            self_instance.append_output(f"Device list fetched: {self_instance.device_list}", "light_cyan")
            self_instance.update_device_comboboxes()
            self_instance.fetching_devices = False
            if self_instance.config.get('autoStart', 'false').lower() == 'true':
                self_instance.process.stdin.write(f'{self_instance.post_compilation_command};\n')
                self_instance.process.stdin.flush()
        elif "ServerOptions.devices.do" not in text:
            self_instance.device_list.append(text.strip())
            self_instance.append_output(f"Device added: {text.strip()}", "light_cyan")
    else:
        self_instance.append_output(text, self_instance.detect_color(text))
    return False