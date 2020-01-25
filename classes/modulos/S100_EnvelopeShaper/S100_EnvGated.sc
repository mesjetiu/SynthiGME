S100_EnvGated {

	var <synth;
	var server;
	var <group;
	classvar settings;


	var <running; // true o false: Si el sintetizador está activo o pausado
	var lag;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_envGated, {
			arg generalGate,
			signalTrigger,
			inFeedbackSignalTrigger,
			inputBus,
			inFeedbackBus,
			outputBus,
			delayTime,
			attackTime,
			decayTime,
			sustainLevel,
			releaseTime,
			envelopeLevel,
			signalLevel;

			var sig, env, gate;
			gate = In.ar(signalTrigger);
			gate = gate + InFeedback.ar(inFeedbackSignalTrigger);
			sig = In.ar(inputBus);
			sig = sig + InFeedback.ar(inFeedbackBus);


			env = Env(
				levels: [
					0,
					0,
					1,
					sustainLevel.linlin(0, 10, 0, 1),
					0,
				],
				times: [delayTime, attackTime, decayTime, releaseTime],
				releaseNode: 3,
			).ar(0, gate: gate * generalGate);

			env = env * envelopeLevel;

			// Se aplica la envolvente y el nivel (level) a la señal
			sig = sig * env * signalLevel;

			Out.ar(outputBus, sig);

		},
		).add;
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
	}

	createSynth {
		arg
		group,
		signalTrigger,
		inFeedbackSignalTrigger,
		inputBus,
		inFeedbackBus,
		outputBus,
		delayTime,
		attackTime,
		decayTime,
		sustainLevel,
		releaseTime,
		envelopeLevel,
		signalLevel;
		if(synth.isPlaying==false, {
			synth = Synth(\S100_envGated, [
				\generalGate, 1,
				\signalTrigger, signalTrigger,
				\inFeedbackSignalTrigger, inFeedbackSignalTrigger,
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\delayTime, delayTime,
				\attackTime, attackTime,
				\decayTime, decayTime,
				\sustainLevel, sustainLevel,
				\releaseTime, releaseTime,
				\envelopeLevel, envelopeLevel,
				\signalLevel, signalLevel,
			], group).register;
		});
		^synth;
		//	this.synthRun;
	}

	// Pausa o reanuda el Synth
	synthRun {|state|
		if(state==true, {synth.set(\generalGate, 1)}, {synth.set(\generalGate, 0)});
		synth.run(state);
		running = state;
	}

}