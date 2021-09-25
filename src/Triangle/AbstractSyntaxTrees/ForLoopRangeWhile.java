package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForLoopRangeWhile extends ForLoop{

    public ForLoopRangeWhile(Expression eAST1,Expression eAST2,RangeVarDecl dAST, Command cAST, SourcePosition thePosition) {
        super(dAST, cAST, thePosition);
        this.E1 = eAST1;
        this.E2 = eAST2;
    }
    
    //TODO:Como se va a saber cual es la del while?
    Expression E1,E2;
}
