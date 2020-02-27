+ Synthi100 {

	// Setter de los diferentes parámetros de los módulos en formato OSC.
	// Separado en archivo aparte por su larga extensión.
	setParameterOSC {|string, value, addrForbidden|
		var splitted = string.split($/);
		switch (splitted[1],
			"osc", { // Ejemplo: "/osc/1/pulse/level"
				var index = splitted[2].asInt - 1;
				var parameter;
				3.do({splitted.removeAt(0)});
				if (splitted.size == 1,
					{parameter = splitted[0]},
					{parameter = splitted[0]++splitted[1]}
				);
				switch (parameter,
					"range", {
						modulOscillators[index].setRange(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value}.defer(0)});
					},
					"frequency", {
						modulOscillators[index].setFrequency(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"pulselevel", {
						modulOscillators[index].setPulseLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"pulseshape", {
						modulOscillators[index].setPulseShape(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(-5,5,0,1)}.defer(0)});
					},
					"sinelevel", {
						modulOscillators[index].setSineLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"sinesymmetry", {
						modulOscillators[index].setSineSymmetry(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(-5,5,0,1)}.defer(0)});
					},
					"trianglelevel", {
						modulOscillators[index].setTriangleLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"sawtoothlevel", {
						modulOscillators[index].setSawtoothLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					}
				);
				// Se envía el mismo mensaje a GUI si está abierta
				//if(guiSC.running, {guiSC.parameterViews[string].value = value.linlin(0,10,0,1);});
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"patchA", { // Ejemplo "/patchA/91/36". Origen de coordenadas izquierda arriba / Orden: vertical y horzontal
				2.do({splitted.removeAt(0)});
				modulPatchbayAudio.administrateNode(splitted[0].asInt, splitted[1].asInt, value);
				if(guiSC.running, {{guiSC.parameterViews[string].value = value}.defer(0)});
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"patchV", { // Ejemplo "/patchV/91/36". Origen de coordenadas izquierda arriba / Orden: vertical y horzontal
				2.do({splitted.removeAt(0)});
				modulPatchbayVoltage.administrateNode(splitted[0].asInt, splitted[1].asInt, value);
				if(guiSC.running, {{guiSC.parameterViews[string].value = value}.defer(0)});
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},


			// Patchbay de Audio de TouchOSC: A1
			"patchATouchOSCA1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 67 + (16-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: A2
			"patchATouchOSCA2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 67 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: B1
			"patchATouchOSCB1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 83 + (16-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: B2
			"patchATouchOSCB2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 83 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: C1
			"patchATouchOSCC1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 99 + (16-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: C2
			"patchATouchOSCC2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 99 + (16-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: D1
			"patchATouchOSCD1a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 115 + (12-splitted[0].asInt);
				hor = splitted[1].asInt;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Patchbay de Audio de TouchOSC: D2
			"patchATouchOSCD2a", {
				var hor, ver;
				2.do({splitted.removeAt(0)});
				ver = 115 + (12-splitted[0].asInt);
				hor = splitted[1].asInt + 32;
				modulPatchbayAudio.administrateNode(ver, hor, value);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},


			"out", { // Ejemplo "/out/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {
						modulOutputChannels[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"filter", {
						modulOutputChannels[index].setFilter(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"on", {
						modulOutputChannels[index].setOn(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value}.defer(0)});
					},
					"pan", {
						modulOutputChannels[index].setPan(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"in", { // Ejemplo "/in/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {
						modulInputAmplifiers[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"env", { // Ejemplo "/env/1/decay"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"delay", {
						modulEnvelopeShapers[index].setDelayTime(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"attack", {
						modulEnvelopeShapers[index].setAttackTime(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"decay", {
						modulEnvelopeShapers[index].setDecayTime(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"sustain", {
						modulEnvelopeShapers[index].setSustainLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"release", {
						modulEnvelopeShapers[index].setReleaseTime(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"envelopeLevel", {
						modulEnvelopeShapers[index].setEnvelopeLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(-5,5,0,1)}.defer(0)});
					},
					"signalLevel", {
						modulEnvelopeShapers[index].setSignalLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(-5,5,0,1)}.defer(0)});
					},
					"gate", {
						modulEnvelopeShapers[index].setGateButton(value);
						//if(guiSC.running, {{guiSC.parameterViews[string].value = value}.defer(0)});
					},
					"selector", {
						modulEnvelopeShapers[index].setSelector(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = S100_GUIPanel1.selectorValuesConvert(value)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"ring", { // Ejemplo "/ring/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {
						modulRingModulators[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"noise", { // Ejemplo "/noise/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"colour", {
						modulNoiseGenerators[index].setColour(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"level", {
						modulNoiseGenerators[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			// Si el mensaje es distinto a los casos anteriores, se sale de la función
			{^this}
		);
		// Si el mensaje es correcto se guarda el último valor de cada cadena en el diccionario
		oscRecievedMessages.put(string, value);
	}
}