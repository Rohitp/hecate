package com.babel.hecate.interpreter;
import com.babel.hecate.Hecate;
import com.babel.hecate.grammar.expressions.BinaryExpression;
import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.GroupExpression;
import com.babel.hecate.grammar.expressions.LiteralExpression;
import com.babel.hecate.grammar.expressions.UnaryExpression;

public class Interpreter implements HecateExpression.Visitor<Object> {
    

    public Object interpret(HecateExpression expr) {
        Object result = 0;
        try {
            result = expr.accept(this);
        } catch(InterpreterError error) {
            Hecate.runtimeError(error);
        }

        return result;
    }

    @Override
    public Object visit(LiteralExpression le) {
        return le.getLiteral();
    }

    @Override
    public Object visit(GroupExpression ge) {
        return ge.getExpression().accept(this);
    }

    @Override
    public Object visit(UnaryExpression ue) {
        Object right = ue.getExpression().accept(this);

        // Here we make every value have a bool value
        // null and false and 0 are falsey
        // literally everything else is truthy
        // thats it. literally it
        switch(ue.getOperator().getType()) {
            case MINUS:
                if(!(right instanceof Double))
                    throw new InterpreterError(ue.getOperator(), "Incompatible types for negation");
                return -(Double)right;
            case NOT:           
                return !getbool(right);
            default:
                // Handle error here as well
                throw new InterpreterError(ue.getOperator(), "Incompatible type for operation");
            
        }

        // TODO: Define error methods for the Interpreter. 
    }

    // Again need to handle casting and errors
    @Override
    public Object visit(BinaryExpression be) {
        Object left = be.getLeftExpression().accept(this);
        Object right = be.getRightExpression().accept(this);

        switch(be.getOperator().getType()) {
            case PLUS:
                if(left instanceof Double && right instanceof Double){
                    return (double)left + (double)right;
                } else if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                } else {
                    throw new InterpreterError(be.getOperator(), "Incompatible types for addition");
                }
            case MINUS:
                if(!(left instanceof Double) || !(right instanceof Double))
                    throw new InterpreterError(be.getOperator(), "Incompatible types for subtraction");
                return (double)left - (double)right;
            case OBELUS:
                if(!(left instanceof Double) || !(right instanceof Double))
                    throw new InterpreterError(be.getOperator(), "Incompatible types for division");
                return (double)left / (double)right;
            case ASTERISK:
                if(!(left instanceof Double) || !(right instanceof Double))
                    throw new InterpreterError(be.getOperator(), "Incompatible types for multiplication");
                return (double)left * (double)right;
            case GREATER:
                if(!(left instanceof Double) || !(right instanceof Double))
                    throw new InterpreterError(be.getOperator(), "Incompatible types for comparison");
                return (double)left > (double)right;
            case GREATER_EQUAL:
                if(!(left instanceof Double) || !(right instanceof Double))
                    throw new InterpreterError(be.getOperator(), "Incompatible types for comparison");
                return (double)left >= (double)right;
            case LESSER:
                if(!(left instanceof Double) || !(right instanceof Double))
                    throw new InterpreterError(be.getOperator(), "Incompatible types for comparison");
                return (double)left < (double)right;
            case LESSER_EQUAL:
                if(!(left instanceof Double) || !(right instanceof Double))
                    throw new InterpreterError(be.getOperator(), "Incompatible types for comparison");
                return (double)left <= (double)right;
            case EQUAL_EQUAL:
                return left.equals(right);
            case NOT_EQUAL:
                return !left.equals(right);
            default:
                throw new InterpreterError(be.getOperator(), "Unspecified operand for calculation");
        }


        
    }


    // Should we do more conversions besides 0 and null being falsey? To be added
    public boolean getbool(Object obj) {
        if(obj == null)
            return false;
        if(obj instanceof Boolean)
            return (Boolean)obj;
        if(obj instanceof Integer && ((int)obj == 0))
            return false;
        return true;    
    }


}
    

