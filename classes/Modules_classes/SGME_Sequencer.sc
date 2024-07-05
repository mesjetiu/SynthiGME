SGME_Sequencer : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de patchbay signals
	var <inBusclock;
	var <inFeedBackBusClock;
	var <inBusReset;
	var <inFeedBackBusReset;
	var <inBusForward;
	var <inFeedBackBusForward;
	var <inBusReverse;
	var <inFeedBackBusReverse;
	var <inBusStop;
	var <inFeedBackBusStop;
	var <outBusKey4; // común en patchbay voltage
	// Buses de entrada y salida de patchbay voltage
	var <inBusACE;
	var <inFeedBackBusACE;
	var <inBusBDF;
	var <inFeedBackBusBDF;
	var <inBusKey;
	var <inFeedBackBusKey;
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
			arg outBus, inBus, inFeedbackBus,
			gain = 0, offset = 0;
			var sig;
			inBus = In.ar(inBus);
			inFeedbackBus = InFeedback.ar(inFeedbackBus);
			sig = inBus + inFeedbackBus;
			sig = (sig * gain) + offset;

			Out.ar(outBus, sig);
			//Out.ar(outBus, PinkNoise.ar(0.1)); // bypass
		},[nil, nil, nil, 0.2, 0.2]).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv;
		server = serv;
	//	outBus = Bus.audio(server);
	//	inBus = Bus.audio(server);
	//	inFeedbackBus = Bus.audio(server);
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
			//	\outBus, outBus,
			//	\inBus, inBus,
			//	\inFeedbackBus, inFeedbackBus,
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