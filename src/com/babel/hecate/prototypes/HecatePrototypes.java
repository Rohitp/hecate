package com.babel.hecate.prototypes;

import java.util.ArrayList;

import com.babel.hecate.interpreter.Interpreter;
import com.babel.hecate.lambdacalculus.InterfaceLambda;

// Okay, so I wanted to implement a prototype system, because I assumed the mutability where all objects are references
// Will make it simple.
// But classes are so much easier.
// In doing this research I found that multiple dispatch is also a thing.
// -> https://en.wikipedia.org/wiki/Multiple_dispatch
// Python even has it -> https://pypi.org/project/multipledispatch/


// This holds all classes and their inheritance information
public class HecatePrototypes implements InterfaceLambda {

    final String name;
    final HecatePrototypes parent;

    public HecatePrototypes(String name, HecatePrototypes parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public Object call(ArrayList<Object> args, Interpreter interpreter) {
        return new HecateObject(this);
    }

    @Override
    public String toString() {
        return "HecateClass{" + "name='" + name + '\'' +", parent=" + parent +'}';
    }

    @Override
    public int params() {
        return 0;
    }

    
}