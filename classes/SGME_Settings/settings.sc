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

+ SGME_Settings {
	* settings {^[

		//******************************************************************************************
		// Archivo de configuración de SynthiGME. Todos los límites y valores ajustables
		// del SynthiGME pueden ser cambiados en este archivo.
		// El esquema del archivo es el de pares clave-valor, separando cada elemento por una coma.
		// Por claridad, cada par de elementos clave-valor estarán en una línea diferente.
		//******************************************************************************************


		// CONFIGURACIÓN GENERAL********************************************************************

		\generalVol, 0.5,
		\OSCDevicePort, 9000,

		// Opciones del servidor de audio
		\numAudioBusChannels, 2048,
		\numOutputBusChannels, 18,
		\numInputBusChannels, 16,
		\blockSize, 64,

		// Output Busses (solo si se elige la opción "standalone")
		\panOutputs1to4Busses, [2,3],
		\panOutputs5to8Busses, [4,5],
		\individualChannelOutputsBusses, [6,7,8,9,10,11,12,13],
		\sendToDeviceBusses, [14,15,16,17],

		// Input Busses (solo si se elige la opción "standalone")
		\returnFromDeviceBusses, [2,3,4,5],
		\inputAmplifiersBusses, [6,7,8,9,10,11,12,13],
		\micAmpBusses, [14,15],

		// GUI (Graphical User Interface de SuperCollider)


		// CONFIGURACIÓN DE OSCILADORES*************************************************************

		\oscOutVol, 1,
		\oscLag, 0.5,
		\oscFreqHiMax, 16000,
		\oscFreqHiMin, 1,
		\oscFreqLoMax, 500,
		\oscFreqLoMin, 0.015,
		\oscPulseLevelMax, 1,
		\oscPulseShapeMin, 0,
		\oscPulseShapeMax, 1,
		\oscSineLevelMax, 1,
		\oscSineSymmetryMin, -1,
		\oscSineSymmetryMax, 1,
		\oscSawtoothLevelMax, 1,
		\oscTriangleLevelMax, 1,

		// CONFIGURACIÓN DE CANALES DE SALIDA*******************************************************

		\outLag, 0.2,
		\outLevelMax, 4,
		\outHPFreqMin, 10,
		\outHPFreqMax, 400,
		\outLPFreqMin, 400,
		\outLPFreqMax, 20000,

		// CONFIGURACIÓN DE NOISE GENERATORS********************************************************

		\noiseLag, 0.2,
		\noiseLevelMax, 2,
		\noiseHPFreqMin, 10,
		\noiseHPFreqMax, 400,
		\noiseLPFreqMin, 400,
		\noiseLPFreqMax, 20000,

		// CONFIGURACIÓN DE INPUT AMPLIFIERS********************************************************

		\inputLag, 0.2,
		\inputLevelMax, 1,

		// CONFIGURACIÓN DE External Treatment Returns**********************************************

		\returnLag, 0.2,
		\returnLevelMax, 1,

		// CONFIGURACIÓN DE RING MODULATOR**********************************************************

		\ringLag, 0.2,
		\ringLevelMax, 2,

		// CONFIGURACIÓN DE ENVELOPE SHAPER*********************************************************

		\envSignalLevelMax, 1,
		\envTimeMin, 0.002, // 2 ms como valor mínimo en todas las duraciones del módulo.
		\envTimeMax, 20,  // 20 s como valor máximo.
		\envPeakLevel, 1,
		\envSustainLevelMax, 1,

		// CONFIGURACIÓN DE RANDOM VOLTAGE GENERATOR************************************************

		\randomTimeMax, 10,
		\randomTimeMin, 0.01,
		\randomFactorVol, 1, // Factor por el que se multiplica la salida de cada voltage


		// CONFIGURACIÓN DE SLEW LIMITERS***********************************************************

		\slewRangeMax, 1, // en segundos
		\slewRangeMin, 0.001,
		\randomFactorVol, 1, // Factor por el que se multiplica la salida de cada voltage







	]}
}