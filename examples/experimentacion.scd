Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/Synthi100")
o = Synthi100().run
o.run

//Quarks.install("https://github.com/mesjetiu/Synthi100.git")

Quarks.uninstall("https://github.com/mesjetiu/Synthi100.git")

Quarks.clear

Window

o = Synthi100().run.pairDevice
o = Synthi100().run
o.run
o.close
o.guiSC.makeWindow
o.pairDevice

o = Synthi100()
o.run
o.setParameterOSC("\env\1\gate", 1)
o.close
o.guiSC.closeWindows;
o.guiSC.frontWindows;
o.guiSC.panels[0].window.front
o.guiSC.panels[2].parameterVisibility(true)















Routine