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

Copyright 2024 Carlos Arturo Guerra Parra <carlosarturoguerra@gmail.com>
*/

SGME_Oscilloscope : SGME_Connectable {

	// Synth de la instancia
	var <synth = nil;
	var <server;

	// ScopeView
	var oscilloscope = nil;

	// Buses de entrada y salida de audio
	var <inputBusCH1;
	var <inFeedbackBusCH1;
	var <inputBusCH2;
	var <inFeedbackBusCH2;
	var stereoBuffer;

	// Knobs del módulo
	var <mode = 0; // 4 modos: X-Y, CH1, Dual, CH2, Add
	var <varSensCH1, varSensCH2; // Sensibilidad de CH1 y CH2


	// Otros atributos de instancia
	var <running; // true o false: Si el sintetizador está activo o pausado
	var pauseRoutine; // Rutina de pausado del Synth
	var resumeRoutine;
	classvar settings;


	// Métodos de clase //////////////////////////////////////////////////////////////////

	*new { |server|
		settings = SGME_Settings.get;
		^super.new.init(server);
	}

	*addSynthDef {
		SynthDef(\SGME_Oscilloscope, {
			arg inputBusCH1,
			inFeedbackBusCH1,
			inputBusCH2,
			inFeedbackBusCH2,
			stereoBufferNum,
			gateCH1, gateCH2; // para abrir y cerrar el paso de entrada.

			var sigInCH1, sigInCH2;
			sigInCH1 = In.ar(inputBusCH1) + InFeedback.ar(inFeedbackBusCH1);
			sigInCH2 = In.ar(inputBusCH2) + InFeedback.ar(inFeedbackBusCH2);

			ScopeOut2.ar([sigInCH1, sigInCH2], stereoBufferNum);
			//Out.ar(0, [sigInCH1, sigInCH2] );
			//Out.ar(0, PinkNoise.ar!2)
		}).add
	}

	// Métodos de instancia //////////////////////////////////////////////////////////////

	init { arg serv = Server.local;
		server = serv;
		inputBusCH1 = Bus.audio(server);
		inFeedbackBusCH1 = Bus.audio(server);
		inputBusCH2= Bus.audio(server);
		inFeedbackBusCH2 = Bus.audio(server);
		stereoBuffer = Buffer.alloc(server,1024,2);
		pauseRoutine = Routine({
			if (resumeRoutine.isPlaying) {resumeRoutine.stop};
			running = false;
		//	1.wait;
			synth.run(false);
		//	1.wait;
			//{oscilloscope.stop}.defer();
		});
		resumeRoutine = Routine({
			if(pauseRoutine.isPlaying) {pauseRoutine.stop};
			running = true;
		//	1.wait;
			synth.run(true);
		//	1.wait;
			//{oscilloscope.stop}.defer();
		});
	}

	setVarSensCH1 {|value|
		varSensCH1 = value;
	}

	setVarSensCH2 {|value|
		varSensCH2 = value;
	}

	setMode {|value|

	}

	// Crea el Synth en el servidor
	createSynth {
		if(synth.isPlaying==false, {
			synth = Synth(\SGME_Oscilloscope, [
				\inputBusCH1, inputBusCH1,
				\inFeedbackBusCH1, inFeedbackBusCH1,
				\inputBusCH2, inputBusCH2,
				\inFeedbackBusCH2, inFeedbackBusCH2,
				\stereoBufferNum, stereoBuffer.bufnum,
				\gateCH1, 1,
				\gateCH2, 1
			], RootNode(server), \addToTail).register;
		});
		this.synthRun;
	}

	// Pausa o reanuda el Synth dependiendo de si su entrada es 0 o no.
	synthRun {
		/*var inputTotal = inCount;
		if (inputTotal == 0, {
			synth.set(\gateCH1, 0, \gateCH2, 0);
			synth.run(false)
		}, {
			synth.run(true);
			synth.set(\gateCH1, 1, \gateCH2, 1);
		});*/
	}

	makeOscilloscope {|parent, rect|
		var scopeView;
		var fondoOscilloscope = Color.new255(77, 118, 147); // Color de fondo del osciloscopio.
		var oscilloscopeWave = Color.new255(0, 235, 253);
		scopeView = ScopeView(parent, rect);
		scopeView.bufnum = stereoBuffer.bufnum;
		scopeView.server = server;
		scopeView.background = fondoOscilloscope;
		scopeView.waveColors = oscilloscopeWave!2; // un array de colores, uno color por canal.
		scopeView.fill = false; // para que dibuje líneas sin rellenar
		scopeView.start;
		oscilloscope = scopeView;
		^scopeView;
	}
}
