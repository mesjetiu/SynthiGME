#!/usr/bin/env python3

import subprocess

def iniciar_supercollider(sclang_path):
    """
    Iniciar sclang con el proceso de SuperCollider.
    :param sclang_path: Ruta al ejecutable de sclang.
    :return: Proceso de SuperCollider.
    """
    try:
        process_SC = subprocess.Popen([sclang_path, "./SGME.scd"], stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                      stderr=subprocess.PIPE, text=True, bufsize=1, universal_newlines=True)
        return process_SC
    except Exception as e:
        print(f"Error al iniciar SuperCollider: {e}")
        return None

# Ruta al ejecutable de sclang (ajusta según sea necesario)
sclang_path = "sclang" #"/ruta/a/sclang"

# Iniciar SuperCollider
process_SC = iniciar_supercollider(sclang_path)

if process_SC is not None:
    # Aquí podrías comunicarte con el proceso o manejar la salida
    stdout, stderr = process_SC.communicate()  # Esto esperará a que termine el proceso
    print("Salida de SuperCollider:", stdout)
    if stderr:
        print("Error de SuperCollider:", stderr)

