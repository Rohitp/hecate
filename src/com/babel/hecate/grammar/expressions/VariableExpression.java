package com.babel.hecate.grammar.expressions;

import com.babel.hecate.scanner.Token;


// A variable expression is where you wan to exaluaate PRINT (x  + 5)*y
public class VariableExpression extends HecateExpression {
    

    final Token name;

    public VariableExpression(Token name) {
        this.name = name;
    }

    public Token getName() {
        return name;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
