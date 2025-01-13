package com.babel.hecate;

import com.babel.hecate.interpreter.InterpreterError;
import java.util.HashMap;

import com.babel.hecate.scanner.Token;


// Defining a class to handles variables and strore them with scoping
// The variables are stored in scope defined hashmaps
// There are also interesting decisions to be made
// Here we let implicit variable re defenitoon
// var a = 5;
// var a = hello;  
// In the same scope is valid. Should be check for defention vs instantiation seperately? Probably. But too much work
// If there isn't a value we set it to 42 for a lark. 

public class Variables {

    private final HashMap<String, Object> variables = new HashMap<>();


    public void declare(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(Token key) {
        if(variables.containsKey(key.lexeme)) {
            return variables.get(key.lexeme);
        }
        throw new InterpreterError(key, "Undefined variable "+key.getLexeme());
    }
    
}
