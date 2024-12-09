import tkinter as tk
from ui import TkinterTerminal


if __name__ == "__main__":
    root = tk.Tk()
    terminal = TkinterTerminal(root)
    terminal.start_sclang()
    root.mainloop()