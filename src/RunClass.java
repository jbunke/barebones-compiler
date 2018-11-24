import compiler_structures.FunctionID;
import compiler_structures.Instance;
import compiler_structures.Variable;

import java.util.ArrayList;
import java.util.List;

public class RunClass {

  public static void main(String[] args) {
    // Filepath to testing user program here:
    String fileLocation = "resources\\user-programs\\FlipProgram.bb";
    Instance codeClass = Compiler.parseCode(fileLocation);
    List<Variable> arguments = new ArrayList<>();
    List<Object> definedArgs = new ArrayList<>();
    Compiler.run(codeClass, new FunctionID("start",
            "void", arguments), definedArgs);
  }
}
