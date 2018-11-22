package compiler_structures;

public class CodeBlock {
  private Range range;
  private CodeBlock parent;
  private boolean satisfiedCond = false;

  public CodeBlock(int from, int to) {
    this.range = new Range(from, to);
    this.parent = null;
    this.satisfiedCond = false;
  }

  public CodeBlock(int from, int to, CodeBlock parent) {
    this.range = new Range(from, to);
    this.parent = parent;
    this.satisfiedCond = false;
  }

  public CodeBlock getParent() { return parent; }

  public boolean getSatisfiedCond() { return satisfiedCond; }

  public void setSatisfiedCond(boolean satisfiedCond) {
    this.satisfiedCond = satisfiedCond;
  }

  public int getFrom() { return range.from; }

  public int getTo() { return range.to; }
}
