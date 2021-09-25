package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

//Cambios(Fernando)
//Se crea esta clase para poder reconocer comandos con la estructura UntilCommand
public class UntilCommand extends Command{

    public UntilCommand(Expression eAST,Command cAST,SourcePosition thePosition) {
        super(thePosition);
        this.C = cAST;
        this.E = eAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Expression E;
    public Command C;
}
