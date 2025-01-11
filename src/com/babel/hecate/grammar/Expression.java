package com.babel.hecate.grammar;


// We eventually add a visitor for each expression here. Plus the holder for accepting the visitor
public abstract class Expression {

    public interface Visitor<T> {
        T visit(BinaryExpression expression);
        T visit(LiteralExpression expression);
        T visit(UnaryExpression expression);
        T visit(GroupExpression expression);
    }

    public abstract <T> T accept(Visitor<T> visitor);


    
}
