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
	var prModuleDictionary;

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
		prOscillators = 12.collect({S100_Oscillator.new});
		// Diccionario de módulos
		prModuleDictionary = Dictionary.newFrom(List[
			\Osc01, prOscillators[0],
			\Osc02, prOscillators[1],
			\Osc03, prOscillators[2],
			\Osc04, prOscillators[3],
			\Osc05, prOscillators[4],
			\Osc06, prOscillators[5],
			\Osc07, prOscillators[6],
			\Osc08, prOscillators[7],
			\Osc09, prOscillators[8],
			\Osc10, prOscillators[9],
			\Osc11, prOscillators[10],
			\Osc12, prOscillators[11],
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


	setOscillatorPulseLevel {|module, level |
		prModuleDictionary[module].setPulseLevel(level);
	}

	/// HACER EL RESTO DE MÉTODOS DEL MISMO MODO QUE EL ANTERIOR.
/*
	setPulseShape {| shape |
		if((shape>=0).and( {shape<=1}), {
			pulseShape = shape;
			oscillator.set(\pulseShape, shape)}, {
			("S100_Oscillator/setPulseShape: " + shape + " no es un valor entre 0 y 1").postln});
	}

	setSineLevel {| level |
		if((level>=0).and( {level<=1}), {
			sineLevel = level;
			oscillator.set(\sineLevel, level)}, {
			("S100_Oscillator/setSineLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSineSymmetry {| symmetry |
		if((symmetry>=-1).and( {symmetry<=1}), {
			sineSymmetry = symmetry;
			oscillator.set(\sineSymmetry, symmetry)}, {
			("S100_Oscillator/setSineSymmetry: " + symmetry + " no es un valor entre -1 y 1").postln});
	}

	setTriangleLevel {| level |
		if((level>=0).and( {level<=1}), {
			pulseLevel = level;
			oscillator.set(\triangleLevel, level)}, {
			("S100_Oscillator/setTriangleLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setSawtoothLevel {| level |
		if((level>=0).and( {level<=1}), {
			sawtoothLevel = level;
			oscillator.set(\sawtoothLevel, level)}, {
			("S100_Oscillator/setSawtoothLevel: " + level + " no es un valor entre 0 y 1").postln});
	}

	setFreqOscilator {| freq |
		if((freq>=0).and( {freq<=10000}), {
			freqOscillator = freq;
			oscillator.set(\frequency, freq)}, {
			("S100_Oscillator/setFreqOscillator: " + freq + " no es un valor entre 0 y 1").postln});
	}
*/

}