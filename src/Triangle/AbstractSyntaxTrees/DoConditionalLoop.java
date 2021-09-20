package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class DoConditionalLoop extends Loop{

    public DoConditionalLoop(Command cAST, Expression eAST, SourcePosition thePosition) {
        super(thePosition);
        this.C = cAST;
        this.E = eAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    Command C;
    Expression E;
    
}
