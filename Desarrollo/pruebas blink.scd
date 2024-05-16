(
var window, size = 32; // try different sizes - from 15 to 200 or more!
window = Window.new("Knob", Rect(640,630,270,70)).front;
k = SGME_Knob.new(window, Rect(20, 10, size, size)).background_(Color.red(alpha: 0.8));
//k.color[1] = Color.gray(alpha:0);
)

k.value
k.value = 0.25
k.valueAction = 0.125

// modes
k.mode = \vert;
k.mode = \horiz;
k.mode = \round; // default

k.visible
k.visible = false
k.visible = true
k.enabled = false
k.enabled_(true)
k.canFocus = false
k.canFocus = true

k.diHola
(
k.value = 0.2;
k.value = 0.5;
k.value = 0.3;
k.value = 0.1;
k.value = 0.8;
k.value = 0.9;
k.value = 0.2;
)