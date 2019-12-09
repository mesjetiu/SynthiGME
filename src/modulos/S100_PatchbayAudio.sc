S100_PatchbayAudio {

	var <server;

	// Array que almacena todas las conexiones. Dos dimensiones [from] [to]. Almacena el Synth Node.
	var <nodeSynths;
	// Array con las funciones para crear cada nodo.
	var nodeFunctions;

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
		nodeSynths = Array.fill2D(60, 60, {nil});
		nodeFunctions = Array.fill2D(60, 60, {nil});
	}

	// Crea nodo de conexión entre dos módulos
	administrateNode {
		arg fromModul = nil,
		fromBus = nil,
		toBus = nil,
		coordenate,
		ganancy = 1;

		if(ganancy > 0, {
			if(nodeSynths[coordenate[0]][coordenate[1]] == nil, {
				nodeSynths[coordenate[0]][coordenate[1]] = Synth(
					\S100_patchNode, [
						\fromBus, fromBus,
						\toBus, toBus,
						\ganancy, ganancy
					],
					fromModul.synth,
					\addAfter);
			}, {
				nodeSynths[coordenate[0]][coordenate[1]].set(\ganancy, ganancy);
			})
		},{
			if(nodeSynths[coordenate[0]][coordenate[1]] != nil, {
				nodeSynths[coordenate[0]][coordenate[1]].free;
				nodeSynths[coordenate[0]][coordenate[1]] = nil;
			})
		})
	}



}