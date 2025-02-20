package com.babel.hecate.prototypes;

import java.util.HashMap;

import com.babel.hecate.interpreter.InterpreterError;
import com.babel.hecate.lambdacalculus.HecateLambda;
import com.babel.hecate.scanner.Token;

// Objects follow javascripts model of prototypes
// Anything can modify anything

public class HecateObject {

    
    // A reference to the class we have
    // A hashmap with the members in it

    private HecatePrototypes HecateClass;

    // Members are initialised only in an instance.
    // As in all things here we're following the javascript defenition
    private final HashMap<String, Object> members = new HashMap<>();

    public HecateObject(HecatePrototypes HecateClass) {
        this.HecateClass = HecateClass;
    }

    // Looks up and gets the neceaary method from the map
    public Object get(Token name) {
        if(members.containsKey(name.lexeme)) {
            return members.get(name.lexeme);
        }


        // We look up if the requisite identifier is a method instead
        // Allows us to do nifty and borderline insane things like
        // var a = dog.bark;
        // a();
        HecateLambda method = HecateClass.functiontrace(name.lexeme);
        if(method != null) {
            return method.self(this);
        }

        // So I considered making this an error as masking object values is a lot more harmful
        // Than a default value.
        // This essentially means that
        // Object.<anything> = 42. you can never tell if a field exists or not.
        // But hell, in for a penny, in for a pound?
        return 42;

        // throw new InterpreterError(name, "Undefined property "+name.lexeme);
    }

    public void set(Token name, Object value) {
        members.put(name.lexeme, value);
    }



    @Override
    public String toString() {
        return "HecateObject{" + "HecateClass=" + HecateClass + '}';
    }


    
}
