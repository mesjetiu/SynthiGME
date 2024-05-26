(
o = SynthiGME();
o.run
)

SynthiGME.update

Syn

i = Image(1)

d = o.getState

d["/reverb/level"].postln

d.at("/osc/1/pulse/level").postln

d.class

Dictionary


o.modulPatchbayAudio

o.pairDevice

Quark("SynthiGME").localPath == Quarks.quarkNameAsLocalPath("SynthiGME")

Quark

Quarks

Collection

[1,2,3,4].any({|item| item == 2})


[1,2].asString

a = 3
b = 8
[a,b].asString


(44..58) ++ [65, 66]

Main.version

thisProcess.openUDPPort(9000)
thisProcess.openPorts

"ifconfig".unixCmdGetStdOut; // macOS o algunas versiones de Linux
"ip addr".unixCmdGetStdOut;  // Otras versiones de Linux

"hola" + 3

"hostname -I".unixCmdGetStdOut

NetAddr.localAddr