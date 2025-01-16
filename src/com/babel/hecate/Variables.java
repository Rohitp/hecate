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


// Another insane trick I learnt anout JS
// if it doesnt find a variable to assign in the local scopp
// It implicitly creates a global one. Bonkers!
public class Variables {


    // A scope one level deeper than the current one
    // var x = 10;
    // print x
    // {
    //     var x = "hello"
    //     print x
    // }
    // print x
    // 10
    // "hello"
    // 10
    // So we keep a track of where we're in, going from innermost to outermost

    final Variables innerscope;
    private final HashMap<String, Object> variables = new HashMap<>();



    // We set two constructors here
    // If variables is initalised without anything, it's the default global scope
    // If variables is initalised within itself, we treat it as an inner scope
    // This recurison should let us nest variables
    public Variables() {
        this.innerscope = null;
    }

    public Variables(Variables scope) {
        this.innerscope = scope;
    }

    // Default scope declaration
    public void declare(String name, Object value) {
        variables.put(name, value);
    }


    // Assign makes sure the variable is declared before setting value
    public void assign(Token var, Object value) {

        if(innerscope != null) {
            innerscope.assign(var, value);
            return;
        } else if(variables.containsKey(var.getLexeme())) {
            variables.put(var.getLexeme(), value);
            return;
        }
        throw new InterpreterError(var, "Vairable "+var.getLexeme()+" not initialised");
    }

    public Object get(Token key) {
        if(innerscope != null && innerscope.variables.containsKey(key.getLexeme())) {
            return innerscope.variables.get(key.lexeme);
        }

        if(variables.containsKey(key.lexeme)) {
            return variables.get(key.lexeme);
        }
        throw new InterpreterError(key, "Undefined variable "+key.getLexeme());
    }
    
}
