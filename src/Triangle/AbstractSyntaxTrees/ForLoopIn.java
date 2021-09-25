package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForLoopIn extends ForLoop{

    public ForLoopIn(Expression eAST,InVarDecl dAST, Command cAST, SourcePosition thePosition) {
        super(dAST, cAST, thePosition);
        this.E = eAST;
    }
    Expression E;
}
