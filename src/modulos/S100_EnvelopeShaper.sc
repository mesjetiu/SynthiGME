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

	Envelope level: tiene su nivel entre -5 y 5. Lo interpretaré como el nivel (con polaridad) de la envolvente, pudiendo ser negativo, algo interesante especialmente para salida de control de voltaje.

	*/

	// Group de la instancia. Esta clase no contiene y Synth como el resto, sino un grupo. En el grupo estarán varios synths, uno por cada opción del selector. Esta clase maneja el selector, y activa y desactiva otras clases implementadas para las diferentes opciones del selector.
	var <group = nil;

	var envFreeRun; // Clase para la opción del selector "FREE RUN"

	var <server;
	var <inputBus; // Entrada de audio.
	var <inFeedbackBus;
	var <outputBus; // Salida de audio.

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10)
	var <selector; // Trigger selector. Los valores del dial se almacenarán como números enteros: 1 = GATED F/N, 2 = FREE RUN, 3 = HOLD, 4 = TRIGGERED, 5 = HOLD.
	var <delayTime = 0;
	var <attackTime = 0;
	var <decayTime = 0;
	var <sustainLevel = 0;
	var <releaseTime = 0;
	var <envelopeLevel = 0; //Valores entre -5 y 5.
	var <signalLevel = 0;


	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*initClass {
		// Inicializa otras clases antes de esta
		//Class.initClassTree(S100_Settings);
		Class.initClassTree(S100_EnvFreeRun);
	}

	*addSynthDef {
		S100_EnvFreeRun.addSynthDef;
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		outputBus = Bus.audio(server);
		envFreeRun = S100_EnvFreeRun(server);
	}

	createSynth {
		Routine({
			var waitTime = 0.001;
			var synth;
			 // se crea el grupo
			group = Group(server).register;
			while({group.isPlaying == false}, {wait(waitTime)});
			// se crea el synth de FREE RUN
			synth = envFreeRun.createSynth(
				group: group,
				gate: 1, // lo dejo abierto para pruebas
				inputBus: inputBus,
				inFeedbackBus: inFeedbackBus,
				outputBus: outputBus,
				delayTime: delayTime,
				attackTime: attackTime,
				decayTime: decayTime,
				sustainLevel: sustainLevel,
				releaseTime: releaseTime,
				envelopeLevel: envelopeLevel,
				signalLevel: signalLevel,
			);
			while({synth.isPlaying == false}, {wait(waitTime)});
		}).play(AppClock);
	}



	// Conversores de unidades. Los diales del Synthi tienen la escala del 0 al 10. Cada valor de cada dial debe ser convertido a unidades comprensibles por los Synths. Se crean métodos ad hoc, de modo que dentro de ellos se pueda "afinar" el comportamiento de cada dial o perilla.

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
		^level.linlin(-5, 5, -1, 1);
	}

	convertSignalLevel {|level|
		^level.linlin(
			inMin: 0,
			inMax: 10,
			outMin: 0,
			outMax: settings[\envSignalLevelMax]);
	}



	// Setters de los parámetros /////////////////////////////////////////////////////////////////

	setSignalLevel {|lev|
		if((lev>=0).and(lev<=10), {
			signalLevel = lev;
			this.synthRun();
			group.set(\level, this.convertSignalLevel(lev))
		}, {
			("S100_EnvelopeShaper/setSignalLevel: " + lev + " no es un valor entre 0 y 1").postln});
	}

	setDelayTime {|time|
		if((time>=0).and(time<=10), {
			delayTime = time;
			this.synthRun();
			group.set(\delayTime, this.convertTime(time))
		}, {
			("S100_EnvelopeShaper/setDelayTime: " + time + " no es un valor entre 0 y 10").postln});
	}

	setAttackTime {|time|
		if((time>=0).and(time<=10), {
			attackTime = time;
			this.synthRun();
			group.set(\attackTime, this.convertTime(time))
		}, {
			("S100_EnvelopeShaper/setAttackTime: " + time + " no es un valor entre 0 y 10").postln});
	}

	setReleaseTime {|time|
		if((time>=0).and(time<=10), {
			releaseTime = time;
			this.synthRun();
			group.set(\releaseTime, this.convertTime(time))
		}, {
			("S100_EnvelopeShaper/setReleaseTime: " + time + " no es un valor entre 0 y 10").postln});
	}
}