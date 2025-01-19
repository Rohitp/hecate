package com.babel.hecate.lambdacalculus;

import java.util.ArrayList;

import com.babel.hecate.interpreter.Interpreter;

// https://svn.python.org/projects/python/trunk/Objects/object.c
// Anything that can be called
// Here anything that can be called - including class constructors will implement this
public interface InterfaceLambda {

    Object call(ArrayList<Object> args, Interpreter interpreter);
    int params();
    
}
