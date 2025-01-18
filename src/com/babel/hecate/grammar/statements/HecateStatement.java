package com.babel.hecate.grammar.statements;

// Same rules as expressions.
// Abstract clss that holds the visitor and common interfaces
// 
public abstract class HecateStatement {
    
    public interface Visitor<T> {
        T visit(PrintStatement ps);
        T visit(ExpressionStatement es);
        T visit(VariableStatement vs);
        T visit(BlockStatement bs);
        T visit(BranchStatement bs);
        T visit(LoopStatement ls);
    }

    public abstract <T> T accept(Visitor<T> visitor);
}
