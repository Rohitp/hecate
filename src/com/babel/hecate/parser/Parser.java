package com.babel.hecate.parser;

import com.babel.hecate.Hecate;
import com.babel.hecate.grammar.expressions.BinaryExpression;
import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.GroupExpression;
import com.babel.hecate.grammar.expressions.LiteralExpression;
import com.babel.hecate.grammar.expressions.UnaryExpression;
import com.babel.hecate.grammar.statements.ExpressionStatement;
import com.babel.hecate.grammar.statements.HecateStatement;
import com.babel.hecate.grammar.statements.PrintStatement;

import java.util.ArrayList;

import com.babel.hecate.scanner.Token;
import com.babel.hecate.scanner.TokenEnum;

// Defining a parser with the following rules
// (Also see -> https://en.wikipedia.org/wiki/LR_parser)
// Order of precedence, from lowest to highest - the same as C
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
        return equals();
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
        if(match(TokenEnum.NUMBER, TokenEnum.STRING, TokenEnum.IDENTIFIER))
            return new LiteralExpression(tokens.get(ptr -1).getLiteral());


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
        if(match(TokenEnum.PRINT)) {
            return printStatement();
        } 

        return expressionStatement();
    }

    private HecateStatement printStatement() {
        HecateExpression expr = formExpression();
        iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
        return new PrintStatement(expr);
    }

    private HecateStatement expressionStatement() {
        HecateExpression expr = formExpression();
        iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
        return new ExpressionStatement(expr);
    }



    private Token iterate(TokenEnum type, String error) {

        if(tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() == type) {
            ptr++;
            return tokens.get(ptr -1);
        }

        throw parserError(tokens.get(ptr), error);
        
    }

    

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
        return new ParserError();
    }
    
}
