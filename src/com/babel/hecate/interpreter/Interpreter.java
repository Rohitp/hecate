package com.babel.hecate.interpreter;
import java.beans.Expression;
import java.util.ArrayList;
import java.util.HashMap;

import com.babel.hecate.Hecate;
import com.babel.hecate.Variables;
import com.babel.hecate.grammar.expressions.BinaryExpression;
import com.babel.hecate.grammar.expressions.FunctioncallExpression;
import com.babel.hecate.grammar.expressions.Getter;
import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.GroupExpression;
import com.babel.hecate.grammar.expressions.LiteralExpression;
import com.babel.hecate.grammar.expressions.LogicalExpression;
import com.babel.hecate.grammar.expressions.AssignmentExpression;
import com.babel.hecate.grammar.expressions.PrettyPrint;
import com.babel.hecate.grammar.expressions.Setter;
import com.babel.hecate.grammar.expressions.UnaryExpression;
import com.babel.hecate.grammar.expressions.VariableExpression;
import com.babel.hecate.grammar.statements.BlockStatement;
import com.babel.hecate.grammar.statements.BranchStatement;
import com.babel.hecate.grammar.statements.ClassStatement;
import com.babel.hecate.grammar.statements.ExpressionStatement;
import com.babel.hecate.grammar.statements.FunctionStatement;
import com.babel.hecate.grammar.statements.HecateStatement;
import com.babel.hecate.grammar.statements.LoopStatement;
import com.babel.hecate.grammar.statements.PrintStatement;
import com.babel.hecate.grammar.statements.ReturnStatement;
import com.babel.hecate.grammar.statements.VariableStatement;
import com.babel.hecate.lambdacalculus.HecateLambda;
import com.babel.hecate.lambdacalculus.InterfaceLambda;
import com.babel.hecate.lambdacalculus.Return;
import com.babel.hecate.prototypes.HecateObject;
import com.babel.hecate.prototypes.HecatePrototypes;
import com.babel.hecate.prototypes.SelfReference;
import com.babel.hecate.scanner.Token;
import com.babel.hecate.scanner.TokenEnum;

public class Interpreter implements HecateExpression.Visitor<Object>, HecateStatement.Visitor<Integer>{
    

    // globals is a fixed reference to the outermost set of variables
    // variables tracks the current scope

    public final Variables globals = new Variables();
    private Variables variables = globals;
    private HashMap<HecateExpression, Integer> localscope = new HashMap<>();

    public Object interpret(HecateExpression expr) {
        Object result = 0;
        try {
            result = expr.accept(this);
        } catch(InterpreterError error) {
            Hecate.interpreterError(error);
        }

        return result;
    }


    public void executeblockstatements(ArrayList<HecateStatement> statements, Variables variables) {
        Variables prev = this.variables;
        try{
            this.variables = variables;
            executestatements(statements);
        } finally {
            this.variables = prev;
        }
    }

    // Naming is hard. Close to impossible
    public void executestatements(ArrayList<HecateStatement> statements) {

        try {

            for(HecateStatement statement: statements) {
                statement.accept(this);
            }

        } catch(InterpreterError error) {
            Hecate.interpreterError(error);
        }

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

    @Override
    public Object visit(Getter getter) {
      Object value = getter.getObject().accept(this);
      if(value instanceof HecateObject) {
        return ((HecateObject) value).get(getter.getName());
      }

      // This is when we try and get a property of a non class -> "hello".name;
      throw new InterpreterError(getter.getName(), "Only objects have properties");
    }

    @Override
    public Object visit(Setter setter) {
        Object object = setter.getObject().accept(this);


        Object value = setter.getValue().accept(this);
        if(object instanceof HecateObject) {
            ((HecateObject)object).set(setter.getToken(), value);
            return value;
        }

        throw new InterpreterError(setter.getToken(), "Trying to set a value to a non class object");
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


    @Override
    public Object visit(VariableExpression ve) {
        return findvar(ve.getName(), ve);
    }

    @Override
    public Object visit(AssignmentExpression ae) {
        Object value = interpret(ae.getExpression());
        Integer level = localscope.get(ae);
        if(level != null) {
            variables.assignvalue(level, ae.getToken(), value);
        } else {
            globals.assign(ae.getToken(), value);
        }


        return value;
    }

    // https://docs.python.org/3/reference/expressions.html#boolean-operations
    // Following the same spec here
    // "forty-two" or 42 -> returns forty-two
    // "forty-two" and 42 -> returns 42
    @Override
    public Object visit(LogicalExpression le) {
        Object left = le.getLeftExpression().accept(this);
        Object right = le.getRightExpression().accept(this);
        if(le.getOperator().getType() == TokenEnum.OR) {
            if(getbool(left)) return left;
        } else {
            if(!getbool(left)) return right;
        }

        return le.getRightExpression().accept(this);
         
    }

    @Override
    public Object visit(SelfReference sr) {
        return findvar(sr.getToken(), sr);
    }

    @Override
    public Object visit(FunctioncallExpression fe) {

        // Evaluating what calls the function
        Object func = interpret(fe.getNamecall());


        ArrayList<Object> args = new ArrayList<>();
        for(HecateExpression exp : fe.getArgs()) {
            args.add(interpret(exp));
        }


        if(!(func instanceof InterfaceLambda)) {
            throw new InterpreterError(fe.getToken(), "Not a callable type");
        }

        
        InterfaceLambda fn = (InterfaceLambda)func;

        // So once again javascript is insane - if the number of params of a function dont match
        // JS simply discards extra params, or adds undefined till it gets to the number
        // The more I look into it, the more insane it is
        if(args.size() != fn.params()) {
            throw new InterpreterError(fe.getToken(), "Parameter mismatch - expected"+fn.params()+" got"+args.size());
        }

        return fn.call(args, this);
    }


    // Binding for statc analysis and looking up variable scopes
    public void bind(HecateExpression expr, int level) {
        localscope.put(expr, level); 
    }

    private Object findvar(Token name, HecateExpression expr) {
        Integer level = localscope.get(expr);
        if(level != null) {
            return variables.seekvalue(level, name.getLexeme());
        } else {
            return globals.get(name);
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

    @Override
    public Integer visit(PrintStatement ps) {
        Object result = interpret(ps.getExpression());
        System.out.println(result.toString());
        return 0;
    }

    @Override
    public Integer visit(ExpressionStatement es) {
        interpret(es.getHe());
        return 0;
    }



    // Variables that are uninitialised are 42 by default
    // https://hitchhikers.fandom.com/wiki/42
    @Override
    public Integer visit(VariableStatement vs) {
        Object value = vs.getExpression() == null? 42 : interpret(vs.getExpression());
        variables.declare(vs.getVariablename().getLexeme(), value);
        return 0;
    }


    // Create an outer enclosing scope with the new one for each block
    // Rinse and repeat
    // Surprisingly ruby has a weird way to shadow variables -> outside in first  -> https://usctcr.medium.com/scope-and-variable-shadowing-in-ruby-7ac80454a055
    // Javascript as usual is bonkers insane 
    // https://www.oreilly.com/library/view/javascript-the-definitive/0596101996/ch04.html#:~:text=If%20you%20assign%20a%20value,the%20body%20of%20a%20function.
    // If it creates a variable in global scope if you assign one without declaring in any scope
    // Strict mode fixes this
    @Override
    public Integer visit(BlockStatement bs) {

        Variables global = this.variables;
        Variables local = new Variables(global);

        try{ 
            this.variables = local;

            for(HecateStatement statement: bs.getStatements()) {
                statement.accept(this);
            }
        } finally {
            this.variables = global;
        }

        return 0;
    }

    @Override
    public Integer visit(BranchStatement bs) {

        Boolean branchcondition = getbool(interpret(bs.getCondition()));
        if(branchcondition) {
            bs.getIfbranch().accept(this);
        } else if(bs.getElsebranch() != null) {
            bs.getElsebranch().accept(this);
        }

        return 0;
    }

    @Override
    public Integer visit(LoopStatement ls) {
        while(getbool(interpret(ls.getCondition()))) {
            ls.getStatement().accept(this);
        }
        return 0;
    }

    @Override
    public Integer visit(FunctionStatement fs) {
        HecateLambda lambda = new HecateLambda(fs, variables);
        variables.declare(fs.getFunc().getLexeme(), lambda);
        return 0;
    }

    @Override
    public Integer visit(ClassStatement cs) {

        // Here 
        variables.declare(cs.getClassname().getLexeme(), null);

        HashMap<String, HecateLambda> methods = new HashMap<>();
        for(FunctionStatement fs : cs.getMethods()) {
            HecateLambda lambda = new HecateLambda(fs, variables);
            methods.put(fs.getFunc().getLexeme(), lambda);
        }
        HecatePrototypes prototype = new HecatePrototypes(cs.getClassname().lexeme,null, methods);
        // This weird quirk is needed so that the class can refer to itself
        // https://stackoverflow.com/questions/336859/var-functionname-function-vs-function-functionname
        variables.assign(cs.getClassname(), prototype);
        return 0;
    }

    @Override
    public Integer visit(ReturnStatement rs) {

        Object returnvalue = null;
        if(rs.getReturnValue() != null) returnvalue = interpret(rs.getReturnValue());
        throw new Return(returnvalue);
    }

 


}
    

