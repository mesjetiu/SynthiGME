SGME_ContextualMenu {
	*contextualMenu {|synthiGME, view, x, y, modifiers, buttonNumber|
		// si se hace un solo click...
		var silenciarString = switch (synthiGME.server.volume.isMuted) {true} {"Desilenciar"} {false} {"Silenciar"};
		var grabarAudio = switch (synthiGME.server.isRecording) {true} {"Terminar grabación de audio"} {false} {"Iniciar grabación de audio"};
		var grabarEvents = "";
		var postWindow = switch (MessageRedirector.window.isNil) {false} {"Cerrar Post Window"} {true} {"Abrir Post Window"};
		var playEvents = "";

		if (synthiGME.eventRecorder.isRecording) {grabarEvents = "Terminar grabación de eventos"};
		if (synthiGME.eventRecorder.isRecording.not && synthiGME.eventRecorder.isPlaying) {
			playEvents = "Stop eventos";
		};
		if (synthiGME.eventRecorder.isRecording.not && synthiGME.eventRecorder.isPlaying.not) {
			playEvents = "Play eventos";
			grabarEvents = "Iniciar grabación de eventos"
		};
		buttonNumber.switch(
			0, {}, // botón izquierdo
			1, {
				Menu(
					//MenuAction("Zoom In", { this.resizePanel(factor) }),
					//MenuAction("Zoom Out", { this.resizePanel(1/factor) }),
					//MenuAction("Invisible", { window.visible = false }),
					MenuAction("Abrir patch", { synthiGME.loadStateGUI }),
					MenuAction("Guardar patch", { synthiGME.saveStateGUI }),
					MenuAction("Resetear patch", { synthiGME.resetState }),
					/*MenuAction(grabarEvents, {
						if (synthiGME.eventRecorder.isRecording) {
							synthiGME.eventRecorder.stopRecording;
						} {
							synthiGME.eventRecorder.startRecording;
						}
					}),*/
					/*MenuAction(playEvents, {
						if (synthiGME.eventRecorder.isPlaying) {
							synthiGME.eventRecorder.stop;
						} {
							synthiGME.eventRecorder.play;
						}
					}),*/
					MenuAction("Abrir grabadora de eventos", {
						synthiGME.eventRecorder.makeWindow;
					}),
			/*		MenuAction(grabarAudio, {
						if (synthiGME.server.isRecording) {
							synthiGME.server.stopRecording;
						} {
							synthiGME.server.record;
						}
					}),
			*/
					MenuAction(silenciarString, {
						if (synthiGME.server.volume.isMuted,
							{synthiGME.server.volume.unmute},
							{synthiGME.server.volume.mute}
						)
					}),
					MenuAction("Abrir controles del servidor de audio", {
						synthiGME.server.makeGui;
					}),
					MenuAction(postWindow, {
						if (MessageRedirector.window.isNil) {
							MessageRedirector.createWindow(synthiGME);
						} {
							MessageRedirector.closeWindow;
						}
					}),
					MenuAction("Ver/ocultar atajos de teclado", {
						synthiGME.guiSC.helpWindow.conmuteVisibility;
					}),
					MenuAction("Acerca de Synthi GME", { SGME_GUIAbout.makeWindow }),
					MenuAction.separator(),
					MenuAction("Salir", { synthiGME.close }),
				).palette_(QPalette.dark).front;
			}, // botón derecho
		)
	}
}