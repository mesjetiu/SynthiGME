SGME_Keyboard : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de salida de audio
	var <outputBus;


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
		SynthDef(\SGME_Reverb, {
			arg outputBus, freq;
			var sigOut = SinOsc.ar(freq);
			Out.ar(outputBus, sigOut);
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv;
		server = serv;
		outputBus = Bus.audio(server);
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
			synth = Synth(\SGME_Reverb, [
				\outputBus, outputBus,
				\freq, rrand(300.0, 400.0);
			], server).register;
		});
	//	this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun { // Dejo esta función aunque no se va a usar. Por ahora no hay manera de saber que no hay output.
		/*var outputTotal = outCount;
		if (outputTotal == 0, {
			synth.run(false);
		}, {
			synth.run(true);
		});*/
	}

	// Conversores de unidades.

	/*convertMix {|m|
		^m.linlin(0, 10, 0, 1);
	}

	convertLevel {|m|
		^m.linlin(0, 10, 0, 1);
	}*/

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	/*setMix {|v|
		mix = v;
		synth.run(true);
		synth.set(\mix, this.convertMix(v));
		//	this.synthRun();
	}

	setLevel {|v|
		level = v;
		synth.run(true);
		synth.set(\level, this.convertLevel(v));
		//	this.synthRun();
	}*/
}