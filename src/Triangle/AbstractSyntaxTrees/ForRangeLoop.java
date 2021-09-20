package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForRangeLoop extends Loop {

    public ForRangeLoop(Identifier iAST, Range rAST, Command cAST, SourcePosition thePosition) {
        super(thePosition);
        this.I = iAST;
        this.R = rAST;
        this.C = cAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return null;
    }

    Identifier I;
    Range R;
    Command C;
    
}
