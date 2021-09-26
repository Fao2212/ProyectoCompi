package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

//Cambios(Fernando)
//Se crea esta clase para poder reconocer comandos con la estructura CommandWhile
public class DoWhileCommand extends Command{

    public DoWhileCommand(Expression eAST, Command cAST, SourcePosition thePosition) {
        super(thePosition);
        this.E = eAST;
        this.C = cAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitRepeatDoWhileCommand(this, o);
    }
    
    public Expression E;
    public Command C;
}
