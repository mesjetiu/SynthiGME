# Versión para Windows
# Instalar dependencias de python con pip install -r requirements.txt


import tkinter as tk
from ui import SynthiGMEApp

if __name__ == "__main__":
    root = tk.Tk()
    app = SynthiGMEApp(root)
    app.start_sclang()
    root.mainloop()