S100_OutputChannel {
	// Synth de la instancia
	var channelSynth = nil;

	var <server;
	var <inputBus; // Array de 8 buses, entradas del amplificador.
	var <outputBus; // Array de 8 buses, salidas del amplificador.
	var <outBusL; // Canal izquierdo de la salida stereo.
	var <outBusR; // Canal derecho de la salida stereo.
	// Parámetros correspondientes a los diales del Synthi.
	var <filter; // Filtro pasabajos y pasaaltos.
	var <pan; // Entre -1 y 1. Para salida stereo (comprobar en el Synthi).
	var <on; // true o false. Activa y desactiva el canal.
	var <level; // Entre 0 y 1. Nivel de volumen de salida.


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
			pan,
			level;

			var sigIn = In.ar(inputBus);
			var sig = sigIn * level;
			var sigPanned = Pan2.ar(sig, pan);

			Out.ar(outputBus, sig);
			Out.ar(outBusL, sigPanned[0]);
			Out.ar(outBusR, sigPanned[1]);
		}
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		outputBus = Bus.audio(server);
		outBusL = Bus.audio(server);
		outBusR = Bus.audio(server);
	}



}