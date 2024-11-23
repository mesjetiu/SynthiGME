import subprocess
import threading
import os
import sys
from datetime import datetime
import io

# Detectar el sistema operativo: "windows" o "linux"
OPERATING_SYSTEM = "windows"  # Cambia a "windows" si estás en Windows

# Configurar codificación para evitar caracteres extraños en Windows
if OPERATING_SYSTEM == "windows":
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

# Detectar el directorio donde se encuentra el archivo Python
SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))
PARENT_DIR = os.path.abspath(os.path.join(SCRIPT_DIR, os.pardir))  # Directorio superior

# Configuración del comando para llamar a sclang
SCLANG_EXECUTABLE = os.path.join(SCRIPT_DIR, "sclang.exe" if OPERATING_SYSTEM == "windows" else "sclang")
SCLANG_CONFIG = os.path.join(PARENT_DIR, "sclang_conf.yaml")
SCLANG_COMMAND = [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG]
# SCLANG_COMMAND = "sclang"

# Directorio para guardar los logs
LOG_DIR = os.path.join(PARENT_DIR, "PostWindow_Logs")
os.makedirs(LOG_DIR, exist_ok=True)  # Crear el directorio si no existe


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
    print(f"Usando el ejecutable de sclang con el comando: {' '.join(SCLANG_COMMAND)}")

    # Verificar si el ejecutable de sclang está disponible
    if not os.path.isfile(SCLANG_EXECUTABLE):
        print(f"Error: No se encontró el ejecutable de sclang en {SCLANG_EXECUTABLE}.")
        print("Asegúrate de que sclang esté en el mismo directorio que este script.")
        sys.exit(1)

    try:
        # Configuración para manejar stdin y ventanas emergentes en Windows
        startupinfo = None
        if OPERATING_SYSTEM == "windows":
            startupinfo = subprocess.STARTUPINFO()
            startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW

        # Abrir el proceso de sclang usando el comando configurado
        process = subprocess.Popen(
            SCLANG_COMMAND,
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
