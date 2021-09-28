package Triangle.SyntacticAnalyzer;

public class TokenPrinter {

  private Scanner lexicalAnalyser;
  private Token currentToken;

  public TokenPrinter(Scanner lexer) {
    lexicalAnalyser = lexer;
  }

  //Se van a agregando los tokens que corresponden a los estilos que se piden en el trabajo se pone en 2 para tomar en cuenta los literales
  //caracteres y enteros
  //Se pone en el 39 como tope para que no tome los parentesis.
  public void printTokens() {
    currentToken = lexicalAnalyser.scan();
    System.out.print(HTMLStyles.startHTML());
      while (currentToken.kind != Token.EOT) {
        //Verifica si hay algo en el separador y si este es un comentario
        if (!currentToken.separator.isEmpty())
          if (currentToken.separator.charAt(0)=='!') // Caso en que el separador es un comentario
            System.out.print(HTMLStyles.commentHTMLString(currentToken.separator));
          else 
            getLineBreaks(currentToken.separator);
            System.out.print(currentToken.separator);

        if (currentToken.kind > 2  && currentToken.kind < 39)
          //Para las palabras reservadas se genera un boldtype
          System.out.print(HTMLStyles.boldHTMLString(currentToken.spelling));
        else if(currentToken.kind < 2){
          System.out.print(HTMLStyles.blueHTMLString(currentToken.spelling));
        }
        else{
          System.out.print(currentToken.spelling); 
        }
        currentToken = lexicalAnalyser.scan();
      }
      System.out.print(HTMLStyles.finishHTML());   
  }
  void getLineBreaks(String separator){
    for (char ch: separator.toCharArray()) {
      if (ch == '\n')
        System.out.print(HTMLStyles.addLineBreak());
      else if (ch == '\t') {
        System.out.print(HTMLStyles.addTab());
      }
    }
  }
}


//Palabras reservadas tokens en negrita
//Numeros variable sstrings en azul CHARLITERAL|INTLITERAL #0000cd
//Comentarios en verde  #00b300'
//IDENTIFIER

//Genera un paragraph.