package com.babel.hecate;


// The token class is an immutable encapsulation of a token. Containing the raw lexeme
// A line number and offset from the start of the file is stored 
public class Token {
    final TokenEnum type;
    final String lexeme;
    final String literal;
    final int lineNumber;
    final int offset;

    public Token(TokenEnum type, String lexeme, String literal, int lineNumber, int offset) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.lineNumber = lineNumber;
        this.offset = offset;
    }

    public String toString() {
        return "The lexeme is "+this.lexeme+" with literal "+this.literal+" with Token "+this.type.toString()+" on line "+Integer.toString(lineNumber);
    }

}

  
