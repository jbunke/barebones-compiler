include "resources\barebones-library\String.bb";
include "resources\barebones-library\Math.bb";
include "resources\barebones-library\Test.bb";

class TestFile1 {

  function Test testFunction(int num, String five) {
    var String whileImAtIt;
    whileImAtIt = "this is going to be interesting";
  }

  function void start() {
    var bool r1 = String.contains("contains", "con");
    var bool r2 = String.contains("plushie", "minus");
    var bool r3 = String.contains("magnificent", "cent");

    Test.assert(r1, true, "'contains' contains 'con'");
    Test.assert(r2, false, "'plushie' does not contain 'minus'");
    Test.assert(r3, true, "'magnificent' contains 'cent'");

    var String sub = String.substring("remedial", 4);
    var bool sub_check = sub == "dial";
    Test.assert(sub_check, true, "Substring of indices 4 to 7 of 'remedial' is 'dial'");

    var String uppercase = String.to_upper("Print in uppercase.");
    print(uppercase);

    var String lowercase = String.to_lower("Print in lowercase.");
    print(lowercase);

    // print_binary_numbers();
  }

  function void print_binary_numbers() {
    var int num = 0;

    while (num < 100) {
      var String bin_num = Math.binary(num);
      print(num ++ " in binary: " ++ bin_num);
      num = num + 1;
    } loop;
  }
}
