package compiler_structures;

import java.util.*;

public class Instance {
  private List<String> code;
  private String className;
  private Map<String, Instance> includedClasses;
  private Map<String, Variable> fields;
  private Map<FunctionID, Function> functions;

  public Instance(String className, Map<String, Instance> includedClasses,
           Map<String, Variable> fields, Map<FunctionID, Function> functions,
           List<String> code) {
    this.className = className;
    this.includedClasses = includedClasses;
    this.fields = fields;
    this.functions = functions;
    this.code = code;
  }

  public List<String> getCode() { return code; }

  public Map<FunctionID, Function> getFunctions() { return functions; }

  public Map<String, Variable> getFields() {
    if (fields == null) {
      return null;
    }

    Map<String, Variable> newFields = new HashMap<>();
    Set<String> fieldSet = fields.keySet();

    for (String field : fieldSet) {
      newFields.put(field, fields.get(field));
    }

    return newFields;
  }

  public void setField(String field, Variable value) { fields.put(field, value); }

  public Map<String, Instance> getIncludedClasses() { return includedClasses; }

  public String getClassName() { return className; }
}
