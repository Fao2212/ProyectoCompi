package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RecursiveProcFuncDeclaration extends Declaration {
    public RecursiveProcFuncDeclaration (Declaration dAST, SourcePosition thePosition) {
        super(thePosition);
        PD = dAST;
    }

    public Object visit(Visitor v, Object o) {
        return null;
    }
    
    public Declaration PD;
    
}
