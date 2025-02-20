package com.babel.hecate.grammar.expressions;

import com.babel.hecate.scanner.Token;

// Setter. Standard value setters.
public class Setter extends HecateExpression {
    
    HecateExpression object;
    Token name;
    HecateExpression value;

    public Setter(HecateExpression object, Token name, HecateExpression value) {
        this.object = object;
        this.name = name;
        this.value = value;
    }

    public Token getToken() {
        return name;
    }
    public HecateExpression getObject() {
        return object;
    }

    public HecateExpression getValue() {
        return value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
    
}
