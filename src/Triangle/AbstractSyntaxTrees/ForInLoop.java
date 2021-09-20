package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForInLoop extends Loop{

    public ForInLoop(Identifier I,Expression eAST,Command C,SourcePosition thePosition) {
        super(thePosition);
        this.I = I;
        this.eAST = eAST;
        this.C = C;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return null;
    }

    Identifier I;
    Expression eAST;
    Command C;
    
}
