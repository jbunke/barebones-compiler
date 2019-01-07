class String {

  function String flip(String s) {
    var String flip_s = "";
    var int index = #s - 1;
    while (index >= 0) {
      flip_s = flip_s ++ (s@index);
      index = index - 1;
    } loop;
    return flip_s;
  }

  function String remove(String l, String s) {
    var String newS = "";
    var int index = 0;
    while (index < #s) {
      var String char = s@index;
      if (char != l) {
        newS = newS ++ char;
      }
      index = index + 1;
    } loop;
    return newS;
  }

  function int indexOf(String of, String in) {
    var int index = 0;
    var int match = 0;
    while (index < (#in)) {
      if (of @ match == in @ (index + match)) {
        match = match + 1;
        if (match == (#of)) {
          return index;
        }
      } else {
        match = 0;
        index = index + 1;
      }
    } loop;
    return -1;
  }

  function bool contains(String of, String in) {
      var int index = 0;
      var int match = 0;
      while (index < (#in)) {
        if (of @ match == in @ (index + match)) {
          match = match + 1;
          if (match == (#of)) {
            return true;
          }
        } else {
          match = 0;
          index = index + 1;
        }
      } loop;
      return false;
    }

  function String substring(String original, int start_index) {
    var String res = "";
    var int index = start_index;
    while (index < (#original)) {
      res = res ++ (original@index);
      index = index + 1;
    } loop;
    return res;
  }

  function String substring_with_end(String original, int start_index, int omit_from) {
    var String res = "";
    var int index = start_index;
    while ((index < (#original)) && (index < omit_from)) {
      res = res ++ (original@index);
      index = index + 1;
    } loop;
    return res;
  }

  function String to_upper(String original) {
    var String res = "";
    var int index = 0;

    while (index < (#original)) {
      var String charAt = original@index;

      if (charAt == "a") {
        res = res ++ "A";
      } else if (charAt == "b") {
        res = res ++ "B";
      } else if (charAt == "c") {
        res = res ++ "C";
      } else if (charAt == "d") {
        res = res ++ "D";
      } else if (charAt == "e") {
        res = res ++ "E";
      } else if (charAt == "f") {
        res = res ++ "F";
      } else if (charAt == "g") {
        res = res ++ "G";
      } else if (charAt == "h") {
        res = res ++ "H";
      } else if (charAt == "i") {
        res = res ++ "I";
      } else if (charAt == "j") {
        res = res ++ "J";
      } else if (charAt == "k") {
        res = res ++ "K";
      } else if (charAt == "l") {
        res = res ++ "L";
      } else if (charAt == "m") {
        res = res ++ "M";
      } else if (charAt == "n") {
        res = res ++ "N";
      } else if (charAt == "o") {
        res = res ++ "O";
      } else if (charAt == "p") {
        res = res ++ "P";
      } else if (charAt == "q") {
        res = res ++ "Q";
      } else if (charAt == "r") {
        res = res ++ "R";
      } else if (charAt == "s") {
        res = res ++ "S";
      } else if (charAt == "t") {
        res = res ++ "T";
      } else if (charAt == "u") {
        res = res ++ "U";
      } else if (charAt == "v") {
        res = res ++ "V";
      } else if (charAt == "w") {
        res = res ++ "W";
      } else if (charAt == "x") {
        res = res ++ "X";
      } else if (charAt == "y") {
        res = res ++ "Y";
      } else if (charAt == "z") {
        res = res ++ "Z";
      } else {
        res = res ++ charAt;
      }

      index = index + 1;
    } loop;

    return res;
  }

  function String to_lower(String original) {
    var String res = "";
    var int index = 0;

    while (index < (#original)) {
      var String charAt = original@index;

      if (charAt == "A") {
        res = res ++ "a";
      } else if (charAt == "B") {
        res = res ++ "b";
      } else if (charAt == "C") {
        res = res ++ "c";
      } else if (charAt == "D") {
        res = res ++ "d";
      } else if (charAt == "E") {
        res = res ++ "e";
      } else if (charAt == "F") {
        res = res ++ "f";
      } else if (charAt == "G") {
        res = res ++ "g";
      } else if (charAt == "H") {
        res = res ++ "h";
      } else if (charAt == "I") {
        res = res ++ "i";
      } else if (charAt == "J") {
        res = res ++ "j";
      } else if (charAt == "K") {
        res = res ++ "k";
      } else if (charAt == "L") {
        res = res ++ "l";
      } else if (charAt == "M") {
        res = res ++ "m";
      } else if (charAt == "N") {
        res = res ++ "n";
      } else if (charAt == "O") {
        res = res ++ "o";
      } else if (charAt == "P") {
        res = res ++ "p";
      } else if (charAt == "Q") {
        res = res ++ "q";
      } else if (charAt == "R") {
        res = res ++ "r";
      } else if (charAt == "S") {
        res = res ++ "s";
      } else if (charAt == "T") {
        res = res ++ "t";
      } else if (charAt == "U") {
        res = res ++ "u";
      } else if (charAt == "V") {
        res = res ++ "v";
      } else if (charAt == "W") {
        res = res ++ "w";
      } else if (charAt == "X") {
        res = res ++ "x";
      } else if (charAt == "Y") {
        res = res ++ "y";
      } else if (charAt == "Z") {
        res = res ++ "z";
      } else {
        res = res ++ charAt;
      }

      index = index + 1;
    } loop;

    return res;
  }
}
