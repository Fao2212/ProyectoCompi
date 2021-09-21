package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatForRange extends Command {
    public RepeatForRange(RangeVarDecl rvdAST, Expression eAST, Command cAST, SourcePosition thePosition) {
        super(thePosition);
        RVD = rvdAST;
        E = eAST;
        C = cAST;
    }

    public Object visit (Visitor v, Object o) {
        return null;
    }

    public RangeVarDecl RVD;
    public Expression E;
    public Command C;
}
