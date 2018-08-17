package compiladorla;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

/**
 *  Trabalho 1 de Compiladores 2
 * @author 
 */
public class CompiladorLA {

     public static void main(String args[]) throws IOException, RecognitionException {
         
        SaidaParser out = new SaidaParser();
        //Selecione o caso de teste
        String caso = "1-algoritmo_2-2_apostila_LA_1_erro_linha_3_acusado_linha_10.txt";
        
        // Coloque aqui o caminho para os arquivos de teste
        // Caminho para pasta onde salvou o arquivo + CompiladorLA/test/casosDeTesteT1/1.arquivos_com_erros_sintaticos/entrada/
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream("/home/kananishi/Documents/UFSCar/CC2/Trabalho1/Trabalho1/CompiladorLA/test/casosDeTesteT1/1.arquivos_com_erros_sintaticos/entrada/" + caso));
        
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(new T1ErrorListener(out));
        parser.programa();
        
        if (!out.isModificado()) {
            out.println("Fim da analise. Sem erros sintaticos.");
            out.println("Tabela de simbolos:");

            TabelaDeSimbolos.imprimirTabela(out);
            System.out.print(out);
        } else {
            System.out.println("Fim da analise. Com erros sintaticos.");
        }
        
        
    }
}
