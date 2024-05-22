/*
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

+ SynthiGME {

	// Setter de los diferentes parámetros de los módulos en formato OSC.
	// Separado en archivo aparte por su larga extensión.
	setParameterOSC {|string, value, addrForbidden, broadcast = true|
		var splitted = string.split($/);
		//value = value.round(0.01); // El resto de decimales es ruido.
		modifiedState = true;
		switch (splitted[1],
			"osc", { // Ejemplo: "/osc/1/pulse/level"
				var index = splitted[2].asInteger - 1;
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
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"patchA", { // Ejemplo "/patchA/91/36". Origen de coordenadas izquierda arriba / Orden: vertical y horizontal
				2.do({splitted.removeAt(0)});
				modulPatchbayAudio.administrateNode(splitted[0].asInteger, splitted[1].asInteger, value);
				if(guiSC.running, {{guiSC.parameterViews[string].value_(value)}.defer(0)});
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"patchV", { // Ejemplo "/patchV/91/36". Origen de coordenadas izquierda arriba / Orden: vertical y horizontal
				2.do({splitted.removeAt(0)});
				modulPatchbayVoltage.administrateNode(splitted[0].asInteger, splitted[1].asInteger, value);
				if(guiSC.running, {{guiSC.parameterViews[string].value_(value)}.defer(0)});
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				this.sendBroadcastMsg(string, value, addrForbidden);
			},

			"out", { // Ejemplo "/out/1/level"
				var index = splitted[2].asInteger - 1;
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
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"in", { // Ejemplo "/in/1/level"
				var index = splitted[2].asInteger - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {
						modulInputAmplifiers[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"return", { // Ejemplo "/return/1/level"
				var index = splitted[2].asInteger - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {
						modulExternalTreatmentReturns[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"env", { // Ejemplo "/env/1/decay"
				var index = splitted[2].asInteger - 1;
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
						if(guiSC.running, {{guiSC.parameterViews[string].value
							= SGME_GUIPanel1.selectorValuesConvert(value)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"ring", { // Ejemplo "/ring/1/level"
				var index = splitted[2].asInteger - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {
						modulRingModulators[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"noise", { // Ejemplo "/noise/1/level"
				var index = splitted[2].asInteger - 1;
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
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"random", { // Ejemplo "/random/mean"
				switch (splitted[2],
					"mean", {
						modulRandomGenerator.setMean(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(-5,5,0,1)}.defer(0)});
					},
					"variance", {
						modulRandomGenerator.setVariance(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(-5,5,0,1)}.defer(0)});
					},
					"voltage1", {
						modulRandomGenerator.setVoltage1(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"voltage2", {
						modulRandomGenerator.setVoltage2(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"key", {
						modulRandomGenerator.setKey(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(-5,5,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},


			"slew", { // Ejemplo "/slew/1/range"
				var index = splitted[2].asInteger - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"rate", {
						modulSlewLimiters[index].setRate(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"filter", { // Ejemplo "/filter/1/response"
				var index = splitted[2].asInteger - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"frequency", {
						modulFilters[index].setFrequency(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"response", {
						modulFilters[index].setResponse(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"level", {
						modulFilters[index].setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},

			"filterBank", { // Ejemplo "/filter/63"
				switch (splitted[2],
					"63", {
						modulFilterBank.setBand63(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"125", {
						modulFilterBank.setBand125(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"250", {
						modulFilterBank.setBand250(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"500", {
						modulFilterBank.setBand500(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"1000", {
						modulFilterBank.setBand1000(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"2000", {
						modulFilterBank.setBand2000(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"4000", {
						modulFilterBank.setBand4000(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"8000", {
						modulFilterBank.setBand8000(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},


			"reverb", { // Ejemplo "/reverb/level"
				switch (splitted[2],
					"mix", {
						modulReverb.setMix(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"level", {
						modulReverb.setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},


			"echo", { // Ejemplo "/echo/level"
				switch (splitted[2],
					"delay", {
						modulEcho.setDelay(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"mix", {
						modulEcho.setMix(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"feedback", {
						modulEcho.setFeedback(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"level", {
						modulEcho.setLevel(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},


			"oscilloscope", { // Ejemplo "/oscilloscope/sensCH1"
				switch (splitted[2],
					"sensCH1", {
						modulEcho.setVarSensCH1(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"sensCH2", {
						modulEcho.setVarSensCH2(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
					"mode", {
						modulEcho.setMode(value);
						if(guiSC.running, {{guiSC.parameterViews[string].value = value.linlin(0,10,0,1)}.defer(0)});
					},
				);
				// Se envía el mismo mensaje a todas las direcciones menos a la remitente
				if (broadcast) {
					this.sendBroadcastMsg(string, value);
				}
			},


			// Si el mensaje es distinto a los casos anteriores, se sale de la función
			{^this}
		);
		// Si el mensaje es correcto se guarda el último valor de cada cadena en el diccionario
		oscRecievedMessages.put(string, value);
		// Se imprime en Post window
		if (this.verboseOSC,
			{(string + value.round(0.01)).postln}
		)
	}
}