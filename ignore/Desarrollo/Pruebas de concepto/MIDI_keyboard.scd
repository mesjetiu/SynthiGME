// Experimentos para hacer los teclados


s.options.blockSize  // Ajusta el tamaño del buffer
s.boot; // Arranca el servidor


s.latency = 0.03 // modificar a voluntad. Se puede modificar con el servidor arrancado...
s.latency


(
var note = 60;
var veloc = 256;
(
type: \note,  // Tipo de evento 'note' que incluye una envolvente por defecto
midinote: note,  // Número MIDI de la nota
dur: 0.5,  // Duración del sonido en segundos
amp: veloc * 0.003  // Amplitud basada en la velocidad de la tecla
).play;
)




// Inicialización de MIDI
MIDIClient.init;
MIDIIn.connectAll;

// Definición de la acción al recibir notas MIDI
(
MIDIdef.noteOn(\midiNoteOn, { |veloc, note, chan, src|
	// Crea y toca un evento sonoro
	[veloc, note, chan, src].postln;
	(
		type: \note,  // Tipo de evento 'note' que incluye una envolvente por defecto
		midinote: note,  // Número MIDI de la nota
		dur: 0.5,  // Duración del sonido en segundos
		amp: veloc * 0.003  // Amplitud basada en la velocidad de la tecla
	).play;
}
)
)


// Por seguridad, se puede agregar una definición para cuando la nota se apague
(
MIDIdef.noteOff(\midiNoteOff, { |veloc, note, chan, src|
	[veloc, note, chan, src].postln;
	// Aquí podrías definir alguna acción cuando la nota se libere, si es necesario
});
)