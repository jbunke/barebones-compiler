package utilities;

public class Line {
  private String value;

  public Line(String value) {
    this.value = value;
  }

  public Line() { this.value = ""; }

  public void trim() {
    if (!value.equals("") && value.replaceAll(" ", "").equals("")) {
      value = " ";
    } else {
      if (value.length() > 0) {
        while (value.charAt(0) == ' ') {
          value = value.substring(1);
        }

        while (value.length() > 0 &&
                (value.charAt(value.length() - 1) == ' ')) {
          value = value.substring(0, value.length() - 1);
        }
      }
    }
  }

  public void set(String value) {
    this.value = value;
  }

  public void shiftPast(String s) {
    assert (value.contains(s));
    value = value.substring(value.indexOf(s) + s.length());
  }

  public String substring(int beginIndex) {
    return value.substring(beginIndex);
  }

  public String substring(int beginIndex, int endIndex) {
    return value.substring(beginIndex, endIndex);
  }

  public String get() {
    return value;
  }

  public int length() { return value.length(); }
}
