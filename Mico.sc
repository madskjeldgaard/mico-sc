/*
*

// Example:
(
m = Mico.new();
m.setEncoderFunc(0, {|...vals| [1, vals].postln; });
m.setEncoderFunc(1, {|...vals| [1, vals].postln; });
m.setEncoderFunc(2, {|...vals| [1, vals].postln; });
m.setEncoderFunc(3, {|...vals| [1, vals].postln; });
)

*/
Mico {
    var <cc14s;

	*new {|useDefaults=true|
		^super.new.init(useDefaults)
	}

    init{|useDefaults|

        this.connect();

        cc14s = [
            CC14.new(14, 46, 1),
            CC14.new(15, 47, 1),
            CC14.new(16, 48, 1),
            CC14.new(17, 49, 1),
        ];

        if(useDefaults, {
            cc14s.do{|cc14|
                cc14.func_({|...vals| vals.postln })
            }
        })

    }

    setEncoderFunc{|encoderNum, func|
        if(encoderNum < 0 or: { encoderNum > cc14s.size }, { "Incorrect encoder number".error }, {
            cc14s[encoderNum].func_(func)
        });
    }

    controllerName{
        ^"Mico"
    }

    // An overengineered way of connecting only this controller to SuperCollider. Much faster (at least on Linux) than connecting all.
    connect{

        // Used to hack MIDIIn to say whether our controller is connected or not
        var connectMethod = "is%Connected".format(this.controllerName).asSymbol;

        // Connect midi controller
        if(MIDIClient.initialized.not, {
            "MIDIClient not initialized... initializing now".postln;
            MIDIClient.init;
        });

        // This ratsnest connects only this controller and not all, which is much faster than the latter.
        MIDIClient.sources.do{|src, srcNum|
            if(src.device == this.controllerName.asString, {
                if(try{MIDIIn.connectMethod}.isNil, {
                    var isSource = MIDIClient.sources.any({|e|
                        e.device==this.controllerName.asString
                    });

                    if(isSource, {
                            "Connecting %".format(this.controllerName).postln;
                            MIDIIn
                            .connect(srcNum, src)
                            .addUniqueMethod(connectMethod, {
                                true
                            }
                        )
                    });
                }, {
                    "% is already connected... (device is busy)".format(
                        this.controllerName
                    ).warn
                });
            });
        };
    }

}
