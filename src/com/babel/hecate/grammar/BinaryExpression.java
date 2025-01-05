package com.babel.hecate.grammar;

import com.babel.hecate.scanner.Token;

public class BinaryExpression extends Expression {
    
    Expression leftExpression;
    Expression rightExpression;
    Token operator;

    public BinaryExpression(Expression leftExpression, Token operator, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }

    
    @Override
    <T> T accept(Expression.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
