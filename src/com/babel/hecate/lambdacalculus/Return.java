package com.babel.hecate.lambdacalculus;



// So returning from a function was more complicated than necessary
// In our java stack a function can be deep within execution
// Both within itself for recursion
// And within the parser chain. 
// Trying to hijack the return keywords conditionally wasnt successful
// There's too much noise and it pollutes every function along the way. 
// Throwing an exception and catching it is the best way
public class Return extends RuntimeException {

    // Supress all errors and take the value of the return expression
    final Object returnvalue;

    public Return(Object value) {
        super(null, null, false, false);
        this.returnvalue = value;
    }
    
}
