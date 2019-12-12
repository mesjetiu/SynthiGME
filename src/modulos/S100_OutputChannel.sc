S100_OutputChannel {

	// Variables de la clase
	classvar lag = 0.5; // Tiempo que dura la transición en los cambios de parámetros en el Synth

	// Synth de la instancia
	var <synth = nil;

	var <server;
	var <inputBus; // Entrada del amplificador.
	var <outputBus; // Salida del amplificador.
	var <inFeedbackBus; //
	var <outBusL; // Canal izquierdo de la salida stereo.
	var <outBusR; // Canal derecho de la salida stereo.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <filter = 0; // Filtro pasabajos y pasaaltos.
	var <pan = 0; // Entre -1 y 1. Para salida stereo (comprobar en el Synthi).
	var <on = true; // true o false. Activa y desactiva el canal.
	var <level = 5; // Entre 0 y 1. Nivel de volumen de salida.


	// Otros atributos de instancia
	var <outVol = 1;
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_outputChannel, {
			arg inputBus,
			outputBus,
			outBusL,
			outBusR,
			filter,
			pan, // entre -1 y 1
			level; // entre 0 y 1
			var sigIn, sig, sigPanned;

			sigIn = In.ar(inputBus);
			// Realizar aquí el filtrado
			// ................


			sig = sigIn * level;
			sigPanned = Pan2.ar(sig, pan);

			Out.ar(outputBus, sig);
			Out.ar(outBusL, sigPanned[0]);
			Out.ar(outBusR, sigPanned[1]);
		}, [nil, nil, nil, nil, lag, lag, lag]
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
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
				\filter, filter,
				\pan, pan,
				\level, this.convertLevel(level),
				\outVol, 1,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Libera el Synth del servidor
	freeSynth {
		if(synth.isPlaying, {
			synth.free;
		});
		synth = nil;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = level * outVol;
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
		^(level/10);
	}

	// Setters de los parámetros
	setLevel {|lev|
		if((lev>=0).and(lev<=10), {
			level = lev;
			this.synthRun();
			synth.set(\level, this.convertLevel(lev))}, {
			("S100_OutputChannel/setLevel: " + lev + " no es un valor entre 0 y 1").postln});
	}

}