Synthi100 {
	var <server; // Servidor por defecto

	// Módulos que incluye:
	var <modulOscillators;
	var <modulOutputChannels;
	var <modulPatchbayAudio;
	var <conectionOut;

	// Buses internos de entrada y salida:
	var <audioInBuses;
	var <audioOutBuses;

	// Buses externos de entrada y salida:
	var <stereoOutBuses;

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

	*new { arg server = Server.local, stereoBuses = [0,1];
		^super.new.init(server, stereoBuses);
	}



	// Métodos de instancia //////////////////////////////////////////////////////////////

	init {|serv, stereoBuses|
		// Se añaden al servidor las declaracines SynthDefs
		S100_Oscillator.addSynthDef;
		S100_OutputChannel.addSynthDef;
		S100_PatchbayAudio.addSynthDef;
		server = serv;
		// Buses de audio de entrada y salida
		audioInBuses = numAudioInBuses.collect({Bus.audio(server, 1)});
		audioOutBuses = numAudioOutBuses.collect({Bus.audio(server, 1)});
		stereoOutBuses = stereoBuses;

		// Módulos
		modulOscillators = 12.collect({S100_Oscillator(serv)});
		modulOutputChannels = 8.collect({S100_OutputChannel(serv)});
		modulPatchbayAudio = S100_PatchbayAudio(server);
	}


	// Métodos de instancia /////////////////////////////////////////////////////////////////////////

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
						Out.ar(stereoOutBuses[i], sig);
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
		//		modulOutputChannels = modulOutputChannels.reverse; // para tenerlos en orden de arriba a abajo según la visibilidad entre synths en el mismo sentido

				// Oscillators
				modulOscillators.do({|i|
					i.createSynth;
					wait(waitTime);
				});
				wait(waitTime);
			//	modulOscillators = modulOscillators.reverse;

				modulPatchbayAudio.connect(modulOscillators, modulOutputChannels);

			}).play;
		});
	}

	stop {
		conectionOut.do({|i| i.free}); // provisional
		modulOscillators.do({|i| i.freeSynth});
		modulOutputChannels.do({|i| i.freeSynth});
		modulPatchbayAudio.freeSynths;
	}


	// Setter de los diferentes parámetros de los módulos en formato OSC
	setParameterOSC {|string, value|
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
					"range", {modulOscillators[index].setRange(value)},
					"frequency", {modulOscillators[index].setFrequency(value)},
					"pulselevel", {modulOscillators[index].setPulseLevel(value)},
					"pulseshape", {modulOscillators[index].setPulseShape(value)},
					"sinelevel", {modulOscillators[index].setSineLevel(value)},
					"sinesymmetry", {modulOscillators[index].setSineSymmetry(value)},
					"trianglelevel", {modulOscillators[index].setTriangleLevel(value)},
					"sawtoothlevel", {modulOscillators[index].setSawtoothLevel(value)}
				)
				//	modulOscillators[index].setParameter(parameter, value);
			},
			"patchA", { // Ejemplo "/patchA/91/36"
				2.do({splitted.removeAt(0)});
				modulPatchbayAudio.administrateNode(splitted[0].asInt, splitted[1].asInt, value);
			},
			"out", { // Ejemplo "/out/1/level"
				var index = splitted[2].asInt - 1;
				3.do({splitted.removeAt(0)});
				switch (splitted[0],
					"level", {modulOutputChannels[index].setLevel(value)},
				)
			},
		)
	}
}
