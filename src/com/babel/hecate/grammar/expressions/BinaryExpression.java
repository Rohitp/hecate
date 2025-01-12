package com.babel.hecate.grammar.expressions;

import com.babel.hecate.scanner.Token;

public class BinaryExpression extends HecateExpression {
    
    HecateExpression leftExpression;
    HecateExpression rightExpression;
    Token operator;

    public HecateExpression getLeftExpression() {
        return leftExpression;
    }


    public HecateExpression getRightExpression() {
        return rightExpression;
    }


    public Token getOperator() {
        return operator;
    }


    public BinaryExpression(HecateExpression leftExpression, Token operator, HecateExpression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }

    
    @Override
    public <T> T accept(HecateExpression.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
