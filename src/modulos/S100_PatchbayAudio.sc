S100_PatchbayAudio {

	var <server;

	// Array con todas las conexiones. Dos dimensiones [from] [to]. Almacena el Synth Node.
	var <nodesTable;

	// Módulos del Synthi 100
	var <modulOscillators;
	var <modulOutputChannels;



	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server, oscillators, outputChannels|
		^super.new.init(server, oscillators, outputChannels);
	}

	*addSynthDef {
		SynthDef(\S100_patchNode, {
			arg fromBus,
			toBus,
			ganancy; // Entre 0 y 1;

			var sig = In.ar(fromBus) * ganancy;

			Out.ar(toBus, sig);
			//Out.ar(0, sig);
		}
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local, oscills, outpts;
		server = serv;
		modulOscillators = oscills;
		modulOutputChannels = outpts;
		nodesTable = Array.fill2D(60, 60, {nil});
	}

	// Método provisional para conectar módulos
	connect {|fromModul, fromBus, toBus, ganancy|
		// No se guardan los Synths en ningun array. Es solo de prueba...
		Synth(\S100_patchNode, [\fromBus, fromBus, \toBus, toBus, \ganancy, ganancy], fromModul.synth, \addAfter);
	}


}