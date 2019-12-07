S100_OutputChannel {

	var <server;
	var <inputBuses; // Array de 8 buses, entradas del amplificador.
	var <outputBuses; // Array de 8 buses, salidas del amplificador.
	var <outBusL; // Canal izquierdo de la salida stereo.
	var <outBusR; // Canal derecho de la salida stereo.
	// Parámetros correspondientes a los diales del Synthi.
	var <filter; // Filtro pasabajos y pasaaltos.
	var <pan; // Para salida stereo mezclando todos los canales (comprobar en el Synthi).
	var <on; // Activa y desactiva el canal.
	var <level; // Nivel de volumen de salida.


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_outputChannel, {

		}
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBuses = 8.collect({Bus.audio(server)});
		outputBuses = 8.collect({Bus.audio(server)});
		outBusL = Bus.audio(server);
		outBusR = Bus.audio(server);
	}



}