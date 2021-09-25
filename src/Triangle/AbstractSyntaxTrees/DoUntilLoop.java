package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class DoUntilLoop extends Loop{

    public DoUntilLoop(Command cAST, Expression eAST,SourcePosition thePosition) {
        super(thePosition);
        this.C = cAST;
        this.E = eAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }
    
    Expression E;
    Command C;
}
