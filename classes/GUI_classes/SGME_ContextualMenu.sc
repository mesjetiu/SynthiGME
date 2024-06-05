SGME_ContextualMenu {
	*contextualMenu {|synthiGME, view, x, y, modifiers, buttonNumber|
		// si se hace un solo click...
		var silenciarString = switch (synthiGME.server.volume.isMuted) {true} {"Desilenciar"} {false} {"Silenciar"};
		var grabarAudio = switch (synthiGME.server.isRecording) {true} {"Terminar grabación de audio"} {false} {"Iniciar grabación de audio"};
		var grabarEvents = "";
		var postWindow = switch (MessageRedirector.window.isNil) {false} {"Cerrar Post Window"} {true} {"Abrir Post Window"};
		var playEvents = "";

		if (SGME_EventRecorder.record) {grabarEvents = "Terminar grabación de eventos"};
		if (SGME_EventRecorder.record.not && SGME_EventRecorder.isPlaying) {
			playEvents = "Stop eventos";
		};
		if (SGME_EventRecorder.record.not && SGME_EventRecorder.isPlaying.not) {
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
					MenuAction("Reiniciar Synthi", { synthiGME.restartState }),
					MenuAction(grabarEvents, {
						if (SGME_EventRecorder.record) {
							SGME_EventRecorder.stopRecording;
						} {
							SGME_EventRecorder.initRecording;
						}
					}),
					MenuAction(playEvents, {
						if (SGME_EventRecorder.isPlaying) {
							SGME_EventRecorder.stop;
						} {
							SGME_EventRecorder.play;
						}
					}),
					MenuAction(grabarAudio, {
						if (synthiGME.server.isRecording) {
							synthiGME.server.stopRecording;
						} {
							synthiGME.server.record;
						}
					}),
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
					MenuAction("Salir", { synthiGME.close }),
					MenuAction("Acerca de Synthi GME", { SGME_GUIAbout.makeWindow }),
				).palette_(QPalette.dark).front;
			}, // botón derecho
		)
	}
}