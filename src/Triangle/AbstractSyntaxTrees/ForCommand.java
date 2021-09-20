package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForCommand extends Command{

    public ForCommand(Identifier I,Command C1,Command C2,SourcePosition thePosition) {
        super(thePosition);
        
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitForCommand(this, o);
    }

    Identifier I;
    Command C1,C2;
    
}
