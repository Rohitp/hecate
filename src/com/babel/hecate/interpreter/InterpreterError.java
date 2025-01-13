package com.babel.hecate.interpreter;

import com.babel.hecate.scanner.Token;

public class InterpreterError extends RuntimeException {
    public final Token token;


    // Java's runtime exception takes a message we pass on to the stack.
    public InterpreterError(Token token, String message) {
        super(message);
        this.token = token;
    }
    
}
