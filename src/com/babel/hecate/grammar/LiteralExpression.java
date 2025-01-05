package com.babel.hecate.grammar;

public class LiteralExpression extends Expression {

    Object literal;

    public LiteralExpression(Object literal) {
        this.literal = literal;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        // TODO Auto-generated method stub
        return visitor.visit(this);
    }
    
}
