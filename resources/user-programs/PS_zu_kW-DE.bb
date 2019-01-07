include "resources\barebones-library\String.bb";

class FlipProgram {
    function void start() {
        // PS -> kW: 1.36

        print("Geben Sie die Leistung in PS ein:");
        var int ps = next();

        var int kw = ps * 1.36;
        print("Das sind umgerechnet " ++ kw ++ " kW");
    }
}