include "resources/barebones-library/String.bb";

class ex {
  function void start() {
    print("Roman Numeral Interpreter");
    print();
    var String numeral;

    while (numeral != "q") {
      print("Write a legal Roman numeral:");
      numeral = next();
      var int num = parseNumeral(String.to_upper(numeral));
      print(numeral ++ " is equivalent to: " ++ num);
    } loop;
  }

  function int parseNumeral(String numeral) {
    var int sum = 0;
    var int done = 0;
    var bool iFlag = false;
    var bool vFlag = false;
    var bool xFlag = false;
    var bool lFlag = false;
    var bool cFlag = false;
    var bool dFlag = false;
    var bool mFlag = false;
    while (done < (#numeral)) {
      var String at = numeral @ done;
      var int after = done + 1;
      if (at == "I") {
        if (iFlag) {
          return -1;
        }
        if (after < #numeral) {
          iFlag = !(numeral@after == "I");
          if ((numeral@after == "X")) {
            sum = sum + 9;
            done = done + 1;
          } else if ((numeral@after == "V")) {
            sum = sum + 4;
            done = done + 1;
          } else {
            sum = sum + 1;
          }
        } else {
          sum = sum + 1;
        }
        // return sum;
      } else if (at == "V") {
        sum = sum + 5;
        if (vFlag) {
          return -1;
        }
        vFlag = true;
      } else if (at == "L") {
        sum = sum + 50;
        if (lFlag || iFlag || vFlag) {
          return -1;
        }
        lFlag = true;
      } else if (at == "D") {
        sum = sum + 500;
        if (dFlag || iFlag || vFlag || xFlag || lFlag) {
          return -1;
        }
        dFlag = true;
      } else if (at == "M") {
        sum = sum + 1000;
        if (mFlag || iFlag || vFlag || xFlag || lFlag || dFlag) {
          return -1;
        }
        if (after < #numeral) {
          mFlag = !(numeral@after == "M");
        }
      } else if (at == "X") {
        if (xFlag || vFlag) {
          return -1;
        }
        if (after < #numeral) {
          xFlag = !(numeral@after == "X");
          if ((numeral@after == "L")) {
            sum = sum + 40;
            done = done + 1;
          } else if ((numeral@after == "C")) {
            sum = sum + 90;
            done = done + 1;
          } else {
            sum = sum + 10;
          }
        } else {
          sum = sum + 10;
        }
      } else if (at == "C") {
        if (cFlag || iFlag || vFlag || lFlag) {
          return -1;
        }
        if (after < #numeral) {
          cFlag = !(numeral@after == "C");
          if ((numeral@after == "D")) {
            sum = sum + 400;
            done = done + 1;
          } else if ((numeral@after == "M")) {
            sum = sum + 900;
            done = done + 1;
          } else {
            sum = sum + 100;
          }
        } else {
          sum = sum + 100;
        }
      } else {
        return -1;
      }
      done = done + 1;
    } loop;
    return sum;
  }
}