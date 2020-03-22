SGME_GUIPanelPatchbay : SGME_GUIPanel {

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
		// A implementar en las clases que heredan
	}
}