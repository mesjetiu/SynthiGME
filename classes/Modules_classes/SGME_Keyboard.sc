SGME_Keyboard : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de salida de audio
	var <outBusPitch;
	var <outBusVelocity;
	var <outBusGate;

	// Parámetros del teclado
	var pitch, velocity, gate;


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
			arg outBusPitch, outBusVelocity, outBusGate;
			var sigPitch = SinOsc.ar(200)*0.1;
			var sigVelocity = SinOsc.ar(300)*0.1;
			var sigGate = SinOsc.ar(400)*0.1;
			Out.ar(outBusPitch, sigPitch);
			Out.ar(outBusVelocity, sigVelocity);
			Out.ar(outBusGate, sigGate);
		//	Out.ar(0, PinkNoise.ar(0.1!2)); // bypass
		}).add
		}

		// Métodos de instancia //////////////////////////////////////////////////////////////

		init { arg serv;
			server = serv;
			outBusPitch = Bus.audio(server);
			outBusVelocity = Bus.audio(server);
			outBusGate = Bus.audio(server);
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

		// Conversores de unidades.

		/*convertMix {|m|
		^m.linlin(0, 10, 0, 1);
		}

		convertLevel {|m|
		^m.linlin(0, 10, 0, 1);
		}*/

		// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

		/*setMix {|v|
		mix = v;
		synth.run(true);
		synth.set(\mix, this.convertMix(v));
		//	this.synthRun();
		}

		setLevel {|v|
		level = v;
		synth.run(true);
		synth.set(\level, this.convertLevel(v));
		//	this.synthRun();
		}*/
	}