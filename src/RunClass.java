import compiler_structures.FunctionID;
import compiler_structures.Instance;
import compiler_structures.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RunClass {

  public static void main(String[] args) {
    System.out.println("Specify a Barebones filepath:");
    Scanner in = new Scanner(System.in);
    String fileLocation = in.nextLine(); // resources/user-programs/FlipProgram.bb
    Instance codeClass = Compiler.parseCode(fileLocation);
    List<Variable> arguments = new ArrayList<>();
    List<Object> definedArgs = new ArrayList<>();
    Compiler.run(codeClass, new FunctionID("start",
            "void", arguments), definedArgs);
  }
}
