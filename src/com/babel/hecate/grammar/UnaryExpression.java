package com.babel.hecate.grammar;

import com.babel.hecate.scanner.Token;

public class UnaryExpression extends Expression {

    Token operator;
    Expression expression;


    public UnaryExpression(Token operator, Expression expression) {
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

    public Expression getExpression() {
        return expression;
    }
    
}
