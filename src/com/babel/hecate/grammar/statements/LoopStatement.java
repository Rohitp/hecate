package com.babel.hecate.grammar.statements;

import com.babel.hecate.grammar.expressions.HecateExpression;

public class LoopStatement extends HecateStatement {

    private HecateStatement statement;
    private HecateExpression condition;

    public LoopStatement(HecateStatement statement, HecateExpression condition) {
        this.statement = statement;
        this.condition = condition;
    }
    public HecateStatement getStatement() {
        return statement;
    }
    public HecateExpression getCondition() {
        return condition;
    }


    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    
}
