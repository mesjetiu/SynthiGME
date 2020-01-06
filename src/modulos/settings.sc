+ S100_Settings {
	* settings {^[

		//******************************************************************************************
		// Archivo de configuración de Synthi100. Todos los límites y valores "afinables"
		// del Synthi100 pueden ser cambiados en este archivo.
		// El esquema del archivo es el de pares clave-valor, separando cada elemento por una coma.
		// Por claridad, cada par de elementos clave-valor estarán en una línea diferente.
		//******************************************************************************************


		// CONFIGURACIÓN GENERAL********************************************************************

		\generalVol, 0.5,
		\OSCDevicePort, 9000,

		// Output Busses (solo si se elige la opción "standalone")
		\panOutputs1to4Busses, [2,3],
		\panOutputs5to8Busses, [4,5],
		\individualChannelOutputsBusses, [6,7,8,9,10,11,12,13],
		\sendToDeviceBusses, [14,15,16,17],

		// Input Busses (solo si se elige la opción "standalone")
		\returnFromDeviceBusses, [2,3,4,5],
		\inputAmplifiersBusses, [6,7,8,9,10,11,12,13],
		\micAmpBusses, [14,15],

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
		\outLevelMax, 2,
		\outHPFreqMin, 10,
		\outHPFreqMax, 1000,
		\outLPFreqMin, 400,
		\outLPFreqMax, 2000,

		// CONFIGURACIÓN DE NOISE GENERATORS********************************************************

		\noiseLag, 0.2,
		\noiseLevelMax, 2,
		\noiseHPFreqMin, 10,
		\noiseHPFreqMax, 1000,
		\noiseLPFreqMin, 400,
		\noiseLPFreqMax, 2000,

		// CONFIGURACIÓN DE INPUT AMPLIFIERS********************************************************

		\inputLag, 0.2,
		\inputLevelMax, 1,

		// CONFIGURACIÓN DE RING MODULATOR**********************************************************

		\ringLag, 0.2,
		\ringLevelMax, 2,

		// CONFIGURACIÓN DE ENVELOPE SHAPER*********************************************************

		\envLag, 0.2,
		\envSignalLevelMax, 1,
		\envTimeMin, 0.002, // 2 ms como valor mínimo en todas las duraciones del módulo.
		\envTimeMax, 20,  // 20 s como valor máximo.
		\envPeakLevel, 1,
		\envSustainLevelMax, 1,












	]}
}