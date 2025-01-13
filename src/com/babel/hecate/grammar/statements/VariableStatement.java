package com.babel.hecate.grammar.statements;

import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.scanner.Token;


// A variable statement is where you declare or assign something. The expression formed becomes a variable expression
public class VariableStatement extends HecateStatement {

    HecateExpression expression;
    Token variablename;

    public VariableStatement(Token variablename, HecateExpression expression) {
        this.expression = expression;
        this.variablename = variablename;
    }


    public HecateExpression getExpression() {
        return expression;
    }

    public Token getVariablename() {
        return variablename;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }



    

    
}
