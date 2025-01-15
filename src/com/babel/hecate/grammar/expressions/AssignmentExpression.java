package com.babel.hecate.grammar.expressions;

import com.babel.hecate.scanner.Token;

// An assignment expression is surprisingly complicated
// For example if we have 
// var foo = 5
// foo = 7, we don't want to evaluate foo here.
// The cases can get complex when you have foo.bar.baz = 7
public class AssignmentExpression extends HecateExpression {
   

    HecateExpression expression;
    Token token;
    
    public AssignmentExpression(HecateExpression expression, Token token) {
        this.expression = expression;
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public HecateExpression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
    
}
