package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatIn extends Command {
    public RepeatIn(InVarDecl ivdAST, Command cAST, SourcePosition thePosition) {
        super(thePosition);
        IVD = ivdAST;
        C = cAST;
    }

    public Object visit (Visitor v, Object o) {
        return v.visitRepeatIn(this, o);
    }

    public InVarDecl IVD;
    public Command C;
}
