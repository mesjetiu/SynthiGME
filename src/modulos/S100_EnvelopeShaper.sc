S100_EnvelopeSharper {

	/*
	Interpretación provisional de los diversos diales (a falta de experimentar con el Synthi), ya que la documentación encontrada sobre Synthi 100 incluye el Envelope Shaper antiguo









	*/









	// Synth de la instancia
	var <synth = nil;

	var <server;
	var <inputBus; // Entrada de audio.
	var <outputBus; // Salida de audio.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <selector; // Trigger selector
	var <delayTime = 0;
	var <attackTime = 0;
	var <decayTime = 0;
	var <sustainLevel = 0; // Interpreto que el dial "sustain" se refiere al nivel, no al tiempo.
	var <releaseTime = 0;
	var <envelopeLevel = 5; //Valores entre -5 y 5. Por comodidad y uniformidad, lo guardaremos entre 0 y 10
	var <signalLevel = 0;


	// Otros atributos de instancia y clase
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = S100_Settings.get[\envLag];
		SynthDef(\S100_envelopeShaper, {
			arg outputBus,
			inputBus,
			attackTime,
			decayTime,
			sustainLevel,
			releaseTime,
			peakLevel,
			envelopeLevel,
			signalLevel,
			gate = 0,
			tr_trigger = 0;

			var sig, env;

			env = Env.adsr(
				attackTime, decayTime, sustainLevel, releaseTime, peakLevel, \exponential
			).ar(2, gate, envelopeLevel);


			// Se aplica la envolvente y el nivel (level) a la señal
			sig = sig * env * signalLevel;

			Out.ar(outputBus, sig);

		}, [nil, nil, lag, lag, lag, lag, lag, lag]
		).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		outputBus = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_noiseGenerator, [
				\inputBus, inputBus,
				\outputBus, outputBus,
				\delayTime, this.convertTime(delayTime),
				\attackTime, this.convertTime(attackTime),
				\delayTime, this.convertTime(delayTime),
				\sustainLevel, (sustainLevel),
				\relaseTime, this.convertTime(releaseTime),
				\envelopeLevel, this.convertEnvelopeLevel(envelopeLevel);
				\signalLevel, this.convertLevel(signalLevel),
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = signalLevel;
		if (outputTotal==0, {
			running = false;
			pauseRoutine.reset;
			pauseRoutine.play;
		}, {
			pauseRoutine.stop;
			running = true;
			synth.run(true);
		});
	}

	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

	convertSignalLevel {|level|
		^level.linlin(
			inMin: 0,
			inMax: 10,
			outMin: 0,
			outMax: settings[\envSignalLevelMax]);
	}

	convertTime {|time|
		^time.linlin(
			inMin: 0,
			inMax: 10,
			outMin: settings[\envTimeMin]
			outMax: settings[\envTimeMax]);
	}

	convertSustainlLevel {|level|
		^level.linlin(
			inMin: 0,
			inMax: 10,
			outMin: 0,
			outMax: settings[\envSustainLevelMax]);
	}

	convertEnvelopeLevel {|level|
		^level.linlin(0, 10, -1, 1); // el nivel 5 del dial (en realidad 0 porque va de -5 a 5) corresponde con nivel 0.
	}


	// Setters de los parámetros /////////////////////////////////////////////////////////////////

	setSignalLevel {|lev|
		if((lev>=0).and(lev<=10), {
			signalLevel = lev;
			this.synthRun();
			synth.set(\level, this.convertSignalLevel(lev))
		}, {
			("S100_EnvelopeShaper/setSignalLevel: " + lev + " no es un valor entre 0 y 1").postln});
	}

	setDelayTime {|time|
		if((time>=0).and(time<=10), {
			delayTime = time;
			this.synthRun();
			synth.set(\delayTime, this.convertTime(time))
		}, {
			("S100_EnvelopeShaper/setDelayTime: " + time + " no es un valor entre 0 y 10").postln});
	}

	setAttackTime {|time|
		if((time>=0).and(time<=10), {
			attackTime = time;
			this.synthRun();
			synth.set(\attackTime, this.convertTime(time))
		}, {
			("S100_EnvelopeShaper/setAttackTime: " + time + " no es un valor entre 0 y 10").postln});
	}

	setReleaseTime {|time|
		if((time>=0).and(time<=10), {
			releaseTime = time;
			this.synthRun();
			synth.set(\releaseTime, this.convertTime(time))
		}, {
			("S100_EnvelopeShaper/setReleaseTime: " + time + " no es un valor entre 0 y 10").postln});
	}
}