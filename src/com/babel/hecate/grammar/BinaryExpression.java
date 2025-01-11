package com.babel.hecate.grammar;

import com.babel.hecate.scanner.Token;

public class BinaryExpression extends Expression {
    
    Expression leftExpression;
    Expression rightExpression;
    Token operator;

    public Expression getLeftExpression() {
        return leftExpression;
    }


    public Expression getRightExpression() {
        return rightExpression;
    }


    public Token getOperator() {
        return operator;
    }


    public BinaryExpression(Expression leftExpression, Token operator, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }

    
    @Override
    public <T> T accept(Expression.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
