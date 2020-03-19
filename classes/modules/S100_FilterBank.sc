S100_FilterBank : S100_Connectable {

	// Synth de la instancia
	var <group = nil;
	var <synths = nil; // Array que contendrá los 8 synths
	var <server;

	// Buses de entrada y salida
	var <inputBus;
	var <inFeedbackBus;
	var <outputBus; // Salida

	// Parámetro correspondiente a los mandos del Synthi (todos escalados entre 0 y 10)
	var <bands; // Array con el valor de cada banda (0-10)

	// Otros atributos de instancia
	var <outVol = 2;
	var pauseRoutine; // Array de rutinas de pausado de los Synths
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = 0.2;
		SynthDef(\S100_FilterBand, {
			arg inputBus,
			inFeedbackBus,
			outputBus,
			freq, // la frecuencia es fija
			level,
			outVol;

			var sigIn, sigOut;
			sigIn = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);

			sigOut = BBandPass.ar(sigIn, freq) * level;

			Out.ar(outputBus, sigOut);
		}, [nil, nil, nil, nil, lag, lag]
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		bands = Array.fill(8, {0});
		server = serv;
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = 8.do({|i|
			Routine({
				1.wait;
				synths[i].run(false);
			});
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		Routine({
			var waitTime = 0.001;
			var synth;
			// se crea el grupo
			group = Group(server).register;
			while({group.isPlaying == false}, {wait(waitTime)});
			// se crea el synth de FREE RUN
			synths = bands.collect({|band, i|
				var synth = Synth(\S100_FilterBand, [
					\inputBus, inputBus,
					\inFeedbackBus, inFeedbackBus,
					\outputBus, outputBus,
					\freq, 62.5*(2**(i)),
					\level, this.convertLevel(band),
					\outVol, outVol,
				], group).register;
				while({synth.isPlaying == false}, {wait(waitTime)});
				synth;
			});
			8.do({|i|
				this.synthRun(i);
			})
		}).play
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {|i|
		var outputTotal = outVol * bands[i];
		if (outputTotal == 0, {
			synths[i].run(false);
		}, {
			synths[i].run(true);
		});
	}

	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.


	convertLevel {|l|
		^l.linlin(0, 10, 0, 1);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setBand63 {|l|
		bands[0] = l;
		this.synthRun(0);
		synths[0].set(\level, this.convertLevel(l))
	}

	setBand125 {|l|
		bands[1] = l;
		this.synthRun(1);
		synths[1].set(\level, this.convertLevel(l))
	}

	setBand250 {|l|
		bands[2] = l;
		this.synthRun(2);
		synths[2].set(\level, this.convertLevel(l))
	}

	setBand500 {|l|
		bands[3] = l;
		this.synthRun(3);
		synths[3].set(\level, this.convertLevel(l))
	}

	setBand1000 {|l|
		bands[4] = l;
		this.synthRun(4);
		synths[4].set(\level, this.convertLevel(l))
	}

	setBand2000 {|l|
		bands[5] = l;
		this.synthRun(5);
		synths[5].set(\level, this.convertLevel(l))
	}

	setBand4000 {|l|
		bands[6] = l;
		this.synthRun(6);
		synths[6].set(\level, this.convertLevel(l))
	}

	setBand8000 {|l|
		bands[7] = l;
		this.synthRun(7);
		synths[7].set(\level, this.convertLevel(l))
	}
}
