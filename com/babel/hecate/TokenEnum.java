package com.babel.hecate;

enum TokenEnum {
    // Foundations and base types
    // Okay, so the concept of NIETZSCHE as a specifier for null amuses me - the abyss looks back at you
    // Am I reinventing a billion dollar mistake? Seems fun. Let's see
    TRUE, FALSE, NIETZSCHE, INT, DOUBLE, CHAR, STRING, VAR,

    //Comparison
    GREATER, LESSER, EQUAL, NOT, NOT_EQUAL, GREATER_EQUAL, LESSER_EQUAL, EQUAL_EQUAL,

    // Control and branching
    IF, ELSE, OR, AND, WHILE, FOR, PRINT,
    
    // OOP and abstraction here
    CLASS, FUNC, THIS, SUPER, RETURN,

    // Other sundries
    // https://en.wikipedia.org/wiki/Obelus
    LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, 
    PLUS, MINUS, OBELUS, ASTERISK, SEMICOLON,

    //Not sure if I'll use these. Let's see
    COLON, MODULO, EOF;


}