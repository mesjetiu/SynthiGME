import sys
import os
import platform
import psutil
import yaml
from datetime import datetime


# Detectar el directorio donde se encuentra el script o el ejecutable
if getattr(sys, 'frozen', False):  # Si está empaquetado como .exe
    SCRIPT_DIR = os.path.dirname(sys.executable)  # Directorio del ejecutable
else:
    SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))  # Directorio del script Python

CONFIG_DIR = os.path.join(SCRIPT_DIR, "Config")
VERSION_FILE = os.path.join(SCRIPT_DIR, ".Extensions", "SynthiGME", "version")
ICON_PATH = os.path.join(SCRIPT_DIR, "icono_SynthiGME.ico")   # Adjust path as needed

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


def load_config():
    """Carga la configuración desde el archivo YAML."""
    config_file = os.path.join(CONFIG_DIR, "synthigme_config.yaml")
    with open(config_file, "r", encoding="utf-8") as file:
        return yaml.safe_load(file)

def save_config(config):
    """Guarda la configuración en el archivo YAML."""
    config_file = os.path.join(CONFIG_DIR, "synthigme_config.yaml")
    with open(config_file, "w", encoding="utf-8") as file:
        yaml.safe_dump(config, file)