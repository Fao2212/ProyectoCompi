package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class WhileDoLoop extends Loop{

    public WhileDoLoop(Expression eAST, Command cAST,SourcePosition thePosition) {
        super(thePosition);
        this.E = eAST;
        this.C = cAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }


    Expression E;
    Command C;
}
