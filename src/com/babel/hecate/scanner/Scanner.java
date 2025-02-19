package com.babel.hecate.scanner;
import java.util.ArrayList;
import java.util.HashMap;

import com.babel.hecate.Hecate;


//Scans a piece of source code. Takes lexemes and assigns it to tokens.
public class Scanner {
    private final String code;
    private ArrayList<Token> tokens = new ArrayList<>();

    // Keeps a track of pointers when you scan them. This is at a lexeme level
    // Where start is the start and ptr is the current state of the lexeme.
    private int ptr = 0;
    private int start = 0;
    private int line = 1;


    public Scanner(String code) {
        this.code = code;
    }

    public ArrayList<Token> getTokens() {
        while(ptr < code.length()) {
            start = ptr;
            scan();
        }

        tokens.add(new Token(TokenEnum.EOF, "", null, line, start));
        return tokens;
    }


    // Scans each token 1 by 1
    private void scan() {
        char c = getNextChar();
        

        switch(c) {


            // Empty space. Just reset the start pointer for the lexemes up top and do nothing.
            case ' ':
            case '\t':
            case '\r':
                break;

            // Newline. Increment the line count and start pointer.     
            case '\n':
                line++;
                break;

            case '(':
                processToken(TokenEnum.LEFT_BRACKET, null);
                break;
            case ')':
                processToken(TokenEnum.RIGHT_BRACKET, null);
                break;
            case '{':
                processToken(TokenEnum.LEFT_BRACE, null);
                break;
            case '}':
                processToken(TokenEnum.RIGHT_BRACE, null);
                break;
            case '+':
                processToken(TokenEnum.PLUS, null);
                break;
            case '-':
                processToken(TokenEnum.MINUS, null);
                break;
            case '*':
                processToken(TokenEnum.ASTERISK, null);
                break;
            case ',':
                processToken(TokenEnum.COMMA, null);
                break;
            case '.':
                processToken(TokenEnum.DOT, null);
                break;
            //The semicolon is weird. Do languages need semi colons? What about implicit insertion of semi colons? Not sure
            // Much more difficult to handle semicolons within the realms of context free grammars. - How do I know where a line ends without store context
            // Side note Javascripts semi colon insertion logic is insane 
            // https://tc39.es/ecma262/multipage/ecmascript-language-lexical-grammar.html#sec-automatic-semicolon-insertion
            // Also without a semi colon the context becomes incrdibly difficult to synchronise when we hit an error condition
            case ';':
                processToken(TokenEnum.SEMICOLON, null);
                break;

            //Going to match which need to have context specific grammar. Getting into type 1 grammar here. 
            case '!':
                if(isNextChar('=')) {
                    processToken(TokenEnum.NOT_EQUAL, null);
                } else {
                    processToken(TokenEnum.NOT, null);
                }
                break;
              
            case '<':
                if(isNextChar('=')) {
                    processToken(TokenEnum.LESSER_EQUAL, null);
                } else {
                    processToken(TokenEnum.LESSER, null);
                }
                break;

            case '>':
                if(isNextChar('=')) {
                    processToken(TokenEnum.GREATER_EQUAL, null);
                } else {
                    processToken(TokenEnum.GREATER, null);
                }
                break;

            case '=': 
                if(isNextChar('=')) {
                    processToken(TokenEnum.EQUAL_EQUAL, null);
                } else {
                    processToken(TokenEnum.EQUAL, null);
                }
                break;  
            
            // Figure out if it's division or comment. If it's a comment ignore the rest of the line. 
            // Nested comments like /* */ with infinite levels are surprisingly tricky to do.      
            case '/':
                if(isNextChar('/')) {
                    // Just ignore the rest of the line by using the next function to iterate
                    while(ptr < code.length() && code.charAt(ptr) != '\n') {
                        getNextChar();
                    }
                } else {
                    processToken(TokenEnum.OBELUS, null); 
                }
                break;

            //No chars allowed. Only strings. Double quotes. Multi line. No escape characters. Why? Easiest to implement.    
            case '"':
                processString();
                break; 

            default:

                // Handling numbers here as we don't have intelligent switch cases here. 
                // TODO: Segregate numbers into INT and Double.
                if(Character.isDigit(c)) {
                    processNumber();
                } else if(isVariableLegalAlphabet(c)) {
                    processKeyword();
                } else {
                    Hecate.errorHandler(line, "Unrecognised character "+c);
                }
                
                break;

        }

    }

    //TODO: Make this a more generic function that serves to fetch the next char and ignores comment chars.
    private char getNextChar() {
        return code.charAt(ptr++);
    }

    private boolean isNextChar(char c) {
        if(ptr >= code.length()) 
            return false;
         if(code.charAt(ptr) == c) {
            //we need to ignore the next character since it's a part of the previous character
            ptr++;
            return true;
         }
        return false; 
        
    }

    private void processString() {
        while(ptr < code.length() && code.charAt(ptr) != '"' ) {
            if(code.charAt(ptr) == '\n')
                line++;
            getNextChar();
        }

        // Reached end of string. No matching close quotes. 
        if(ptr == code.length()) {
            Hecate.errorHandler(line, "String literal isn't bounded by closing pair");
            return;
        }

        getNextChar();


        //removing quotes for the actual literal
        String literal = code.substring(start + 1, ptr -1);
        processToken(TokenEnum.STRING,literal);

    }

    // Defining a custom method here. Character.isLetter is too permissive. We only want standard a-z and A-Z. 
    // isLetter lets weird shit like ʰ pass through
    private boolean isVariableLegalAlphabet(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c<= 'Z') || (c == '_');
    }

    // We process all keywords here, if reserved we mark them as such with an enum. Otherwise they become user defined.
    private void processKeyword() {

        final HashMap<String, TokenEnum> identifiermap = new HashMap<String, TokenEnum>();

        identifiermap.put("true", TokenEnum.TRUE);
        identifiermap.put("false", TokenEnum.FALSE);
        identifiermap.put("nietzsche", TokenEnum.NIETZSCHE);
        identifiermap.put("null", TokenEnum.NIETZSCHE);
        identifiermap.put("if", TokenEnum.IF);
        identifiermap.put("else", TokenEnum.ELSE);
        identifiermap.put("or", TokenEnum.OR);
        identifiermap.put("and", TokenEnum.AND);
        identifiermap.put("print", TokenEnum.PRINT);
        identifiermap.put("func", TokenEnum.FUNC);
        identifiermap.put("function", TokenEnum.FUNC);
        identifiermap.put("while", TokenEnum.WHILE);
        identifiermap.put("for", TokenEnum.FOR);
        identifiermap.put("var", TokenEnum.VAR);
        identifiermap.put("return", TokenEnum.RETURN);
        identifiermap.put("class", TokenEnum.CLASS);

        while(ptr < code.length() && ( isVariableLegalAlphabet(code.charAt(ptr)) || Character.isDigit(code.charAt(ptr)) ) ) {
            getNextChar();
        }

        String identifier = code.substring(start, ptr);

        // Deciding to make reserved keywords case sensitive here. 
        TokenEnum type = (identifiermap.get(identifier) == null)? TokenEnum.IDENTIFIER : identifiermap.get(identifier);
        processToken(type, identifier);


    }

    private void processNumber() {
        //Flag to make sure we only consume the decimal point one - 123.456.78 is invalid.
        boolean seenDecimal = false;
        while(ptr < code.length() && (Character.isDigit(code.charAt(ptr)) || code.charAt(ptr) == '.') ) {
        
        // If the next digit after decimal isn't a number
        if( code.charAt(ptr) == '.' && !Character.isDigit(code.charAt(ptr + 1)) )
            break;
        // If the next digit after a decimal is a number but we've already consumed the decimal once    
        if(code.charAt(ptr) == '.' && Character.isDigit(code.charAt(ptr + 1)) && seenDecimal )
            break;
        // First time consuming the decimal - set flag    
        if(code.charAt(ptr) == '.' && Character.isDigit(code.charAt(ptr + 1)) && !seenDecimal ) {
            seenDecimal = true;
        } 

        getNextChar();

        } 
        processToken(TokenEnum.NUMBER, null);
    }

    // Helper methood to bind the cursor and the token, cleaner code and most iportantly seperating the two didnt make sense
    private void processToken(TokenEnum type, String literal) {
        String lexeme = code.substring(start, ptr); 
        if(literal == null) {
            literal = lexeme;
        }
        if(type == TokenEnum.NUMBER) {
            tokens.add(new Token(type, lexeme, Double.parseDouble(literal), line, start));
        } else {
            tokens.add(new Token(type, lexeme, literal, line, start));
        }
    }



}
