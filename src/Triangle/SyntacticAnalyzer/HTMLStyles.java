package Triangle.SyntacticAnalyzer;

//Se crean metodos estaticos para el formato del HTML 
public class HTMLStyles {
    
    static String boldHTMLString(String string){
        return "<b>"+string+"</b>";
    }
    static String blueHTMLString(String string){
        return "<font color='#0000cd'>"+string+"</font>";
    }
    static String commentHTMLString(String string){
        return "<font color='#00b300'>" + string + "</font>";
    }
    static String startHTML(){
        return "<p style=\"font-family: 'DejaVu Sans', monospace;\">";
    }
    static String addLineBreak(){
        return "<br>";
    }
    static String finishHTML(){
        return "</p>";
    }
    static String addTab(){
        return "&emsp;";
    }
}