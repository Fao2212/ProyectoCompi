package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatForRangeWhile extends Command {
    public RepeatForRangeWhile(RangeVarDecl rvdAST, Expression eAST1, Command cAST, Expression eAST2, SourcePosition thePosition) {
        super(thePosition);
        RVD = rvdAST;
        E1 = eAST1;
        C = cAST;
        E2 = eAST2;
    }

    public Object visit (Visitor v, Object o) {
        return v.visitRepeatForRangeWhile(this, o);
    }

    public RangeVarDecl RVD;
    public Expression E1, E2;
    public Command C;
}
