// Ejemplo exhaustivo de uso de ScopeView



(
s.waitForBoot({
    var func, sdef1, sdef2, syn1, syn2,startButton ;
    f = Buffer.alloc(s,1024,2);
    b = Bus.audio(s,1);

    w = Window("Scope", Rect(150, Window.screenBounds.height-500,790,400)).front;
    c = ScopeView(w,Rect(10,10,380,380)); // this is SCScope
    c.bufnum = f.bufnum;

    // IMPORTANT
    c.server = s;

    v = CompositeView(w,Rect(400,10,380,380)).background_(Color.rand(0.7));
    v.decorator = n = FlowLayout(v.bounds, margin: 0@0, gap: 5@5);

    a = StaticText(v, Rect(20, 70, 90, 20)).string_(" xZoom = 1").background_(Color.rand);
    m = Slider(v, Rect(20, 60, 285, 20)).background_(a.background).action_({func.value}).value_(0.5);
    d = StaticText(v, Rect(20, 70, 90, 20)).string_(" yZoom = 1").background_(Color.rand);
    g = Slider(v, Rect(20, 60, 285, 20)).background_(d.background).action_({func.value}).value_(0.5);

    h = StaticText(v, Rect(20, 70, 90, 20)).string_(" x = 0").background_(Color.rand);
    i = Slider(v, Rect(20, 60, 285, 20)).background_(h.background).action_({func.value}).value_(0.5);

    Button(v, Rect(0,0,380, 20))
    .states_([["waveColors = [ Color.rand, ... ]",Color.black,Color.rand]])
    .action_({c.waveColors = [Color.rand,Color.rand]});

    Button(v, Rect(0,0,380, 20))
    .states_([[" background = Color.rand(0.1,0.3) ",Color.black,Color.rand]])
    .action_({c.background = Color.rand(0.1,0.3) });

    t = Button(v, Rect(0,0,380, 20))
    .states_([["Current style is 0",Color.black,Color.rand],
        ["Current style is 1",Color.black,Color.rand],
        ["Current style is 2",Color.black,Color.rand]])
    .action_({func.value});

    func = {
        c.xZoom = ([0.25, 10, \exp, 1/8, 1].asSpec.map(m.value)); a.string = " xZoom = %".format(c.xZoom);
        c.yZoom = ([0.25, 10, \exp, 1/8, 1].asSpec.map(g.value)); d.string = " yZoom = %".format(c.yZoom);
        c.x = ([ -1024,1024, \linear, 1/8, 1].asSpec.map(i.value)); h.string = " x = %".format(c.x);
        c.style=t.value
    };

    startButton = Button.new(v, Rect(0,0,380, 50))
    .states_([["Start Sound",Color.black,Color.green],["Stop Sound",Color.black,Color.red]]).action_({});


    startButton.action_{
        (startButton.value==1).if{
            syn1 = SynthDef("test1", { arg bus, bufnum;
                var z;
                z = In.ar(bus,2);
                // ScopeOut2 writes the audio to the buffer
                // IMPORTANT - ScopeOut2, not ScopeOut
                ScopeOut2.ar(z, bufnum);
                Out.ar(0,z);
            }).play(
                RootNode(s),
                [\bus,b.index, \bufnum, f.bufnum] ,
                \addToTail // make sure it goes after what you are scoping
            );

            // making noise onto the buffer
            syn2 = SynthDef("test2", { arg bus;
                var z;
                z = PMOsc.ar([300,250],*SinOsc.ar([0.027,0.017])*pi) * 0.1;
                Out.ar(bus, z);
            }).play(s,[\bus,b.index]);

        }{syn1.free; syn2.free};
    };

    // IMPORTANT
    c.start;

    w.onClose={syn1.free; syn2.free; b.free; f.free};
    CmdPeriod.doOnce({w.close});
})
)