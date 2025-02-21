[Hecate](https://en.wikipedia.org/wiki/Hecate) is a dynamically typed (surprsingly easier to design than static typing), objext oriented, interpreted language written in Java. 

This was written on the back of multiple giants, including

- [Robert Nystrom's amazing book](https://www.amazon.co.uk/dp/0990582930?ref=ppx_yo2ov_dt_b_fed_asin_tit) on building interpeters
- [Nikalaus Wirth's treatise on compiler design](https://docslib.org/doc/9121091/niklaus-wirths-compiler-construction)
- [The holy bible of compiler design](https://www.amazon.co.uk/Compilers-Principles-Techniques-Alfred-Aho/dp/0201100886)

There are quirks and idiosynchronacies. On purpose (Null is Nietzsche, Variables are initialised to [42](https://hitchhikers.fandom.com/wiki/42) by default). The language is primarilly modelled after python and regretablly, Javacript.

You can use the shell scripts, *build_and_run.sh* and _run.sh_ to execute. You need to pass a text file containing source code as a parameter.

The full language and syntax of the language is too much effort to write down, but I will update this read me, eventually. Probably. But check the sample programs in tests to get an understanding till then. language-test.txt is a good start



An example that usea a lot of features of the language

```func fib(n) {
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
// Prints the 5th fibonacci number
print "This function is recursive as well. Neat, right!";
print fib(5);```

The program shows you how classes, variables, functions, scopes and recursion works.





