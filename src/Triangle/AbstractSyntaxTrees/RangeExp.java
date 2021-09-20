package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RangeExp extends Command{

    public RangeExp(Expression e1AST,Expression e2AST,Command C,SourcePosition thePosition) {
        super(thePosition);
        this.e1AST = e1AST;
        this.e2AST = e2AST;
        this.C = C;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    Expression e1AST,e2AST;
    //Que pasa como es opcional. se pone o no se pone?
    Command C;

    
}
