/*

This is an example of using the Mico with SuperCollider.
It's a simple setup with four synthesizers, each having their frequency controlled by the Mico controller.

*/
(
// Connect Mico
m = Mico.new();

// Set up 4 synths
4.do{|i|
    var synthName = "saw%".format(i).asSymbol;

    Ndef(synthName, {|amp=0.4, freq=100, cutoff=1000, res=0.75, pan=0|
    var sig = SawDPW.ar(freq);
    sig = DFM1.ar(sig, cutoff, res);

    Pan2.ar(sig * amp, pan);

    }).set(
        \freq, i + 1 * 83,
        \cutoff, 750,
        \pan, i.linlin(0,3,-1.0,1.0)
    ).play;

    // Register callback function
    m.setEncoderFunc(i, {|val ... otherthings|
        Ndef(synthName).set(\freq, val.linexp(0.0000001,1.0, 40.0, 500))
    })
};

)
