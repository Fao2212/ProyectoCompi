package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ConditionalDoLoop extends Loop{

    public ConditionalDoLoop(Expression eAST, Command C,SourcePosition thePosition) {
        super(thePosition);
        this.eAST = eAST;
        this.C = C;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression eAST;
    public Command C;
    
}
