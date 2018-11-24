include "resources\barebones-library\String.bb";

class FlipProgram {
    function void start() {
        var String toFlip = next();
        print(String.flip(toFlip));
    }
}