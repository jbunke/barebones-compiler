package compiler_structures;

import java.util.List;

public class FunctionID {
  private String name;
  private String returnType;
  private List<Variable> arguments;

  public FunctionID(String name, String returnType,
             List<Variable> arguments) {
    this.name = name;
    this.returnType = returnType;
    this.arguments = arguments;
  }

  public String getName() { return name; }

  public int getArgTotal() { return arguments.size(); }

  @Override
  public boolean equals(Object other) {
    if (other == null || !(other instanceof FunctionID)) {
      return false;
    }

    FunctionID otherID = (FunctionID) other;
    if (name.equals(otherID.name) && returnType.equals(otherID.returnType) &&
            (arguments.size() == otherID.arguments.size())) {
      for (int i = 0; i < arguments.size(); i++) {
        if (!arguments.get(i).getType().equals(
                otherID.arguments.get(i).getType())) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
