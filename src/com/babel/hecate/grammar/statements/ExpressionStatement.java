package com.babel.hecate.grammar.statements;

import com.babel.hecate.grammar.expressions.HecateExpression;

public class ExpressionStatement extends HecateStatement {

    HecateExpression he;

    public HecateExpression getHe() {
        return he;
    }

    public ExpressionStatement(HecateExpression he) {
        this.he = he;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
    
}
