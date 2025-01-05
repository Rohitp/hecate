package com.babel.hecate.grammar;

public class GroupExpression extends Expression {

    Expression expression;

    public GroupExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    
}
