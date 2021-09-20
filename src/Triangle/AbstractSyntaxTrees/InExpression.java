package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class InExpression extends Command{

    public InExpression(Expression eAST,SourcePosition thePosition) {
        super(thePosition);
        this.eAST = eAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitInExpression(this, o);
    }

    Expression eAST;
    
}
