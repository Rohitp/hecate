package tests;

import com.babel.hecate.grammar.BinaryExpression;
import com.babel.hecate.grammar.GroupExpression;
import com.babel.hecate.grammar.LiteralExpression;
import com.babel.hecate.grammar.PrettyPrint;
import com.babel.hecate.grammar.UnaryExpression;
import com.babel.hecate.scanner.Token;
import com.babel.hecate.scanner.TokenEnum;


// Just here for random testing of packages and business logic. 
public class Tester {

    public static void main(String args[]) {

        LiteralExpression le = new LiteralExpression(56);
        LiteralExpression le1 = new LiteralExpression(89);
        UnaryExpression ue = new UnaryExpression(new Token(TokenEnum.MINUS, "-", null, 0, 0), le1);
        BinaryExpression be = new BinaryExpression(le, new Token(TokenEnum.PLUS, "+", null, 0, 0), ue);
        GroupExpression ge = new GroupExpression(be);
        BinaryExpression be2 = new BinaryExpression(ge, new Token(TokenEnum.PLUS, "+", null, 0, 0), le1);
        



        System.out.println(be2.accept(new PrettyPrint()));
        // System.out.println(new PrettyPrint().visit(new LiteralExpression(34.67)));
    }
    
}
