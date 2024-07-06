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
	classvar synthiGME = nil;
	classvar imagesPath = nil;
	var <visible; // atributo "dummy" para el cambio de visibilidad de los witches en los paneles. No afecta a los Nodos pero se incluye el atributo para cambios generales en la visibilidad de GUI.
	var <tooltipHandler;
	var <tooltip; // true o false

	*initClass {
		Class.initClassTree(SGME_Path);
		Class.initClassTree(SGME_TooltipHandler);
	}

	*loadImages {
		imagesPath = SGME_Path.imagesPath;
		imageHole = Image(imagesPath +/+ "widgets" +/+ "patchbay_hole");
		imageWhite = Image(imagesPath +/+ "widgets" +/+ "patchbay_white_pin");
		imageYellow = Image(imagesPath +/+ "widgets" +/+ "patchbay_yellow_pin");
	}

	*new {arg synthi, parent, bounds, stringOSC, min = 0, max = 1, funcParam = nil, tooltipEnable = true, tooltipTextFunc = {};
		if (synthiGME.isNil) {synthiGME = synthi};
		^super.new.init(parent, bounds, stringOSC, min, max, funcParam, tooltipEnable, tooltipTextFunc);
	}

	init {|parent, bounds, stringOSC, min, max, funcParam, tooltipEnable, tooltipTextFunc|
		value = 0;

		tooltip = tooltipEnable;

		view = View(parent, bounds)
		.setBackgroundImage(imageHole, 10)
		.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			buttonNumber.switch(
				0, {
					if ((value>0) && modifiers.isCtrl.not, { // Se hace clic en un nodo encendido
						value=0;
						view.setBackgroundImage(imageHole, 10);
					}, {
						if ((value==0) && modifiers.isCtrl.not,{ // Se hace click en un nodo apagado
							value=1;
							view.setBackgroundImage(imageWhite, 10);
						}, {
							if ((value>0) && modifiers.isCtrl, { // Se hace Ctrl+click en un nodo encendido
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
			);

			if (tooltip) {
				tooltipHandler.updateTooltip();
			}
		});
		if (tooltip) {
			tooltipHandler = SGME_TooltipHandler.new(view, min, max, funcParam, prefix: "Estado:", funcParam: {}, funcMakeText: tooltipTextFunc);
		};
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

	bounds {
		^view.bounds;
	}

	bounds_ {|bounds|
		view.bounds = bounds;
	}

	value_ {|val|
		value = val.clip(-1,1);
		case
		{ val>0 } { view.setBackgroundImage(imageWhite, 10) }
		{ val==0 } { view.setBackgroundImage(imageHole, 10) }
		{ val<0 } { value = -1; view.setBackgroundImage(imageYellow, 10) }
		{ view.setBackgroundImage(imageHole, 10) };
	}

	/**isQuarkInstalled {
		var quarkName = "SynthiGME";
		^Quarks.isInstalled(quarkName)
	}

	*getAppPath {
		var name = "SynthiGME";
		if (SGME_GUINode.isQuarkInstalled(name)) {
			var quarkPath = Quarks.quarkNameAsLocalPath(name);
			"Quark encontrado en: %".format(quarkPath).postln;
			^quarkPath
		} { // Si no es Quark, entonces es extensión
			//if (SynthiGME.isExtensionInstalled.(name)) {
			var userExtensionPath = Platform.userExtensionDir +/+ name;
			var systemExtensionPath = Platform.systemExtensionDir +/+ name;
			var extensionPath = if (File.existsCaseSensitive(userExtensionPath +/+ "SynthiGME.quark")) {
				userExtensionPath
			} {
				systemExtensionPath
			};
			"Extensión encontrada en: %".format(extensionPath).postln;
			^extensionPath
			/*} {
			"Ni Quark ni Extensión encontrados con el nombre: %".format(name).postln;
			nil
			}*/
		}
	}*/
}