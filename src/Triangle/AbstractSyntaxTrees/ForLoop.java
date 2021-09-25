package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForLoop extends Loop{

    public ForLoop(Declaration dAST,Command cAST,SourcePosition thePosition) {
        super(thePosition);
        this.D = dAST;
        this.C = cAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    Declaration D;
    Command C;
}
