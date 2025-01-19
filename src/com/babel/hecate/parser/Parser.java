package com.babel.hecate.parser;

import com.babel.hecate.Hecate;
import com.babel.hecate.grammar.expressions.AssignmentExpression;
import com.babel.hecate.grammar.expressions.BinaryExpression;
import com.babel.hecate.grammar.expressions.FunctioncallExpression;
import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.grammar.expressions.GroupExpression;
import com.babel.hecate.grammar.expressions.LiteralExpression;
import com.babel.hecate.grammar.expressions.LogicalExpression;
import com.babel.hecate.grammar.expressions.PrettyPrint;
import com.babel.hecate.grammar.expressions.UnaryExpression;
import com.babel.hecate.grammar.expressions.VariableExpression;
import com.babel.hecate.grammar.statements.BlockStatement;
import com.babel.hecate.grammar.statements.BranchStatement;
import com.babel.hecate.grammar.statements.ExpressionStatement;
import com.babel.hecate.grammar.statements.HecateStatement;
import com.babel.hecate.grammar.statements.LoopStatement;
import com.babel.hecate.grammar.statements.PrintStatement;
import com.babel.hecate.grammar.statements.VariableStatement;

import java.util.ArrayList;

import com.babel.hecate.scanner.Token;
import com.babel.hecate.scanner.TokenEnum;

// Defining a parser with the following rules
// (Also see -> https://en.wikipedia.org/wiki/LR_parser)
// Order of precedence, from lowest to highest - the same as C
// =               : right associative
// or, and         : left assosiative
// ==, !=          : left associative
// >, <, <=, >=    : left associative
// +, -            : left associative
// /, *            : left associative 
// !,-             : right associative
// function()      : right associative
// Recursive descent parsing is where we're going -> https://en.wikipedia.org/wiki/Recursive_descent_parser
 // This is preferable also because, each production drectly translates to a function. 
 // We can continue with the visitor pattern and add a visitor for each object
 
// Adding support for statements here
// A program has a list of statements
// A statement can be of many types - branch, loop, expression, declaration
// The precedence of rules is that anywhere a declaration is possible anything else is possible.

 // On a fundamental level this takes tokens and converts it into expressions. 
public class Parser {

    private final ArrayList<Token> tokens;
    private int ptr = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }


    public ArrayList<HecateStatement> parse() {
        ArrayList<HecateStatement> statements = new ArrayList<>();
        while(tokens.get(ptr).getType() != TokenEnum.EOF) {
            statements.add(processStatement());
        }
        return statements;
    }



    // Unless it's a literal or unary for a repl, everything is a binary expression
    // In theory you have a program like
    // !true
    // print false
    // but again I can't figure out a single instance outside the repl where it wouldn't make sense
    public HecateExpression formExpression() {
        return assignment();
    }


    // This is tricky as we need to account for the fact that
    // Assignment cannot be treated without context 
    // var foo = 5
    // foo = 6
    // here foo cannot be expanded without context, even if we look ahead and use the equald sign 
    // we can have assignment be levels deep like 
    // foo.bar.baz = "hello"
    // Like a binary expression we recursively call it only on the right hand side
    public HecateExpression assignment() {
        HecateExpression expression =  or();

        // Only consider asssignment here
        if(match(TokenEnum.EQUAL)) {
            Token equal = tokens.get(ptr -1);
            HecateExpression right = assignment();
            
            if(expression instanceof VariableExpression) {

                Token token = ((VariableExpression)expression).getName();
                return new AssignmentExpression(right, token);
            }

            throw parserError(equal, "Invalid assignment target");

        }

        return expression;
    }

    private HecateExpression or() {
        HecateExpression left = and();
        while(match(TokenEnum.OR)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = and();
            left = new LogicalExpression(left, operator, right);
        }
        return left;
    }

    private HecateExpression and() {
        HecateExpression left = equals();
        while(match(TokenEnum.AND)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = equals();
            left = new LogicalExpression(left, operator, right);
        }
        return left;
    }

    // Can potentially be infinitely long 
    // a == b == b == d and so on
    private HecateExpression equals() {
        HecateExpression left = comparisson();
        while(match(TokenEnum.EQUAL_EQUAL, TokenEnum.NOT_EQUAL)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = comparisson();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }
    

    private HecateExpression comparisson() {
        HecateExpression left = summations();
        while(match(TokenEnum.GREATER, TokenEnum.LESSER, TokenEnum.GREATER_EQUAL, TokenEnum.LESSER_EQUAL)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = summations();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    private HecateExpression summations() {
        HecateExpression left = multiplication();
        while(match(TokenEnum.PLUS, TokenEnum.MINUS)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = multiplication();
            left = new BinaryExpression(left, operator, right);

        }
        return left;
    }

    private HecateExpression multiplication() {
        HecateExpression left = unary();
        while(match(TokenEnum.ASTERISK, TokenEnum.OBELUS)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = unary();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }



    // These return unary expressions
    private HecateExpression unary() {
        // Expression expr = literal();
        while(match(TokenEnum.NOT, TokenEnum.MINUS)) {
            Token operator = tokens.get(ptr -1);
            HecateExpression right = literal();
            return new UnaryExpression(operator, right);
        }
        return func();
    }


    // Keep in mind we can call arbitary functions like func()()
    // def outer_function():
    // def inner_function():
    //     print("Inner function called")
    // return inner_function

    // and then calling 

    // outer_function()()

    // is valid

    private HecateExpression func() {
        HecateExpression expr = literal();

        //Loop till we don't get an opening bracket
        while(true) {
            if(match(TokenEnum.LEFT_BRACKET)) {
                expr = callstack(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    // https://bugs.python.org/issue27213
    // Looking at this for behaviour
    private HecateExpression callstack(HecateExpression expr) {

        ArrayList<HecateExpression> arguments = new ArrayList<>();
        //Add all function arguments to an array
        if(tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() != TokenEnum.RIGHT_BRACKET) {
            do {
                arguments.add(formExpression());
            } while(match(TokenEnum.COMMA));
        }

        Token rightBracket = iterate(TokenEnum.RIGHT_BRACKET, "Missing closing ) for function");
        return new FunctioncallExpression(expr, arguments, rightBracket);
    }

    // Literal can be true, false, string, number or Nietzsche
    // Adding grouping support here. 
    private HecateExpression literal() {

        // common cases
        if(match(TokenEnum.TRUE))
            return new LiteralExpression(true);
        if(match(TokenEnum.FALSE))
            return new LiteralExpression(false);
        if(match(TokenEnum.NIETZSCHE))
            return new LiteralExpression(null);
        if(match(TokenEnum.NUMBER, TokenEnum.STRING))
            return new LiteralExpression(tokens.get(ptr -1).getLiteral());
        if(match(TokenEnum.IDENTIFIER))
            return new VariableExpression(tokens.get(ptr -1));


        // Parse and match griup expressions    
        if(match(TokenEnum.LEFT_BRACKET)) {
            HecateExpression group = formExpression();


            // Need to figure out the error interface. We synchronise here?
            if(tokens.get(ptr).getType() == TokenEnum.EOF) {
                System.out.println("Missing right brace");
            }
            if (tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() == TokenEnum.RIGHT_BRACKET) {
                    ptr++;
            }

            return new GroupExpression(group);
        }
        

        // I need to figure out how to fix the unary stack. This will definitely come back to bite me.
        // return new LiteralExpression("NA");
        throw parserError(tokens.get(ptr), "UNexpected parsing error");
    }



    // Statements for a hierarchy similair to expressions
    // For example PRINT 5 + 8 needs to evaluate the RHS expression before coming back
    private HecateStatement processStatement() {

        try {
            if(match(TokenEnum.VAR)) {
                return declarations();
            }
            return nondeclarations(); 
        } catch(ParserError pe) {
            dontpanic();
            return null;
        }
    }

    private HecateStatement declarations() {
        try {
            Token varname = iterate(TokenEnum.IDENTIFIER, "Expected variable name - name your variables");
            HecateExpression expr = null;
            if(match(TokenEnum.EQUAL)) {
                expr = formExpression();
            }

            iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
            return new VariableStatement(varname, expr);
        } catch (ParserError pe) {
            dontpanic();
            return null;
        }
    }

    private HecateStatement nondeclarations() {
        if(match(TokenEnum.IF)) {
            return branchstatement();
        }

        if(match(TokenEnum.WHILE)) {
            return loopstatement();
        }

        if(match(TokenEnum.FOR)) {
            return sugar();
        }

        if(match(TokenEnum.PRINT)) {
            return printStatement();
        } 

        if(match(TokenEnum.LEFT_BRACE)) {
            return new BlockStatement(blockStatement());

        }

        return expressionStatement();
    }


    // a for loop is just syntactic sugar over a while
    // Using the specification here -> https://en.cppreference.com/w/c/language/for
    // Its versatile in the sense it has 
    // 1 - An initialisation clause
    // 2 - a condition
    // 3 - an iteration expression
    // all of which are optional
    // for( i = 0; i < 10; i++) is as valid as for(;;)
    // we just match for the overall brackets and semicolon syntax and map it to a while loop
    private HecateStatement sugar() {
        iterate(TokenEnum.LEFT_BRACKET, "Expected ( after for");

        HecateStatement init;
        
        // No initialiser
        if(match(TokenEnum.SEMICOLON)) {
            init = null;
        } else if(match(TokenEnum.VAR)) { // Declaration statement here
            init = declarations();
        } else {
            init = expressionStatement();
        }

        HecateExpression condition = null;
        if(!(tokens.get(ptr).getType() == TokenEnum.SEMICOLON && tokens.get(ptr).getType() != TokenEnum.EOF)) {
            condition = formExpression();
        }

        iterate(TokenEnum.SEMICOLON, "Expected ; after condition in for");

        HecateExpression incr = null;
        if(!(tokens.get(ptr).getType() == TokenEnum.RIGHT_BRACKET && tokens.get(ptr).getType() != TokenEnum.EOF)) {
            incr = formExpression();
        }

        iterate(TokenEnum.RIGHT_BRACKET, "Expected ) in for");

        HecateStatement loopbody = processStatement();

        if(incr != null) {
            ArrayList<HecateStatement> loopstatements = new ArrayList<>();
            loopstatements.add(loopbody);
            loopstatements.add(new ExpressionStatement(incr));
            loopbody = new BlockStatement(loopstatements);
        }

        if(condition == null) {
            condition = new LiteralExpression(true);
        }

        loopbody = new LoopStatement(loopbody, condition);

        if(init != null) {
            ArrayList<HecateStatement> loopstatements = new ArrayList<>();
            loopstatements.add(init);
            loopstatements.add(loopbody);
            loopbody = new BlockStatement(loopstatements);
        }

        //So the variable isn't getting scoped correctly in the wrapping/
        // So will figure this out later
        // BlockStatement loopdebug = (BlockStatement)loopbody;
        // for(HecateStatement statement: loopdebug.getStatements()) {
        //     System.out.println("Outermost "+statement);
        //     System.out.println(" ");
        //     if(statement instanceof VariableStatement) {
        //         VariableStatement vs = (VariableStatement)statement;
        //         HecateExpression  expr = vs.getExpression();
        //         LiteralExpression le = (LiteralExpression)expr;
        //         System.out.println("Variable statement: "+vs.getVariablename().getLexeme()+ " "+le.getLiteral());
        //     }

        //     if(statement instanceof LoopStatement) {
        //         LoopStatement ls = (LoopStatement)statement;
        //         HecateStatement loops = ls.getStatement();
        //         HecateExpression cond = ls.getCondition();
        //         System.out.println("Condition: "+new PrettyPrint().visit((BinaryExpression)cond));

        //         if(loops instanceof BlockStatement) {
        //             BlockStatement bs = (BlockStatement)loops;
        //             for(HecateStatement is: bs.getStatements()) {
        //                 if(is instanceof BlockStatement) {
        //                     for(HecateStatement iis : ((BlockStatement)is).getStatements() ) {
        //                         System.out.println("innermost"+iis);
        //                     }
        //                 }
        //                 if(is instanceof ExpressionStatement) {
        //                     ExpressionStatement es = (ExpressionStatement)is;
        //                     System.out.println("increment "+ new PrettyPrint().visit ( ((BinaryExpression)((AssignmentExpression)es.getHe()).getExpression())  ) );
        //                 }
        //             }
        //         }
                
        //     }
        // }





        return loopbody;
    }

    private HecateStatement loopstatement() {
        iterate(TokenEnum.LEFT_BRACKET, "Expected ( after while");
        HecateExpression consition = formExpression();
        iterate(TokenEnum.RIGHT_BRACKET, "Missing mathcing ) ");

        HecateStatement body = processStatement();

        return new LoopStatement(body, consition);
    }

    private HecateStatement branchstatement() {
        iterate(TokenEnum.LEFT_BRACKET, "Expected ( after If");
        HecateExpression condition = formExpression();
        iterate(TokenEnum.RIGHT_BRACKET, "Missing mathcing ) ");

        HecateStatement ifbranch =  processStatement();
        HecateStatement elsebranch = null;
        if(match(TokenEnum.ELSE)) {
            elsebranch = processStatement();
        }


        return new BranchStatement(ifbranch, elsebranch, condition);
    }

    private HecateStatement printStatement() {
        HecateExpression expr = formExpression();

        iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
        return new PrintStatement(expr);
    }

    private ArrayList<HecateStatement> blockStatement() {
        ArrayList<HecateStatement> statements = new ArrayList<>();

        while(tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() != TokenEnum.RIGHT_BRACE) {
            statements.add(processStatement());
        }
        iterate(TokenEnum.RIGHT_BRACE, "Missing matching } symbol");
        return statements;
    }

    private HecateStatement expressionStatement() {
        HecateExpression expr = formExpression();
        iterate(TokenEnum.SEMICOLON, "Expected ; at the end of statement");
        return new ExpressionStatement(expr);
    }



    private Token iterate(TokenEnum type, String error) {

        try {
            if(tokens.get(ptr).getType() != TokenEnum.EOF && tokens.get(ptr).getType() == type) {
                ptr++;
                return tokens.get(ptr -1);
            }

            throw new ParserError(tokens.get(ptr), error);
        } catch(ParserError pe) {
            System.out.println(pe.getMessage());
            return null;
        }
        
    }

    

    // Matches a token type and gets to the next step if not end. The main ticking clock of the program
    private boolean match(TokenEnum ...tokentypes) {

        for(TokenEnum tokentype : tokentypes) {    
            if(tokens.get(ptr).getType() != TokenEnum.EOF)  {
                if(tokens.get(ptr).getType() == tokentype) {
                    ptr++;
                    return true;
                }
            }
                
                
        }
        return false;
    }

    private ParserError parserError(Token token, String message) {
        Hecate.errorHandler(token, "Error at token "+token.getLexeme());
        ParserError pe = new ParserError(token, message);
        throw pe;
    }


    // Gets us to the next statement if we start panicking over an error
    private void dontpanic() {

        if(tokens.get(ptr).getType() != TokenEnum.EOF) {
            ptr++;
        }

        while(tokens.get(ptr).getType() != TokenEnum.EOF) {

            if(tokens.get(ptr -1).getType() == TokenEnum.SEMICOLON) return;

            switch(tokens.get(ptr).getType()) {
                case VAR:
                case IF:
                case ELSE:
                case FOR:
                case WHILE:
                case FUNC:
                case PRINT:
                    return;
                default:
                    assert true;
            }

            if(tokens.get(ptr).getType() != TokenEnum.EOF) {
                ptr++;
            }
        }
    }
    
}
