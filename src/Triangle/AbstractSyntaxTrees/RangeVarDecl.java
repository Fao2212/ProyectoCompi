package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RangeVarDecl extends Declaration {
    public RangeVarDecl (Identifier iAST, Expression eAST, SourcePosition thePosition) {
        super(thePosition);
        I = iAST;
        E = eAST;
    }

    public Object visit(Visitor v, Object o) {
        return null;
    }
    
    public Identifier I;
    public Expression E;
}
