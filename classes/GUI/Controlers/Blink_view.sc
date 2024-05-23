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


Blink_view {

	var
	<view,
	<defaultColor,
	<blinkColor1,
	<blinkColor2,
	<blinkRate,
	<blinkDuration,
	<isBlinking,
	<currentColor;

	//*********************************************************************************************

	*new {|view, duration, rate|
		^super.new.init(view, duration, rate);
	}

	init {|v, duration, rate|
		view = v;
		// Configuración de colores y parpadeo
		//defaultColor = view.background; // Color predeterminado del slider
		blinkColor1 = Color.red(alpha: 1); // Primer color de parpadeo
		blinkColor2 = Color.yellow(alpha: 1); // Segundo color de parpadeo
		blinkRate = rate; // Tiempo entre cambios de estado en el parpadeo
		blinkDuration = duration; // Duración total del parpadeo
		isBlinking = true; // Indicador de si el parpadeo está activo
		Routine({
			wait(blinkDuration);
			isBlinking = false;
		}).play(AppClock);
		currentColor = defaultColor; // Variable para rastrear el color actual de forma local

		// Pasar la función blink como referencia usando una función anónima
		//view.action_({ this.blink });

	}

	blink {
		if (isBlinking.not) { // Verificar si ya está parpadeando
			var defaultColor = view.background;
			isBlinking = true;
			fork {
				var startTime = Main.elapsedTime;
				var endTime = startTime + blinkDuration;
				while({ Main.elapsedTime < endTime }) {
					defer {
						// Alternar colores de forma segura utilizando la variable local
						if (currentColor == blinkColor1) {
							view.background = blinkColor2;
							currentColor = blinkColor2; // Actualizar el estado local
							//"Changing to blinkColor2".sgmePostln;
						} {
							view.background = blinkColor1;
							currentColor = blinkColor1; // Actualizar el estado local
							//"Changing to blinkColor1".sgmePostln;
						}
					};
					(blinkRate).wait; // Esperar el tiempo definido antes de cambiar de nuevo
				};
				defer { view.background = defaultColor; currentColor = defaultColor; }; // Restablecer el color original de forma segura
				isBlinking = false;
			};
		}
	}

	setDefaultColor_ {|color|
		defaultColor = color;
	}
}

