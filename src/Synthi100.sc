Synthi100 {
	var <server; // Servidor por defecto

	// Módulos que incluye:
	var oscillators;

	// Buses internos de entrada y salida:
	var <audioInBuses;
	var <audioOutBuses;

	// Buses externos de entrada y salida: (se unen uno a uno a los buses internos)
	var <externAudioInBuses;
	var <externAudioOutBuses;

	// Opciones:
	var <numAudioInBuses = 8;
	var <numAudioOutBuses = 8;

	// Métodos de clase:

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(S100_Oscillator);
	}

	*new { |server, audioInBuses, audioOutBuses|
		^super.new.init(server, audioInBuses, audioOutBuses);
	}

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
		oscillators = 12.collect({S100_Oscillator.new});
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
		oscillators.do({|i| i.createSynth});
		//oscillators[0].createSynth(server);
	}

}