S100_NoiseGenerator {

	// Synth de la instancia
	var <synth = nil;

	var <server;
	var <outputBus; // Salida.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <colour = 5; // Filtro pasabajos y pasaaltos.
	var <level = 0; // Entre 0 y 1. Nivel de volumen de salida.


	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = S100_Settings.get[\noiseLag];
		SynthDef(\S100_noiseGenerator, {
			arg outputBus,
			freqHP,
			freqLP,
			level; // entre 0 y 1

			var sig;

			sig = WhiteNoise.ar;

			// Se realiza el filtrado
			sig = HPF.ar(sig, freqHP);
			sig = LPF.ar(sig, freqLP);

			// Se aplica el nivel (level)
			sig = sig * level;

			Out.ar(outputBus, sig);

		}, [nil, lag, lag, lag]
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_noiseGenerator, [
				\outputBus, outputBus,
				\freqHP, this.convertColour(colour)[0],
				\freqLP, this.convertColour(colour)[1],
				\level, this.convertLevel(level),
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = level;
		if (outputTotal==0, {
			running = false;
			pauseRoutine.reset;
			pauseRoutine.play;
		}, {
			pauseRoutine.stop;
			running = true;
			synth.run(true);
		});
	}

	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertLevel {|level|
		^level.linlin(0, 10, 0, settings[\noiseLevelMax]);
	}

	convertColour {|col| // Retorna las frecuencias de corte de ambos filtros: pasabajos y pasaaltos
		var filterHP, filterLP;
		filterHP = col.linexp(
			inMin: 5, // valor mínimo del dial
			inMax: 10, // valor máximo del dial
			outMin: settings[\noiseHPFreqMin], // frecuencia mínima (valor del dial: 5 o menos)
			outMax: settings[\noiseHPFreqMax] //frecuencia máxima (valor del dial: 10)
		);
		filterLP = col.linexp(
			inMin: 0, // valor mínimo del dial
			inMax: 5, // valor máximo del dial
			outMin: settings[\noiseLPFreqMin], // frecuencia mínima (valor del dial: 1)
			outMax: settings[\noiseLPFreqMax] //frecuencia máxima (valor del dial: 5 o más)
		);
		^[filterHP, filterLP];
	}


	// Setters de los parámetros
	setLevel {|lev|
		if((lev>=0).and(lev<=10), {
			level = lev;
			this.synthRun();
			synth.set(\level, this.convertLevel(lev))
		}, {
			("S100_NoiseGenerator/setLevel: " + lev + " no es un valor entre 0 y 1").postln});
	}

	setColour {|col|
		if((col>=0).and(col<=10), {
			var freqHP, freqLP;
			#freqHP,freqLP = this.convertColour(col);
			colour = col;
			synth.set(\freqHP, freqHP);
			synth.set(\freqLP, freqLP);
		}, {
			("S100_NoiseGenerator/setColour: " + col + " no es un valor entre 0 y 1").postln});
	}
}