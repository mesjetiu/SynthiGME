a = Server(\server_a, NetAddr("127.0.0.1", port: 57111))

b = Server(\server_b, NetAddr("127.0.0.1", port: 57112))

a.boot

b.boot

rand(3)

address = NetAddr.new("127.0.0.1", port);

					// Intentar conectar a un servidor remoto
~remoteServer = Server.remote(\temp, NetAddr("127.0.0.1", port: 57111));
~remoteServer.isRunning

Server.all

SynthiGME()

s.ping
(
fork {
	s.wait();
	"hola".postln
}.play
)

OSCdef(\captureIP)

// example usage:
// on machine running the server

u = Date.seed;
r = ("server_" ++ Date.seed.asString).asSymbol
a = Server(r, NetAddr("127.0.0.1", port: 57166))
a.options.bindAddress = "0.0.0.0"; // allow connections from any address
a.options.maxLogins = 2; // set to correct number of clients
a.boot;
a.makeWindow


// on remote machine connecting to server

o = ServerOptions.new;
o.maxLogins = 2;
u = Date.seed;
r = ("remote_" ++ Date.seed.asString).asSymbol
t = Server.remote(r, NetAddr("127.0.0.1", 57166), o); // set to correct address and port
// info about returned client ID should be posted in the post window
t.serverRunning
t.notify = false
t.makeWindow; // make a window for monitoring


t = Server.remote(r, NetAddr("127.0.0.1", 57164), o); // set to correct address and port
