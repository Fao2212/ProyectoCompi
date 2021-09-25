package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatCommand extends Command{

    public RepeatCommand(Loop lAST, SourcePosition thePosition) {
        super(thePosition);
        this.L= lAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        //TODO
        return null;
    }
    
    Loop L;
}
