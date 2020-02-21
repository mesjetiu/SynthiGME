S100_GUIPanelPatchbay : S100_GUIPanel {

	var visibleNodes = true;
	var nodes;

	*new {|synthi, parameters|
		^super.new.init(synthi, parameters);
	}

	init {|synthi, parameters|
		super.init(synthi, parameters);
		nodes = Dictionary.new;
	}

	enableNodes { // Enable o disable los nodos según si el sintetizador los usa o no.
		var option;
		if (visibleNodes == true, {option = false; visibleNodes = false}, {option = true; visibleNodes = true});
		nodes.values.do({|node|
			var coordenates = nodes.findKeyForValue(node);
			var ver, hor;
			ver = coordenates[1] - 1;
			hor = coordenates[0] - 1;
			if ((synthi100.modulPatchbayAudio.inputsOutputs[ver] == nil)
				.or(synthi100.modulPatchbayAudio.inputsOutputs[hor] == nil), {
					node.visible_(option);
				}, {
					node.visible_(true);
				}
			);
		})
	}
}