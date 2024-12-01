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
import platform
import psutil # pip install psutil (windows)
from datetime import datetime
import io
import traceback
from rich.console import Console # pip install rich (windows)
from rich.table import Table
from rich.traceback import install
from rich.panel import Panel

# Instalar manejo enriquecido de errores con Rich
install()

# Configurar consola Rich
console = Console()

# Configurar codificación para evitar caracteres extraños en Windows
# No parece ser necesario con Rich...
#sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
#sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

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
LOG_DIR = os.path.join(SCRIPT_DIR, "PostWindow_Logs")  # Directorio de logs
VERSION_FILE = os.path.join(SCRIPT_DIR, ".Extensions", "SynthiGME", "version")  # Archivo con la versión

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
        log.flush()  # Asegurar que todo se escriba en el archivo

    # Mostrar en consola la misma información en formato Rich
    table = Table(title="SynthiGME Session Details")
    table.add_column("Property", style="bold blue")
    table.add_column("Value", style="bold green")
    for key, value in system_info.items():
        table.add_row(key, str(value))
    console.print(table)


def log_error(log_file, error_message):
    """Registra mensajes de error en el archivo de log."""
    with open(log_file, "a", encoding="utf-8") as log:
        log.write("\n==== ERROR ====\n")
        log.write(error_message + "\n")
        log.write("==============\n")
        log.flush()
    console.print(f"[bold red]ERROR:[/bold red] {error_message}")


def read_sclang_output(process, stop_event, log_file):
    """Lee y muestra en tiempo real las salidas del proceso de sclang."""
    with open(log_file, "a", encoding="utf-8") as log:
        while not stop_event.is_set():
            try:
                output = process.stdout.readline()
                if output:
                    decoded_output = output.strip()

                    # Detectar patrones clave y aplicar estilos
                    if "==== SynthiGME Log ====" in decoded_output or "==== Log Output ====" in decoded_output:
                        console.print(f"[bold underline gold3]{decoded_output}[/bold underline gold3]")
                    elif "WARNING:" in decoded_output:
                        console.print(f"[bold sandy_brown]{decoded_output}[/bold sandy_brown]")
                    elif "FAILURE IN SERVER" in decoded_output or "Node" in decoded_output and "not found" in decoded_output:
                        console.print(f"[bold light_coral]{decoded_output}[/bold light_coral]")
                    elif "SuperCollider 3 server ready." in decoded_output or "*** SynthiGME" in decoded_output:
                        console.print(f"[bold olive_drab1]{decoded_output}[/bold olive_drab1]")
                    elif "compiling" in decoded_output or "Arrancando servidor" in decoded_output or "Booting server" in decoded_output:
                        console.print(f"[light_goldenrod3]{decoded_output}[/light_goldenrod3]")
                    elif "Device options:" in decoded_output or "Conexión de salida stereo" in decoded_output:
                        console.print(f"[light_slate_blue]{decoded_output}[/light_slate_blue]")
                    else:
                        # Texto general
                        console.print(decoded_output, style="bright_black")

                    # Escribir en el log
                    log.write(decoded_output + "\n")
                    log.flush()  # Asegurar que la salida se escriba en el archivo

                    # Detectar "exit" en la salida de la post window
                    if decoded_output.lower() == "exit":
                        console.print("[bold light_goldenrod3]Mensaje 'exit' detectado. Cerrando automáticamente...[/bold light_goldenrod3]")
                        stop_event.set()
                        process.terminate()
                        break

                if process.poll() is not None:
                    stop_event.set()
                    break
            except Exception as e:
                error_message = f"Error reading sclang output: {str(e)}\n{traceback.format_exc()}"
                log_error(log_file, error_message)
                stop_event.set()
                break


def main():
    process = None
    log_file = None

    try:
        # Crear archivo de log con nombre único en el directorio de logs
        log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
        console.print(f"[bold green]Registro de la sesión se guardará en:[/bold green] {log_file}")
        console.print(f"[bold blue]Usando el ejecutable de sclang en:[/bold blue] {SCLANG_EXECUTABLE}")
        console.print(f"[bold magenta]Directorio de trabajo:[/bold magenta] {SUPER_COLLIDER_DIR}")

        # Escribir el encabezado del log
        write_log_header(log_file)

        # Verificar si el ejecutable de sclang está disponible
        if not os.path.isfile(SCLANG_EXECUTABLE):
            raise FileNotFoundError(f"sclang executable not found at {SCLANG_EXECUTABLE}. "
                                    "Ensure the SuperCollider directory contains sclang.")

        # Cambiar el directorio de trabajo a SuperCollider
        os.chdir(SUPER_COLLIDER_DIR)

        # Configuración para manejar stdin y ventanas emergentes en Windows
        startupinfo = subprocess.STARTUPINFO()
        startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW

        # Abrir el proceso de sclang usando el comando configurado
        console.print("[bold green]Starting sclang process...[/bold green]")
        process = subprocess.Popen(
            [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            encoding='utf-8',
            errors='replace',
            startupinfo=startupinfo
        )

        # Evento para señalar cuando parar el hilo
        stop_event = threading.Event()

        # Iniciar un hilo para leer la salida de sclang en tiempo real
        thread = threading.Thread(target=read_sclang_output, args=(process, stop_event, log_file), daemon=True)
        thread.start()

        console.print("[bold cyan]sclang está corriendo. Escribe tu código SuperCollider y presiona Enter.[/bold cyan]")
        console.print("[bold yellow]Para salir manualmente, escribe 'exit' o 'quit'.[/bold yellow]")

        # Manejar entrada del usuario
        while not stop_event.is_set():
            try:
                user_input = console.input("[bold green]> [/bold green]")
                if user_input.lower() in ["exit", "quit"]:
                    console.print("[bold yellow]Cerrando sclang...[/bold yellow]")
                    if process.poll() is None and process.stdin:
                        process.stdin.write("0.exit\n")
                    stop_event.set()
                    break
                elif process.poll() is None and process.stdin:
                    process.stdin.write(user_input + "\n")
            except EOFError:
                break

        if process and process.poll() is None:
            process.terminate()
        if process:
            process.wait()
        stop_event.set()
        thread.join()
        console.print("[bold green]sclang cerrado.[/bold green]")
    except Exception as e:
        error_message = f"Unexpected error occurred: {str(e)}\n{traceback.format_exc()}"
        log_error(log_file, error_message)
    finally:
        if log_file:
            console.print(f"[bold green]Log saved to:[/bold green] {log_file}")


if __name__ == "__main__":
    main()
