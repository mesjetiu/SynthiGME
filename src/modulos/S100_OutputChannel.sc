S100_OutputChannel {

	// Variables de la clase
	classvar lag = 0.5; // Tiempo que dura la transición en los cambios de parámetros en el Synth

	// Synth de la instancia
	var channelSynth = nil;

	var <server;
	var <inputBus; // Array de 8 buses, entradas del amplificador.
	var <outputBus; // Array de 8 buses, salidas del amplificador.
	var <outBusL; // Canal izquierdo de la salida stereo.
	var <outBusR; // Canal derecho de la salida stereo.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <filter = 0; // Filtro pasabajos y pasaaltos.
	var <pan = 0; // Entre -1 y 1. Para salida stereo (comprobar en el Synthi).
	var <on = true; // true o false. Activa y desactiva el canal.
	var <level = 0; // Entre 0 y 1. Nivel de volumen de salida.


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
			pan,
			level;
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
			channelSynth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(channelSynth.isPlaying==false, {
			channelSynth = Synth(\S100_outputChannel, [
				\inputBus, inputBus,
				\outputBus, outputBus,
				\outBusL, outBusL,
				\outBusR, outBusR,
				\filter, filter,
				\pan, pan,
				\level, level,
				\outVol, 1,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Libera el Synth del servidor
	freeSynth {
		if(channelSynth.isPlaying, {
			channelSynth.free;
		});
		channelSynth = nil;
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
			channelSynth.run(true);
		});
	}

}