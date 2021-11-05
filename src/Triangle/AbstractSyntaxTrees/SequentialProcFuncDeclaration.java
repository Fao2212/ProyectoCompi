package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class SequentialProcFuncDeclaration extends Declaration {

  public SequentialProcFuncDeclaration (Declaration d1AST, Declaration d2AST,
                       SourcePosition thePosition) {
    super (thePosition);
    D1 = d1AST;
    D2 = d2AST;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitSequentialProcFuncDeclaration(this, o);
  }

  public Declaration D1, D2;
}
