# This file is part of SynthiGME.

# SynthiGME is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# SynthiGME is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with SynthiGME. If not, see <https://www.gnu.org/licenses/>.

# Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>

import subprocess
import threading
import os
import sys
import platform
import psutil
from datetime import datetime
import io
import traceback

# Configure encoding to avoid strange characters on Windows
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

# Detect the directory where the script or executable is located
if getattr(sys, 'frozen', False):  # If packaged as .exe
    SCRIPT_DIR = os.path.dirname(sys.executable)  # Directory of the executable
else:
    SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))  # Directory of the Python script

# Configure paths based on SCRIPT_DIR
SUPER_COLLIDER_DIR = os.path.join(SCRIPT_DIR, ".SuperCollider")  # Path to .SuperCollider/
SCLANG_EXECUTABLE = os.path.join(SUPER_COLLIDER_DIR, "sclang.exe")
CONFIG_DIR = os.path.join(SCRIPT_DIR, "Config")  # Path to configuration directory
SCLANG_CONFIG = os.path.join(CONFIG_DIR, "sclang_conf.yaml")  # SC configuration
LOG_DIR = os.path.join(SCRIPT_DIR, "PostWindow_Logs")  # Log directory

# Ensure the log directory exists
os.makedirs(LOG_DIR, exist_ok=True)


def get_system_info():
    """Retrieve system information for the log header."""
    system_info = {
        "Timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "OS": platform.system(),
        "OS Version": platform.version(),
        "Architecture": platform.architecture()[0],
        "Processor": platform.processor(),
        "CPU Cores (Logical)": psutil.cpu_count(logical=True),
        "CPU Cores (Physical)": psutil.cpu_count(logical=False),
        "Total RAM (GB)": round(psutil.virtual_memory().total / (1024 ** 3), 2),
    }
    return system_info


def write_log_header(log_file):
    """Write a header with system information to the log file."""
    system_info = get_system_info()
    with open(log_file, "w", encoding="utf-8") as log:
        log.write("==== SynthiGME Log ====\n")
        log.write("Session Details:\n")
        for key, value in system_info.items():
            log.write(f"{key}: {value}\n")
        log.write("\n==== Log Output ====\n")
        log.flush()  # Ensure all data is written to the file


def log_error(log_file, error_message):
    """Log error messages to the log file."""
    with open(log_file, "a", encoding="utf-8") as log:
        log.write("\n==== ERROR ====\n")
        log.write(error_message + "\n")
        log.write("==============\n")
        log.flush()


def read_sclang_output(process, stop_event, log_file):
    """Reads and displays sclang process output in real time."""
    with open(log_file, "a", encoding="utf-8") as log:
        while not stop_event.is_set():
            try:
                output = process.stdout.readline()
                if output:
                    decoded_output = output.strip()
                    print(decoded_output)
                    sys.stdout.flush()  # Force output buffer to flush
                    log.write(decoded_output + "\n")
                    log.flush()  # Ensure the output is written to the file

                    # Detect "exit" in the post window output
                    if decoded_output.lower() == "exit":
                        print("Detected 'exit' message in Post Window. Automatically closing.")
                        sys.stdout.flush()
                        stop_event.set()
                        process.terminate()
                        break

                if process.poll() is not None:
                    stop_event.set()
                    break
            except Exception as e:
                error_message = f"Error reading sclang output: {str(e)}\n{traceback.format_exc()}"
                print(error_message)
                log_error(log_file, error_message)
                stop_event.set()
                break


def main():
    process = None
    log_file = None

    try:
        # Create a uniquely named log file in the log directory
        log_file = os.path.join(LOG_DIR, f"sclang_session_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
        print(f"Session log will be saved to: {log_file}")
        print(f"Using sclang executable at: {SCLANG_EXECUTABLE}")
        print(f"Working directory: {SUPER_COLLIDER_DIR}")

        # Write the log header
        write_log_header(log_file)

        # Verify that the sclang executable is available
        if not os.path.isfile(SCLANG_EXECUTABLE):
            raise FileNotFoundError(f"sclang executable not found at {SCLANG_EXECUTABLE}. "
                                    "Ensure the SuperCollider directory contains sclang.")

        # Change the working directory to SuperCollider
        os.chdir(SUPER_COLLIDER_DIR)

        # Configuration to handle stdin and prevent popup windows on Windows
        startupinfo = subprocess.STARTUPINFO()
        startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW

        # Open the sclang process with the configured command
        print("Starting sclang process...")
        process = subprocess.Popen(
            [SCLANG_EXECUTABLE, "-l", SCLANG_CONFIG],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,  # Combine stdout and stderr
            encoding='utf-8',  # Specify UTF-8 encoding
            errors='replace',  # Replace characters that cannot be decoded
            startupinfo=startupinfo
        )

        # Event to signal when to stop the thread
        stop_event = threading.Event()

        # Start a thread to read the sclang output in real time
        thread = threading.Thread(target=read_sclang_output, args=(process, stop_event, log_file), daemon=True)
        thread.start()

        print("sclang is running. Type your SuperCollider code and press Enter.")
        sys.stdout.flush()
        print("To exit manually, type 'exit' or 'quit'.")
        sys.stdout.flush()

        # Handle user input
        while not stop_event.is_set():
            try:
                user_input = input("> ")
                if user_input.lower() in ["exit", "quit"]:
                    print("Closing sclang...")
                    sys.stdout.flush()
                    if process.poll() is None and process.stdin:
                        process.stdin.write("0.exit\n")
                    stop_event.set()
                    break
                elif process.poll() is None and process.stdin:
                    process.stdin.write(user_input + "\n")
            except EOFError:
                break

        if process and process.poll() is None:
            process.terminate()
        if process:
            process.wait()
        stop_event.set()
        thread.join()
        print("sclang closed.")
        sys.stdout.flush()
    except Exception as e:
        error_message = f"Unexpected error occurred: {str(e)}\n{traceback.format_exc()}"
        print(error_message)
        if log_file:
            log_error(log_file, error_message)
    finally:
        if log_file:
            print(f"Log saved to: {log_file}")


if __name__ == "__main__":
    main()
