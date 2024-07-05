SGME_Sequencer_Layer : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;


	// Buses de entrada y salida de patchbay voltage
	var <inBusVoltage1;
	var <inFeedBackBusVoltage1;
	var <inBusKey;
	var <inFeedBackBusKey;
	var <outBusVoltage1;
	var <outBusVoltage2;
	var <outBusKey;

	// Parámetros del Sequencer ///////////////////////////////////////

	// Sequencer Output Range (panel 4)
	var <rangeVoltage1; // 0 - 10
	var <rangeVoltage2; // 0 - 10
	var <rangeKey; // -5 - +5

	// Sequencer Operational Control (panel 7)
	var <switchVol1key, switchVol2; // 1 == up, 0 == down.

	// Otros atributos de instancia
	var pauseRoutine; // Rutina de pausado del Synth
	var resumeRoutine;

	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = SGME_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\SGME_Sequencer_Layer, {
			arg inBusVoltage1, inFeedBackBusVoltage1, inBusKey, inFeedBackBusKey, outBusVoltage1, outBusVoltage2, outBusKey;


			//Out.ar(outBus, PinkNoise.ar(0.1)); // bypass
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv;
		server = serv;

		inBusVoltage1 = Bus.audio(server);
		inFeedBackBusVoltage1 = Bus.audio(server);
		inBusKey = Bus.audio(server);
		inFeedBackBusKey = Bus.audio(server);
		outBusVoltage1 = Bus.audio(server);
		outBusVoltage2 = Bus.audio(server);
		outBusKey = Bus.audio(server);

		pauseRoutine = Routine({
			//running = false;
			1.wait;
			synth.run(false);
			//	1.wait;
		});
		resumeRoutine = Routine({
			//running = true;
			//	1.wait;
			synth.run(true);
			//	1.wait;
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_Sequencer, [
				\inBusVoltage1, inBusVoltage1,
				\inFeedBackBusVoltage1, inFeedBackBusVoltage1,
				\inBusKey, inBusKey,
				\inFeedBackBusKey, inFeedBackBusKey,
				\outBusVoltage1, outBusVoltage1,
				\outBusVoltage2, outBusVoltage2,
				\outBusKey, outBusKey;
			], server).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = outCount;
		if (outputTotal == 0, {
			synth.run(false);
		}, {
			synth.run(true);
		});
	}

}