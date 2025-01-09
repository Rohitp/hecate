package com.babel.hecate.parser;

import com.babel.hecate.grammar.BinaryExpression;
import com.babel.hecate.grammar.Expression;
import com.babel.hecate.grammar.GroupExpression;
import com.babel.hecate.grammar.LiteralExpression;
import com.babel.hecate.grammar.UnaryExpression;

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
 


 // On a fundamental level this takes tokens and converts it into expressions. 
public class Parser {

    private final ArrayList<Token> tokens;
    private int ptr = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }


    // Unless it's a literal or unary for a repl, everything is a binary expression
    // In theory you have a program like
    // !true
    // print false
    // but again I can't figure out a single instance outside the repl where it wouldn't make sense
    public Expression formExpression() {
        return equals();
    }

    // Can potentially be infinitely long 
    // a == b == b == d and so on
    private Expression equals() {
        Expression left = comparisson();
        while(match(TokenEnum.EQUAL_EQUAL, TokenEnum.NOT_EQUAL)) {
            Token operator = tokens.get(ptr -1);
            Expression right = comparisson();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }
    

    private Expression comparisson() {
        Expression left = summations();
        while(match(TokenEnum.GREATER, TokenEnum.LESSER, TokenEnum.GREATER_EQUAL, TokenEnum.LESSER_EQUAL)) {
            Token operator = tokens.get(ptr -1);
            Expression right = summations();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    private Expression summations() {
        Expression left = multiplication();
        while(match(TokenEnum.PLUS, TokenEnum.MINUS)) {
            Token operator = tokens.get(ptr -1);
            Expression right = multiplication();
            left = new BinaryExpression(left, operator, right);

        }
        return left;
    }

    private Expression multiplication() {
        Expression left = unary();
        while(match(TokenEnum.ASTERISK, TokenEnum.OBELUS)) {
            Token operator = tokens.get(ptr -1);
            Expression right = unary();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }



    // These return unary expressions
    private Expression unary() {
        Expression expr = literal();
        while(match(TokenEnum.NOT, TokenEnum.MINUS)) {
            Token operator = tokens.get(ptr -1);
            Expression right = literal();
            expr = new UnaryExpression(operator, right);
        }
        return expr;
    }

    // Literal can be true, false, string, number or Nietzsche
    // Adding grouping support here. 
    private Expression literal() {

        // common cases
        if(match(TokenEnum.TRUE))
            return new LiteralExpression(true);
        if(match(TokenEnum.FALSE))
            return new LiteralExpression(false);
        if(match(TokenEnum.NIETZSCHE))
            return new LiteralExpression(null);
        if(match(TokenEnum.NUMBER, TokenEnum.STRING, TokenEnum.IDENTIFIER))
            return new LiteralExpression(tokens.get(ptr -1).getLexeme());


        // Parse and match griup expressions    
        if(match(TokenEnum.LEFT_BRACKET)) {
            Expression group = formExpression();

            if(tokens.get(ptr).getType() == TokenEnum.EOF) {
                System.out.println("Missing right brace");
            }
            if (tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() == TokenEnum.RIGHT_BRACKET) {
                    ptr++;
            }

            return new GroupExpression(group);
        }
        

        return new LiteralExpression("NA");
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
    
}
