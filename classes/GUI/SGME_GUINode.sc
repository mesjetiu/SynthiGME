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

SGME_GUINode {
	var <view;
	var <value;
	classvar imageHole, imageWhite, imageYellow;
	classvar installedPath;
	classvar synthiGME;
	var <visible; // atributo "dummy" para el cambio de visibilidad de los witches en los paneles. No afecta a los Nodos pero se incluye el atributo para cambios generales en la visibilidad de GUI.

	*new {arg synthi, parent, bounds, stringOSC;
		^super.new.init(synthi, parent, bounds, stringOSC);
	}

	init {|synthi, parent, bounds, stringOSC|
		synthiGME = synthi;
		installedPath = Quarks.quarkNameAsLocalPath("SynthiGME");
		imageHole = Image(installedPath +/+ "classes" +/+ "GUI" +/+ "images" +/+ "widgets" +/+ "patchbay_hole");
		imageWhite = Image(installedPath +/+ "classes" +/+ "GUI" +/+ "images" +/+ "widgets" +/+ "patchbay_white_pin");
		imageYellow = Image(installedPath +/+ "classes" +/+ "GUI" +/+ "images" +/+ "widgets" +/+ "patchbay_yellow_pin");
		value = 0;
		view = View(parent, bounds)
		.setBackgroundImage(imageHole, 10)
		.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			buttonNumber.switch(
				0, {
					if ((value==1) && modifiers.isCtrl.not, { // Se hace clic en un nodo encendido
						value=0;
						view.setBackgroundImage(imageHole, 10);
					}, {
						if ((value==0) && modifiers.isCtrl.not,{ // Se hace click en un nodo apagado
							value=1;
							view.setBackgroundImage(imageWhite, 10);
						}, {
							if ((value==1) && modifiers.isCtrl, { // Se hace Ctrl+click en un nodo encendido
								value=(-1);
								view.setBackgroundImage(imageYellow, 10);
							}, {
								if ((value==0) && modifiers.isCtrl, { // Se hace Ctrl+click en un nodo apagado
									value=(-1);
									view.setBackgroundImage(imageYellow, 10);
								}, {
									if ((value==(-1)) && modifiers.isCtrl.not, { // Se hace Ctrl+click en un nodo con pin a medias
										value=1;
										view.setBackgroundImage(imageWhite, 10);
									}, {
										if ((value==(-1)) && modifiers.isCtrl, { // Se hace Ctrl+click en un nodo con pin a medias
											value=0;
											view.setBackgroundImage(imageHole, 10);
					})})})})})});
					synthiGME.setParameterOSC(
						string: stringOSC,
						value: value,
						addrForbidden: \GUI,
					);

				}, // click izquierdo
				1, {}, // click derecho, no implementado
			)
		});
	}

	visible_ {|option|
		view.visible = option;
	}

	enable_ {|option| // habilita y deshabilita el nodo
		view.enabled = option;
		if (option==false, {
			if(value==1, {
				view.setBackgroundImage(imageWhite, 10, alpha: 0.3);
			}, {
				view.setBackgroundImage(imageHole, 10, alpha: 0.3);
			})
		}, {
			if(value==1, {
				view.setBackgroundImage(imageWhite, 10, alpha: 1);
			}, {
				view.setBackgroundImage(imageHole, 10, alpha: 1);
			})
		})
	}
/*
	value_ {|valueIn| // Desde setParameterOSC() se cambia el valor del nodo
		value = valueIn;
		this.enable_(value == 1)
	}
	*/

	bounds {
		^view.bounds;
	}

	bounds_ {|bounds|
		view.bounds = bounds;
	}

	value_ {|val|
		value = val;
		switch(val)
		{ 1 } { view.setBackgroundImage(imageWhite, 10) }
		{ 0 } { view.setBackgroundImage(imageHole, 10) }
		{ (-1) } { view.setBackgroundImage(imageYellow, 10) }
		{ view.setBackgroundImage(imageHole, 10) };
	}
}