package compiler_structures;

public class Variable {
  private String type;
  private String name;
  private Object value;
  private Range scope;

  public Variable(String type, String name, Range scope) {
    // Argument variable constructor
    this.type = type;
    this.name = name;
    this.scope = scope;
    this.value = null;
  }

  public Variable(String type, String name, Range scope, Object value) {
    this.type = type;
    this.name = name;
    this.scope = scope;
    this.value = value;
  }

  public Object get() { return value; }

  public void set(Object newValue) { value = newValue; }

  public Range getScope() { return scope; }

  public String getType() { return type; }

  public String getName() { return name; }

  public boolean inScope(int line) {
    return scope.isWithin(line);
  }
}
