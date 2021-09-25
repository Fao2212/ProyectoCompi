package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForLoopRange extends ForLoop{

    public ForLoopRange(Expression eAST,RangeVarDecl dAST, Command cAST, SourcePosition thePosition) {
        super(dAST, cAST, thePosition);
        this.E = eAST;
    }
    Expression E;
}
