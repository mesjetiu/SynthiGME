# ui_events.py

import tkinter.messagebox as mb

def send_command(self_instance, event=None):
    """Envía un comando al proceso sclang."""
    if self_instance.process and self_instance.process.stdin:
        command = self_instance.input_area.get().strip()
        if command.lower() in ["exit", "quit"]:
            self_instance.process.stdin.write("SynthiGME.instance.close\n")
            self_instance.process.stdin.flush()
        elif command.lower() == "force_exit":
            self_instance.on_close()
        else:
            self_instance.process.stdin.write(command + "\n")
            self_instance.process.stdin.flush()
        self_instance.input_area.delete(0, tk.END)

def process_command(self_instance, text):
    """Procesa comandos específicos enviados desde sclang."""
    if text.startswith("command: "):
        command = text[len("command: "):].strip()
        self_instance.append_output(f"Comando recibido: {command}", "light_cyan")
        if command == "exit":
            self_instance.append_output("Recibido comando 'exit'. Cerrando la aplicación...", "light_goldenrod3")
            self_instance.on_close()
        elif command == "force_exit":
            self_instance.append_output("Recibido comando 'force_exit'. Forzando cierre...", "light_coral")
            self_instance.force_exit()
        else:
            self_instance.append_output(f"Comando desconocido: {command}", "sandy_brown")
    elif self_instance.fetching_devices:
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

def on_close(self_instance):
    """Lógica para cerrar la ventana y finalizar el proceso sclang."""
    if self_instance.close_attempted:
        self_instance.root.after_cancel(self_instance.double_click_timer)
        self_instance.double_click_timer = None
        self_instance.close_attempted = False
        confirm_force_close(self_instance)
    else:
        self_instance.close_attempted = True
        self_instance.double_click_timer = self_instance.root.after(2000, self_instance.reset_close_attempt)
        log_message = "Intentando cerrar SynthiGME de forma ordenada..."
        self_instance.append_output(log_message, "light_goldenrod3")
        if self_instance.log_file_handle:
            self_instance.log_file_handle.write(log_message + "\n")
            self_instance.log_file_handle.flush()

        if self_instance.process and self_instance.process.stdin:
            try:
                self_instance.process.stdin.write("SynthiGME.instance.close\n")
                self_instance.process.stdin.flush()
                log_message = "Comando enviado a sclang: SynthiGME.instance.close"
                self_instance.append_output(log_message, "light_cyan")
                if self_instance.log_file_handle:
                    self_instance.log_file_handle.write(log_message + "\n")
                    self_instance.log_file_handle.flush()
            except Exception as e:
                log_message = f"Error al enviar comando 'exit': {e}"
                self_instance.append_output(log_message, "light_coral")
                if self_instance.log_file_handle:
                    self_instance.log_file_handle.write(log_message + "\n")
                    self_instance.log_file_handle.flush()
        else:
            self_instance.root.destroy()

def confirm_force_close(self_instance):
    """Confirma el cierre forzado con el usuario."""
    log_message = "El proceso no ha respondido. Preguntando al usuario si desea forzar el cierre."
    self_instance.append_output(log_message, "bright_black")
    if self_instance.log_file_handle:
        self_instance.log_file_handle.write(log_message + "\n")
        self_instance.log_file_handle.flush()

    if mb.askyesno("Confirmación de cierre", "El proceso no ha respondido.\n¿Deseas forzar el cierre?"):
        log_message = "Respuesta del usuario: Sí. Procediendo con el cierre forzado."
        self_instance.append_output(log_message, "light_coral")
        if self_instance.log_file_handle:
            self_instance.log_file_handle.write(log_message + "\n")
            self_instance.log_file_handle.flush()
        self_instance.force_exit()
    else:
        log_message = "Respuesta del usuario: No. Cancelando el cierre forzado."
        self_instance.append_output(log_message, "sandy_brown")
        if self_instance.log_file_handle:
            self_instance.log_file_handle.write(log_message + "\n")
            self_instance.log_file_handle.flush()

def reset_close_attempt(self_instance):
    """Resetea el intento de cierre."""
    self_instance.close_attempted = False
    self_instance.double_click_timer = None