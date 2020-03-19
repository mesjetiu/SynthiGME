S100_Echo : S100_Connectable {

	// TODO: (Importante para eficiencia)
	// Hacer que Patchbay cambie un semáforo en cada módulo cada vez que esté conectado. De este modo el módulo podrá saber que tiene input o tiene output. En función de estos semáforos se podrá tomar decisiones como la de poner en pausa los synths. En el caso de este módulo (y algunos más), si no existe este sistema, no hay manera de saber cuándo ponerse en pausa.


	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de audio
	var <inputBus;
	var <inFeedbackBus;
	var <outputBus;

	// Knobs del módulo
	var <delay = 0;
	var <mix = 0;
	var <feedback = 0;
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
		lag = 0.01;
		SynthDef(\S100_Echo, {
			arg inputBus,
			inFeedbackBus,
			outputBus,
			delay,
			mix,
			feedback,
			level;

			var sigIn, sigOut;
			sigIn = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);

			sigOut = SwitchDelay.ar(sigIn, mix, 1, delay, feedback * 0.7) * level;

			Out.ar(outputBus, sigOut);
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			1.wait;
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_Echo, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\delay, this.convertDelay(delay),
				\mix, this.convertMix(mix),
				\feedback, this.convertFeedback(feedback),
				\level, this.convertLevel(level),
			], server).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun { // Dejo esta función aunque no se va a usar. Por ahora no hay manera de saber que no hay output.
		var outputTotal = level * inCount * outCount;
		if (outputTotal == 0, {
			running = false;
			synth.run(false);
		}, {
			running = true;
			synth.run(true);
		});
	}

	// Conversores de unidades.

	convertDelay {|d|
		^d.linexp(0, 10, 1/0.002, 20);
	}

	convertMix {|m|
		^m.linlin(0, 10, 0, 1);
	}

	convertFeedback {|f|
		^f.linlin(0, 10, 0, 1);
	}

	convertLevel {|m|
		^m.linlin(0, 10, 0, 2);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setDelay {|v|
		delay = v;
		synth.run(true);
		synth.set(\delay, this.convertDelay(v));
		this.synthRun();
	}

	setMix {|v|
		mix = v;
		synth.run(true);
		synth.set(\mix, this.convertMix(v));
		this.synthRun();
	}

	setFeedback {|v|
		feedback = v;
		synth.run(true);
		synth.set(\feedback, this.convertFeedback(v));
		this.synthRun();
	}

	setLevel {|v|
		level = v;
		synth.run(true);
		synth.set(\level, this.convertLevel(v));
		this.synthRun();
	}
}
