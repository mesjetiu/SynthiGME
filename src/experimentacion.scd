Quarks.install("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.uninstall("/home/carlos/Dropbox/Máster Arte Sonoro TFM/TFM/trabajo/TFM_Arte_Sonoro_Supercollider")

Quarks.clear

// no sé por qué no funciona tras instalarse desde Github...
Quarks.install("https://github.com/mesjetiu/TFM_Arte_Sonoro_Supercollider.git")

o = Synthi100(s, stereoBuses: [0,1]);
o.play
o.stop
(
o.setParameterOSC("/patchA/91/36", 0.2);
o.setParameterOSC("/patchA/92/37", 0.3);
)
o.setParameterOSC2("/osc/1/pulse/level", 9);
o.setParameterOSC2("/osc/1/pulse/shape", 9);
o.setParameterOSC2("/osc/1/sine/level", 4);
o.setParameterOSC2("/osc/1/sine/symmetry", 5);
o.setParameterOSC2("/osc/1/sawtooth/level", 2);
o.setParameterOSC2("/osc/1/triangle/level", 2);
o.setParameterOSC2("/osc/1/frequency", 2);






a = Array.fill2D(2,2,{0})
a.do({"h".postln})

"2".asInt

s.scope;

"/osc/1/triangle/level".split($/)
"/patchA/92/37".split($/)

a = [1,2,3].add(nil)
a.pop
a.reverse
a

s.quit({s.waitForBoot({"yaaa".postln})}, {s.waitForBoot({"yaaa".postln})})
s.ping
s.status(s)
s.quit



(
var string ="/osc/1/triangle/level";
var value = 10;
var splitted = string.split($/);
switch (splitted[1],
	"osc", {
		var parameter;
		parameter = splitted[3];
		if(splitted.size == 5, {
			splitted[4][0] = splitted[4][0].toUpper;
			parameter = parameter ++ splitted[4];
		});
		modulOscillators[splitted[2]].

})

)

a = [1,2,3,4,5]
a.copyRange(2)
a


y = [ 1, 2, 3, 4, 5 ];
y.takeAt(1);
y.postln;