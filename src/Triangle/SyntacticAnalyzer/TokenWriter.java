package Triangle.SyntacticAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TokenWriter {

  private Scanner lexicalAnalyser;
  private Token currentToken;
  private String sourceFileName;

  public TokenWriter(String sourceName, Scanner lexer) {
    lexicalAnalyser = lexer;
    sourceFileName = sourceName;
  }

  //Se van a agregando los tokens que corresponden a los estilos que se piden en el trabajo se pone en 2 para tomar en cuenta los literales
  //caracteres y enteros
  //Se pone en el 39 como tope para que no tome los parentesis.
  public void writeTokens() {
    try {
      sourceFileName = sourceFileName.replaceAll("\\.tri", ".html"); // Se cambia la extensión a HTML (Austin)
      FileWriter fileWriter = new FileWriter(sourceFileName); // Se agrega el nombre del archivo del código fuente (Austin)
      currentToken = lexicalAnalyser.scan();
      fileWriter.write(HTMLStyles.startHTML());

        while (currentToken.kind != Token.EOT) {

          //Verifica si hay algo en el separador y si este es un comentario
          if (!currentToken.separator.isEmpty()){
              Separator formatedSeparator = formatSeparator(fileWriter, currentToken.separator);
              if(formatedSeparator.isComment())
                fileWriter.write(HTMLStyles.commentHTMLString(formatedSeparator.getSeparator()));
              else
                fileWriter.write(formatedSeparator.getSeparator());
          }
          if (currentToken.kind > 2  && currentToken.kind < 39)
            //Para las palabras reservadas se genera un boldtype
            fileWriter.write(HTMLStyles.boldHTMLString(currentToken.spelling));
          else if (currentToken.kind < 2){
            fileWriter.write(HTMLStyles.blueHTMLString(currentToken.spelling));
          }
          else{
            fileWriter.write(currentToken.spelling); 
          }
          currentToken = lexicalAnalyser.scan();
          
        }
        fileWriter.write(HTMLStyles.finishHTML());   
        fileWriter.close();
    } catch (IOException e) {
      System.err.println("Ocurrió un error al escribir el código fuente al archivo HTML.");
      e.printStackTrace();
    }
  }

  Separator formatSeparator(FileWriter fileWriter, String separator){

      Separator formatedSeparator = new Separator();
      for (char ch: separator.toCharArray()) {
        if (ch == '\n')
          formatedSeparator.addSeparator(HTMLStyles.addLineBreak());
        if (ch == '\t') {
          formatedSeparator.addSeparator(HTMLStyles.addTab());
        }
        if(ch == '!' && !formatedSeparator.isComment())
        {
          formatedSeparator.setComment(true);
        }
        formatedSeparator.addSeparator(ch);
      }
      return formatedSeparator;
  }
  
}


//Palabras reservadas tokens en negrita
//Numeros variable sstrings en azul CHARLITERAL|INTLITERAL #0000cd
//Comentarios en verde  #00b300'
//IDENTIFIER

//Genera un paragraph.

//En todos los separadores tengo que buscar comentarios y espacios