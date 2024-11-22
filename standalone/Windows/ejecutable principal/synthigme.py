import subprocess
import threading
import time
import sys
import os
from datetime import datetime

# Detectar el directorio donde se encuentra el archivo Python
SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))

# Path personalizado para el ejecutable `sclang`, basado en el directorio del script
SCLANG_EXECUTABLE = os.path.join(SCRIPT_DIR, "sclang")

def read_sclang_output(process, stop_event, log_file):
    """Lee y muestra en tiempo real las salidas del proceso de sclang, buscando las frases consecutivas clave para salir automáticamente del programa."""
    buffer = []  # Almacena las últimas líneas leídas
    required_phrases = [ # Estas frases son devueltas tras el comando "0.exit" en SuperCollider 3.13.0
        "main: waiting for input thread to join...",
        "main: quitting...",
        "cleaning up OSC"
    ]
    
    while not stop_event.is_set():
        output = process.stdout.readline()
        if output:
            decoded_output = output.decode('utf-8').strip()
            print(decoded_output)
            
            # Escribir en el archivo de log
            with open(log_file, "a") as log:
                log.write(decoded_output + "\n")
            
            # Añadir la línea al buffer y mantener el tamaño máximo
            buffer.append(decoded_output)
            if len(buffer) > len(required_phrases):
                buffer.pop(0)
            
            # Verificar si las últimas líneas coinciden con las frases requeridas
            if buffer == required_phrases:
                print("Frases clave detectadas. Cerrando automáticamente...")
                stop_event.set()
                process.terminate()
                process.wait()
                print("sclang cerrado.")
                sys.exit(0)  # Salida directa al detectar las frases clave

        if process.poll() is not None:
            stop_event.set()
            return

def main():
    # Crear archivo de log con nombre único en el mismo directorio del script
    log_file = os.path.join(SCRIPT_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
    print(f"Registro de la sesión se guardará en: {log_file}")
    print(f"Usando el ejecutable de sclang en: {SCLANG_EXECUTABLE}")
    
    try:
        # Abrir el proceso de sclang usando el path personalizado
        process = subprocess.Popen(
            [SCLANG_EXECUTABLE],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=False  # Para manejar las salidas como bytes
        )
        
        # Evento para señalar cuando parar el hilo
        stop_event = threading.Event()
        
        # Iniciar un hilo para leer la salida de sclang en tiempo real
        thread = threading.Thread(target=read_sclang_output, args=(process, stop_event, log_file), daemon=True)
        thread.start()

        print("sclang está corriendo. Escribe tu código SuperCollider y presiona Enter.")
        print("Para salir manualmente, escribe 'exit' o 'quit'.")

        # Bucle principal revisando periódicamente el evento de salida
        while not stop_event.is_set():
            try:
                if sys.stdin in select.select([sys.stdin], [], [], 0.1)[0]:
                    user_input = input("> ")
                    if user_input.lower() in ["exit", "quit"]:
                        print("Cerrando sclang...")
                        process.stdin.write(b"\x03")  # Enviar señal Ctrl+C
                        process.stdin.flush()
                        break
                    else:
                        process.stdin.write((user_input + "\n").encode('utf-8'))
                        process.stdin.flush()
            except BrokenPipeError:
                # Detectar si el proceso ha cerrado su canal stdin
                print("Canal de comunicación con sclang cerrado.")
                break

        # Si el hilo se detiene, esperar su finalización y cerrar
        stop_event.set()
        thread.join()
        process.terminate()
        process.wait()
        print("sclang cerrado.")
    except KeyboardInterrupt:
        print("\nInterrumpido por el usuario. Cerrando sclang...")
        process.terminate()
        stop_event.set()
        process.wait()

if __name__ == "__main__":
    import select  # Mover importación aquí para claridad
    main()
