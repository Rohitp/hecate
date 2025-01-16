package com.babel.hecate.grammar.statements;

import java.util.ArrayList;


// A block statement is just a list of statements within a block 
// {
//     x = 10;
//     y + x / 55;
//     ...
//     ... 
//     print y;
// }
public class BlockStatement extends HecateStatement {
    
    ArrayList<HecateStatement> statements;

    public BlockStatement(ArrayList<HecateStatement> statements) {
        this.statements = statements;
    }

    public ArrayList<HecateStatement> getStatements() {
        return statements;
    }

    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
