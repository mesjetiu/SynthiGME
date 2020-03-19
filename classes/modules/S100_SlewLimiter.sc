S100_SlewLimiter : S100_Connectable {

	// TODO: (Importante para eficiencia)
	// Hacer que Patchbay cambie un semáforo en cada módulo cada vez que esté conectado. De este modo el módulo podrá saber que tiene input o tiene output. En función de estos semáforos se podrá tomar decisiones como la de poner en pausa los synths. En el caso de este módulo (y algunos más), si no existe este sistema, no hay manera de saber cuándo ponerse en pausa.


	// Synth de la instancia
	var <synth = nil;
	var <server;

	// Buses de entrada y salida de voltaje
	var <inputBusVol;
	var <inFeedbackBusVol;
	var <outputBusVol;

	// Parámetro correspondiente a los mandos del Synthi (todos escalados entre 0 y 10)
	var <rate = 0;

	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var <outVol = 1;
	var pauseRoutine; // Rutina de pausado del Synth
	classvar lag; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		lag = 0.01; //S100_Settings.get[\ringLag];
		SynthDef(\S100_slewLimiter, {
			arg inputBusVol,
			inFeedbackBusVol,
			outputBusVol,
			rate;

			var sig;
			sig = In.ar(inputBusVol) + InFeedback.ar(inFeedbackBusVol);
			sig = Slew.ar(sig, rate, rate);

			Out.ar(outputBusVol, sig);
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBusVol = Bus.audio(server);
		inFeedbackBusVol = Bus.audio(server);
		outputBusVol = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\S100_slewLimiter, [
				\inputBusVol, inputBusVol,
				\inFeedbackBusVol, inFeedbackBusVol,
				\outputBusVol, outputBusVol,
				\rate, this.convertRate(rate),
			], server).register;
		});
	//	this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun { // Dejo esta función aunque no se va a usar. Por ahora no hay manera de saber que no hay output.
		var outputTotal = 1;
		if (outputTotal == 0, {
			running = false;
			synth.run(false);
		}, {
			running = true;
			synth.run(true);
		});
	}

	// Conversores de unidades.

	convertRate {|r|
		^r.linexp(0, 10, 1/0.001, 1/1);//settings[\slewRangeMin], settings[\slewRangeMax]);
	}

	// Setters de los parámetros ///////////////////////////////////////////////////////////////////////

	setRate {|r|
			rate = r;
		//	this.synthRun();
			synth.set(\rate, this.convertRate(r))
	}
}
