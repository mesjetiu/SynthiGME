SGME_Invertor : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de audio
	var <inBus;
	var <inFeedbackBus;
	var <outBus;

	// Parámetros del Invertor
	var <gain = 0; // -5 - 5
	var <offset = 0; // -5 - 5

	// Otros atributos de instancia
	var pauseRoutine; // Rutina de pausado del Synth
	var resumeRoutine;

	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = SGME_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\SGME_Keyboard, {
			arg outBus, inBus, inFeedbackBus,
			gain, offset;
			var sig;
			inBus = In.ar(inBus);
			inFeedbackBus = InFeedback.ar(inBus);
			sig = inBus + inFeedbackBus;

			//Out.ar(outBus, sig);
			Out.ar(0, PinkNoise.ar(0.1!2)); // bypass
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv;
		server = serv;
		outBus = Bus.audio(server);
		inBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		pauseRoutine = Routine({
			//running = false;
			1.wait;
			synth.run(false);
			//	1.wait;
		});
		resumeRoutine = Routine({
			//running = true;
			//	1.wait;
			synth.run(true);
			//	1.wait;
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_Invertor, [
				\outBus, outBus,
				\inBus, inBus,
				\inFeedbackBus, inFeedbackBus,
			], server).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = outCount;
		if (outputTotal == 0, {
		synth.run(false);
		}, {
		synth.run(true);
		});
	}


	// Setters de los parámetros con sus efectos en el synth. Estos métodos se usan internamente desde otro método "inteligente" que lleva recuento de teclas pulsadas, etc.
	///////////////////////////////////////////////////////////////////////

	gain_ {|g|
		gain = g;
		g = linlin(-5, 5, -1, 1); // gain no es otra cosa que un factor de multiplicación por 1 y -1 en sus valores extremos.
		synth.set(\gain, g);
	}

	offset_ {|o|
		offset = o;
		o = linlin(-5, 5, -2, 2); // los valores de -2 y 2 son arbitrarios. Buscar unos valores más adecuados a la realidad.
		synth.set(\pitch, o);
	}
}