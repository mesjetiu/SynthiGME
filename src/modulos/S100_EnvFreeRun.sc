S100_EnvFreeRun {

	var <synth;
	var server;
	var <group;


	// buses de entrada y salida
	var <inputBus;
	var <outputBus;

	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		(
			SynthDef(\S100_envFreeRun, {
				arg gate,
				inputBus,
				inFeedbackBus,
				outputBus = 0,
				delayTime=1,
				attackTime=1,
				decayTime=1,
				sustainLevel=1,
				releaseTime=1,
				envelopeLevel,
				signalLevel=1;

				var sig, env;
				sig = SinOsc.ar; // pruebas

				env = Env(
					levels: [
						0, // loopNode (ver Help de "Env")
						0,
						1,
						sustainLevel,
						0,
						0, // releaseNode (añadido con valor igual al inicial y con tiempo 0, para que funcione el loop)
					],
					times: [delayTime, attackTime, decayTime, releaseTime, 0],
					releaseNode: 4,
					loopNode: 0,
				).ar(0, gate);


				// Se aplica la envolvente y el nivel (level) a la señal
				sig = sig * env * signalLevel * gate; // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.

				Out.ar(outputBus, sig);

			}, [0.5]
			).add
		)
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local, grp;
		server = serv;
		inputBus = Bus.audio(server);
		outputBus = Bus.audio(server);
	}

	createSynth {
		arg
		group,
		gate,
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
				\gate, gate,
	/*			\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\delayTime, delayTime,
				\attackTime, attackTime,
				\decayTime, decayTime,
				\sustainLevel, sustainLevel,
				\releaseTime, releaseTime,
				\envelopeLevel, envelopeLevel,
				\signalLevel, signalLevel, */
			], group).register;
		});
		^synth;
		//	this.synthRun;
	}
}