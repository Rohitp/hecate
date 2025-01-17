package com.babel.hecate.grammar.statements;

import com.babel.hecate.grammar.expressions.HecateExpression;

public class BranchStatement extends HecateStatement {
    
    private HecateStatement ifbranch;
    private HecateStatement elsebranch;
    private HecateExpression condition;

    public BranchStatement(HecateStatement ifbranch, HecateStatement elsebranch, HecateExpression condition) {
        this.ifbranch = ifbranch;
        this.elsebranch = elsebranch;
        this.condition = condition;
    }


    public HecateStatement getIfbranch() {
        return ifbranch;
    }
    public HecateStatement getElsebranch() {
        return elsebranch;
    }
    public HecateExpression getCondition() {
        return condition;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


    
}
