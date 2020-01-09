/*

Experimentos para ejecutar un archivo .scd desde la terminal.



La orden es:
sclang ./S100.scd

*/

Synthi100(standalone:true).run().pairDevice;





// Código de pruebas de interfaz gráfica:

a = {
	var w;
w = Window("my name is... panel", Rect(128, 64, 340, 360));

32.do({ arg i;
    b = Button(w, Rect(rrand(20,300),rrand(20,300), 75, 24));
    b.states = [["Start "++i, Color.black, Color.rand],
        ["Stop "++i, Color.white, Color.red]];
});
w.front;

};


a.value;