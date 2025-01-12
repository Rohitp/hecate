package com.babel.hecate.grammar.expressions;

import com.babel.hecate.scanner.Token;

public class UnaryExpression extends HecateExpression {

    Token operator;
    HecateExpression expression;


    public UnaryExpression(Token operator, HecateExpression expression) {
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public Token getOperator() {
        return operator;
    }

    public HecateExpression getExpression() {
        return expression;
    }
    
}
