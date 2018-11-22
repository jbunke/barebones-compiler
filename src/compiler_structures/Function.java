package compiler_structures;

import java.util.Map;

public class Function {
  private Range range;
  private String name;
  private String returnType;
  private Map<String, Variable> arguments;

  public Function(String name, Range range, String returnType,
           Map<String, Variable> arguments) {
    this.name = name;
    this.range = range;
    this.returnType = returnType;
    this.arguments = arguments;
  }

  public Map<String, Variable> getArguments() { return arguments; }

  public int getFrom() { return range.from; }

  public int getTo() { return range.to; }
}
