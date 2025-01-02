package com.babel.hecate;
import java.util.ArrayList;


//Scans a piece of source code. Takes lexemes and assigns it to tokens.
public class Scanner {
    private final String code;
    private ArrayList<Token> tokens = new ArrayList<>();

    // Keeps a track of pointers when you scan them. This is at a lexeme level
    // Where start is the start and ptr is the current state of the lexeme.
    private  int ptr = 0;
    private  int start = 0;
    private  int line = 1;

    Scanner(String code) {
        this.code = code;
    }

    ArrayList<Token> getTokens() {
        while(ptr < code.length()) {
            start = ptr;
            scan();
        }
        tokens.add(new Token(TokenEnum.EOF, "", null, line));
        return tokens;
    }


    // Scans each token 1 by 1
    private void scan() {
        char c = getNextChar();
        String lexeme = code.substring(start, ptr);

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
                tokens.add(new Token(TokenEnum.LEFT_BRACKET, lexeme, null, line));
                break;
            case ')':
                tokens.add(new Token(TokenEnum.RIGHT_BRACKET, lexeme, null, line));
                break;
            case '{':
                tokens.add(new Token(TokenEnum.LEFT_BRACE, lexeme, null, line));
                break;
            case '}':
                tokens.add(new Token(TokenEnum.RIGHT_BRACE, lexeme, null, line));
                break;
            case '+':
                tokens.add(new Token(TokenEnum.PLUS, lexeme, null, line));
                break;
            case '-':
                tokens.add(new Token(TokenEnum.MINUS, lexeme, null, line));
                break;
            case '*':
                tokens.add(new Token(TokenEnum.ASTERISK, lexeme, null, line));
                break;
            case ',':
                tokens.add(new Token(TokenEnum.COMMA, lexeme, null, line));
                break;
            case '.':
                tokens.add(new Token(TokenEnum.DOT, lexeme, null, line));
                break;
            //The semicolon is weird. Do languages need semi colons? What about implicit insertion of semi colons? Not sure 
            case ';':
                tokens.add(new Token(TokenEnum.SEMICOLON, lexeme, null, line));
                break;

            //Going to match which need to have context specific grammar. Getting into type 1 grammar here. 
            case '!':
                if(isNextChar('=')) {
                    tokens.add(new Token(TokenEnum.NOT_EQUAL, lexeme, null, line));
                } else {
                    tokens.add(new Token(TokenEnum.NOT, lexeme, null, line));
                }
                break;
              
            case '<':
                if(isNextChar('=')) {
                    tokens.add(new Token(TokenEnum.LESSER_EQUAL, lexeme, null, line));
                } else {
                    tokens.add(new Token(TokenEnum.LESSER, lexeme, null, line));
                }
                break;

            case '>':
                if(isNextChar('=')) {
                    tokens.add(new Token(TokenEnum.GREATER_EQUAL, lexeme, null, line));
                } else {
                    tokens.add(new Token(TokenEnum.GREATER, lexeme, null, line));
                }
                break;

            case '=': 
                if(isNextChar('=')) {
                    tokens.add(new Token(TokenEnum.EQUAL_EQUAL, lexeme, null, line));
                } else {
                    tokens.add(new Token(TokenEnum.EQUAL, lexeme, null, line));
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
                    tokens.add(new Token(TokenEnum.OBELUS, lexeme, null, line)); 
                }
                break;

            default:
                Hecate.errorHandler(line, "Unrecognised character "+c);
                break;

        }

    }

    //TODO: Make this a more generic function that serves to fetch the next char and ignores comment chars.
    private char getNextChar() {
        return code.charAt(ptr++);
    }

    private boolean isNextChar(char c) {
        if(ptr > code.length()) 
            return false;
         if(code.charAt(ptr) == c) {
            //we need to ignore the next character since it's a part of the previous character
            ptr++;
            return true;
         }
        return false; 
        
    }



}
