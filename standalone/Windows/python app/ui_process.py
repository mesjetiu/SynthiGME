# ui_process.py
import subprocess
import threading
import tkinter.messagebox as mb

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

def close_synthigme(self_instance):
    """Cierra la instancia de SynthiGME."""
    if hasattr(self_instance, 'synthi_running') and self_instance.synthi_running:
        if self_instance.process and self_instance.process.stdin:
            self_instance.process.stdin.write("SynthiGME.instance.close\n")
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
    """Procesa comandos específicos enviados desde sclang."""
    
    # Detectar errores que contienen "ERROR:"
    if text.startswith("ERROR: "):
        if mb.askyesno("Error en SuperCollider",
                       f"{text}\n\n¿Desea cerrar el programa?",
                       parent=self_instance.root):
            self_instance.append_output("Cerrando SynthiGME...", "light_coral")
            # Comentar la línea actual
            # if self_instance.process and self_instance.process.stdin:
            #     self_instance.process.stdin.write("SynthiGME.instance.close\n")
            #     self_instance.process.stdin.flush()
            # Ejecutar force_exit en su lugar
            self_instance.force_exit()
        return True
    
    # Detectar cuando SynthiGME está en ejecución
    if "*** SynthiGME" in text and "en ejecución ***" in text:
        self_instance.synthi_running = True
        return True
    
    # Detectar comandos que comienzan con "command:"
    if text.startswith("command: "):
        command = text[len("command: "):].strip()
        if command == "exit":
            self_instance.append_output("Recibido comando 'exit'. Cerrando la aplicación...", "light_goldenrod3")
            self_instance.close_synthigme() 
        elif command == "force_exit":
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