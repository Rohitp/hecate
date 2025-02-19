package com.babel.hecate.prototypes;


// Okay, so I wanted to implement a prototype system, because I assumed the mutability where all objects are references
// Will make it simple.
// But classes are so much easier.
// In doing this research I found that multiple dispatch is also a thing.
// -> https://en.wikipedia.org/wiki/Multiple_dispatch
// Python even has it -> https://pypi.org/project/multipledispatch/


// This holds all classes and their inheritance information
public class HecatePrototypes {

    final String name;
    final HecatePrototypes parent;

    public HecatePrototypes(String name, HecatePrototypes parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "HecateClass{" + "name='" + name + '\'' +", parent=" + parent +'}';
    }

    
}