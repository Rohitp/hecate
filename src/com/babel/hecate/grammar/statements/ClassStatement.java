package com.babel.hecate.grammar.statements;

import java.util.ArrayList;
import com.babel.hecate.scanner.Token;


public class ClassStatement extends HecateStatement {

    Token classname;
    ArrayList<FunctionStatement> methods;


    public ClassStatement(Token classname, ArrayList<FunctionStatement> methods) {
        this.classname = classname;
        this.methods = methods;
    }

    public Token getClassname() {
        return classname;
    }

    public ArrayList<FunctionStatement> getMethods() {
        return methods;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }



    
}
