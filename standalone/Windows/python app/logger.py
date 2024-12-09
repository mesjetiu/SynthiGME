import os
from config import SCRIPT_DIR, get_system_info

LOG_DIR = os.path.join(SCRIPT_DIR, "PostWindow_Logs")
os.makedirs(LOG_DIR, exist_ok=True)


def write_log_header(log_file):
    """Escribe un encabezado con información del sistema en el archivo de log."""
    system_info = get_system_info()
    with open(log_file, "w", encoding="utf-8") as log:
        log.write("==== SynthiGME Log ====\n")
        log.write("Session Details:\n")
        for key, value in system_info.items():
            log.write(f"{key}: {value}\n")
        log.write("\n==== Log Output ====\n")
        log.flush()