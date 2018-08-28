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
        
        File fin = new File(args[0]);
        File fout = new File(args[1]); 
        PrintWriter pw = new PrintWriter(new FileWriter(fout));
        /*
         //Seleção do caso de Teste
        String CAMINHO_CASOS_TESTE = "/home/kananishi/Documents/UFSCar/CC2/Trabalho1/Trabalho1/CompiladorLA/test/casosDeTesteT1/";
        String tipo = "2.arquivos_com_erros_semanticos/";
        String caso = "1.algoritmo_2-2_apostila_LA.txt";
        String fin = CAMINHO_CASOS_TESTE + tipo + "entrada/" + caso;
        */
        
        SaidaParser out = new SaidaParser();
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(fin));
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(new T1ErrorListener(out));
        
        try {
            ProgramaContext arvore = parser.programa();
            //AnalisadorSemantico as = new AnalisadorSemantico(out);
            //as.visitPrograma(arvore);
        } catch(ParseCancellationException pce) {
            out.println(pce.getMessage());
        }

        if (!out.isModificado()) {
            // Fim da analise. Sem erros sintaticos
            //System.out.println(out);
            //pw.print(out);
        } else {
            //Fim da analise. Com erros sintaticos
            //System.out.println(out);
            pw.println(out);
            pw.println("Fim da compilacao");
            
        }
        pw.close();

    }// end main
     
    
}
