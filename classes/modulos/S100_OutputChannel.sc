S100_OutputChannel {

	// Group y synths de la instancia
	var <group = nil;
	var <synth = nil;
	var <synthBypass = nil;

	var <server;
	var <inputBus; // Entrada del amplificador.
	var <inFeedbackBus; // Entrada de feedback: admite audio del ciclo anterior.
	var <outputBus; // Salida del amplificador.
	var <outBusL; // Canal izquierdo de la salida stereo.
	var <outBusR; // Canal derecho de la salida stereo.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <filter = 5; // Filtro pasabajos y pasaaltos.
	var <pan = 5; // Entre -1 y 1. Para salida stereo (comprobar en el Synthi).
	var <on = 0; // 1 o 0. Activa y desactiva el canal.
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
		lag = S100_Settings.get[\outLag];
		SynthDef(\S100_outputChannel, {
			arg inputBus,
			inFeedbackBus,
			outputBus,
			outBusL,
			outBusR,
			freqHP,
			freqLP,
			pan, // entre -1 y 1
			level, // entre 0 y 1
			on;

			var sigIn, sigInFeedback, sig, sigPannedR, sigPannedL;

			sigIn = In.ar(inputBus);
			sigIn = sigIn + InFeedback.ar(inFeedbackBus);

			// Se realiza el filtrado
			sig = HPF.ar(sigIn, freqHP);
			sig = LPF.ar(sig, freqLP);

			// Se aplica el nivel (level)
			sig = sig * level;

			// Se aplica el paneo
			#sigPannedL, sigPannedR = Pan2.ar(sig, pan) * on;

			//Out.ar(outputBus, sigIn); // señal que pasa por el canal sin ser procesada (incluso si está en modo off), a modo de bus
			Out.ar(outBusL, sigPannedL);
			Out.ar(outBusR, sigPannedR);
		}, [nil, nil, nil, nil, nil, lag, lag, lag, lag, lag]
		).add;

		SynthDef(\S100_outputChannelBypass, {
			arg inputBus,
			inFeedbackBus,
			outputBus;

			var sig = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);

			Out.ar(outputBus, sig);
		}
		).add;
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		outputBus = Bus.audio(server);
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		outBusL = Bus.audio(server);
		outBusR = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		Routine({
			var waitTime = 0.001;
			// se crea el grupo
			group = Group(server).register;
			while({group.isPlaying == false}, {wait(waitTime)});
			// se crea el synth bypass (hace del canal un bus aunque esté apagado)
			synthBypass = Synth(\S100_outputChannelBypass, [
					\inputBus, inputBus,
					\inFeedbackBus, inFeedbackBus,
					\outputBus, outputBus,
			], group).register;
			while({synthBypass.isPlaying == false}, {wait(waitTime)});
			// se crea el synth principal
			if(synth.isPlaying==false, {
				synth = Synth(\S100_outputChannel, [
					\inputBus, inputBus,
					\inFeedbackBus, inFeedbackBus,
					\outputBus, outputBus,
					\outBusL, outBusL,
					\outBusR, outBusR,
					\freqHP, this.convertFilter(filter)[0],
					\freqLP, this.convertFilter(filter)[1],
					\pan, this.convertPan(pan),
					\level, this.convertLevel(level),
					\on, on,
				], group).register; //".register" registra el Synth para poder testear ".isPlaying"
			});
			while({synth.isPlaying == false}, {wait(waitTime)});
			this.synthRun;
		}).play;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = level * on;
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
		^level.linlin(0, 10, 0, settings[\outLevelMax]);
	}

	convertFilter {|filter| // Retorna las frecuencias de corte de ambos filtros: pasabajos y pasaaltos
		var filterHP, filterLP;
		filterHP = filter.linexp(
			inMin: 5, // valor mínimo del dial
			inMax: 10, // valor máximo del dial
			outMin: settings[\outHPFreqMin], // frecuencia mínima (valor del dial: 5 o menos)
			outMax: settings[\outHPFreqMax] //frecuencia máxima (valor del dial: 10)
		);
		filterLP = filter.linexp(
			inMin: 0, // valor mínimo del dial
			inMax: 5, // valor máximo del dial
			outMin: settings[\outLPFreqMin], // frecuencia mínima (valor del dial: 1)
			outMax: settings[\outLPFreqMax] //frecuencia máxima (valor del dial: 5 o más)
		);
		^[filterHP, filterLP];
	}

	convertPan {|p|
		^p.linlin(0, 10, -1, 1);
	}

	// Setters de los parámetros
	setLevel {|lev|
		if((lev>=0).and(lev<=10), {
			level = lev;
			this.synthRun();
			synth.set(\level, this.convertLevel(lev))
		}, {
			("S100_OutputChannel/setLevel: " + lev + " no es un valor entre 0 y 1").postln});
	}

	setFilter {|filt|
		if((filt>=0).and(filt<=10), {
			var freqHP, freqLP;
			#freqHP,freqLP = this.convertFilter(filt);
			filter = filt;
			synth.set(\freqHP, freqHP);
			synth.set(\freqLP, freqLP);
		}, {
			("S100_OutputChannel/setFilter: " + filt + " no es un valor entre 0 y 1").postln});
	}

	setOn {|value|
		if((value == 0).or(value == 1), {
			on = value;
			synth.set(\on, on);
			this.synthRun();
		}, {
			("S100_OutputChannel/setOn: " + value + " no es un valor de 0 o 1").postln});
	}

	setPan {|p|
		if((p>=0).and(p<=10), {
			pan = p;
			this.synthRun();
			synth.set(\pan, this.convertPan(p))
		}, {
			("S100_OutputChannel/setPan: " + p + " no es un valor entre -1 y 1").postln});
	}

}