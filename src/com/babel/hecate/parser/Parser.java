package com.babel.hecate.parser;

import java.beans.Expression;
import java.util.ArrayList;

import com.babel.hecate.scanner.Token;

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
 
public class Parser {

    private final ArrayList<Token> tokens;
    private int ptr = 0;

    Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }


    private Expression equals() {

    }
    
    private Expression comparisson() {

    }

    private Expression summations() {

    }

    private Expression multiplication() {

    }

    private Expression unary() {

    }

    private Expression literal() {
        
    }
    
}
