o = SynthiGME()

o.modulKeyboards[0].pitch
o.modulKeyboards[0].velocity
o.modulKeyboards[0].gate

o.modulKeyboards[0].midiPitch
o.modulKeyboards[0].midiVelocity
o.modulKeyboards[0].keyGate
o.modulKeyboards[1].midiChannel



o.modulKeyboards[0].pitch = 2.35

o.modulKeyboards[0].velocity = 1
o.modulKeyboards[0].gate = 1

o.modulKeyboards[0].midiPitch = 40
o.modulKeyboards[0].midiVelocity = 0
o.modulKeyboards[0].keyGate = 0



o.modulKeyboards[0].pressRelease(60, 127, 0)
o.modulKeyboards[0].pressRelease(64, 50, 0)
o.modulKeyboards[0].pressRelease(67, 10, 0)
o.modulKeyboards[0].pressRelease(80, 50, 0)
o.modulKeyboards[0].pressRelease(84, 100, 0)
o.modulKeyboards[0].pressRelease(90, 120, 0)


o.setParameterOSC("/keyboard/1/midiNote", [60, 127, 0])
o.setParameterOSC("/keyboard/1/midiNote", [64, 50, 0])
o.setParameterOSC("/keyboard/1/midiNote", [67, 10, 1])
o.setParameterOSC("/keyboard/1/midiNote", [60, 127, 1])
o.setParameterOSC("/keyboard/1/midiNote", [80, 50, 1])
o.setParameterOSC("/keyboard/1/midiNote", [90, 120, 1])