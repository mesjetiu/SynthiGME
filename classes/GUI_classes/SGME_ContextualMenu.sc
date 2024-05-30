SGME_ContextualMenu {
	*contextualMenu {|synthiGME, view, x, y, modifiers, buttonNumber|
		// si se hace un solo click...
		var silenciarString = switch (synthiGME.server.volume.isMuted) {true} {"Desilenciar"} {false} {"Silenciar"};
		var grabarString = switch (synthiGME.server.isRecording) {true} {"Terminar grabación"} {false} {"Iniciar grabación"};
		var postWindow = switch (MessageRedirector.window.isNil) {false} {"Cerrar Post Window"} {true} {"Abrir Post Window"};
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
					MenuAction(grabarString, {
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
						if (synthiGME.guiSC.helpWindow.window.visible == true, {
							synthiGME.guiSC.helpWindow.window.visible = false
						}, {synthiGME.guiSC.helpWindow.window.visible = true
						})
					}),
					MenuAction("Salir", { synthiGME.close }),
					MenuAction("Acerca de Synthi GME", { SGME_GUIAbout.makeWindow }),
				).front;
			}, // botón derecho
		)
	}
}