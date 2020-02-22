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
			outputBusVol,
			delayTime,
			attackTime,
			decayTime,
			sustainLevel,
			releaseTime,
			envelopeLevel,
			signalLevel;

			var sig, vol, env;
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


			// Se aplica la envolvente a la señal
			sig = sig * env * signalLevel; // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.


			// Se aplica la envolvente al voltage
			vol = env * envelopeLevel.lag(0.2); // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.

			Out.ar(outputBus, sig);
			Out.ar(outputBusVol, vol);

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
		outputBusVol,
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
				outputBusVol: outputBusVol,
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