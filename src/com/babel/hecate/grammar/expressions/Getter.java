package com.babel.hecate.grammar.expressions;

import com.babel.hecate.scanner.Token;

public class Getter extends HecateExpression {
    
    HecateExpression object;
    Token name;

    public Getter(HecateExpression object, Token name) {
        this.object = object;
        this.name = name;
    }

    public HecateExpression getObject() {
        return object;
    }

    public Token getName() {
        return name;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
