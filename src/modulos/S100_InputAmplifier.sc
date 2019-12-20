S100_InputAmplifier {

	// Synth de la instancia
	var <synth = nil;

	var <server;
	var <inputBus; // Entrada del amplificador.
	var <outputBus; // Salida del amplificador.
	var <inFeedbackBus; // Entrada de feedback: admite audio del ciclo anterior.

	// Parámetros correspondientes a los mandos del Synthi (todos escalados entre 0 y 10)
	var <on = 0; // 1 o 0. Activa y desactiva el canal.


	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var outVol = 1;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_inputAmplifier, {
			arg inputBus,
			inFeedbackBus,
			outputBus,
			outVol;

			var sig;

			sig = In.ar(inputBus) + InFeedback.ar(inFeedbackBus);
			sig = sig * outVol;

			Out.ar(outputBus, sig);
		}).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBus = Bus.audio(server);
		inFeedbackBus = Bus.audio(server);
		outputBus = Bus.audio(server);
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_inputAmplifier, [
				\inputBus, inputBus,
				\inFeedbackBus, inFeedbackBus,
				\outputBus, outputBus,
				\outVol, outVol,
			], server).register; //".register" registra el Synth para poder testear ".isPlaying"
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		var outputTotal = on * outVol;
		if (outputTotal == 0, {
			running = false;
			synth.run(false);
		}, {
			running = true;
			synth.run(true);
		});
	}


	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setOn {|value|
		if((value == 0).or(value == 1), {
			on = value;
			this.synthRun();
		}, {
			("S100_InputAmplifier/setOn: " + value + " no es un valor de 0 o 1").postln});
	}
}