package com.babel.hecate.scanner;

import java.util.HashMap;
import java.util.Stack;

import com.babel.hecate.Hecate;
import com.babel.hecate.grammar.expressions.AssignmentExpression;
import com.babel.hecate.grammar.expressions.BinaryExpression;
import com.babel.hecate.grammar.expressions.FunctioncallExpression;
import com.babel.hecate.grammar.expressions.GroupExpression;
import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.LiteralExpression;
import com.babel.hecate.grammar.expressions.LogicalExpression;
import com.babel.hecate.grammar.expressions.UnaryExpression;
import com.babel.hecate.grammar.expressions.VariableExpression;
import com.babel.hecate.grammar.statements.BlockStatement;
import com.babel.hecate.grammar.statements.BranchStatement;
import com.babel.hecate.grammar.statements.ExpressionStatement;
import com.babel.hecate.grammar.statements.FunctionStatement;
import com.babel.hecate.grammar.statements.HecateStatement;
import com.babel.hecate.grammar.statements.LoopStatement;
import com.babel.hecate.grammar.statements.PrintStatement;
import com.babel.hecate.grammar.statements.ReturnStatement;
import com.babel.hecate.grammar.statements.VariableStatement;
import com.babel.hecate.interpreter.Interpreter;


// For satic analysis - the tools are verry similair to an interpretter
// So it should be easy enough to implement
public class Analyser implements HecateExpression.Visitor<Void>, HecateStatement.Visitor<Void> {

    private final Interpreter interpreter;

    // We maintain a stack where we push each successive closure or functions scope
    // Refer to it for the duration of the pass
    // Then pop it once it's done
    // We also only use this for local closures. Not the global scope
    // If we can't find it here it's global
    private final Stack<HashMap<String, Boolean>> scopes = new Stack<>(); 

    public Analyser(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    
    @Override
    public Void visit(BlockStatement statement) {
        scope();
        for(HecateStatement stmt: statement.getStatements()) {
            staticanalyse(stmt);
        }
        descope();
        return null;
    }

    @Override
    public Void visit(VariableStatement statement) {
        declare(statement.getVariablename());
        if(statement.getExpression() != null) {
            staticanalyse(statement.getExpression());
        }

        define(statement.getVariablename());

        return null;
    }

    @Override
    public Void visit(ReturnStatement statement) {
        if(statement.getReturnValue() != null) {
            staticanalyse(statement.getReturnValue());
        }
        return null;
    }

    @Override
    public Void visit(FunctionStatement statement) {
        declare(statement.getFunc());
        define(statement.getFunc());
        scope();
        for(Token parameter : statement.getParameters()) {
            declare(parameter);
            define(parameter);
        }

        for(HecateStatement stmt: statement.getBody()) {
            staticanalyse(stmt);
        }
        descope();
        return null;
    }

    @Override
    public Void visit(ExpressionStatement statement) {
        staticanalyse(statement.getHe());
        return null;
    }

    @Override
    public Void visit(BranchStatement statement) {
        staticanalyse(statement.getCondition());
        staticanalyse(statement.getIfbranch());
        if(statement.getElsebranch() != null) {
            staticanalyse(statement.getElsebranch());
        }
        return null;
    }

    @Override
    public Void visit(PrintStatement statement) {
        staticanalyse(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(LoopStatement statement) {
        staticanalyse(statement.getCondition());
        staticanalyse(statement.getStatement());
        return null;
    }

   

    private void scope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void descope() {
        scopes.pop();
    }

    private void staticanalyse(HecateStatement statement) {
        statement.accept(this);
    }

    private void staticanalyse(HecateExpression expression) {
        expression.accept(this);
    }

    private void declare(Token name) {
        if(scopes.isEmpty()) return;
        scopes.peek().put(name.getLexeme(), false);
    }

    private void define(Token name) {
        if(scopes.isEmpty()) return;
        scopes.peek().put(name.getLexeme(), true);
    }

    private void analysescope(HecateExpression expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.bind(expr, scopes.size() - 1 - i);
                return;
            }

        }
    }


    @Override
    public Void visit(VariableExpression expr) {
        if(!scopes.isEmpty() && scopes.peek().get(expr.getName().getLexeme()) == Boolean.FALSE) {
            Hecate.errorHandler(expr.getName(), "Can't reinitialise a variable here");
        }
        analysescope(expr, expr.getName());
        return null;
    }


    @Override
    public Void visit(AssignmentExpression expr) {
        staticanalyse(expr.getExpression());
        analysescope(expr, expr.getToken());
        return null;
    }

    @Override
    public Void visit(BinaryExpression expression) {
        staticanalyse(expression.getLeftExpression());
        staticanalyse(expression.getRightExpression());
        return null;
    }

    @Override
    public Void visit(FunctioncallExpression expression) {
        staticanalyse(expression.getNamecall());
        for(HecateExpression arg: expression.getArgs()) {
            staticanalyse(arg);
        }
        return null;
    }

    @Override
    public Void visit(GroupExpression expression) {
        staticanalyse(expression.getExpression());
        return null;
    }

    @Override
    public Void visit(LiteralExpression expression) {
        return null;
    }

    @Override
    public Void visit(LogicalExpression expression) {
        staticanalyse(expression.getLeftExpression());
        staticanalyse(expression.getRightExpression());
        return null;
    }

    @Override
    public Void visit(UnaryExpression expression) {
        staticanalyse(expression.getExpression());
        return null;
    }





    
}
