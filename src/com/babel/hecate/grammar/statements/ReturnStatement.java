package com.babel.hecate.grammar.statements;

import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.scanner.Token;

public class ReturnStatement extends HecateStatement {

    HecateExpression returnValue;
    Token returnkeyword;


    public ReturnStatement(HecateExpression returnValue, Token returnkeyword) {
        this.returnValue = returnValue;
        this.returnkeyword = returnkeyword;
    }

    public Token getReturnkeyword() {
        return returnkeyword;
    }

    public HecateExpression getReturnValue() {
        return returnValue;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
    
}
