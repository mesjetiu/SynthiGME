import subprocess
import threading
import time
import sys

def read_sclang_output(process, stop_event):
    """Lee y muestra en tiempo real las salidas del proceso de sclang, buscando las frases consecutivas clave."""
    buffer = []  # Almacena las últimas líneas leídas
    required_phrases = [
        "main: waiting for input thread to join...",
        "main: quitting...",
        "cleaning up OSC"
    ]
    
    while not stop_event.is_set():
        output = process.stdout.readline()
        if output:
            decoded_output = output.decode('utf-8').strip()
            print(decoded_output)
            
            # Añadir la línea al buffer y mantener el tamaño máximo
            buffer.append(decoded_output)
            if len(buffer) > len(required_phrases):
                buffer.pop(0)
            
            # Verificar si las últimas líneas coinciden con las frases requeridas
            if buffer == required_phrases:
                print("Frases clave detectadas. Cerrando automáticamente...")
                stop_event.set()  # Marcar el evento de detención
                return

        if process.poll() is not None:
            stop_event.set()
            return

def main():
    try:
        # Abrir el proceso de sclang
        process = subprocess.Popen(
            ["sclang"],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=False  # Para manejar las salidas como bytes
        )
        
        # Evento para señalar cuando parar el hilo
        stop_event = threading.Event()
        
        # Iniciar un hilo para leer la salida de sclang en tiempo real
        thread = threading.Thread(target=read_sclang_output, args=(process, stop_event), daemon=True)
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

        # Esperar a que el proceso termine completamente
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
