# This file is part of SynthiGME.

# SynthiGME is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# SynthiGME is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

# Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>

import subprocess
import threading
import os
import sys
from datetime import datetime
import io

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
CONFIG_DIR = os.path.join(SCRIPT_DIR, "Config") # Ruta a directorio de configuración
SCLANG_CONFIG = os.path.join(CONFIG_DIR, "sclang_conf.yaml")  # Configuración de SC
LOG_DIR = os.path.join(SCRIPT_DIR, "PostWindow_Logs")  # Directorio de logs

# Asegurar que el directorio de logs existe
os.makedirs(LOG_DIR, exist_ok=True)


def read_sclang_output(process, stop_event, log_file):
    """Lee y muestra en tiempo real las salidas del proceso de sclang."""
    with open(log_file, "a", encoding="utf-8") as log:
        while not stop_event.is_set():
            output = process.stdout.readline()
            if output:
                decoded_output = output.strip()
                print(decoded_output)
                sys.stdout.flush()  # Forzar vaciado del buffer de salida
                log.write(decoded_output + "\n")

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


def main():
    # Crear archivo de log con nombre único en el directorio de logs
    log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
    print(f"Registro de la sesión se guardará en: {log_file}")
    print(f"Usando el ejecutable de sclang en: {SCLANG_EXECUTABLE}")
    print(f"Directorio de trabajo: {SUPER_COLLIDER_DIR}")

    # Verificar si el ejecutable de sclang está disponible
    if not os.path.isfile(SCLANG_EXECUTABLE):
        print(f"Error: No se encontró el ejecutable de sclang en {SCLANG_EXECUTABLE}.")
        print("Asegúrate de que el directorio SuperCollider contiene sclang.")
        sys.exit(1)

    try:
        # Cambiar el directorio de trabajo a SuperCollider
        os.chdir(SUPER_COLLIDER_DIR)

        # Configuración para manejar stdin y ventanas emergentes en Windows
        startupinfo = subprocess.STARTUPINFO()
        startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW

        # Abrir el proceso de sclang usando el comando configurado
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
                    process.terminate()  # Termina el proceso sin enviar "0.exit"
                    break
                else:
                    if process.stdin:  # Validar que stdin está disponible
                        process.stdin.write(user_input + "\n")
                        process.stdin.flush()
            except EOFError:
                break

        # Esperar a que el proceso termine
        process.wait()
        stop_event.set()
        thread.join()
        print("sclang cerrado.")
        sys.stdout.flush()
    except KeyboardInterrupt:
        print("\nInterrumpido por el usuario. Cerrando sclang...")
        sys.stdout.flush()
        process.terminate()
        stop_event.set()
        process.wait()
    except OSError as e:
        print(f"Error al manejar stdin: {e}")
        sys.stdout.flush()


if __name__ == "__main__":
    main()

