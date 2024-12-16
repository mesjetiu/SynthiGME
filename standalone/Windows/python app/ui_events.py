# ui_events.py
import tkinter as tk
import tkinter.messagebox as mb
from ui_process import process_command  # Importar process_command desde ui_process.py

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
        # Llamar a process_command para manejar el comando
        self_instance.process_command(command)

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