package com.babel.hecate.grammar.expressions;

public class PrettyPrint implements HecateExpression.Visitor<String> {

    @Override
    public String visit(BinaryExpression be) {
        return process(be.operator.lexeme, be.leftExpression, be.rightExpression);
    }

    @Override
    public String visit(UnaryExpression ue) {
        return process(ue.operator.lexeme, ue.expression);
    }

    @Override
    public String visit(GroupExpression ge) {
        return process("group", ge.expression);
    }

    @Override 
    public String visit(LiteralExpression le) {
        if(le.literal == null)
            return "NIETZSCHE";
        return le.literal.toString();
    }

    @Override
    public String visit(AssignmentExpression ae) {
        return process(ae.token.getLexeme(), ae.expression);
    }

    @Override
    public String visit(LogicalExpression le) {
        return process(le.operator.getLexeme(),le.leftExpression, le.rightExpression);
    }

    @Override
    public String visit(VariableExpression ve) {
        return ve.name.toString();
    }

    private String process(String name, HecateExpression ...exprs) {
        StringBuilder print = new StringBuilder();
        print.append("( "+name+" ");

        for(HecateExpression e : exprs) {
            print.append(e.accept(this));
            print.append(" ");
        }

        print.append(" )");
        return print.toString();
    }

    public String visit(FunctioncallExpression fe) {
        return "function";
    }

    
}
