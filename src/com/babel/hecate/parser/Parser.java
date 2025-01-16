package com.babel.hecate.parser;

import com.babel.hecate.Hecate;
import com.babel.hecate.grammar.expressions.AssignmentExpression;
import com.babel.hecate.grammar.expressions.BinaryExpression;
import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.GroupExpression;
import com.babel.hecate.grammar.expressions.LiteralExpression;
import com.babel.hecate.grammar.expressions.UnaryExpression;
import com.babel.hecate.grammar.expressions.VariableExpression;
import com.babel.hecate.grammar.statements.BlockStatement;
import com.babel.hecate.grammar.statements.ExpressionStatement;
import com.babel.hecate.grammar.statements.HecateStatement;
import com.babel.hecate.grammar.statements.PrintStatement;
import com.babel.hecate.grammar.statements.VariableStatement;

import java.util.ArrayList;

import com.babel.hecate.scanner.Token;
import com.babel.hecate.scanner.TokenEnum;

// Defining a parser with the following rules
// (Also see -> https://en.wikipedia.org/wiki/LR_parser)
// Order of precedence, from lowest to highest - the same as C
// =               : right associative
// ==, !=          : left associative
// >, <, <=, >=    : left associative
// +, -            : left associative
// /, *            : left associative 
// !,-             : right associative
// Recursive descent parsing is where we're going -> https://en.wikipedia.org/wiki/Recursive_descent_parser
 // This is preferable also because, each production drectly translates to a function. 
 // We can continue with the visitor pattern and add a visitor for each object
 
// Adding support for statements here
// A program has a list of statements
// A statement can be of many types - branch, loop, expression, declaration
// The precedence of rules is that anywhere a declaration is possible anything else is possible.

 // On a fundamental level this takes tokens and converts it into expressions. 
public class Parser {

    private final ArrayList<Token> tokens;
    private int ptr = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }


    public ArrayList<HecateStatement> parse() {
        ArrayList<HecateStatement> statements = new ArrayList<>();
        while(tokens.get(ptr).getType() != TokenEnum.EOF) {
            statements.add(processStatement());
        }
        return statements;
    }



    // Unless it's a literal or unary for a repl, everything is a binary expression
    // In theory you have a program like
    // !true
    // print false
    // but again I can't figure out a single instance outside the repl where it wouldn't make sense
    public HecateExpression formExpression() {
        return assignment();
    }


    // This is tricky as we need to account for the fact that
    // Assignment cannot be treated without context 
    // var foo = 5
    // foo = 6
    // here foo cannot be expanded without context, even if we look ahead and use the equald sign 
    // we can have assignment be levels deep like 
    // foo.bar.baz = "hello"
    // Like a binary expression we recursively call it only on the right hand side
    public HecateExpression assignment() {
        HecateExpression expression =  equals();

        // Only consider asssignment here
        if(match(TokenEnum.EQUAL)) {
            Token equal = tokens.get(ptr -1);
            HecateExpression right = assignment();
            
            if(expression instanceof VariableExpression) {

                Token token = ((VariableExpression)expression).getName();
                return new AssignmentExpression(right, token);
            }

            throw parserError(equal, "Invalid assignment target");

        }

        return expression;
    }

    // Can potentially be infinitely long 
    // a == b == b == d and so on
    private HecateExpression equals() {
        HecateExpression left = comparisson();
        while(match(TokenEnum.EQUAL_EQUAL, TokenEnum.NOT_EQUAL)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = comparisson();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }
    

    private HecateExpression comparisson() {
        HecateExpression left = summations();
        while(match(TokenEnum.GREATER, TokenEnum.LESSER, TokenEnum.GREATER_EQUAL, TokenEnum.LESSER_EQUAL)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = summations();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    private HecateExpression summations() {
        HecateExpression left = multiplication();
        while(match(TokenEnum.PLUS, TokenEnum.MINUS)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = multiplication();
            left = new BinaryExpression(left, operator, right);

        }
        return left;
    }

    private HecateExpression multiplication() {
        HecateExpression left = unary();
        while(match(TokenEnum.ASTERISK, TokenEnum.OBELUS)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = unary();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }



    // These return unary expressions
    private HecateExpression unary() {
        // Expression expr = literal();
        while(match(TokenEnum.NOT, TokenEnum.MINUS)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = literal();
            return new UnaryExpression(operator, right);
        }
        return literal();
    }

    // Literal can be true, false, string, number or Nietzsche
    // Adding grouping support here. 
    private HecateExpression literal() {

        // common cases
        if(match(TokenEnum.TRUE))
            return new LiteralExpression(true);
        if(match(TokenEnum.FALSE))
            return new LiteralExpression(false);
        if(match(TokenEnum.NIETZSCHE))
            return new LiteralExpression(null);
        if(match(TokenEnum.NUMBER, TokenEnum.STRING))
            return new LiteralExpression(tokens.get(ptr -1).getLiteral());
        if(match(TokenEnum.IDENTIFIER))
            return new VariableExpression(tokens.get(ptr -1));


        // Parse and match griup expressions    
        if(match(TokenEnum.LEFT_BRACKET)) {
            HecateExpression group = formExpression();


            // Need to figure out the error interface. We synchronise here?
            if(tokens.get(ptr).getType() == TokenEnum.EOF) {
                System.out.println("Missing right brace");
            }
            if (tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() == TokenEnum.RIGHT_BRACKET) {
                    ptr++;
            }

            return new GroupExpression(group);
        }
        

        // I need to figure out how to fix the unary stack. This will definitely come back to bite me.
        // return new LiteralExpression("NA");
        throw parserError(tokens.get(ptr), "UNexpected parsing error");
    }



    // Statements for a hierarchy similair to expressions
    // For example PRINT 5 + 8 needs to evaluate the RHS expression before coming back
    private HecateStatement processStatement() {

        try {
            if(match(TokenEnum.VAR)) {
                return declarations();
            }
            return nondeclarations(); 
        } catch(ParserError pe) {
            dontpanic();
            return null;
        }
    }

    private HecateStatement declarations() {
        try {
            Token varname = iterate(TokenEnum.IDENTIFIER, "Expected variable name - name your variables");
            HecateExpression expr = null;
            if(match(TokenEnum.EQUAL)) {
                expr = formExpression();
            }

            iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
            return new VariableStatement(varname, expr);
        } catch (ParserError pe) {
            dontpanic();
            return null;
        }
    }

    private HecateStatement nondeclarations() {
        if(match(TokenEnum.PRINT)) {
            return printStatement();
        } 

        if(match(TokenEnum.LEFT_BRACE)) {
            return new BlockStatement(blockStatement());

        }

        return expressionStatement();
    }

    private HecateStatement printStatement() {
        HecateExpression expr = formExpression();

        iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
        return new PrintStatement(expr);
    }

    private ArrayList<HecateStatement> blockStatement() {
        ArrayList<HecateStatement> statements = new ArrayList<>();

        while(tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() != TokenEnum.RIGHT_BRACE) {
            statements.add(processStatement());
        }
        iterate(TokenEnum.RIGHT_BRACE, "Missing matching } symbol");
        return statements;
    }

    private HecateStatement expressionStatement() {
        HecateExpression expr = formExpression();
        iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
        return new ExpressionStatement(expr);
    }



    private Token iterate(TokenEnum type, String error) {

        try {
            if(tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() == type) {
                ptr++;
                return tokens.get(ptr -1);
            }

            throw new ParserError(tokens.get(ptr), error);
        } catch(ParserError pe) {
            throw new ParserError(tokens.get(ptr), error);
        }
        
    }

    

    // Matches a token type and gets to the next step if not end. The main ticking clock of the program
    private boolean match(TokenEnum ...tokentypes) {

        for(TokenEnum tokentype : tokentypes) {    
            if(tokens.get(ptr).getType() != TokenEnum.EOF)  {
                if(tokens.get(ptr).getType() == tokentype) {
                    ptr++;
                    return true;
                }
            }
                
                
        }
        return false;
    }

    private ParserError parserError(Token token, String message) {
        Hecate.errorHandler(token, "Error at token "+token.getLexeme());
        ParserError pe = new ParserError(token, message);
        throw pe;
    }


    // Gets us to the next statement if we start panicking over an error
    private void dontpanic() {

        if(tokens.get(ptr).getType() != TokenEnum.EOF) {
            ptr++;
        }

        while(tokens.get(ptr).getType() != TokenEnum.EOF) {

            if(tokens.get(ptr -1).getType() == TokenEnum.SEMICOLON) return;

            switch(tokens.get(ptr).getType()) {
                case VAR:
                case IF:
                case ELSE:
                case FOR:
                case WHILE:
                case FUNC:
                case PRINT:
                    return;
                default:
                    assert true;
            }

            if(tokens.get(ptr).getType() != TokenEnum.EOF) {
                ptr++;
            }
        }
    }
    
}
