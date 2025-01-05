package com.babel.meta;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Hephaestus {

    public static void main(String args[]) throws IOException {
        String dir = "base";
        String className = "Expression";
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("Binary-Expression left, Operator operator, Expression right");
        arguments.add("Unary-Operator operator, Expression right");
        arguments.add("Literal-Object literal");
        arguments.add("Grouping-Expression expression");
        gen(dir, className, arguments);
    }

    private static void gen(String directory, String classname, ArrayList<String> arguments) throws IOException {
        PrintWriter writer = new PrintWriter(directory+"/"+classname, "UTF-8");

        writer.println("package com.babel.hecate;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + classname + " {");

        for (String argument : arguments) {
            String subclass = argument.split(":")[0].trim();
            String fields = argument.split(":")[1].trim(); 
            // defineType(writer, classname, subclass, fields);
          }

        writer.println("}");
        writer.close();
    }
    
}
