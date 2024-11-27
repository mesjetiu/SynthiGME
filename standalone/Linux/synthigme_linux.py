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





#----NO TESTADO--------------------------------------------------


import subprocess
import threading
import os
import sys
from datetime import datetime

# Detectar el directorio donde se encuentra el script o el ejecutable
if getattr(sys, 'frozen', False):  # Si está empaquetado
    SCRIPT_DIR = os.path.dirname(sys.executable)  # Directorio del ejecutable
else:
    SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))  # Directorio del script Python

# Configurar rutas basadas en SCRIPT_DIR
SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, "SuperCollider")  # Ruta a SuperCollider/
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang")
SCLANG_CONFIG = os.path.join(SCRIPT_DIR, "sclang_conf.yaml")  # Configuración en el directorio raíz
LOG_DIR = os.path.join(SCRIPT_DIR, "PostWindow_Logs")  # Directorio de logs

# Asegurar que el directorio de logs existe
os.makedirs(LOG_DIR, exist_ok=True)


def read_sclang_output(process, stop_event, log_file):
    """Lee y muestra en tiempo real las salidas del proceso de sclang."""
    with open(log_file, "a", encoding="utf-8") as log:
        while not stop_event.is_set():
            output = process.stdout.readline()
            if output:
                print(output.strip())
                sys.stdout.flush()
                log.write(output.strip() + "\n")

                if output.strip().lower() == "exit":
                    stop_event.set()
                    process.terminate()
                    break

            if process.poll() is not None:
                stop_event.set()
                break


def main():
    log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")

    if not os.path.isfile(SCLANG_EXECUTABLE):
        print(f"No se encontró el ejecutable en {SCLANG_EXECUTABLE}")
        sys.exit(1)

    os.chdir(SUPER_COLLIDER_DIR)

    process = subprocess.Popen(
        [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        universal_newlines=True
    )

    stop_event = threading.Event()
    thread = threading.Thread(target=read_sclang_output, args=(process, stop_event, log_file), daemon=True)
    thread.start()

    while not stop_event.is_set():
        try:
            user_input = input("> ")
            if user_input.lower() in ["exit", "quit"]:
                process.terminate()
                break
            else:
                process.stdin.write(user_input + "\n")
                process.stdin.flush()
        except EOFError:
            break

    process.wait()
    stop_event.set()
    thread.join()


if __name__ == "__main__":
    main()
