S100_Connectable {
	// Clase abstracta. Contiene variables y métodos para llevar el conteo de conexiones en las matrices (patchbay). Los módulos que se puden conectar por medio de la matriz, heredan de esta clase. Cada vez que se haga o deshaga una conexión se se hace crecer o decrecer el recuento.

	var <audioInCounter = 0;
	var <audioOutCounter = 0;
	var <voltageInCounter = 0;
	var <voltageOutCounter = 0;

	audioInCount {|bool|
		if (bool == true, {
			audioInCounter = audioInCounter + 1;
		}, {
			audioInCounter = audioInCounter - 1;
		});
	}

	voltageInCount {|bool|
		if (bool == true, {
			voltageInCounter = voltageInCounter + 1;
		}, {
			voltageInCounter = voltageInCounter - 1;
		});
	}

	audioOutCount {|bool|
		if (bool == true, {
			audioOutCounter = audioOutCounter + 1;
		}, {
			audioOutCounter = audioOutCounter - 1;
		});
	}

	voltageOutCount {|bool|
		if (bool == true, {
			voltageOutCounter = voltageOutCounter + 1;
		}, {
			voltageOutCounter = voltageOutCounter - 1;
		});
	}
}