# Hecate

Hecate is a dynamically typed, object-oriented language I wrote in Java. I borrowed from Python and, regrettably, JavaScript. Variables declared without a value start at 42.

## Run an example

With a JDK and Zsh installed, run this from the repository root:

```sh
zsh build_and_run.sh tests/parsertest-class.txt
```

The script compiles the interpreter and runs the [sample program](tests/parsertest-class.txt). It creates a dog named maple, calls a method on it, and prints:

```text
maple is barking at night
100
```

There are more examples in [tests](tests). Here’s one with classes, functions, and recursion:

```
func fib(n) {
    if(n <= 2) {
        return 1;
    }
    return fib(n-1) + fib(n - 2);
}


class Dog {
    greet() {
        print "This is a static function and I'm barking!";
    }
    printname() {
        print "My name is "+self.name+"!";
    }
}


Dog().greet();
var d = Dog();
d.name = "Hector";
d.printname();
// Prints the fifth Fibonacci number
print "This function is recursive as well. Neat, right!";
print fib(5);
```

## What I read while building it

- [Robert Nystrom's amazing book](https://www.amazon.co.uk/dp/0990582930?ref=ppx_yo2ov_dt_b_fed_asin_tit) on building interpeters
- [Nikalaus Wirth's treatise on compiler design](https://docslib.org/doc/9121091/niklaus-wirths-compiler-construction)
- [The holy bible of compiler design](https://www.amazon.co.uk/Compilers-Principles-Techniques-Alfred-Aho/dp/0201100886)
