/*
This file is part of SynthiGME.

SynthiGME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SynthiGME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SynthiGME.  If not, see <https://www.gnu.org/licenses/>.

Copyright 2020 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_EnvTriggered {

	var <synth;
	var server;
	var <group;
	classvar settings;


	var <running; // true o false: Si el sintetizador está activo o pausado
	var lag;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = SGME_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\SGME_envTriggered, {
			arg generalGate,
			signalTrigger,
			inFeedbackSignalTrigger,
			inputBus,
			inFeedbackBus,
			outputBus,
			outputBusVol,
			inDelayVol,
			inFeedbackDelayVol,
			inAttackVol,
			inFeedbackAttackVol,
			inDecayVol,
			inFeedbackDecayVol,
			inSustainVol,
			inFeedbackSustainVol,
			inReleaseVol,
			inFeedbackReleaseVol,
			delayTime,
			attackTime,
			decayTime,
			sustainLevel,
			releaseTime,
			envelopeLevel,
			signalLevel;

			var sig, vol, env, gate;
			var inDelay, inAttack, inDecay, inSustain, inRelease;

			// Suma de Delay
			inDelay = In.ar(inDelayVol) + InFeedback.ar(inFeedbackDelayVol);
			delayTime = inDelay.linlin(-1, 1, -5, 5) + delayTime;
			delayTime = delayTime.clip(0.002, 20);
			delayTime = A2K.kr(delayTime);

			// Suma de Attack
			inAttack = In.ar(inAttackVol) + InFeedback.ar(inFeedbackAttackVol);
			attackTime = inAttack.linlin(-1, 1, -5, 5) + attackTime;
			attackTime = attackTime.clip(0.002, 20);
			attackTime = A2K.kr(attackTime);

			// Suma de Decay
			inDecay = In.ar(inDecayVol) + InFeedback.ar(inFeedbackDecayVol);
			decayTime = inDecay.linlin(-1, 1, -5, 5) + decayTime;
			decayTime = decayTime.clip(0.002, 20);
			decayTime = A2K.kr(decayTime);

			// Suma de Sustain Level
			inSustain = In.ar(inSustainVol) + InFeedback.ar(inFeedbackSustainVol);
			sustainLevel = inSustain.linlin(-1, 1, -1, 1) + sustainLevel.lag(0.2);
			sustainLevel = sustainLevel.clip(0.002, 20);

			// Suma de Release
			inRelease = In.ar(inReleaseVol) + InFeedback.ar(inFeedbackReleaseVol);
			releaseTime = inRelease.linlin(-1, 1, -5, 5) + releaseTime;
			releaseTime = releaseTime.clip(0.002, 20);
			releaseTime = A2K.kr(releaseTime);

			gate = In.ar(signalTrigger);
			gate = gate + InFeedback.ar(inFeedbackSignalTrigger);
			sig = In.ar(inputBus);
			sig = sig + InFeedback.ar(inFeedbackBus);


			env = Env(
				levels: [
					0, // loopNode (ver Help de "Env")
					0,
					1,
					sustainLevel,
					0,
				],
				times: [delayTime, attackTime, decayTime, releaseTime],
			).ar(0, gate: gate * generalGate);

			// Se aplica la envolvente a la señal
			sig = sig * env * signalLevel; // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.


			// Se aplica la envolvente al voltage
			vol = env * envelopeLevel.lag(0.2); // gate tiene lag, para que cuando se envíe valor 0, no se corte bruscamente.

			Out.ar(outputBus, sig);
			Out.ar(outputBusVol, vol);
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
		outputBusVol,
		inDelayVol,
		inFeedbackDelayVol,
		inAttackVol,
		inFeedbackAttackVol,
		inDecayVol,
		inFeedbackDecayVol,
		inSustainVol,
		inFeedbackSustainVol,
		inReleaseVol,
		inFeedbackReleaseVol,
		delayTime,
		attackTime,
		decayTime,
		sustainLevel,
		releaseTime,
		envelopeLevel,
		signalLevel;
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_envTriggered, [
				\generalGate, 1,
				\signalTrigger, signalTrigger,
				\inFeedbackSignalTrigger, inFeedbackSignalTrigger,
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\outputBusVol: outputBusVol,
				\inDelayVol: inDelayVol,
				\inFeedbackDelayVol: inFeedbackDelayVol,
				\inAttackVol: inAttackVol,
				\inFeedbackAttackVol: inFeedbackAttackVol,
				\inDecayVol: inDecayVol,
				\inFeedbackDecayVol: inFeedbackDecayVol,
				\inSustainVol: inSustainVol,
				\inFeedbackSustainVol: inFeedbackSustainVol,
				\inReleaseVol: inReleaseVol,
				\inFeedbackReleaseVol: inFeedbackReleaseVol,
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