S100_EnvFreeRun {

	var <synth;
	var server;
	var <group;
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
			SynthDef(\S100_envFreeRun, {
				arg gate = 1,
				inputBus,
				inFeedbackBus,
				outputBus,
				delayTime,
				attackTime,
				decayTime,
				sustainLevel,
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
						0, // releaseNode (añadido con valor igual al inicial y con tiempo 0, para que funcione el loop)
					],
					times: [delayTime, attackTime, decayTime, 0, 0],
					releaseNode: 4,
					loopNode: 0,
				).ar(0, gate);

				env = env * envelopeLevel;


				// Se aplica la envolvente y el nivel (level) a la señal
				sig = sig * env * signalLevel * gate; // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.

				Out.ar(outputBus, sig);

			}, [0.1]
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
		envelopeLevel,
		signalLevel;
		if(synth.isPlaying==false, {
			synth = Synth(\S100_envFreeRun, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\delayTime, delayTime,
				\attackTime, attackTime,
				\decayTime, decayTime,
				\sustainLevel, sustainLevel,
				\envelopeLevel, envelopeLevel,
				\signalLevel, signalLevel,
			], group).register;
		});
		^synth;
		//	this.synthRun;
	}
}