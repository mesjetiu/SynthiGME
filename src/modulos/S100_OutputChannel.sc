S100_OutputChannel {

	// Variables de la clase
	classvar lag = 0.5; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar outVol = 1; // Entre 0 y 1

	// Synth de la instancia
	var <synth = nil;

	var <server;
	var <inputBus; // Entrada del amplificador.
	var <outputBus; // Salida del amplificador.
	var <inFeedbackBus; // Entrada de feedback: admite audio del ciclo anterior.
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


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server, inBus|
		^super.new.init(server, inBus);
	}

	*addSynthDef {
		SynthDef(\S100_outputChannel, {
			arg inputBus,
			outputBus,
			outBusL,
			outBusR,
			freqHP,
			freqLP,
			pan, // entre -1 y 1
			level, // entre 0 y 1
			outVol; // entre 0 y 1

			var sigIn, sig, sigPannedR, sigPannedL;

			sigIn = In.ar(inputBus);

			// Se realiza el filtrado
			sig = HPF.ar(sigIn, freqHP, 0.5);
			sig = LPF.ar(sig, freqLP, 0.5);

			// Se aplica el nivel (level)
			sig = sig * level * outVol;

			// Se aplica el paneo
			#sigPannedL,sigPannedR = Pan2.ar(sig, pan);

			Out.ar(outputBus, sig);
			Out.ar(outBusL, sigPannedL);
			Out.ar(outBusR, sigPannedR);
		}, [nil, nil, nil, nil, lag, lag, lag, lag, nil]
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local, inBus;
		server = serv;
		inputBus = inBus;
		outputBus = Bus.audio(server);
		outBusL = Bus.audio(server);
		outBusR = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_outputChannel, [
				\inputBus, inputBus,
				\outputBus, outputBus,
				\outBusL, outBusL,
				\outBusR, outBusR,
				\freqHP, this.convertFilter(filter)[0],
				\freqLP, this.convertFilter(filter)[1],
				\pan, this.convertPan(pan),
				\level, this.convertLevel(level),
				\outVol, 1,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
	//	this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = on * level * outVol;
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
		^level.linlin(0, 10, 0, 1);
	}

	convertFilter {|filter| // Retorna las frecuencias de corte de ambos filtros: pasabajos y pasaaltos
		var filterHP, filterLP;
		filterHP = filter.linexp(
			inMin: 5, // valor mínimo del dial
			inMax: 10, // valor máximo del dial
			outMin: 10, // frecuencia mínima (valor del dial: 1)
			outMax: 4000 //frecuencia máxima (valor del dial: 10)
		);
		filterLP = filter.linexp(
			inMin: 0, // valor mínimo del dial
			inMax: 5, // valor máximo del dial
			outMin: 200, // frecuencia mínima (valor del dial: 1)
			outMax: 20000 //frecuencia máxima (valor del dial: 10)
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