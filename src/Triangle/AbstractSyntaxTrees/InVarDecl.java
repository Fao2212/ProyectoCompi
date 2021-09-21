package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class InVarDecl extends Declaration {
    public InVarDecl (Identifier iAST, Expression eAST, SourcePosition thePosition) {
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
