import subprocess
import threading
import os
import sys
from datetime import datetime

# Especifica el sistema operativo: "windows" o "linux"
OPERATING_SYSTEM = "windows"  # Cambia a "linux" si estás en Linux

# Detectar el directorio donde se encuentra el archivo Python
SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))

# Ejecutable de sclang, se puede modificar directamente aquí.
# Si necesitas usar un archivo específico de sclang en un directorio concreto, descomenta y ajusta la línea siguiente:
SCLANG_EXECUTABLE = os.path.join(SCRIPT_DIR, "sclang.exe" if OPERATING_SYSTEM == "windows" else "sclang")
# SCLANG_EXECUTABLE = "sclang.exe" if OPERATING_SYSTEM == "windows" else "sclang"

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
            if process.poll() is not None:
                stop_event.set()
                break

def main():
    # Crear archivo de log con nombre único en el mismo directorio del script
    log_file = os.path.join(SCRIPT_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
    print(f"Registro de la sesión se guardará en: {log_file}")
    print(f"Usando el ejecutable de sclang en: {SCLANG_EXECUTABLE}")

    try:
        # Configuración para manejar stdin y ventanas emergentes en Windows
        startupinfo = None
        if OPERATING_SYSTEM == "windows":
            startupinfo = subprocess.STARTUPINFO()
            startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW

        # Abrir el proceso de sclang usando el path personalizado
        process = subprocess.Popen(
            [SCLANG_EXECUTABLE],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,  # Combinar stdout y stderr
            universal_newlines=True,  # Convertir la salida a texto
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
                    if process.stdin:  # Validar que stdin está disponible
                        process.stdin.write("0.exit;\n")
                        process.stdin.flush()
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
