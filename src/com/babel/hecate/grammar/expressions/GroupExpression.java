package com.babel.hecate.grammar.expressions;

public class GroupExpression extends HecateExpression {

   

    HecateExpression expression;

    public GroupExpression(HecateExpression expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public HecateExpression getExpression() {
        return expression;
    }

    
}
