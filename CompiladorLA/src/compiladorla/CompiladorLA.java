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
        TabelaDeSimbolos tabela = new TabelaDeSimbolos("GC");
        pw.println("#include<stdio.h>\n#include<stdlib.h>\n");
            Token t = null;
            while ((t = lex.nextToken()).getType() != Token.EOF) {
                // Inicio da main
                if(t.getText().equals("algoritmo")){
                    pw.println("int main(){");
                }
                // Fim da funcao main
                else if(t.getText().equals("fim_algoritmo")){
                    pw.print("\treturn 0;\n}");
                }
                // Declaracao de variavel
                else if(t.getText().equals("declare")){
                    if((t=lex.nextToken()).getType() == 60){
                        ArrayList<String> var = new ArrayList();
                        var.add(t.getText());
                        while((t=lex.nextToken()).getText().equals(",")){
                            t = lex.nextToken();
                            if(t.getType() == 60)
                                var.add(t.getText());
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
                            case 15:
                                break;
                        }
      
                    }
                    
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
                    
                }
                //Escreva
                else if(t.getText().equals("escreva")){
                    pw.print("\tprintf(\""); // printf("
                    t=lex.nextToken(); // "("
                    while(!(t=lex.nextToken()).getText().equals(")")){                       
                        // Se for cadeia
                        if(t.getType() == 61){
                            String s = t.getText();
                            s = s.replace("\"", "");
                            pw.print(s+"\"");
                        } 
                        // Se for um identificador
                        else if(t.getType() == 60){
                            ArrayList<String> expressao = new ArrayList();
                            Token var = t;
                            expressao.add(var.getText());
                            pw.print(expressao.remove(0));
                            while(!(t=lex.nextToken()).getText().equals(",") && !t.getText().equals(")")){
                                expressao.add(t.getText());
                            }
                            String tipo = tabela.getTipo(var.getText());
                            switch (tipo) {
                                case "inteiro":
                                    pw.print("%d\",");
                                    for(int i = 0;i<expressao.size();i++){
                                        pw.print(expressao.remove(0));
                                    }
                                    break;
                                case "real":
                                    pw.print("%f\",");
                                    for(int i = 0;i<expressao.size();i++){
                                        pw.print(expressao.remove(0));
                                    }
                                    break;
                                case "literal":
                                    pw.print("%s\",");
                                    for(int i = 0;i<expressao.size();i++){
                                        pw.print(expressao.remove(0));
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    
                    }
                    
                    if(t.getText().equals(")")){
                        pw.println(");");
                    }
                }
            }
        
    }
}
