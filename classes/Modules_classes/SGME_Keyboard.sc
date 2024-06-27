SGME_Keyboard : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de salida de audio
	var <outBusPitch;
	var <outBusVelocity;
	var <outBusGate;

	// Parámetros del teclado en términos de las perillas
	var <pitch = 9; // 0 - 10 . Al valor de 9 aprox. el factor ha de ser 1, una octava.
	var <velocity = 0; // -5 - 5
	var <gate = 0; // -5 - 5
	// Parámetros del teclado en sí que van a estar dirigiendo las señales que se envían.
	var <midiPitch = nil;
	var <midiVelocity = 0;
	var <keyGate = -3; // rango -3V - 3V.

	// Set para la administración de teclas del teclado
	var <keysPressed;


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
		SynthDef(\SGME_Keyboard, {
			arg outBusPitch, outBusVelocity, outBusGate,
			pitch = 0, velocity = 0, gate = 0;
			// este synth tan solo devuelve en forma de señal los valores de los parámetros que le entran.
			Out.ar(outBusPitch, K2A.ar(pitch));
			Out.ar(outBusVelocity, K2A.ar(velocity));
			Out.ar(outBusGate, K2A.ar(gate));
			//	Out.ar(0, PinkNoise.ar(0.1!2)); // bypass
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv;
		server = serv;
		outBusPitch = Bus.audio(server);
		outBusVelocity = Bus.audio(server);
		outBusGate = Bus.audio(server);
		keysPressed = Set();
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
			synth = Synth(\SGME_Keyboard, [
				\outBusPitch, outBusPitch,
				\outBusVelocity, outBusVelocity,
				\outBusGate, outBusGate,
			], server).register;
		});
		//	this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun { // Dejo esta función aunque no se va a usar. Por ahora no hay manera de saber que no hay output.
		/*var outputTotal = outCount;
		if (outputTotal == 0, {
		synth.run(false);
		}, {
		synth.run(true);
		});*/
	}


	// Conversores de unidades de las perillas.

	convertPitch {
		^pitch * 1/9; // factor entre 0 y 1.22 para aplicar a pitch del teclado. Según el manual, al valor de 9 la octava es justa (1V), aunque la rueda tiene un rango de 0-10.
	}

	convertVelocity {
		^velocity.linlin(-5, 5, -1, 1); // factor de -1 a 1 para la velocidad del teclado.
	}

	convertGate {
		^gate.linlin(-5, 5, -1, 1) * keyGate; // factor de -1 a 1 para la envolvente o gate del teclado.
	}


	// Conversores de unidades de valores MIDI.

	convertMIDInote {
		^(midiPitch - 36) / 12; //36 es C1, valor 0. Una octava más aguda (12), dará 1 V. A 1V/octava.
	}

	convertMIDIvel {
		// vel tiene 128 valores desde 0 a 127. El synthi da desde -3V a 4V, es decir, 7 V de rango.
		var vel = (midiVelocity / 128) * 7;
		^vel.linlin(0,7, (-3.5), 3.5); // así situamos el valor dentro del rango -3.5 y 3.5.
	}

	// Setters de los parámetros con sus efectos en el synth. Estos métodos se usan internamente desde otro método "inteligente" que lleva recuento de teclas pulsadas, etc.
	///////////////////////////////////////////////////////////////////////

	pitch_ {|p|
		var synthPitch;
		pitch = p;
		synthPitch = this.convertPitch * this.convertMIDInote;
		synth.set(\pitch, synthPitch.postln);
	}

	midiPitch_ {|n|
		var synthPitch;
		midiPitch = n;
		synthPitch = this.convertPitch * this.convertMIDInote;
		synth.set(\pitch, synthPitch.postln);
	}

	velocity_ {|v|
		var synthVelocity;
		velocity = v;
		synthVelocity = this.convertMIDIvel + (this.convertVelocity * 3.5); // Añadimos el factor dado por la perilla, que va de -1 a 1
		synth.set(\velocity, synthVelocity);
	}

	midiVelocity_ {|v|
		var synthVelocity;
		midiVelocity = v;
		synthVelocity = this.convertMIDIvel + (this.convertVelocity * 3.5); // Añadimos el factor dado por la perilla, que va de -1 a 1
		synth.set(\velocity, synthVelocity);
	}

	gate_ {|g|
		gate = g;
		synth.set(\gate, this.convertGate);
	}

	keyGate_ {|g|
		keyGate = g;
		synth.set(\gate, this.convertGate);
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	// Lógica de administración de teclas y modos del teclado
	// Método para presionar o liberar una tecla
	pressRelease {|midiNote, midiVel, onOff|
		// aquí se realiza toda la lógica de lo que el teclado ha de hacer con el output de pitch, gate y velocity
		var maxKeyPressed;
		var lastPitch = pitch;

		case // Actualizamos base de datos de teclas pulsadas
		{ onOff == \on } {
			keysPressed.add(midiNote);
		}
		{ onOff == \off } {
			keysPressed.remove(midiNote);
		};

		if (keysPressed.size > 0) {
			maxKeyPressed = keysPressed.maxItem;
			"Tecla más aguda presionada: %".format(maxKeyPressed).postln;
			// Enviar pitch y gate al sintetizador
			this.midiPitch_(maxKeyPressed);
			this.keyGate_(3);
			if (pitch > lastPitch) { // si se ha añadido una nueva tecla hacia el agudo
				this.midiVelocity_(midiVel); // Se toma la nueva velocidad
			}
		}  {
			"No hay teclas presionadas".postln;
			// Enviar gate off al sintetizador
			this.keyGate_(-3);
			// pitch y velocity no se actualizan porque guardan memoria
		}
	}
}