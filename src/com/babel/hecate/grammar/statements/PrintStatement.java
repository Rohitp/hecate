package com.babel.hecate.grammar.statements;

import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.HecateExpression.Visitor;

public class PrintStatement extends HecateStatement {

    

    final HecateExpression expression;

    public PrintStatement(HecateExpression expression) {
        this.expression = expression;
    }

    public HecateExpression getExpression() {
        return expression;
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
    
}
