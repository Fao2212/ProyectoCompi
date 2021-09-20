package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class Range extends AST{

    public Range(Expression eAST1, Expression eAST2, Expression eAST3, SourcePosition thePosition) {
        super(thePosition);
        this.eAST1 = eAST1;
        this.eAST2 = eAST2;
        this.eAST3 = eAST3;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    Expression eAST1,eAST2,eAST3;
    
}
