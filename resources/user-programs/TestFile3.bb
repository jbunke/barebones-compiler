include "resources\barebones-library\String.bb";
include "resources\barebones-library\Math.bb";

class TestFile3 {
  function void start() {
    var int nums = 1;

    while (nums <= 100) {
      print("Number: " ++ nums);

      var int cycleBases = 2;

      while (cycleBases <= 16) {
        print(nums ++ " in base " ++ cycleBases ++ ": " ++ Math.toBase(nums, cycleBases));
        cycleBases = cycleBases + 1;
      } loop;

      nums = nums + 1;
    } loop;
  }
}
