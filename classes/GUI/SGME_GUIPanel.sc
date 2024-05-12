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


// De esta clase heredan los paneles.
SGME_GUIPanel : SGME_GUIShortcuts{

	var <id;
	var <window;
	var <compositeView;
	var viewSizes;
	var zoomWLevel = 1;
	var zoomHLevel = 1;
	var rectWindow;
	var rectCompositeView;
	var virtualWidth = 1920; // Todos las Views usan unidades de medida en relación a esta anchura.
	var origin; // El tamaño y posición original de la ventana. Para volver a ella cuando se quiera
	var <>hasFocus; // booleano. Solo un panel tendrá el foco (para zoom)

	// Cantidad que varían los valores de incremento y decremento usando el ratón
	var stepDefault = 0.001;
	var step = 0.001;

	var synthiGME;
	var <parameterViews; // Views de los parámetros (widgets)

	var installedPath;

	// Colores de la intefaz (tomados de fotografías del Synthi 100)
	var blue;
	var green;
	var yellow;
	var red;
	var white;
	var black;
	var whiteBackground;
	var blackForniture;


	//*********************************************************************************************

	*initClass {
		// Inicializa otras clases antes de esta
		Class.initClassTree(SGME_GUINode);
		Class.initClassTree(SGME_GUIShortcuts);
	}

	*new {|synthi, parameters|
		^super.new.init(synthi, parameters);
	}

	init {|synthi, parameters|
		synthiGME = synthi;
		hasFocus = false;
		parameterViews = parameters;
		installedPath = Quarks.quarkNameAsLocalPath("SynthiGME");

		blue = Color.new255(61.8, 86.7, 118.4);
		green = Color.new255(68.6, 107.2, 82.6);
		white = Color.new255(172.7, 166.6, 160.3);
		black = Color.new255(34.4, 36.3, 38.7);
		yellow = Color.new255(224, 186, 69);
		red = Color.new255(183, 56, 66);
		whiteBackground = Color.new255(191, 180, 176); // Color de los paneles del Synthi 100
		blackForniture = Color.new255(18, 18, 19.2); // Color negro del mueble.
	}

	makeWindow { // Para sobrescribir desde clases que heredan
		rectWindow = Rect(0, 0,  virtualWidth/4,  virtualWidth/4);
		rectCompositeView = Rect(
			0,
			0,
			rectWindow.width,
			rectWindow.height,
		);
		window =  Window("", rectWindow, false, true, scroll: true)
		.userCanClose_(false);
		compositeView = CompositeView(window, rectCompositeView);

		this.makeShortcuts(window, synthiGME);
		compositeView.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			var factor = 2;
			if(clickCount == 2, {
				buttonNumber.switch(
					0, {this.resizePanel(factor)}, // botón izquierdo
					1, {this.resizePanel(1/factor)}, // botón derecho
				)
			}, { // si se hace un solo click...
				buttonNumber.switch(
					0, {}, // botón izquierdo
					1, {
						Menu(
							//MenuAction("Zoom In", { this.resizePanel(factor) }),
							//MenuAction("Zoom Out", { this.resizePanel(1/factor) }),
							//MenuAction("Invisible", { window.visible = false }),
							MenuAction("Abrir patch", { synthiGME.loadStateGUI }),
							MenuAction("Guardar patch", { synthiGME.saveStateGUI }),
							MenuAction("Reiniciar patch", { synthiGME.restartState }),
							MenuAction("Inicia/termina grabación", {
								if (synthiGME.server.isRecording) {
									synthiGME.server.stopRecording;
								} {
									synthiGME.server.record;
								}
							}),
							MenuAction("Ver/ocultar atajos de teclado", {
								if (synthiGME.guiSC.helpWindow.window.visible == true, {
									synthiGME.guiSC.helpWindow.window.visible = false
								}, {synthiGME.guiSC.helpWindow.window.visible = true
								})
							}),
							MenuAction("Salir", { synthiGME.close })
						).front;
					}, // botón derecho
				)
			}
			)
		});

		window.toFrontAction = {
			this.focus;
		};

		viewSizes = [];
		viewSizes = viewSizes.add([window, rectWindow]);
		viewSizes = viewSizes.add([compositeView, rectCompositeView]);
	}

	goToOriginFocusedPanel {
		synthiGME.guiSC.panels.do({|panel|
			if (panel.hasFocus == true, {
				panel.goToOrigin;
				^this;
			})
		})
	}
	resizeFocusedPanel {|factor|
		synthiGME.guiSC.panels.do({|panel|
			if (panel.hasFocus == true, {
				panel.resizePanel(factor);
				^this;
			})
		})
	}

	focus {arg numPanel = id;
		synthiGME.guiSC.panels.do({|panel, i|
			if (i == numPanel, {
				panel.hasFocus = true;
			}, {
				panel.hasFocus = false;
			})
		})
	}


	resizePanel {arg factor;
		var factorW, factorH;
		if ((factor * zoomWLevel * viewSizes[0][1].width) > (Window.availableBounds.width * 1), {
			factorW = (Window.availableBounds.width * 1) /  (viewSizes[0][1].width);
		}, {factorW = factor * zoomWLevel});


		if ((factor * zoomWLevel * viewSizes[0][1].width) < (Window.availableBounds.width/4), {
			factorW = (Window.availableBounds.width/4) / viewSizes[0][1].width;
		});

		if ((factor * zoomHLevel * viewSizes[0][1].width) > (Window.availableBounds.height), {
			factorH = Window.availableBounds.height / viewSizes[0][1].width;
		},{
			factorH = factorW;
		});

		//[factorH, factorW].postln;

		viewSizes.do({|v|
			if (v[0].class === Window, {
				v[0].bounds_(Rect(
					left: origin.left,
					top: origin.top,
					width: v[1].width * factorW,
					height: v[1].height * factorH,
				))
			}, {
				v[0].bounds_(Rect(
					left: v[1].left * factorW,
					top: v[1].top * factorW,
					width: v[1].width * factorW,
					height: v[1].height * factorW,
				))
			})
		});
		zoomWLevel = factorW;
		zoomHLevel = factorH;

		this.goInside;
	}

	// lleva la ventana a un lugar visible (dentro de los límites de la pantalla)
	goInside {
		if ((window.bounds.left + window.bounds.width) > Window.availableBounds.width, {
			window.bounds = Rect (
				left: Window.availableBounds.width - window.bounds.width,
				top: window.bounds.top,
				width: window.bounds.width,
				height: window.bounds.height,
			)
		});
		if ((window.bounds.top + window.bounds.height) > Window.availableBounds.height, {
			window.bounds = Rect (
				left: window.bounds.left,
				top: Window.availableBounds.height - window.bounds.height,
				width: window.bounds.width,
				height: window.bounds.height,
			)
		});
	}


	parameterVisibility {|bool|
		viewSizes.do({|v|
			if(
				(v[0].class === Window)
				.or(v[0].class === CompositeView),
				{},
				{v[0].visible_(bool)}
			)
		})
	}


	// Hace visible o invisible los mandos de una ventana
	conmuteVisibility {
		var visible;
		viewSizes.do({|v|
			if(
				(v[0].class === Window)
				.or(v[0].class === CompositeView),
				{},
				{if(v[0].visible == true,
					{v[0].visible = false},
					{v[0].visible = true}
				)}
			)
		})
	}

	saveOrigin {
		origin = window.bounds;
	}

	goToOrigin {
		Routine ({
			var factor = origin.width / window.bounds.width;
			var rect = Rect(
				left: origin.left,
				top: origin.top,
				width: window.bounds.width,
				height: window.bounds.width,
			);
			window.bounds = rect;
			while({window.bounds != rect}, {wait(0.01)}); // Nos aseguramos que se realiza la primera operación antes de seguir
			this.resizePanel(factor);
		}).play(AppClock)
	}
}
