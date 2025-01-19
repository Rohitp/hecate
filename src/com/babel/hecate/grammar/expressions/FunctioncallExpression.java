package com.babel.hecate.grammar.expressions;

import java.util.ArrayList;

import com.babel.hecate.scanner.Token;

public class FunctioncallExpression extends HecateExpression {

    HecateExpression namecall;
    ArrayList<HecateExpression> args;
    Token token;
    
    public FunctioncallExpression(HecateExpression namecall, ArrayList<HecateExpression> args, Token token) {
        this.namecall = namecall;
        this.args = args;
        this.token = token;
    }
    public HecateExpression getNamecall() {
        return namecall;
    }
    public ArrayList<HecateExpression> getArgs() {
        return args;
    }
    public Token getToken() {
        return token;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        // TODO Auto-generated method stub
        return visitor.visit(this);
    }

    
}
