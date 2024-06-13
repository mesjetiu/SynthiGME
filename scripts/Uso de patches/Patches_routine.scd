o = SynthiGME();


~patchPath = "/home/carlos/Dropbox/SC Quarks/SynthiGME/Patches";

o.restartState

(
r = Routine {
	loop {
		/*{o.loadState(~patchPath, "old_computer.spatch", true)}.defer();
		5.wait;*/
		{o.loadState(~patchPath, "pulsos_reverberantes.spatch",  true)}.defer();
		5.wait;
		{o.loadState(~patchPath, "pulsos_reverberantes_02.spatch",  true)}.defer();
		5.wait;
		{o.loadState(~patchPath, "pulsos_reverberantes.spatch",  true)}.defer();
		5.wait;
	}
}
)

r.play
r.stop

o.close;