package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RecursiveProcFuncsDeclaration extends Declaration {
    public RecursiveProcFuncsDeclaration (Declaration dAST, SourcePosition thePosition) {
        super(thePosition);
        PFD = dAST;
    }

    public Object visit(Visitor v, Object o) {
        return null;
    }
    
    public Declaration PFD;
    
}
