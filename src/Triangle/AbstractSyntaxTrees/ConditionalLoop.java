package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ConditionalLoop extends Command{


    public ConditionalLoop(Command C,Expression eAST,SourcePosition thePosition) {
        super(thePosition);
        this.eAST = eAST;
        this.C = C;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression eAST;
    
    //Aca se le agrega un command porque puede tener un doCommand.
    public Command C;
    
}
