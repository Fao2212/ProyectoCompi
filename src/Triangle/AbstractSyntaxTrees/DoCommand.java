package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class DoCommand extends Command{

    public DoCommand(Command C1, Command C2, SourcePosition thePosition) {
        super(thePosition);
        this.C1 = C1;
        this.C2 = C2;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitDoCommand(this, o);
    }
    
    public Command C1,C2;
}
