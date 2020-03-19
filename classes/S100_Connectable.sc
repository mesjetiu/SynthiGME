S100_Connectable {
	// Clase abstracta. Contiene variables y métodos para llevar el conteo de conexiones en las matrices (patchbay). Los módulos que se puden conectar por medio de la matriz, heredan de esta clase. Cada vez que se haga o deshaga una conexión se se hace crecer o decrecer el recuento.

	var <inCount = 0;
	var <outCount = 0;

	inPlusOne {arg bool = true;
		if (bool == true, {
			inCount = inCount + 1;
		}, {
			inCount = inCount - 1;
		});
		this.synthRun;
	}

	outPlusOne {arg bool = true;
		if (bool == true, {
			outCount = outCount + 1;
		}, {
			outCount = outCount - 1;
		});
		this.synthRun;
	}
}