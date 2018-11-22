import compiler_structures.*;
import utilities.Line;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Compiler {

  enum errorType {
    IO,
    Runtime
  }

  private static List<String> readCode(String fileName) {
    List<String> code = new ArrayList<>();
    String line;

    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);

      while ((line = br.readLine()) != null) {
        code.add(line);
      }
    } catch (FileNotFoundException fnfe) {
      printError(errorType.IO, "File at location was not found.");
    } catch (IOException ioe) {
      printError(errorType.IO, "Error reading from file.");
    }

    return code;
  }

  private static List<String> modifyCode(List<String> code) {
    List<String> moddedCode = new ArrayList<>();

    for (String codeLine : code) {
      Line line = new Line(codeLine);
      line.trim();

      if (line.get().contains("//") && !enclosed(line.get(), "//")) {
        line.set(line.get().substring(0, line.get().indexOf("//")));
      }

      moddedCode.add(line.get());
    }

    return moddedCode;
  }

  public static Instance parseCode(String fileName) {
    return parseCode(modifyCode(readCode(fileName)));
  }

  public static Instance parseCode(List<String> code) {
    int curLine = 0;

    // Determine INCLUDED CLASSES
    Map<String, Instance> includedClasses = new HashMap<>();

    for (int i = curLine; i < code.size(); i++) {
      if (code.get(i).contains("include ") &&
              !enclosed(code.get(i), "include ")) {
        Line location = new Line(code.get(i));
        location.trim();
        location.set(String.valueOf(evaluate(location.get().substring(
                location.get().indexOf('"'), location.get().indexOf(';')),
                null, null, null, null)));
        Instance include = parseCode(modifyCode(readCode(location.get())));
        includedClasses.put(include.getClassName(), include);
      }
    }
    // INCLUDED CLASSES determined

    // Determine CLASS NAME & SCOPE
    curLine = 0;
    for (int i = 0; i < code.size(); i++) {
      if (code.get(i).contains("class ")) {
        curLine = i;
        break;
      }
    }

    Line className = new Line(code.get(curLine));
    className.shiftPast("class ");
    className.set(className.substring(0, className.get().indexOf('{')));
    className.trim();

    Range scope = new Range(curLine,
            closingLine('{', '}', curLine, code));
    // CLASS NAME & SCOPE determined

    // Determine FIELDS
    Map<String, Variable> fields = new HashMap<>();

    for (int i = curLine; i < code.size(); i++) {
      if (code.get(i).contains("field ") &&
              !enclosed(code.get(i), "field ")) {
        curLine = i;
        Line line = new Line(code.get(curLine));
        line.shiftPast("field ");
        String type = line.substring(0, line.get().indexOf(' '));
        Line name = new Line();
        boolean init = (line.get().contains("=") &&
                !enclosed(line.get(), "="));
        Line value = new Line();

        if (init) {
          value.set(line.substring(line.get().indexOf('=') + 1,
                  line.get().indexOf(';')));
          value.trim();
          name.set(line.substring(line.get().indexOf(' ') + 1,
                  line.get().indexOf('=')));
          name.trim();
        } else {
          name.set(line.substring(line.get().indexOf(' ') + 1,
                  line.get().indexOf(';')));
          name.trim();
        }

        fields.put(name.get(), new Variable(type, name.get(), scope,
                evaluate(value.get(),
                        null, null,
                        null, null)));
      }
    }
    // FIELDS determined

    // Determine FUNCTIONS
    Map<FunctionID, Function> functions = new HashMap<>();
    curLine = 0;
    for (int i = curLine; i < code.size(); i++) {
      if (code.get(i).contains("function ") &&
              !enclosed(code.get(i), "function ")) {
        curLine = i;
        Line line = new Line(code.get(curLine));
        line.shiftPast("function ");
        String functionType = line.substring(0, line.get().indexOf(' '));
        Line functionName = new Line(line.substring(line.get().indexOf(' ') + 1,
                line.get().indexOf('(')));
        functionName.trim();

        int from = curLine;
        int to = closingLine('{', '}', curLine, code);
        Range functionScope = new Range(from, to);

        Line argumentList = new Line(
                line.substring(line.get().indexOf('(') + 1,
                closingSpot('(', ')',
                        line.get().indexOf('('), line.get())));
        List<String> arguments = splitArgs(argumentList);

        List<Variable> argList = new ArrayList<>();
        Map<String, Variable> argMap = new HashMap<>();
        for (int j = 0; j < arguments.size(); j++) {
          Line argLine = new Line(arguments.get(j));
          String type = argLine.substring(0, argLine.get().indexOf(' '));
          argLine.shiftPast(" ");
          String name = argLine.get();
          Variable var = new Variable(type, name, functionScope);
          argList.add(var);
          argMap.put(var.getName(), var);
        }

        functions.put(
                new FunctionID(functionName.get(), functionType, argList),
                new Function(functionName.get(), functionScope,
                        functionType, argMap));
      }
    }
    // FUNCTIONS determined

    return new Instance(className.get(), includedClasses, fields,
            functions, code);
  }

  public static Object run(Instance codeClass, FunctionID functionID,
                           List<Object> args) {
    if (!codeClass.getFunctions().containsKey(functionID)) {
      printError(errorType.Runtime, "compiler_structures.Function '" +
              functionID.getName() + "' was not found in the class '" +
              codeClass.getClassName() + "'.");
      return null;
    }

    int curLine = codeClass.getFunctions().get(functionID).getFrom();
    CodeBlock codeBlock = null;
    Map<String, Variable> vars = codeClass.getFields();

    /* 'run' is called on a function, so the program counter will stop
     * when it reaches the end of the function and a return statement
     * has not been reached. */
    while (curLine < codeClass.getFunctions().get(functionID).getTo()) {
      Line line = new Line(codeClass.getCode().get(curLine));

      // Determine STATEMENT TYPE
      if (line.get().contains("function ") &&
              !enclosed(line.get(), "function ")) {
        // Decode arguments and add them to compiler_structures.Variable Map 'vars'
        Line argumentList = new Line(
                line.substring(line.get().indexOf('(') + 1,
                        closingSpot('(', ')',
                                line.get().indexOf('('), line.get())));
        List<String> arguments = splitArgs(argumentList);

        for (int i = 0; i < arguments.size(); i++) {
          Line argLine = new Line(arguments.get(i));
          argLine.shiftPast(" ");
          String name = argLine.get();
          vars.put(name, new Variable(
                  codeClass.getFunctions().get(functionID).
                          getArguments().get(name).getType(), name,
                  codeClass.getFunctions().get(functionID).
                          getArguments().get(name).getScope(), args.get(i)));
        }

        curLine++;
      } else if (line.get().contains("while ") &&
              !enclosed(line.get(), "while ")) {
        // WHILE STATEMENT
        if (codeBlock == null) {
          codeBlock = new CodeBlock(curLine,
                  closingLine('{', '}', curLine,
                          codeClass.getCode()));
        } else if (codeBlock.getFrom() != curLine) {
          codeBlock = new CodeBlock(curLine,
                  closingLine('{', '}', curLine,
                          codeClass.getCode()), codeBlock);
        }

        Line condition = new Line(line.substring(line.get().indexOf('(') + 1,
                closingSpot('(', ')', line.get().indexOf('('),
                        line.get())));
        condition.trim();

        boolean cond =
                Boolean.parseBoolean(String.valueOf(evaluate(condition.get(),
                        vars,
                codeClass.getFunctions(), codeClass.getIncludedClasses(),
                codeClass))) ;

        if (cond) {
          curLine++;
        } else {
          curLine = codeBlock.getTo() + 1;
        }
      } else if (line.get().contains("loop;") &&
              !enclosed(line.get(), "loop;")) {
        // LOOP KEYWORD
        if (line.get().contains("}") &&
                line.get().indexOf('}') < line.get().indexOf("loop;") &&
                codeBlock != null) {
          curLine = codeBlock.getFrom();
        } else {
          curLine++;
        }
      } else if (line.get().contains("if ") &&
              !enclosed(line.get(), "if ")) {
        boolean skipElse = false;

        // DETERMINE IF "IF" OR "ELSE IF" STATEMENT AND
        // IF A BRANCH HAS ALREADY BEEN EVALUATED
        if (codeBlock != null && line.get().contains("else ")) {
          skipElse = codeBlock.getSatisfiedCond();

          if (codeBlock.getParent() != null) {
            codeBlock = new CodeBlock(curLine,
                    closingLine('{', '}',
                            curLine, codeClass.getCode()),
                    codeBlock.getParent());
          } else {
            codeBlock = new CodeBlock(curLine,
                    closingLine('{', '}',
                            curLine, codeClass.getCode()));
          }
        } else if (codeBlock == null) {
          codeBlock = new CodeBlock(curLine, closingLine('{', '}',
                  curLine, codeClass.getCode()));
        } else {
          codeBlock = new CodeBlock(curLine, closingLine('{', '}',
                  curLine, codeClass.getCode()), codeBlock);
        }

        Line condition = new Line(line.substring(line.get().indexOf('(') + 1,
                closingSpot('(', ')', line.get().indexOf('('),
                        line.get())));
        condition.trim();

        boolean cond =
                Boolean.parseBoolean(String.valueOf(evaluate(condition.get(),
                        vars,
                codeClass.getFunctions(), codeClass.getIncludedClasses(),
                codeClass)));

        if (cond & !skipElse) {
          codeBlock.setSatisfiedCond(true);
          curLine++;
        } else {
          codeBlock.setSatisfiedCond(skipElse);
          curLine = codeBlock.getTo();
        }

      } else if (line.get().contains("else ") &&
              !enclosed(line.get(), "else ")) {
        // ELSE STATEMENT
        boolean skipElse = false;

        // only encased in if to prevent NullPointerException warning
        if (codeBlock != null) {
          skipElse = codeBlock.getSatisfiedCond();
        }

        if (codeBlock.getParent() != null) {
          codeBlock = new CodeBlock(curLine, closingLine('{',
                  '}', curLine, codeClass.getCode()),
                  codeBlock.getParent());
        } else {
          codeBlock = new CodeBlock(curLine, closingLine('{',
                  '}', curLine, codeClass.getCode()));
        }

        if (skipElse) {
          curLine = codeBlock.getTo();
        } else {
          curLine++;
        }
      } else if (line.get().contains("var ") &&
              !enclosed(line.get(), "var ")) {
        // VARIABLE DECLARATION
        line.shiftPast("var ");
        String type = line.substring(0, line.get().indexOf(' '));
        line.shiftPast(type + " ");
        Line name = new Line();
        boolean init = (line.get().contains("=") &&
                !enclosed(line.get(), "="));
        Line value = new Line();

        if (init) {
          value.set(line.substring(line.get().indexOf('=') + 1,
                  line.get().indexOf(';')));
          value.trim();
          name.set(line.substring(0, line.get().indexOf('=')));
          name.trim();

          if (codeBlock != null) {
            vars.put(name.get(), new Variable(type, name.get(),
                    new Range(curLine, codeBlock.getTo()),
                    evaluate(value.get(), vars, codeClass.getFunctions(),
                            codeClass.getIncludedClasses(), codeClass)));
          } else {
            vars.put(name.get(), new Variable(type, name.get(),
                    new Range(curLine,
                            codeClass.getFunctions().get(functionID).getTo()),
                    evaluate(value.get(), vars, codeClass.getFunctions(),
                            codeClass.getIncludedClasses(), codeClass)));
          }
        } else {
          name.set(line.substring(0, line.get().indexOf(';')));
          name.trim();

          if (codeBlock != null) {
            vars.put(name.get(), new Variable(type, name.get(),
                    new Range(curLine, codeBlock.getTo())));
          } else {
            vars.put(name.get(), new Variable(type, name.get(),
                    new Range(curLine,
                            codeClass.getFunctions().get(functionID).getTo())));
          }
        }

        curLine++;
      }  else if (line.get().contains("return ") &&
              !enclosed(line.get(), "return ")) {
        // RETURN STATEMENT
        line.shiftPast("return ");
        line = new Line(line.substring(0, line.get().indexOf(';')));
        return evaluate(line.get(), vars, codeClass.getFunctions(),
                codeClass.getIncludedClasses(), codeClass);
      } else if (line.get().contains(" = ") &&
              !enclosed(line.get(), " = ")) {
        // ASSIGNMENT
        Line name = new Line(line.substring(0, line.get().indexOf('=')));
        name.trim();
        Line value = new Line(line.substring(line.get().indexOf('=') + 1,
                line.get().indexOf(';')));
        value.trim();

        if (!vars.containsKey(name.get())) {
          printError(errorType.Runtime, "Attempted assignment is " +
          "invalid as variable '" + name.get() + "' is undeclared.");
          return null;
        } else {
          Variable var = vars.get(name.get());
          if (!var.inScope(curLine)) {
            printError(errorType.Runtime, "Attempted assignment is " +
            "invalid as variable '" + name.get() + "' is out of scope.");
            return null;
          } else {
            var.set(evaluate(value.get(), vars, codeClass.getFunctions(),
                    codeClass.getIncludedClasses(), codeClass));
            vars.put(name.get(), var);
            curLine++;
          }
        }
      } else if (line.get().contains("print(") &&
              !enclosed(line.get(), "print(")) {
        // SPECIAL FUNCTION: print
        line.shiftPast("print(");
        String toPrint = line.substring(0, line.get().indexOf(");"));
        toPrint = String.valueOf(evaluate(toPrint, vars, codeClass.getFunctions(),
                codeClass.getIncludedClasses(), codeClass));
        System.out.println(toPrint);
        curLine++;
        // TODO: made; - acts as a special return statement
      } else if (line.get().contains("made;") &&
              !enclosed(line.get(), "made;")) {
        // SPECIAL: made; signifies end of a make(...) constructor
        Instance retInstance = new Instance(codeClass.getClassName(),
                codeClass.getIncludedClasses(), new HashMap<>(),
                codeClass.getFunctions(), codeClass.getCode());

        Set<String> fieldNameList = new HashSet<>();

        if (codeClass.getFields() != null) {
          fieldNameList = codeClass.getFields().keySet();
        }

        for (String field : fieldNameList) {
          if (vars.containsKey(field)) {
            retInstance.setField(field, new Variable(vars.get(field).getType(),
                    field, new Range(vars.get(field).getScope().from,
                    vars.get(field).getScope().to), vars.get(field).get()));
          }
        }

        return retInstance;

      } else {
        // GENERAL PURPOSE EVALUATION, COULD BE VOID FUNCTION CALL
        String spaceless = line.get().replaceAll(" ", "");
        if (spaceless.equals("")) {
          curLine++;
        } else if (line.get().contains(";")) {
          line.set(line.substring(0, line.get().indexOf(';')));
          evaluate(line.get(), vars, codeClass.getFunctions(),
                  codeClass.getIncludedClasses(), codeClass);
          curLine++;
        } else {
          curLine++;
        }
      }

      // CODE BLOCK MAINTENANCE
      if (codeBlock != null) {
        if (curLine > codeBlock.getTo()) {
          if (codeBlock.getParent() != null) {
            codeBlock = codeBlock.getParent();
          } else {
            codeBlock = null;
          }
        }
      }

      // VARIABLES IN SCOPE
      // if a variable is out of scope, remove it from vars map
      Set<String> varKeySet = vars.keySet();
      for (String key : varKeySet) {
        if (!vars.get(key).inScope(curLine)) {
          // vars.remove(key); - this might be problematic
        }
      }
    }

    return null;
  }

  private static List<String> splitArgs(Line argumentList) {
    List<String> arguments = new ArrayList<>();

        /* TODO: Potential compiler mistake
         * If a comma is placed in a string that is part of an argument,
         * it will not compile correctly. */
    while (argumentList.get().indexOf(',') >= 0) {
      Line arg = new Line(argumentList.substring(0,
              argumentList.get().indexOf(',')));
      arg.trim();
      arguments.add(arg.get());
      argumentList.shiftPast(",");
    }

    if (argumentList.length() > 0 &&
            !argumentList.get().replaceAll(
                    " ", "").equals("")) {
      argumentList.trim();
      arguments.add(argumentList.get());
    }

    return arguments;
  }

  private static Object evaluate(String expression, Map<String, Variable> vars,
                         Map<FunctionID, Function> functions,
                         Map<String, Instance> includedClasses,
                         Instance codeClass) {
    Set<FunctionID> functionIDSet = new HashSet<>();

    if (functions != null) {
      functionIDSet = functions.keySet();
    }

    Set<String> functionNameSet = new HashSet<>();

    for (FunctionID ID : functionIDSet) {
      functionNameSet.add(ID.getName());
    }

    Set<String> includedClassNameSet = new HashSet<>();

    if (includedClasses != null) {
      includedClassNameSet = includedClasses.keySet();
    }
    // END OF SETUP

    // SPECIAL:
    // Random:
    if (expression.equals("random()")) {
      return Math.random();
    } else if (expression.equals("next()")) {
      Scanner in = new Scanner(System.in);
      return in.nextLine();
    }

    // STRINGS
    if (expression.length() >= 2) {
      if (expression.charAt(0) == '"' &&
              expression.charAt(expression.length() - 1) == '"') {
        return expression.substring(1, expression.length() - 1);
      }
    }

    // VARIABLE (in current class)
    if (vars != null) {
      if (vars.containsKey(expression)) {
        return vars.get(expression).get();
      }
    }

    // EVALUATE FUNCTION CALLS
    // this must be done before finding sub-expressions because function calls
    // are signified with ( and ) as well
    for (String name : functionNameSet) {
      if (expression.contains(name + "(")) {
        String left = expression.substring(0, expression.indexOf(name));
        String call = expression.substring(expression.indexOf(name),
                closingSpot('(', ')',
                        expression.indexOf(name + "(") + name.length(),
                        expression) + 1);
        String right = expression.substring(left.length() +
                call.length());

        String result = "";

        Line argumentList = new Line(call.substring(call.indexOf('(') + 1,
                closingSpot('(', ')', call.indexOf('('), call)));
        List<String> args = splitArgs(argumentList);
        List<Object> arguments = new ArrayList<>();

        for (String arg : args) {
          arguments.add(evaluate(arg, vars, functions, includedClasses, codeClass));
        }

        for (FunctionID ID : functionIDSet) {
          if (ID.getName().equals(name) &&
                  arguments.size() == ID.getArgTotal()) {
            result = String.valueOf(run(codeClass, ID, arguments));
            break;
          }
        }

        expression = left + result + right;
      }
    }

    Set<String> varsNameList = new HashSet<>();

    if (vars != null) {
      varsNameList = vars.keySet();
    }

    for (String varName : varsNameList) {
      if (expression.contains(varName + ".")) {
        Instance object = (Instance) vars.get(varName).get();

        Set<FunctionID> incFunctionIDSet = new HashSet<>();

        if (object.getFunctions() != null) {
          incFunctionIDSet = object.getFunctions().keySet();
        }

        Set<String> incFunctionNameSet = new HashSet<>();

        for (FunctionID ID : incFunctionIDSet) {
          incFunctionNameSet.add(ID.getName());
        }
        // SETUP COMPLETE

        for (String functionName : incFunctionNameSet) {
          String searchFor = varName + "." + functionName + "(";

          if (expression.contains(searchFor)) {
            // TODO
            String left = expression.substring(0,
                    expression.indexOf(searchFor));
            String call = expression.substring(expression.indexOf(searchFor),
                    closingSpot('(', ')',
                            expression.indexOf(searchFor) +
                                    searchFor.length() - 1,
                            expression) + 1);
            String right = expression.substring(left.length() +
                    call.length());

            String result = "";

            Line argumentList = new Line(call.substring(call.indexOf('(') + 1,
                    closingSpot('(', ')', call.indexOf('('), call)));
            List<String> args = splitArgs(argumentList);
            List<Object> arguments = new ArrayList<>();

            for (String arg : args) {
              arguments.add(evaluate(arg, vars, functions, includedClasses, codeClass));
            }

            for (FunctionID ID : incFunctionIDSet) {
              if (ID.getName().equals(functionName) &&
                      arguments.size() == ID.getArgTotal()) {
                // object constructors cannot be chained in expressions
                if (left.equals("") && right.equals("")) {
                  return run(object, ID, arguments);
                }
                result = String.valueOf(run(object, ID, arguments));
                break;
              }
            }

            expression = left + result + right;
          }
        }
      }
    }

    // EVALUATE INCLUDED CLASS FUNCTION CALLS
    for (String className : includedClassNameSet) {
      if (expression.contains(className + ".")) {
        Instance curClass = includedClasses.get(className);

        Set<FunctionID> incFunctionIDSet = new HashSet<>();

        if (curClass.getFunctions() != null) {
          incFunctionIDSet = curClass.getFunctions().keySet();
        }

        Set<String> incFunctionNameSet = new HashSet<>();

        for (FunctionID ID : incFunctionIDSet) {
          incFunctionNameSet.add(ID.getName());
        }
        // SETUP COMPLETE

        for (String functionName : incFunctionNameSet) {
          String searchFor = className + "." + functionName + "(";

          if (expression.contains(searchFor)) {
            String left = expression.substring(0,
                    expression.indexOf(searchFor));
            String call = expression.substring(expression.indexOf(searchFor),
                    closingSpot('(', ')',
                            expression.indexOf(searchFor) +
                                    searchFor.length() - 1,
                            expression) + 1);
            String right = expression.substring(left.length() +
                    call.length());

            String result = "";

            Line argumentList = new Line(call.substring(call.indexOf('(') + 1,
                    closingSpot('(', ')', call.indexOf('('), call)));
            List<String> args = splitArgs(argumentList);
            List<Object> arguments = new ArrayList<>();

            for (String arg : args) {
              arguments.add(evaluate(arg, vars, functions, includedClasses, codeClass));
            }

            for (FunctionID ID : incFunctionIDSet) {
              if (ID.getName().equals(functionName) &&
                      arguments.size() == ID.getArgTotal()) {
                // object constructors cannot be chained in expressionsS
                if (functionName.equals("make")) {
                  return run(curClass, ID, arguments);
                }
                result = String.valueOf(run(curClass, ID, arguments));
                break;
              }
            }

            expression = left + result + right;
          }
        }
      }
    }

    // EVALUATING SUB-EXPRESSIONS
    int recentOpen = 0;
    for (int i = 0; i < expression.length(); i++) {
      switch (expression.charAt(i)) {
        case '(':
          recentOpen = i;
          break;
        case ')':
          expression = expression.substring(0, recentOpen) +
                  String.valueOf(evaluate(expression.substring(recentOpen + 1, i),
                          vars, functions, includedClasses, codeClass)) +
                  expression.substring(i + 1);
      }
    }

    // OPERATORS
    if (expression.indexOf('(') == -1) {
      /* PRECEDENCE ORDER:
       * ++
       * @
       * -
       * +
       * %
       * *
       * /
       * ^
       * >=
       * <=
       * >
       * <
       * ==
       * !=
       * ||
       * &&
       * !
       * #*/
      Line exp = new Line(expression);
      String[] operators = {"||", "&&", ">=", "<=", ">", "<", "==", "!=",
              "++", "@", "^", "%", "*", "/", "-", "+", "!", "#"};

      for (String op : operators) {
        if (exp.get().contains(op)) {
          int opIndex = exp.get().indexOf(op);

          boolean isBinOp = false;
          Line op1 = new Line();
          Line op2 = new Line();
          Line operand = new Line();

          switch (op) {
            case "++":
            case "@":
            case "+":
            case "%":
            case "*":
            case "/":
            case "^":
            case ">=":
            case "<=":
            case ">":
            case "<":
            case "==":
            case "!=":
            case "||":
            case "&&":
              isBinOp = true;

              op1.set(exp.substring(0, opIndex));
              op1.trim();
              op1.set(String.valueOf(evaluate(op1.get(), vars, functions,
                      includedClasses, codeClass)));
              op2.set(exp.substring(opIndex +
                      op.length()));
              op2.trim();
              op2.set(String.valueOf(evaluate(op2.get(), vars, functions,
                      includedClasses, codeClass)));
              break;
            case "!":
            case "#":
              isBinOp = false;

              operand.set(exp.substring(opIndex + op.length()));
              operand.trim();
              operand.set(String.valueOf(evaluate(operand.get(), vars,
                      functions, includedClasses, codeClass)));
              break;
            case "-":
              // PROGRAM '-' SEPARATELY AND USE INDEX OF '-' TO CHECK IF
              // SUBTRACTION OR ARITHMETIC NEGATION
              if (opIndex == 0) {
                isBinOp = false;

                operand.set(exp.substring(opIndex + op.length()));
                operand.trim();
                operand.set(String.valueOf(evaluate(operand.get(), vars,
                        functions, includedClasses, codeClass)));
              } else {
                isBinOp = true;

                op1.set(exp.substring(0, opIndex));
                op1.trim();
                op1.set(String.valueOf(evaluate(op1.get(), vars, functions,
                        includedClasses, codeClass)));
                op2.set(exp.substring(opIndex + op.length()));
                op2.trim();
                op2.set(String.valueOf(evaluate(op2.get(), vars, functions,
                        includedClasses, codeClass)));
              }
              break;
          }

          switch (op) {
            case "++":
              return op1.get() + op2.get();
            case "@":
              if (!isNumber(op2.get())) {
                printError(errorType.Runtime, "Element at index " +
                "operation (@) attempted with non-integer index type.");
                return null;
              }

              Integer index = Integer.parseInt(op2.get());
              return op1.get().charAt(index);
            case "+":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Addition (+) must be " +
                "performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());
                return db1 + db2;
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return in1 + in2;
              }
            case "%":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Modulo (%) must be " +
                        "performed between numeric objects.");
                return null;
              }

              if (!(op1.get().contains(".") || op2.get().contains("."))) {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return in1 % in2;
              } else {
                printError(errorType.Runtime, "Operands of modulo " +
                "operation (%) must be integers.");
                return null;
              }
            case "*":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Multiplication (*) " +
                                "must be performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());
                return db1 * db2;
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return in1 * in2;
              }
            case "/":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Division (/) " +
                        "must be performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());

                if (db2 == 0.0) {
                  printError(errorType.Runtime, "Divide by 0 cannot " +
                  "be computed.");
                  return null;
                }

                return db1 / db2;
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());

                if (in2 == 0) {
                  printError(errorType.Runtime, "Divide by 0 cannot " +
                          "be computed.");
                  return null;
                }

                return in1 / in2;
              }
            case "^":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Exponentiation (^) " +
                        "must be performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());
                return Math.pow(db1, db2);
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return Math.pow(in1, in2);
              }
            case ">=":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Greater than or " +
                        "equal to (>=) " +
                        "must be performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());
                return db1 >= db2;
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return in1 >= in2;
              }
            case "<=":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Less than or " +
                        "equal to (<=) " +
                        "must be performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());
                return db1 <= db2;
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return in1 <= in2;
              }
            case ">":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Greater than " +
                        "(>) " +
                        "must be performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());
                return db1 > db2;
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return in1 > in2;
              }
            case "<":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                printError(errorType.Runtime, "Less than " +
                        "(<) " +
                        "must be performed between numeric objects.");
                return null;
              }

              if (op1.get().contains(".") || op2.get().contains(".")) {
                Double db1 = Double.parseDouble(op1.get());
                Double db2 = Double.parseDouble(op2.get());
                return db1 < db2;
              } else {
                Integer in1 = Integer.parseInt(op1.get());
                Integer in2 = Integer.parseInt(op2.get());
                return in1 < in2;
              }
            case "==":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                return op1.get().equals(op2.get());
              } else if (op1.get().contains(".") || op2.get().contains(".")) {
                double db1 = Double.parseDouble(op1.get());
                double db2 = Double.parseDouble(op2.get());
                return db1 == db2;
              } else {
                int in1 = Integer.parseInt(op1.get());
                int in2 = Integer.parseInt(op2.get());
                return in1 == in2;
              }
            case "!=":
              if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                return !op1.get().equals(op2.get());
              } else if (op1.get().contains(".") || op2.get().contains(".")) {
                double db1 = Double.parseDouble(op1.get());
                double db2 = Double.parseDouble(op2.get());
                return db1 != db2;
              } else {
                int in1 = Integer.parseInt(op1.get());
                int in2 = Integer.parseInt(op2.get());
                return in1 != in2;
              }
            case "||":
              Boolean b1 = Boolean.parseBoolean(op1.get());
              Boolean b2 = Boolean.parseBoolean(op2.get());
              return b1 || b2;
            case "&&":
              b1 = Boolean.parseBoolean(op1.get());
              b2 = Boolean.parseBoolean(op2.get());
              return b1 && b2;
            case "!":
              Boolean b = Boolean.parseBoolean(operand.get());
              return !b;
            case "#":
              return operand.length();
            case "-":
              if (isBinOp) {
                if (!isNumber(op1.get()) || !isNumber(op2.get())) {
                  printError(errorType.Runtime, "Subtraction (-) " +
                          "must be performed between numeric objects.");
                  return null;
                }

                if (op1.get().contains(".") || op2.get().contains(".")) {
                  Double db1 = Double.parseDouble(op1.get());
                  Double db2 = Double.parseDouble(op2.get());
                  return db1 - db2;
                } else {
                  Integer in1 = Integer.parseInt(op1.get());
                  Integer in2 = Integer.parseInt(op2.get());
                  return in1 - in2;
                }
              } else {
                if (!isNumber(operand.get())) {
                  printError(errorType.Runtime, "Arithmetic negation " +
                          "(-) must be performed on a numeric object.");
                  return null;
                }

                if (operand.get().contains(".")) {
                  double db = Double.parseDouble(operand.get());
                  return -db;
                } else {
                  int in = Integer.parseInt(operand.get());
                  return -in;
                }
              }
          }
        }
      }
    }

    // FLOAT
    if (expression.length() > 1 && expression.contains(".") &&
            isNumber(expression)) {
      return Double.parseDouble(expression);
    }

    // INTEGER
    if (expression.length() >= 1 && isNumber(expression)) {
      return Integer.parseInt(expression);
    }

    // BOOLEAN
    if (expression.toLowerCase().equals("true") ||
            expression.toLowerCase().equals("false")) {
      return Boolean.parseBoolean(expression.toLowerCase());
    }

    return expression;
  }

  private static boolean isNumber(String s) {
    boolean periodPlaced = false;

    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(0) == '-') {
        i++;
      }

      switch (s.charAt(i)) {
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '0':
          break;
        case '.':
          if (periodPlaced) {
            return false;
          } else {
            periodPlaced = true;
          }
          break;
        default:
          return false;
      }
    }
    return true;
  }

  private static void printError(errorType type, String message) {
    String printOut = type.toString() + " ERROR: " + message;
    System.out.println(printOut);
  }

  private static boolean enclosed(String line, String checkFor) {
    if (!line.contains(checkFor)) {
      return false;
    }

    int quotationTotals = 0;

    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) == '"') {
        quotationTotals += 1;
      }
    }

    if ((quotationTotals % 2) == 1) {
      return false;
    } else {
      int index = line.indexOf(checkFor);

      int quotationPartialSum = 0;

      for (int i = 0; i < index; i++) {
        if (line.charAt(i) == '"') {
          quotationPartialSum += 1;
        }
      }

      return ((quotationPartialSum % 2) == 1);
    }
  }

  private static int closingLine(char open, char close, int lineOpen,
                                 List<String> code) {
    if (code.get(lineOpen).indexOf(open) == -1) {
      return -1;
    } else {
      int cur_line = lineOpen + 1;
      int score = 1;

      while (cur_line < code.size()) {
        String processor = code.get(cur_line);
        while (processor.length() > 0) {
          if (processor.charAt(0) == close) {
            score--;
            if (score == 0) {
              return cur_line;
            }
          } else if (processor.charAt(0) == open) {
            score++;
          }
          processor = processor.substring(1);
        }
        cur_line++;
      }
      return -1;
    }
  }

  private static int closingSpot(char open, char close, int charOpen,
                                 String line) {
    if (line.indexOf(open) == -1) {
      return -1;
    } else {
      int score = 1;
      int char_close = charOpen + 1;
      boolean inQuotes = false;

      while (char_close < line.length()) {
        if (line.charAt(char_close) == close && !inQuotes) {
          score--;
          if (score == 0) {
            return char_close;
          }
        } else if (line.charAt(char_close) == open && !inQuotes) {
          score++;
        } else if (line.charAt(char_close) == '"') {
          inQuotes = !inQuotes;
        }
        char_close++;
      }
      return -1;
    }
  }
}
