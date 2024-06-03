#!/usr/bin/env python3

import subprocess
import time
import os

def iniciar_supercollider(sclang_path):
    """
    Inicia sclang y espera a que esté listo para recibir comandos.
    :param sclang_path: Ruta al ejecutable de sclang.
    :return: Proceso de SuperCollider.
    """
    try:
        # Configuración específica para Windows para evitar abrir una ventana de terminal
        startupinfo = None
        if os.name == 'nt':  # Configuración específica para Windows
            startupinfo = subprocess.STARTUPINFO()
            startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW

        process_SC = subprocess.Popen(
            [sclang_path],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1,
            universal_newlines=True,
            startupinfo=startupinfo  # Agregar startupinfo para evitar ventana
        )
        return process_SC
    except Exception as e:
        print(f"Error al iniciar SuperCollider: {e}")
        return None

def esperar_listo(process_SC):
    """
    Espera hasta que SuperCollider imprima el mensaje de bienvenida indicando que está listo.
    :param process_SC: Proceso de SuperCollider.
    :return: None.
    """
    print("Esperando a que SuperCollider esté listo...")
    while True:
        output = process_SC.stdout.readline()
        if "Welcome to SuperCollider" in output:
            print("SuperCollider está listo.")
            break
        elif output == '':
            break
        else:
            print(output.strip())

def enviar_comando(process_SC, comando):
    """
    Envía un comando a SuperCollider y muestra la respuesta.
    :param process_SC: Proceso de SuperCollider.
    :param comando: Comando de SuperCollider a ejecutar.
    :return: None.
    """
    print(f"Enviando comando: {comando}")
    process_SC.stdin.write(comando + '\n')
    process_SC.stdin.flush()  # Asegúrate de que el comando se envía

    # Leer la respuesta
    while True:
        output = process_SC.stdout.readline()
        if output == '':
            break
        print(output.strip())

# Ruta al ejecutable de sclang (ajusta según sea necesario)
sclang_path = "sclang"

# Iniciar SuperCollider
process_SC = iniciar_supercollider(sclang_path)

if process_SC:
    # Esperar a que SuperCollider esté listo
    esperar_listo(process_SC)
    # Enviar el comando SynthiGME() a SuperCollider
    enviar_comando(process_SC, "SynthiGME(numOutputChannels: inf, numInputChannels: inf, numReturnChannels: inf);")
    process_SC.terminate()  # Termina el proceso después de enviar el comando
