# This file is part of SynthiGME.

# SynthiGME is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# SynthiGME is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with SynthiGME. If not, see <https://www.gnu.org/licenses/>.

# Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>

import subprocess
import threading
import os
import sys
import platform
import psutil
from datetime import datetime
import io
import traceback

# Configurar codificación para evitar caracteres extraños en Windows
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

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


def log_error(log_file, error_message):
    """Registra mensajes de error en el archivo de log."""
    with open(log_file, "a", encoding="utf-8") as log:
        log.write("\n==== ERROR ====\n")
        log.write(error_message + "\n")
        log.write("==============\n")
        log.flush()


def read_sclang_output(process, stop_event, log_file):
    """Lee y muestra en tiempo real las salidas del proceso de sclang."""
    with open(log_file, "a", encoding="utf-8") as log:
        while not stop_event.is_set():
            try:
                output = process.stdout.readline()
                if output:
                    decoded_output = output.strip()
                    print(decoded_output)
                    sys.stdout.flush()  # Forzar vaciado del buffer de salida
                    log.write(decoded_output + "\n")
                    log.flush()  # Asegurar que la salida se escriba en el archivo

                    # Detectar "exit" en la salida de la post window
                    if decoded_output.lower() == "exit":
                        print("Mensaje 'exit' detectado en la Post Window. Cerrando automáticamente.")
                        sys.stdout.flush()
                        stop_event.set()
                        process.terminate()
                        break

                if process.poll() is not None:
                    stop_event.set()
                    break
            except Exception as e:
                error_message = f"Error reading sclang output: {str(e)}\n{traceback.format_exc()}"
                print(error_message)
                log_error(log_file, error_message)
                stop_event.set()
                break


def main():
    process = None
    log_file = None

    try:
        # Crear archivo de log con nombre único en el directorio de logs
        log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
        print(f"Registro de la sesión se guardará en: {log_file}")
        print(f"Usando el ejecutable de sclang en: {SCLANG_EXECUTABLE}")
        print(f"Directorio de trabajo: {SUPER_COLLIDER_DIR}")

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
        print("Starting sclang process...")
        process = subprocess.Popen(
            [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,  # Combinar stdout y stderr
            encoding='utf-8',  # Especificar codificación UTF-8
            errors='replace',  # Reemplazar caracteres que no se puedan decodificar
            startupinfo=startupinfo
        )

        # Evento para señalar cuando parar el hilo
        stop_event = threading.Event()

        # Iniciar un hilo para leer la salida de sclang en tiempo real
        thread = threading.Thread(target=read_sclang_output, args=(process, stop_event, log_file), daemon=True)
        thread.start()

        print("sclang está corriendo. Escribe tu código SuperCollider y presiona Enter.")
        sys.stdout.flush()
        print("Para salir manualmente, escribe 'exit' o 'quit'.")
        sys.stdout.flush()

        # Manejar entrada del usuario
        while not stop_event.is_set():
            try:
                user_input = input("> ")
                if user_input.lower() in ["exit", "quit"]:
                    print("Cerrando sclang...")
                    sys.stdout.flush()
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
        print("sclang cerrado.")
        sys.stdout.flush()
    except Exception as e:
        error_message = f"Unexpected error occurred: {str(e)}\n{traceback.format_exc()}"
        print(error_message)
        if log_file:
            log_error(log_file, error_message)
    finally:
        if log_file:
            print(f"Log saved to: {log_file}")


if __name__ == "__main__":
    main()
