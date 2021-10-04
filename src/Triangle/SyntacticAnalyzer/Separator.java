package Triangle.SyntacticAnalyzer;

public class Separator {
    String treatedSeparator = "";
    boolean isComment = false;
    
    public boolean isComment(){
        return isComment;
    }
    public String getSeparator(){
        return treatedSeparator;
    }
    public void addSeparator(char ch){
        this.treatedSeparator+=ch;
    }
    public void addSeparator(String str){
        this.treatedSeparator+=str;
    }
    public void setComment(boolean bool){
        this.isComment = bool;
    }
}
