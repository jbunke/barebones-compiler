package compiler_structures;

public class Range {
  public final int from;
  public final int to;

  public Range(int from, int to) {
    assert (to >= from) : "Code block range is invalid.";
    this.from = from;
    this.to = to;
  }

  boolean isWithin(int line) {
    return (line >= from && line <= to);
  }
}
