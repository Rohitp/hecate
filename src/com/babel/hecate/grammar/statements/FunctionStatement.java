package com.babel.hecate.grammar.statements;

import java.util.ArrayList;

import com.babel.hecate.scanner.Token;

public class FunctionStatement extends HecateStatement {

    Token func;
    ArrayList<HecateStatement> body;
    ArrayList<Token> parameters;
    
    public FunctionStatement(Token func, ArrayList<HecateStatement> body, ArrayList<Token> parameters) {
        this.func = func;
        this.body = body;
        this.parameters = parameters;
    }
    public Token getFunc() {
        return func;
    }
    public ArrayList<HecateStatement> getBody() {
        return body;
    }
    public ArrayList<Token> getParameters() {
        return parameters;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
