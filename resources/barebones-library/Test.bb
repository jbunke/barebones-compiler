class Test {

  function void assert(bool expression, bool t_or_f, String code) {
    if (expression == t_or_f) {
      print("[" ++ code ++ "] Assertion passed.");
    } else {
      print("[" ++ code ++ "] Assertion failed.");
      print("Expression: " ++ expression);
      print("Assertion expected: " ++ t_or_f);
    }
  }

  function void assert(bool expression, String code) {
    if (expression) {
      print("[" ++ code ++ "] Assertion passed.");
    } else {
      print("[" ++ code ++ "] Assertion failed.");
    }
  }
}
