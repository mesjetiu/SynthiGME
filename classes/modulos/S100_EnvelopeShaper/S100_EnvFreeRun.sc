S100_EnvFreeRun {

	var <synth;
	var server;
	var <group;
	classvar settings;


	var <running; // true o false: Si el sintetizador está activo o pausado


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_envFreeRun, {
			arg generalGate,
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

			var sig, env;
			//sig = SinOsc.ar; // pruebas
			sig = In.ar(inputBus);
			sig = sig + InFeedback.ar(inFeedbackBus);

			env = Env(
				levels: [
					0, // loopNode (ver Help de "Env")
					0,
					1,
					sustainLevel,
					0,
					0,
				],
				times: [delayTime, attackTime, decayTime, releaseTime, 0],
				releaseNode: 4,
				loopNode: 0,
			).ar(0, gate: generalGate);

			env = env * envelopeLevel;


			// Se aplica la envolvente y el nivel (level) a la señal
			sig = sig * env * signalLevel; // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.

			Out.ar(outputBus, sig);

		},
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
	}

	createSynth {
		arg
		group,
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
			synth = Synth(\S100_envFreeRun, [
				\generalGate, 1,
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
	}

	// Pausa o reanuda el Synth
	synthRun {|state|
		if(state==true, {synth.set(\generalGate, 1)}, {synth.set(\generalGate, 0)});
		synth.run(state);
		running = state;
	}
}