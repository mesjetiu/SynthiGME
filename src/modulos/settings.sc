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












	]}
}