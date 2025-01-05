package com.babel.hecate.grammar;


// We eventually add a visitor for each expression here. Plus the holder for accepting the visitor
abstract class Expression {

    interface Visitor<T> {
        T visit(BinaryExpression expression);
    }

    abstract <T> T accept(Visitor<T> visitor);


    
}
