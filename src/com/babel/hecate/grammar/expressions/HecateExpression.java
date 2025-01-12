package com.babel.hecate.grammar.expressions;


// We eventually add a visitor for each expression here. Plus the holder for accepting the visitor
// Also apparantly expression and statement are taken in java.beans. 
// Refacotoring to avoid annoynaces. 
public abstract class HecateExpression {

    public interface Visitor<T> {
        T visit(BinaryExpression expression);
        T visit(LiteralExpression expression);
        T visit(UnaryExpression expression);
        T visit(GroupExpression expression);
    }

    public abstract <T> T accept(Visitor<T> visitor);


    
}
