package compiladorla;

import java.io.File;
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
         
        //File fin = new File(args[0]);
        //File fout = new File(args[1]); 
          
        //Seleção do caso de Teste
        String CAMINHO_CASOS_TESTE = "/home/kananishi/Documents/UFSCar/CC2/Trabalho1/Trabalho1/CompiladorLA/test/casosDeTesteT1/3.arquivos_sem_erros/";
        File diretorioCasosTeste = new File(CAMINHO_CASOS_TESTE + "/1.entrada");
        String caso = "18.procedimento_impressao.alg";
        File[] casosTeste = diretorioCasosTeste.listFiles();
        int totalCasosTeste = casosTeste.length;
        int casosTesteErrados = 0;
        
        //String casoTeste = CAMINHO_CASOS_TESTE + "1.entrada/" + caso;{
        for (File casoTeste : casosTeste){
            SaidaParser out = new SaidaParser();
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(casoTeste));
            TabelaDeSimbolos.limparTabela();
            LALexer lexer = new LALexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);
            
            parser.addErrorListener(new T1ErrorListener(out));
            try {
               parser.programa();
            } catch(ParseCancellationException pce) {
                out.println(pce.getMessage());
            }

            if (!out.isModificado()) {
                System.out.println("Fim da analise. Sem erros sintaticos.");
                System.out.println("Tabela de simbolos:");

                TabelaDeSimbolos.imprimirTabela(out);
                System.out.print(out);
            } else {
                System.out.println("Fim da analise. Com erros sintaticos.");
                casosTesteErrados++;
            }
        }
        System.out.println("Casos de teste errados: " + casosTesteErrados);
    }// end main
}
