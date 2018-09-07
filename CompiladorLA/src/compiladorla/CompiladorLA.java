package compiladorla;

import compiladorla.LAParser.ProgramaContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(new T1ErrorListener(out));
        
        try {
            ProgramaContext arvore = parser.programa();   
            
            // Nao houve erros sintaticos
            if(!out.isModificado()){
                AnalisadorSemantico as = new AnalisadorSemantico(out);
                as.visitPrograma(arvore);
            }
        } catch(ParseCancellationException pce) {
            out.println(pce.getMessage());
        }

        // Não houve erros ao compilar o programa
        if (!out.isModificado()) {
            pw.print("Sem erros");
        } 
        
        else {
            // Mostra no arquivo de saida os erros na compilacao do programa
            pw.print(out);
            pw.println("Fim da compilacao");
            
        }
        pw.close();

    }// end main
     
    
}
