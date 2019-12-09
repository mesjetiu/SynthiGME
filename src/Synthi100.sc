Synthi100 {
	var <server; // Servidor por defecto

	// Módulos que incluye:
	var modulOscillators;
	var <modulOutputChannels;
	var modulPatchbayAudio;
	var <conectionOut;

	// Buses internos de entrada y salida:
	var <audioInBuses;
	var <audioOutBuses;

	// Buses externos de entrada y salida: (se unen uno a uno a los buses internos)
	var <externAudioInBuses;
	var <externAudioOutBuses;

	// Diccionario con los símbolos de cada módulo:
	var prParameterDictionary;

	// Opciones:
	const numAudioInBuses = 8;
	const numAudioOutBuses = 8;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(S100_Oscillator);
		Class.initClassTree(S100_OutputChannel);
		Class.initClassTree(S100_PatchbayAudio);
	}

	*new { |server, audioInBuses, audioOutBuses|
		^super.new.init(server, audioInBuses, audioOutBuses);
	}



	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local, aInBuses, aOutBuses;
		// Se añaden al servidor las declaracines SynthDefs
		S100_Oscillator.addSynthDef;
		S100_OutputChannel.addSynthDef;
		S100_PatchbayAudio.addSynthDef;
		server = serv;
		// Buses de audio de entrada y salida
		audioInBuses = numAudioInBuses.collect({Bus.audio(server, 1)});
		audioOutBuses = numAudioOutBuses.collect({Bus.audio(server, 1)});
		externAudioInBuses = numAudioInBuses.collect({nil});
		externAudioOutBuses = numAudioOutBuses.collect({nil});
		this.setExternAudioInBuses (aInBuses);
		this.setExternAudioOutBuses (aOutBuses);

		// Módulos
		modulOscillators = 12.collect({S100_Oscillator(serv)});
		modulOutputChannels = 8.collect({S100_OutputChannel(serv)});
		modulPatchbayAudio = S100_PatchbayAudio(server, modulOscillators, modulOutputChannels);

		// Diccionario de parámetros de la interfaz física del Synthi 100
		prParameterDictionary = this.createParameterDictionary;
	}


	// Métodos de instancia /////////////////////////////////////////////////////////////////////////
	setExternAudioInBuses {arg audioBuses;
		if(audioBuses.class == Array, {
			numAudioInBuses.do({|i|
				externAudioInBuses[i] = audioBuses[i];
			})
		})
	}

	setExternAudioOutBuses {arg audioBuses;
		if(audioBuses.class== Array, {
			numAudioOutBuses.do({|i|
				externAudioOutBuses[i] = audioBuses[i];
			})
		})
	}


	play {
		server.waitForBoot({
			// TODO: Crear alguna rutina para colocar en orden todos los Synths en el servidor. Ahora están colocados mezclados los osciladores y los conectores.

			// Rutina para espaciar temporalmente la creacion de cada Synth, de forma que queden ordenados.
			Routine({
				var waitTime = 0.01; // Tiempo de espera entre la creación de cada Synth

				// Se conectan provisionalmente las salidas de todos los módulos a los dos primeros buses de salida especificados en externAudioBuses
				conectionOut = 2.collect({|i|
					SynthDef(\conection, {
						var sig = In.ar(modulOutputChannels[i].outputBus);
						Out.ar(i, sig);
					}).play(server);
					wait(waitTime);
				});
				wait(waitTime);

				// Se arrancan todos los Synths de todos los módulos //////////////////////////////////

				// Output Channels
				modulOutputChannels.do({|i|
					i.createSynth;
					wait(waitTime);
				});
				wait(waitTime);

				// Oscillators
				modulOscillators.do({|i|
					i.createSynth;
					wait(waitTime);
				});
				wait(waitTime);

				// Conexiones provisionales con patchbayAudio
				modulPatchbayAudio.connect(
					modulOscillators[0],
					modulOscillators[0].outBus1,
					modulOutputChannels[0].inputBus,
					1);
				modulPatchbayAudio.connect(
					modulOscillators[0],
					modulOscillators[0].outBus2,
					modulOutputChannels[1].inputBus,
					1);
			}).play;
		});
	}

	stop {
		conectionOut.do({|i| i.free}); // provisional
		modulOscillators.do({|i| i.freeSynth});
	}


	// Setter de los diferentes parámetros de los módulos en formato OSC

	setParameterOSC {|simbol, value|
		prParameterDictionary[simbol].value(value);
	}
}
