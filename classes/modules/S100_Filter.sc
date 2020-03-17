S100_Filter {
	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida
	var <inputBus;
	var <inFeedbackBus;
	var <outputBus; // Salida

	// Parámetro correspondiente a los mandos del Synthi (todos escalados entre 0 y 10)
	var <level = 0;

	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var <outVol = 1;
	var pauseRoutine; // Rutina de pausado del Synth
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = 0.01; //= S100_Settings.get[\ringLag];
		SynthDef(\S100_Filter, {
			arg inputBus,
			inFeedbackBus,
			outputBus,
			level,
			outVol;

			var sig;
			sig= In.ar(inputBus) + InFeedback.ar(inFeedbackBus);

			sig = SinOsc.ar; // Salida de prueba

			Out.ar(outputBus, sig);
		}, [nil, nil, nil, lag, lag]
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_Filter, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\level, this.convertLevel(level),
				\outVol, outVol,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
	//	this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = outVol * level;
		if (outputTotal == 0, {
			running = false;
			synth.run(false);
		}, {
			running = true;
			synth.run(true);
		});
	}

	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertLevel {|level|
		^level.linlin(0, 10, 0, settings[\ringLevelMax]);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////


	setLevel {|lev|
		if((lev>=0).and(lev<=10), {
			level = lev;
			this.synthRun();
			synth.set(\level, this.convertLevel(lev))
		}, {
			("S100_RingModulator/setLevel: " + lev + " no es un valor entre 0 y 1").postln});
	}
}
