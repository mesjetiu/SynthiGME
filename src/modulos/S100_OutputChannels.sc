S100_OutputChannels {

	var <server;
	var <inputBuses; // Array de 8 buses, entradas del amplificador.
	var <outputBuses; // Array de 8 buses, salidas del amplificador.
	var <outBusL; // Canal izquierdo de la salida stereo
	var <outBusR; // Canal derecho de la salida stereo


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		^super.new.init(server);
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