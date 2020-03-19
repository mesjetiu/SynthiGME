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

o.modulFilterBank.inCount(false)
o.modulFilterBank.outCount