/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorla;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author kananishi
 */
public class CompiladorLA {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        //Teste Análise Léxica
            ANTLRInputStream ais = new ANTLRInputStream(new FileInputStream("./test/"));///home/kananishi/Documents/UFSCar/CC2/Trabalho1/CompiladorLA/test/casosDeTesteT1"));
            LALexer lex = new LALexer(ais);
            Token t = null;
            System.out.println("Analise Léxica:");
            while ((t = lex.nextToken()).getType() != Token.EOF) {
                System.out.println("<"+t.getType() + "," + t.getText()+">");
            }
        
    }
}
