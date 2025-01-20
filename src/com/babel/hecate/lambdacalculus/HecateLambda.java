package com.babel.hecate.lambdacalculus;
// Like expression and statement, lambda is taken

import java.util.ArrayList;

import com.babel.hecate.Variables;
import com.babel.hecate.grammar.statements.FunctionStatement;
import com.babel.hecate.interpreter.Interpreter;

public class HecateLambda  implements InterfaceLambda{


    private final FunctionStatement declaration;

    public HecateLambda(FunctionStatement declaration) {
        this.declaration = declaration;

    }

    public FunctionStatement getDeclaration() {
        return declaration;
    }

    @Override
    public Object call(ArrayList<Object> args, Interpreter interpreter) {
        Variables variables = new Variables(interpreter.globals);
        for(int i = 0; i < declaration.getParameters().size(); i++) {
            variables.declare(declaration.getParameters().get(i).lexeme, args.get(i));
        }

        // Each function needs its level of scoping. 
        // Take a simple recursion 
        // This cannot be deduped unless at the variable level
        interpreter.executeblockstatements(this.declaration.getBody(), variables);

        return 0;
    }

    @Override
    public int params() {
        return this.declaration.getParameters().size();
    }

    @Override
    public String toString() {
        return "function "+declaration.getFunc().getLexeme();
    }

    
}
