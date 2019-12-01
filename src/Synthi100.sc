Synthi100 {
	var <>server; // Servidor por defecto

	// Módulos que incluye:
	var prOscillators;

	// Buses internos de entrada y salida:
	var <audioInBuses;
	var <audioOutBuses;

	// Buses externos de entrada y salida: (se unen uno a uno a los buses internos)
	var <externAudioInBuses;
	var <externAudioOutBuses;

	// Diccionario con los símbolos de cada módulo:
	var prParameterDictionary;

	// Opciones:
	var <numAudioInBuses = 8;
	var <numAudioOutBuses = 8;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(S100_Oscillator);
	}

	*new { |server, audioInBuses, audioOutBuses|
		^super.new.init(server, audioInBuses, audioOutBuses);
	}



	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local, aInBuses, aOutBuses;
		server = serv;
		// Buses de audio de entrada y salida
		audioInBuses = numAudioInBuses.collect({Bus.audio(server, 1)});
		audioOutBuses = numAudioOutBuses.collect({Bus.audio(server, 1)});
		externAudioInBuses = numAudioInBuses.collect({nil});
		externAudioOutBuses = numAudioOutBuses.collect({nil});
		this.setExternAudioInBuses (aInBuses);
		this.setExternAudioOutBuses (aOutBuses);
		// Módulos
		prOscillators = 12.collect({S100_Oscillator.new(serv, externAudioOutBuses[0])}); // Se conecta provisionalmente directamente con "externAudioOutBuses". En el futuro se hara por medio de PatchBay.

		// Diccionario de parámetros de la interfaz física del Synthi 100
		prParameterDictionary = Dictionary.newFrom(List[
			\Osc01PulseLevel, {|level| prOscillators[0].setPulseLevel(level)},
			\Osc01PulseShape, {|shape| prOscillators[0].setPulseShape(shape)},
			\Osc01SineLevel, {|level| prOscillators[0].setSineLevel(level)},
			\Osc01SineSymmetry, {|symmetry| prOscillators[0].setPulseLevel(symmetry)},
			\Osc01TriangleLevel, {|level| prOscillators[0].setTriangleLevel(level)},
			\Osc01SawtoothLevel, {|level| prOscillators[0].setSawtoothLevel(level)},
			\Osc01Frequency, {|freq| prOscillators[0].setFreqOscillator(freq)},

			\Osc02PulseLevel, {|level| prOscillators[1].setPulseLevel(level)},
			\Osc02PulseShape, {|shape| prOscillators[1].setPulseShape(shape)},
			\Osc02SineLevel, {|level| prOscillators[1].setSineLevel(level)},
			\Osc02SineSymmetry, {|symmetry| prOscillators[1].setPulseLevel(symmetry)},
			\Osc02TriangleLevel, {|level| prOscillators[1].setTriangleLevel(level)},
			\Osc02SawtoothLevel, {|level| prOscillators[1].setSawtoothLevel(level)},
			\Osc02Frequency, {|freq| prOscillators[1].setFreqOscillator(freq)},

			\Osc03PulseLevel, {|level| prOscillators[2].setPulseLevel(level)},
			\Osc03PulseShape, {|shape| prOscillators[2].setPulseShape(shape)},
			\Osc03SineLevel, {|level| prOscillators[2].setSineLevel(level)},
			\Osc03SineSymmetry, {|symmetry| prOscillators[2].setPulseLevel(symmetry)},
			\Osc03TriangleLevel, {|level| prOscillators[2].setTriangleLevel(level)},
			\Osc03SawtoothLevel, {|level| prOscillators[2].setSawtoothLevel(level)},
			\Osc03Frequency, {|freq| prOscillators[2].setFreqOscillator(freq)},

			\Osc04PulseLevel, {|level| prOscillators[3].setPulseLevel(level)},
			\Osc04PulseShape, {|shape| prOscillators[3].setPulseShape(shape)},
			\Osc04SineLevel, {|level| prOscillators[3].setSineLevel(level)},
			\Osc04SineSymmetry, {|symmetry| prOscillators[3].setPulseLevel(symmetry)},
			\Osc04TriangleLevel, {|level| prOscillators[3].setTriangleLevel(level)},
			\Osc04SawtoothLevel, {|level| prOscillators[3].setSawtoothLevel(level)},
			\Osc04Frequency, {|freq| prOscillators[3].setFreqOscillator(freq)},

			\Osc05PulseLevel, {|level| prOscillators[4].setPulseLevel(level)},
			\Osc05PulseShape, {|shape| prOscillators[4].setPulseShape(shape)},
			\Osc05SineLevel, {|level| prOscillators[4].setSineLevel(level)},
			\Osc05SineSymmetry, {|symmetry| prOscillators[4].setPulseLevel(symmetry)},
			\Osc05TriangleLevel, {|level| prOscillators[4].setTriangleLevel(level)},
			\Osc05SawtoothLevel, {|level| prOscillators[4].setSawtoothLevel(level)},
			\Osc05Frequency, {|freq| prOscillators[4].setFreqOscillator(freq)},

			\Osc06PulseLevel, {|level| prOscillators[5].setPulseLevel(level)},
			\Osc06PulseShape, {|shape| prOscillators[5].setPulseShape(shape)},
			\Osc06SineLevel, {|level| prOscillators[5].setSineLevel(level)},
			\Osc06SineSymmetry, {|symmetry| prOscillators[5].setPulseLevel(symmetry)},
			\Osc06TriangleLevel, {|level| prOscillators[5].setTriangleLevel(level)},
			\Osc06SawtoothLevel, {|level| prOscillators[5].setSawtoothLevel(level)},
			\Osc06Frequency, {|freq| prOscillators[5].setFreqOscillator(freq)},

			\Osc07PulseLevel, {|level| prOscillators[6].setPulseLevel(level)},
			\Osc07PulseShape, {|shape| prOscillators[6].setPulseShape(shape)},
			\Osc07SineLevel, {|level| prOscillators[6].setSineLevel(level)},
			\Osc07SineSymmetry, {|symmetry| prOscillators[6].setPulseLevel(symmetry)},
			\Osc07TriangleLevel, {|level| prOscillators[6].setTriangleLevel(level)},
			\Osc07SawtoothLevel, {|level| prOscillators[6].setSawtoothLevel(level)},
			\Osc07Frequency, {|freq| prOscillators[6].setFreqOscillator(freq)},

			\Osc08PulseLevel, {|level| prOscillators[7].setPulseLevel(level)},
			\Osc08PulseShape, {|shape| prOscillators[7].setPulseShape(shape)},
			\Osc08SineLevel, {|level| prOscillators[7].setSineLevel(level)},
			\Osc08SineSymmetry, {|symmetry| prOscillators[7].setPulseLevel(symmetry)},
			\Osc08TriangleLevel, {|level| prOscillators[7].setTriangleLevel(level)},
			\Osc08SawtoothLevel, {|level| prOscillators[7].setSawtoothLevel(level)},
			\Osc08Frequency, {|freq| prOscillators[7].setFreqOscillator(freq)},

			\Osc09PulseLevel, {|level| prOscillators[8].setPulseLevel(level)},
			\Osc09PulseShape, {|shape| prOscillators[8].setPulseShape(shape)},
			\Osc09SineLevel, {|level| prOscillators[8].setSineLevel(level)},
			\Osc09SineSymmetry, {|symmetry| prOscillators[8].setPulseLevel(symmetry)},
			\Osc09TriangleLevel, {|level| prOscillators[8].setTriangleLevel(level)},
			\Osc09SawtoothLevel, {|level| prOscillators[8].setSawtoothLevel(level)},
			\Osc09Frequency, {|freq| prOscillators[8].setFreqOscillator(freq)},

			\Osc10PulseLevel, {|level| prOscillators[9].setPulseLevel(level)},
			\Osc10PulseShape, {|shape| prOscillators[9].setPulseShape(shape)},
			\Osc10SineLevel, {|level| prOscillators[9].setSineLevel(level)},
			\Osc10SineSymmetry, {|symmetry| prOscillators[9].setPulseLevel(symmetry)},
			\Osc10TriangleLevel, {|level| prOscillators[9].setTriangleLevel(level)},
			\Osc10SawtoothLevel, {|level| prOscillators[9].setSawtoothLevel(level)},
			\Osc10Frequency, {|freq| prOscillators[9].setFreqOscillator(freq)},

			\Osc11PulseLevel, {|level| prOscillators[10].setPulseLevel(level)},
			\Osc11PulseShape, {|shape| prOscillators[10].setPulseShape(shape)},
			\Osc11SineLevel, {|level| prOscillators[10].setSineLevel(level)},
			\Osc11SineSymmetry, {|symmetry| prOscillators[10].setPulseLevel(symmetry)},
			\Osc11TriangleLevel, {|level| prOscillators[10].setTriangleLevel(level)},
			\Osc11SawtoothLevel, {|level| prOscillators[10].setSawtoothLevel(level)},
			\Osc11Frequency, {|freq| prOscillators[10].setFreqOscillator(freq)},

			\Osc12PulseLevel, {|level| prOscillators[11].setPulseLevel(level)},
			\Osc12PulseShape, {|shape| prOscillators[11].setPulseShape(shape)},
			\Osc12SineLevel, {|level| prOscillators[11].setSineLevel(level)},
			\Osc12SineSymmetry, {|symmetry| prOscillators[11].setPulseLevel(symmetry)},
			\Osc12TriangleLevel, {|level| prOscillators[11].setTriangleLevel(level)},
			\Osc12SawtoothLevel, {|level| prOscillators[11].setSawtoothLevel(level)},
			\Osc12Frequency, {|freq| prOscillators[11].setFreqOscillator(freq)},
		]);
	}


	// Métodos de instancia:
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
		// Aquí se arrancan todos los Synths de todos los módulos (el servidor debe están arrancado)
		prOscillators.do({|i| i.createSynth});
		//prOscillators[0].createSynth(server);
	}

	stop {
		prOscillators.do({|i| i.freeSynth});
	}


	// Setter de los diferentes parámetros de los módulos en formato OSC /////////////////////////

	setParameterOSC {|simbol, value|
		prParameterDictionary[simbol].value(value);
	}
}
