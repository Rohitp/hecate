package com.babel.hecate.prototypes;

import com.babel.hecate.grammar.expressions.HecateExpression;
import com.babel.hecate.scanner.Token;


// The this pointer.
// Refer for internal redirection. This should work off the bat
// we're going with the self syntax in python
public class SelfReference extends HecateExpression  {


    Token keyword;

    public SelfReference(Token keyword) {
        this.keyword = keyword;
    }
    
    public Token getToken() {
        return keyword;
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
