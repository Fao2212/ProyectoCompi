package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class LocalDeclaration extends Declaration {
    public LocalDeclaration (Declaration d1AST, Declaration d2AST, SourcePosition thePosition) {
        super(thePosition);
        H = d1AST;
        B = d2AST;
    }

    public Object visit (Visitor v, Object o) {
        return null;
    }
    
    public Declaration H,B;
}
