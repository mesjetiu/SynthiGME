S100_PatchbayAudio {

	var <server;
	var <oscillators;
	var <outputs; // 8 outputs

	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server, oscillators, outputs|
		^super.new.init(server, oscillators, outputs);
	}

	*addSynthDef {
		SynthDef(\S100_pachNode, {
		}
		).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local, oscills, outpts;
		server = serv;
		oscillators = oscills;
		outputs = outpts;
	}


}