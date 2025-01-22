package com.babel.hecate.lambdacalculus;
// Like expression and statement, lambda is taken

import java.util.ArrayList;

import com.babel.hecate.Variables;
import com.babel.hecate.grammar.statements.FunctionStatement;
import com.babel.hecate.interpreter.Interpreter;

public class HecateLambda  implements InterfaceLambda{


    private final FunctionStatement declaration;
    private final Variables closure;

    public HecateLambda(FunctionStatement declaration, Variables closure) {
        this.declaration = declaration;
        this.closure = closure;

    }

    public FunctionStatement getDeclaration() {
        return declaration;
    }

    @Override
    public Object call(ArrayList<Object> args, Interpreter interpreter) {

        
        Variables variables = new Variables(closure);
        for(int i = 0; i < declaration.getParameters().size(); i++) {
            variables.declare(declaration.getParameters().get(i).lexeme, args.get(i));
        }



        // Each function needs its level of scoping. 
        // Take a simple recursion 
        // This cannot be deduped unless at the variable level
        try{
            interpreter.executeblockstatements(this.declaration.getBody(), variables);
        } catch(Return value) {
            return value.returnvalue;
        }

        return 42;
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
