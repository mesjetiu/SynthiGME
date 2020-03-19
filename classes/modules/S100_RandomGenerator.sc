S100_RandomGenerator : S100_Connectable {

	// Synth de la instancia
	var <synth = nil;

	var <server;

	// Salidas del módulo
	var <outputBusVoltage1;
	var <outputBusVoltage2;
	var <outputBusKey;

	// Rutina que genera los cambios aleatorios
	var <randomRoutine;

	// Parámetros correspondientes a los diales del Synthi (todos escalados entre 0 y 10 o -5 y 5)
	var <mean = 0;
	var <variance = 0;
	var <voltage1 = 0;
	var <voltage2 = 0;
	var <key = 0;


	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth
	classvar lag = 0.2; // Tiempo que dura la transición en los cambios de parámetros en el Synth
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = S100_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\S100_randomGenerator, {
			arg outputBusVoltage1,
			outputBusVoltage2,
			outputBusKey,
			voltage1 = 0,
			voltage2 = 0,
			key = 0;

			Out.ar(outputBusVoltage1, K2A.ar(voltage1));
			Out.ar(outputBusVoltage2, K2A.ar(voltage2));
			Out.ar(outputBusKey, K2A.ar(key));
		}).add
	}


	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		outputBusVoltage1 = Bus.audio(server);
		outputBusVoltage2 = Bus.audio(server);
		outputBusKey = Bus.audio(server);
		pauseRoutine = Routine({
			lag.wait; // espera el mismo tiempo que el rate de los argumentos del Synth.
			synth.run(false);
		});
		randomRoutine = Routine({
			var time; // tiempo entre cambios
			var timeMax = settings[\randomTimeMax];
			var timeMin = settings[\randomTimeMin];
			var factorVol = settings[\randomFactorVol];
			var keyTime0 = 0.01; // el tiempo en el que key está a 0 al final de la nota. Hago que el valor de key no sea 0 durante el resto de la nota, a modo de gate. Esto hay que probarlo en el Synthi.
			var vol1, vol2, k;
			loop {
				time = (this.convertMean(mean) + this.convertVariance(variance).rand2).clip(timeMin, timeMax);
				vol1 = this.convertVoltage(voltage1).rand2 * factorVol;
				vol2 = this.convertVoltage(voltage2).rand2 * factorVol;
				k = this.convertKey(key).rand2 * factorVol;
				// aquí se setea el synth de acuerdo a los parámetros.
				synth.set(\voltage1, vol1);
				synth.set(\voltage2, vol2);
				synth.set(\key, k);
				(time - keyTime0).wait;
				synth.set(\key, 0);
				keyTime0.wait;
			}
		});
	}

	// Crea el Synth en el servidor
	createSynth {
		Routine{
			if(synth.isPlaying==false, {
				synth = Synth(\S100_randomGenerator, [
					\outputBusVoltage1, outputBusVoltage1,
					\outputBusVoltage2, outputBusVoltage2,
					\outputBusKey, outputBusKey,
				], server).register; //".register" registra el Synth para poder testear ".isPlaying"
			});
			while({synth.isPlaying == false}, {wait(0.001)});
			randomRoutine.play;
			this.synthRun;
		}.play
	}

	// Pausa o reanuda el Synth dependiendo de si su salida es 0 o no.
	synthRun {
		if ((voltage1==0)
			.and(voltage2==0)
			.and(key==0), {
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

	convertMean {|mean|
		^mean.linexp(
			inMin: -5,
			inMax: 5,
			outMin: settings[\randomTimeMin],
			outMax: settings[\randomTimeMax],
		)
	}

	convertVariance {|variance|
		^variance.linexp(
			inMin: -5,
			inMax: 5,
			outMin: 1/1000,
			outMax: 1,
		)
	}

	convertVoltage {|vol|
		^vol.linlin(
			inMin: 0,
			inMax: 10,
			outMin: 0,
			outMax: 1,
		)
	}

	convertKey {|k|
		^k.linlin(
			inMin: -5,
			inMax: 5,
			outMin: -1,
			outMax: 1,
		)
	}




	// Setters de los parámetros
	setMean {|m|
		mean = m;
		this.synthRun();
	}

	setVariance {|v|
		variance = v;
		this.synthRun();
	}

	setVoltage1 {|vol|
		voltage1 = vol;
		this.synthRun();
	}

	setVoltage2 {|vol|
		voltage2 = vol;
		this.synthRun();
	}

	setKey {|k|
		key = k;
		this.synthRun();
	}

}