package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatCommand extends Command{

    public RepeatCommand(Command C,SourcePosition thePosition) {
        super(thePosition);
        this.C = C;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitRepeatCommand(this, o);
    }
    
    Command C;
}
