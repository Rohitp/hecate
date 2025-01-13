package com.babel.hecate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.PrettyPrint;
import com.babel.hecate.grammar.statements.HecateStatement;
import com.babel.hecate.interpreter.Interpreter;
import com.babel.hecate.interpreter.InterpreterError;
import com.babel.hecate.parser.Parser;
import com.babel.hecate.scanner.Scanner;
import com.babel.hecate.scanner.Token;

//TODO: Fix case where scanner fails for empty files 
// And parsr fails for files with only comments
public class Hecate {

    private static boolean debug = true;

    private static boolean error = false;
    // Seperating parsing/ scanning error and runtime errors
    private static boolean runtimeError = false;
    public static void main(String args[]) throws IOException {

        // If 1 argument assume it's a file that we need to tokensie and parse
        // If no arguments start a REPL
        // If more than 1, throw error, use sysexits.h as a referene point
        if(args.length > 1){
            System.out.println("Usage: hecate: [optional] file");

            // https://man.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE&format=html
            System.exit(64);

        } else if(args.length == 1) {
            parseFile(args[0]);
        } else if(debug) {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            parseFile("../tests/parsertest.txt");
        } else {
            promptLoop();
        }
    }


    private static void parseFile(String path) throws IOException {

        try {
        
            parseText(new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8));
            if(error) System.exit(65);
            if(runtimeError) System.exit(70);

        } catch(FileNotFoundException fnf) {
            System.out.println("No such file "+path);
        } 

    }

    private static void promptLoop() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        // infinite loop for the REPL and get a line from prompt and parse till the users     
        // Exits when the user presses crtl-d, which we interpret as null
        // TODO: Add exit as a keyword.
        while(true) {
            System.out.print(">> ");
            String line = reader.readLine();
            if(line == null) break;
            parseText(line);

            //Sets error a false here so the repl doesn't stop each time there is an error
            error = false;
        }

    }

    // The main function which parses each line. Everything else is just a syntactic sugar over it    
    private static void parseText(String code) {

        Scanner scanner = new Scanner(code);
        ArrayList<Token> tokens = scanner.getTokens();
        for(Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        // HecateExpression exp = parser.formExpression();
        if(error)
            return;
        // System.out.println(exp.accept(new PrettyPrint()));

        Interpreter interpreter = new Interpreter();
        // System.out.println(interpreter.interpret(exp).toString());
        ArrayList<HecateStatement> statements = parser.parse();
        interpreter.executestatements(statements);
        

        
    }

    // The main error handler for Hecate. Ideally this becomes an error interface. 
    // unsure of how much I want to lean into OOP and higher level abstractions here
    // (Specially ironic since Hecate is itself aspriing to be object oriented)
    public static void errorHandler(int lineNumber, String errorMessage) {
        reportError(lineNumber, " ", errorMessage);
    }

    // Error  handler for the parser
    public static void errorHandler(Token token, String errorMessage) {
        reportError(token.getLineNumber(), " ", errorMessage);
    }

    public static void interpreterError(InterpreterError error) {
        System.err.println(error.getMessage() +"\n[line " + error.token.getLineNumber() + "]");
        runtimeError = true;
      }


    // The theory is that the reporting aspect of errors are seperate from the actual errors. 
    private static void reportError(int lineNumber, String line, String errorMessage) {
        System.out.println("Error at line number "+Integer.toString(lineNumber)+" "+line+" : "+errorMessage);
        error = true;
    }


}