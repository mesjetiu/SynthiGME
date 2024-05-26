#!/usr/bin/env python3

import subprocess

# Comando que queremos ejecutar
command = "sclang"

# Código SuperCollider que queremos enviar
sc_code = "SynthiGME()"

# Ejecutar el comando sclang y pasar el código usando un pipe
process = subprocess.Popen([command], stdin=subprocess.PIPE, text=True)
process.communicate(sc_code)

