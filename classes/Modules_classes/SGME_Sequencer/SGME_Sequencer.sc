SGME_Sequencer : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de patchbay signals
	var <inBusClock;
	var <inFeedbackBusClock;
	var <inBusReset;
	var <inFeedbackBusReset;
	var <inBusForward;
	var <inFeedbackBusForward;
	var <inBusReverse;
	var <inFeedbackBusReverse;
	var <inBusStop;
	var <inFeedbackBusStop;
	var <outBusKey4; // común en patchbay voltage
	// Buses de entrada y salida de patchbay voltage
	var <inBusACE;
	var <inFeedbackBusACE;
	var <inBusBDF;
	var <inFeedbackBusBDF;
	var <inBusKey;
	var <inFeedbackBusKey;
	var <outBusLayer1VoltageA;
	var <outBusLayer1VoltageB;
	var <outBusLayer1Key;
	var <outBusLayer2VoltageA;
	var <outBusLayer2VoltageB;
	var <outBusLayer2Key;
	var <outBusLayer3VoltageA;
	var <outBusLayer3VoltageB;
	var <outBusLayer3Key;

	// Parámetros del Sequencer ///////////////////////////////////////

	// Sequencer Output Range (panel 4)
	// Layer 1
	var <rangeVoltageA; // 0 - 10
	var <rangeVoltageB; // 0 - 10
	var <rangeKey1; // -5 - +5
	// Layer 2
	var <rangeVoltageC; // 0 - 10
	var <rangeVoltageD; // 0 - 10
	var <rangeKey2; // -5 - +5
	// Layer 3
	var <rangeVoltageE; // 0 - 10
	var <rangeVoltageF; // 0 - 10
	var <rangeKey3; // -5 - +5

	// Sequencer Operational Control (panel 7)
	var <switchAkey1, switchB, switchCkey2, switchD, switchEkey3, switchF, switchKey4; // 1 == up, 0 == down.
	var buttonMasterReset, buttonRunForward, buttonRunReverse, buttonStop, buttonResetSequence, buttonStepForward, buttonStepReverse, buttonTest;

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
		SynthDef(\SGME_Sequencer, {
			arg inBusClock, inFeedbackBusClock, inBusReset, inFeedbackBusReset,	inBusForward, inFeedbackBusForward, inBusReverse, inFeedbackBusReverse, inBusStop, inFeedbackBusStop, outBusKey4, inBusACE, inFeedbackBusACE, inBusBDF, inFeedbackBusBDF, inBusKey, inFeedbackBusKey, outBusLayer1VoltageA, outBusLayer1VoltageB, outBusLayer1Key, outBusLayer2VoltageA, outBusLayer2VoltageB, outBusLayer2Key, outBusLayer3VoltageA, outBusLayer3VoltageB, outBusLayer3Key;


			Out.ar(0, PinkNoise.ar(0.1)); // bypass
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv;
		server = serv;

		inBusClock = Bus.audio(server);
		inFeedbackBusClock = Bus.audio(server);
		inBusReset = Bus.audio(server);
		inFeedbackBusReset = Bus.audio(server);
		inBusForward = Bus.audio(server);
		inFeedbackBusForward = Bus.audio(server);
		inBusReverse = Bus.audio(server);
		inFeedbackBusReverse = Bus.audio(server);
		inBusStop = Bus.audio(server);
		inFeedbackBusStop = Bus.audio(server);
		outBusKey4 = Bus.audio(server);
		inBusACE = Bus.audio(server);
		inFeedbackBusACE = Bus.audio(server);
		inBusBDF = Bus.audio(server);
		inFeedbackBusBDF = Bus.audio(server);
		inBusKey = Bus.audio(server);
		inFeedbackBusKey = Bus.audio(server);
		outBusLayer1VoltageA = Bus.audio(server);
		outBusLayer1VoltageB = Bus.audio(server);
		outBusLayer1Key = Bus.audio(server);
		outBusLayer2VoltageA = Bus.audio(server);
		outBusLayer2VoltageB = Bus.audio(server);
		outBusLayer2Key = Bus.audio(server);
		outBusLayer3VoltageA = Bus.audio(server);
		outBusLayer3VoltageB = Bus.audio(server);
		outBusLayer3Key = Bus.audio(server);

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
				\inBusClock, inBusClock,
				\inFeedbackBusClock, inFeedbackBusClock,
				\inBusReset, inBusReset,
				\inFeedbackBusReset,	inFeedbackBusReset,
				\inBusForward, inBusForward,
				\inFeedbackBusForward, inFeedbackBusForward,
				\inBusReverse, inBusReverse,
				\inFeedbackBusReverse, inFeedbackBusReverse,
				\inBusStop, inBusStop,
				\inFeedbackBusStop, inFeedbackBusStop,
				\outBusKey4, outBusKey4,
				\inBusACE, inBusACE,
				\inFeedbackBusACE, inFeedbackBusACE,
				\inBusBDF, inBusBDF,
				\inFeedbackBusBDF, inFeedbackBusBDF,
				\inBusKey, inBusKey,
				\inFeedbackBusKey, inFeedbackBusKey,
				\outBusLayer1VoltageA, outBusLayer1VoltageA,
				\outBusLayer1VoltageB, outBusLayer1VoltageB,
				\outBusLayer1Key, outBusLayer1Key,
				\outBusLayer2VoltageA, outBusLayer2VoltageA,
				\outBusLayer2VoltageB, outBusLayer2VoltageB,
				\outBusLayer2Key, outBusLayer2Key,
				\outBusLayer3VoltageA, outBusLayer3VoltageA,
				\outBusLayer3VoltageB, outBusLayer3VoltageB,
				\outBusLayer3Key, outBusLayer3Key,
			], server).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var inOutputTotal = inCount + outCount;
		if (inOutputTotal == 0, {
			synth.run(false);
		}, {
			synth.run(true);
		});
	}

}