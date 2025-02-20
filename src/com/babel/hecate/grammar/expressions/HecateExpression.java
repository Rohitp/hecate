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
        T visit(VariableExpression expression);
        T visit(AssignmentExpression expression);
        T visit(LogicalExpression expression);
        T visit(FunctioncallExpression expression);
        T visit(Getter expression);
        T visit(Setter expression);
    }

    public abstract <T> T accept(Visitor<T> visitor);


    
}
