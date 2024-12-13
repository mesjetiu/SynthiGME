# Versión para Windows
# Instalar dependencias de python con pip install -r requirements.txt
# Compilar con pyinstaller --noconsole main.py
# Compilar con pyinstaller --noconsole main.py (o python -m PyInstaller ...)
# Añadir icono al ejecutable con --icon=.\ruta_icono.ico

import tkinter as tk
import subprocess
import os
from ui import SynthiGMEApp
from config import SCRIPT_DIR

SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, ".SuperCollider")
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang.exe")
SCLANG_CONFIG = os.path.join(SCRIPT_DIR, "Config", "sclang_conf.yaml")
ICON_PATH = os.path.join(SCRIPT_DIR, "icono_SynthiGME.ico")  # Ruta al icono

def start_sclang():
    """Inicia el proceso de sclang y redirige la salida."""
    try:
        if not os.path.isfile(SCLANG_EXECUTABLE):
            raise FileNotFoundError(f"sclang executable not found at {SCLANG_EXECUTABLE}.")

        os.chdir(SUPER_COLLIDER_DIR)

        # Iniciar el proceso con configuración para capturar toda la salida
        process = subprocess.Popen(
            [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            bufsize=1,
            universal_newlines=True,
            encoding="utf-8",
            errors="replace",
            creationflags=subprocess.CREATE_NO_WINDOW,
        )
        return process
    except Exception as e:
        print(f"Error al iniciar sclang: {e}")
        raise

if __name__ == "__main__":
    sclang_process = start_sclang()
    root = tk.Tk()
    root.iconbitmap(ICON_PATH)  # Añadir el icono a la ventana
    app = SynthiGMEApp(root, sclang_process)
    root.mainloop()