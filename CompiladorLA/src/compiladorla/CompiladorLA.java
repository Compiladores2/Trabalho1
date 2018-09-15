package compiladorla;

import compiladorla.LAParser.ProgramaContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 *  Trabalho 1 de Compiladores 2
 * @author 
 */
public class CompiladorLA {

     public static void main(String args[]) throws IOException, RecognitionException {
        
        // Arquivos de Entrada e Saida passados como parametros
        File fin = new File(args[0]);
        File fout = new File(args[1]); 
        PrintWriter pw = new PrintWriter(new FileWriter(fout));
        
        SaidaParser out = new SaidaParser();
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(fin));
        LALexer lexer = new LALexer(input);
        LALexer lex = new LALexer(new ANTLRInputStream(new FileInputStream(fin)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(new T1ErrorListener(out));
        
        
        
        try {
            ProgramaContext arvore = parser.programa();   
            
            // Nao houve erros sintaticos - Analise Semantica
            if(!out.isModificado()){
                AnalisadorSemantico as = new AnalisadorSemantico(out);
                as.visitPrograma(arvore);
            }
            
           
        } catch(ParseCancellationException pce) {
            out.println(pce.getMessage());
        }

         // Não houve erros ao compilar o programa - Gerador de Codigo
        if (!out.isModificado()) {
            
            compilador(pw, lex);

        } 

        else {
            // Mostra no arquivo de saida os erros na compilacao do programa
            pw.print(out);
            pw.println("Fim da compilacao");

        }
        pw.close();
        

    }// end main
     
    public static void compilador(PrintWriter pw, LALexer lex){   
        boolean caso = false;
        String idReg = null;
        TabelaDeSimbolos tabela = new TabelaDeSimbolos("GC");
        pw.println("#include <stdio.h>\n#include <stdlib.h>\n");
        Token t = lex.nextToken();
        
        while (t.getType() != Token.EOF) {
            // Inicio da main
            if(t.getText().equals("algoritmo")){
                pw.println("int main(){");
                t=lex.nextToken();
            }
            // Fim da funcao main
            else if(t.getText().equals("fim_algoritmo")){
                pw.print("\treturn 0;\n}");
                t=lex.nextToken();
            }
            // Declaracao de variavel
            else if(t.getText().equals("declare")){
                if((t=lex.nextToken()).getType() == 60){
                    ArrayList<String> var = new ArrayList();
                    var.add(t.getText());
                    while((t=lex.nextToken()).getText().equals(",")){
                        t = lex.nextToken();
                        if(t.getType() == 60){
                           var.add(t.getText());
                        }
                    }
                        
                    t = lex.nextToken(); // tipo
                    int i = 0;
                    switch(t.getType()){
                        case 12:
                            pw.print("\tchar ");
                            tabela.adicionarSimbolo(var.get(0), "literal");
                            pw.print(var.remove(0)+"[80]");
                            while(!var.isEmpty()){
                                tabela.adicionarSimbolo(var.get(i), "literal");
                                pw.print(","+var.remove(i)+"[80]");
                                    //i++;
                            }
                            pw.println(";");
                            break;
                        case 13:
                            pw.print("\tint ");
                            tabela.adicionarSimbolo(var.get(0), "inteiro");
                            pw.print(var.remove(0));
                            while(!var.isEmpty()){
                                tabela.adicionarSimbolo(var.get(0), "inteiro");
                                pw.print(","+var.remove(0));
                            }
                            pw.println(";");
                            break;
                        case 14:
                            pw.print("\tfloat ");
                            tabela.adicionarSimbolo(var.get(0), "real");
                            pw.print(var.remove(0));
                            while(!var.isEmpty()){
                                tabela.adicionarSimbolo(var.get(i), "real");
                                pw.print(","+var.remove(i));
                                //i++;
                            }
                            pw.println(";");
                            break;
                        case 19:    // Registro
                            pw.print("\t struct{");
                            tabela.adicionarSimbolo(var.get(0), "registro");
                            idReg = var.remove(0);
                            break;
                        default:
                            break;
                        }
      
                }
                t=lex.nextToken();
            }
            
            // Leia
            else if(t.getText().equals("leia")){
                t=lex.nextToken(); // (
                while((t=lex.nextToken()).getType() == 60){
                    String tipo = tabela.getTipo(t.getText());
                    switch (tipo) {
                        case "inteiro":
                            pw.print("\tscanf(\"%d\",&" + t.getText());
                            break;
                        case "real":
                            pw.print("\tscanf(\"%f\",&" + t.getText());
                            break;
                        case "literal":
                            pw.print("\tgets(");
                            pw.print(t.getText());
                             break;
                        default:
                            break;
                    }
                        
                }
                   
                if(t.getText().equals(")")){
                    pw.println(");");
                }
                t=lex.nextToken();
                  
            }
            
            //Escreva
            else if(t.getText().equals("escreva")){
                pw.print("\tprintf(\""); // printf("
                t=lex.nextToken(); // "("
                t=lex.nextToken(); // Primeira
                ArrayList<String> expressao = new ArrayList();
                while(!t.getText().equals(")")){                       
                    // Se for cadeia
                    if(t.getType() == 61){
                        String s = t.getText();
                        s = s.replace("\"", "");
                        pw.print(s);
                        t=lex.nextToken();
                        if(t.getText().equals(")")){
                            //pw.print("\"");
                            //expressao.add("\"");
                        }
                    } 
                    // Se for um identificador
                    else if(t.getType() == 60){
                        String tipo = tabela.getTipo(t.getText());
                        switch (tipo) {
                            case "inteiro":
                                pw.print("%d");
                                break;
                            case "real":
                                pw.print("%f");
                                break;
                            case "literal":
                                pw.print("%s");
                                break;
                            default:
                                break;
                        }
                        //pw.print(t.getText()); // identificador
                        expressao.add(t.getText());
                        t=lex.nextToken();
                    }
                     
                    else if(t.getText().equals("+") || t.getText().equals("-")){
                        Token var = lex.nextToken();
                        if(var.getType() == 60){
                            //pw.print(t.getText() + var.getText());
                            expressao.add(t.getText());
                            expressao.add(var.getText());
                       }
                        t=lex.nextToken();
                    }
                    else
                        t=lex.nextToken();
                 
                }// Fim while ")"
                
                if(t.getText().equals(")")){
                    pw.print("\"");
                    if(!expressao.isEmpty()){
                        pw.print(", ");
                        while(!expressao.isEmpty()){
                            pw.print(expressao.remove(0));
                        }
                    }
                    
                    pw.println(");");
                    t=lex.nextToken();
                }
            }// Fim escreva
                
                
            else if(t.getType() == 60){
                Token op = lex.nextToken();
                //Atribuicao
                if(op.getText().equals("<-")){
                    pw.print("\t" + t.getText() + "=");
                    t = lex.nextToken();
                    pw.print(t.getText());
                    //  Operadores matemáticos recebem valor acima de 45
                    while((t=lex.nextToken()).getType() > 45){
                        pw.print(t.getText());
                    }
                    pw.println(";");
                }
                // Comparação
                else
                    t=lex.nextToken();
            }// Fim identificador
            
            // Comando Condicional - Inicio
            else if(t.getText().equals("se")){
                pw.print("\tif(");
                while(!(t=lex.nextToken()).getText().equals("entao")){
                    if(t.getText().equals("=")){
                        pw.print("==");
                    }
                    else if(t.getText().equals("<>")){
                        pw.print("!=");
                    }
                    else if(t.getText().equals("e")){
                        pw.print(" && ");
                    }
                    else if(t.getText().equals("ou")){
                        pw.print(" || ");
                    }
                    else{
                        pw.print(t.getText());
                    }
               }
                pw.print("){\n");
                t=lex.nextToken();
            }
            
            // Comando Condicional - else
            else if(t.getText().equals("senao") && caso == false){
                pw.println("\t}"); // Fim do if
                pw.println("\telse{");
                t=lex.nextToken();
            }
                
            // Comando Condicional - Fim
            else if(t.getText().equals("fim_se")){
                pw.println("\t}");
                t=lex.nextToken();
            }
            
            // Caso - switch
            else if(t.getText().equals("caso")){
                pw.print("\tswitch(");
                t=lex.nextToken(); //comparador
                pw.println(t.getText() + "){");
                t=lex.nextToken(); // seja
                t=lex.nextToken();
                caso = true;
            } 
            
            else if(t.getType() == 62){
                Token op = lex.nextToken();
                pw.println("\tbreak;");
                if(op.getText().equals("..")){
                    Token limit = lex.nextToken();
                    for(int i = Integer.parseInt(t.getText()); i <= Integer.parseInt(limit.getText());i++){
                        pw.println("\tcase " + i + ":");
                    }
                    op = lex.nextToken(); // :
                }
                      
                else if(op.getText().equals(":")){
                    pw.println("\tcase " + t.getText() + ":");
                }
                    
                t = lex.nextToken();
            }
                    
            else if(t.getText().equals("senao") && caso == true){
                pw.println("\tdefault:");
                pw.println("\tbreak;");
                t = lex.nextToken();
                caso = false;
            }
            
            // Fim caso
            else if(t.getText().equals("fim_caso")){
                pw.println("\t}");
                caso = false;
                t=lex.nextToken();
            }
            
            // Declaracao de constante
            else if(t.getText().equals("constante")){
                Token constante = lex.nextToken(); // identificador
                lex.nextToken(); // :
                lex.nextToken(); // tipo
                lex.nextToken(); // ==
                Token valor = lex.nextToken();
                pw.println("#define " + constante.getText() + " " + valor.getText());
                t=lex.nextToken();
            }
            
            // Loop - for
            else if(t.getText().equals("para")){
                pw.print("\tfor(");
                
                Token contLaco = lex.nextToken();   // Variavel contadora de laco
                pw.print("int " + contLaco.getText() + " = ");
                lex.nextToken();    // <-
                // Expressao - valor inicial da variavel contadora de laco
                while(!(t=lex.nextToken()).getText().equals("ate")){
                    pw.print(t.getText());
                }
                pw.print("; i <= ");
                //Expressao valor de parada
                while(!(t=lex.nextToken()).getText().equals("faca")){
                    pw.print(t.getText());
                }
                pw.println("; i++){");
                t = lex.nextToken();
            }
            
            // Fim for
            else if(t.getText().equals("fim_para")){
                pw.println("\t}");
                t = lex.nextToken();
            }
            
            // Loop while
            else if((t.getText()).equals("enquanto")){
                pw.print("\twhile(");
               
                // Condicao de parada
                while(!(t=lex.nextToken()).getText().equals("faca")){
                    pw.print(t.getText());
                }
                pw.println("){");
                t = lex.nextToken();
            }
            
            // Fim while
            else if(t.getText().equals("fim_enquanto")){
                pw.println("\t}");
                t = lex.nextToken();
            }
            
            // Loop do while
            else if(t.getText().equals("faca")){
                pw.println("\tdo{");
                t = lex.nextToken();
            }
            
            // End loop do while
            else if(t.getText().equals("ate")){
                pw.println("\t}");
                pw.print("\twhile(");
                while(!(t=lex.nextToken()).getText().equals(")")){
                    if(t.getText().equals("nao")){
                        pw.print("!");
                    }
                    else if(t.getText().equals("=")){
                        pw.print("==");
                    }
                    else{
                        pw.print(t.getText());
                    }
                }
                pw.println("));");   // da pra fazer um função de imprimir o ) toda vez que encontrar
                t=lex.nextToken();
            }
            
            // Registro
            else if(t.getText().equals("fim_registro")){
                pw.println("}");
                t = lex.nextToken();
            }
            
            else
                t=lex.nextToken();
            
            }// Fim da Geração de código
    }//Fim funcao compilacao
    
}// Fim class


/*
algoritmo
  declare 
     reg: registro
            nome: literal
            idade: inteiro
          fim_registro
  reg.nome <- "Maria"
  reg.idade <- 24
  escreva(reg.nome," tem ",reg.idade," anos")
fim_algoritmo
*/