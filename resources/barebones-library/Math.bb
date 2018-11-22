class Math {
  function int max(int a, int b) {
    if (a > b) {
      return a;
    }
    return b;
  }

  function double abs(double a) {
    if (a < 0) {
      return -a;
    }
    return a;
  }

  function double pow(double a, double b) {
    return a ^ b;
  }

  function double sqrt(double radicand) {
    return radicand ^ 1.0 / 2.0;
  }

  function String binary(int dec) {
    var String bin = "";
    while (dec > 0) {
      if ((dec % 2) == 1) {
        bin = "1" ++ bin;
      } else {
        bin = "0" ++ bin;
      }
      dec = dec / 2;
    } loop;
    return bin;
  }

  function String toBase(int dec, int base) {
    if (base > 16) {
      return "Bases above hexadecimal [16] not supported";
    }

    var String str = "";
    while (dec > 0) {
      if ((dec % base) != 0) {
        if ((dec % base) == 10) {
          str = "a" ++ str;
        } else if ((dec % base) == 11) {
          str = "b" ++ str;
        } else if ((dec % base) == 12) {
          str = "c" ++ str;
        } else if ((dec % base) == 13) {
          str = "d" ++ str;
        } else if ((dec % base) == 14) {
          str = "e" ++ str;
        } else if ((dec % base) == 15) {
          str = "f" ++ str;
        } else {
          str = (dec % base) ++ str;
        }
      } else {
        str = "0" ++ str;
      }
      dec = dec / base;
    } loop;
    return str;
  }
}
