S100_EnvelopeSharper {

	/*
	Interpretación provisional de los diversos diales (a falta de experimentar con el Synthi), ya que la documentación encontrada sobre Synthi 100 incluye el Envelope Shaper antiguo, los parámetros del S100 de Cuenca deben de ser relativamente modernos en esa época (Las descripciones de S100 no tienen "sustain"). Tras leer el funcionamiento de otros sintetizadores, estas son las conclusiones a las que he llegado:

	Delay: Tiempo sin actuar tras un trigger o apertura de gate
	Attack: Tiempo en llegar al nivel más alto desde 0.
	Decay: Tiempo en llegar desde el nivel más alto al nivel "sustain"
	Sustain: Nivel que puede ser mantenido un tiempo indeterminado.
	Release: Tiempo de vuelta al nivel 0 inicial

	Opciones del selector:
	    Gated F/R: Un voltage mayor que 0 activa Free Run hasta que vuelve a 0.
	    Free Run: El ciclo se repite una vez tras otra sin detenerse ningún tiempo en "sustain". El tiempo total de duración del ciclo será: delay + attack + decay + release.
	    Triggered: Un voltage mayor que 0 activa un único ciclo.
	    Gated y Hold: Aparentemente es lo mismo. En las descripciones de S100, que no tienen "sustain", solo tiene "hold", y su comportamiento consiste en pararse en el nivel tras el attack (una vez iniciado el ciclo con un valor mayor que 0), y se mantiene en el hasta que el nivel desciende a 0. Ya que este es el comportamiento esperable, por otra parte, de "gated", doy por supuesto que "hold" significa lo mismo que en los sintetizadores clásicos, y que es "gated" el que es un concepto nuevo, como lo es también el de "sustain", y que el ciclo se para precisamente en el valor de "sustain", algo, por otra parte, que es lo que suelen hacer todas las implementaciones modernas de ADSR.

	"Envelope level" lo voy a dejar por ahora sin implementar. No es claro si se refiere solo al nivel de salida del control de voltage, equivalente al dial "trapezoid" de los S100 antiguos, ya que no lo trae el de Cuenca (es muy probable).

	*/

	// Synth de la instancia
	var <synth = nil;

	var <server;
	var <inputBus; // Entrada de audio.
	var <outputBus; // Salida de audio.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <selector; // Trigger selector. Los valores del dial se almacenarán como números enteros: 1 = GATED F/N, 2 = FREE RUN, 3 = HOLD, 4 = TRIGGERED, 5 = HOLD.
	var <delayTime = 0;
	var <attackTime = 0;
	var <decayTime = 0;
	var <sustainLevel = 0;
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

		// Habrá un Synth distinto por cada modo del selector: Free Run, Gated, Triggered y Hold. Todos ellos estarán en un grupo en el servidor para facilitar el orden entre todos los synths de Synthi100, tratándose este grupo como una sola unidad. Siempre estará funcionando aquel que corresponda con el selector ya que podría ser conectado en cualquier momento y la fase no se reiniciaría. El resto de synths estarán en pausa.
		lag = S100_Settings.get[\envLag];


		SynthDef(\S100_FreeRun, {
			arg outputBus,
			inputBus,
			delayTime,
			attackTime,
			decayTime,
			sustainLevel,
			releaseTime,
			signalLevel,
			gate = 0;

			var sig, env;

			env = Env(
				levels: [
					0, // loopNode (ver Help de "Env")
					0,
					1,
					sustainLevel,
					0, // releaseNode
				],
				times: [delayTime, attackTime, decayTime, releaseTime],
				releaseNode: 3,
				loopNode: 0,
			);


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
			synth = Synth(\S100_freeRun, [
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