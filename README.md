# Barebones Compiler
The Barebones Compiler is a high-level language to Java compiler that I wrote in the summer of 2018 as a proof of concept.
Barebones is a rudimentary language with a C-based syntax.

## Barebones Syntax Overview:

Classes are defined with the `class` keyword followed by the class name. For example:

    class class_name {
      // empty class
    }

Global variables (fields) are declared with the `field` keyword, followed by type, followed by the field name.
Fields can be initialised or simply declared. For example:

    class field_example {
      field int foo;
      field int fee = 2;
      field bool faa = true;
      field bool fii;
      field bool fuu = (1 > 0);
    }
    
Functions are declared with the `function` keyword, followed by the return type, followed by the function name, 
followed by a bracket-enclosed and comma-separated argument list of types and names. For example:

    class function_example {
      function int a() {
        return 1;
      }
      
      function void b(int arg1, int arg2) {
        print(arg1 ++ arg2);
      }
    }

Local variables are declared with `var` keyword as opposed to fields with the `field` keyword.

### Control flow

If statement:

    if (a > b) {
      if (a > c) {
        return a;
      }
    } else if (b > c) {
      return b;
    }
    return c;

While-loop statement:

    while (increment < limit) {
      increment = increment + 1;
    } loop;

### Operators

    > + (Addition)
    > - (Subtraction)
    > * (Multiplication)
    > / (Division)
    > % (Modulo)
    > ^ (Exponentiation)
    > >= (Greather than or equal to)
    > <= (Less than or equal to)
    > > (Greater than)
    > < (Less than)
    > == (Equal to)
    > != (Not equal to)
    > || (Or)
    > && (And)
    > ! (Not; UNARY)
    > ++ (Concatenation)
    > # (Length; UNARY)
    > @ (Element at index)

Examples:

    > #"start" -> 5
    > "start"@2 -> "a"
    > "start"++"this"++"off"++"right" -> "startthisoffright"
    > 5 / 2 -> 2
    > #"testing" - 3 -> 4
