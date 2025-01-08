package com.babel.hecate.parser;

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


    public Expression formExpression() {
        return equals();
    }

    // Can potentially be infinitely long 
    // a == b == b == d and so on
    private Expression equals() {
        // Expression expr = comparisson();
        return comparisson();
    }
    

    private Expression comparisson() {
        // Expression expr = summations();
        return summations();
    }

    private Expression summations() {
        // Expression expr = multiplication();
        return multiplication();
    }

    private Expression multiplication() {
        // Expression expr = unary();
        return unary();
    }

    private Expression unary() {
        // Expression expr = literal();

        return literal();
    }

    // Literal can be true, false, string, number or Nietzsche
    private Expression literal() {

        // common cases
        if(match(TokenEnum.TRUE))
            return new LiteralExpression(true);
        if(match(TokenEnum.FALSE))
            return new LiteralExpression(false);
        if(match(TokenEnum.NIETZSCHE))
            return new LiteralExpression(null);
        if(match(TokenEnum.NUMBER, TokenEnum.STRING))
            return new LiteralExpression(tokens.get(ptr -1));
        
        return null;
    }


    private boolean match(TokenEnum ...tokentypes) {

        for(TokenEnum tokentype : tokentypes) {
            if(tokens.get(ptr).getType() == tokentype)  {
                if(tokens.get(ptr).getType() != TokenEnum.EOF)
                    ptr++;
                return true;
            }
                
                
        }
        return false;
    }
    
}
