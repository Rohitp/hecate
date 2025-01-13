package com.babel.hecate.parser;

import com.babel.hecate.scanner.Token;

// A class where we throw runtime errors and synchronise back.
public class ParserError extends RuntimeException{

    public final Token token;


    // Java's runtime exception takes a message we pass on to the stack.
    ParserError(Token token, String message) {
        super(message);
        this.token = token;
    }
    
}
