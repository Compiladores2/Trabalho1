package compiladorla;

import java.util.List;

/* Rodando: 1,2,3,4,5,6,9,18
    1 - identificador ja declarado anteriormente
    2 - tipo nao declarado v
    3 - identificador nao declarado
    4 - incompatibilidade de argumentos(num, ordem e tipo) na chamada de funcoes/proc
    5 - atribuicao n compativel com o tipo declarado
    6 - uso de return em escopo nao permitido
*/
class AnalisadorSemantico extends LABaseVisitor<Void>{
    
    // Arquivo para mostrar erro
    public SaidaParser sp;
    public AnalisadorSemantico(SaidaParser sp) {
        this.sp = sp;
    }

    // Tabelas diferentes para escopos diferentes
    private final PilhaDeTabelas pilha = new PilhaDeTabelas();
    
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        TabelaDeSimbolos geral = new TabelaDeSimbolos("Global");
        pilha.empilhar(geral);
        if(ctx.declaracoes() != null){
            visitDeclaracoes(ctx.declaracoes());
        }
        visitCorpo(ctx.corpo());
        pilha.desempilhar();
        return null;
    }
    
    @Override
    public Void visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        for(LAParser.Decl_local_globalContext dlg:ctx.decl_local_global()){
            visitDecl_local_global(dlg);
        }
        return null;
    }
    
    @Override
    public Void visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        if(ctx.declaracao_local() != null){
            visitDeclaracao_local(ctx.declaracao_local());
        } else if(ctx.declaracao_global() != null){
            return visitDeclaracao_global(ctx.declaracao_global());
        }
        return null;
    }
    
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        
        if(ctx.variavel() != null){
            //TabelaDeSimbolos ts = pilha.topo();
            visitVariavel(ctx.variavel());
        }
        
        // Definicao de tipo
        else if(ctx.IDENT() != null){
            TabelaDeSimbolos escopoAtual = pilha.topo();
            if(escopoAtual.existeSimbolo(ctx.IDENT().getText())){
                sp.println("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.IDENT().getText() + " ja declarado anteriormente");
            }
            else{
                escopoAtual.adicionarSimbolo(ctx.IDENT().getText(), "tipo");
            }
            if(ctx.tipo() != null){
                visitTipo(ctx.tipo());
            }
        }
        
        return null;    
    }
    
    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        
        if(ctx.IDENT() != null){
            //pilha.empilhar(new TabelaDeSimbolos("Funcao"));
        }
        
        return null;
    }


    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) { 
        
        for(LAParser.IdentificadorContext id:ctx.identificador()){
            TabelaDeSimbolos escopoAtual = pilha.topo();
            if(escopoAtual.existeSimbolo(id.getText())){
                sp.println("Linha " + id.getStart().getLine() + ": identificador " + id.getText() + " ja declarado anteriormente");
            }

            else{ // Variavel nao declarada
                // Verificar se o tipo é valido antes de inserir na tabela
                if(ctx.tipo() != null){
                    visitTipo(ctx.tipo());
                }
                
                escopoAtual.adicionarSimbolo(id.getText(), ctx.tipo().getText());
                
                // Falta fazer os vetores e ponteirosif(id.dimensao() != null){
                /*   sp.println((id.dimensao().getText()));
                    for(int i=id.dimensao().getAltNumber(); i > 0 ; i--){
                        escopoAtual.adicionarSimbolo(ctx.getText()+"["+i+"]", ctx.tipo().getText() );
                        
                    }
                }   
                else{
                    escopoAtual.adicionarSimbolo(ctx.getText(), ctx.tipo().getText());
                }*/
            }  
            
        }
        
        
        return null;
    }
    
    @Override
    public Void visitTipo(LAParser.TipoContext ctx) {
        if(ctx.registro() != null)
            visitRegistro(ctx.registro());
        else if (ctx.tipo_estendido() != null)
            visitTipo_estendido(ctx.tipo_estendido());
        
        return null;
    }

    @Override
    public Void visitRegistro(LAParser.RegistroContext ctx) {
        for(LAParser.VariavelContext var: ctx.variavel()){
            visitVariavel(var);
        }
        return null;
    }
    
    @Override
    public Void visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        
        if(ctx.tipo_basico_ident() != null)
            visitTipo_basico_ident(ctx.tipo_basico_ident());
        
        else{
            TabelaDeSimbolos escopoAtual = pilha.topo();
            if(escopoAtual.existeSimbolo(ctx.getText())){
                return null;
            }
            else{   // Tipo não declarado
                sp.println("Linha " + ctx.getStart().getLine() + ": tipo " + ctx.getText() + " nao declarado");
            }
        }
        return null;
    }

    @Override
    public Void visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        if(ctx.tipo_basico() != null)
            return null;
            //visitTipo_basico(ctx.tipo_basico());
        else if(ctx.IDENT() != null){
            TabelaDeSimbolos ts = pilha.topo();
            if(!ts.existeSimbolo(ctx.getText())){
                sp.println("Linha " +  ctx.getStart().getLine() + ": tipo " + ctx.getText() + " nao declarado");
            }
        }
            
        return null;
    }
    
    @Override
    public Void visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        String tipo = ctx.getText();
        // Verificar se o tipo existe
        if(ctx.getText().equals("literal") | ctx.getText().equals("inteiro") | ctx.getText().equals("real") | ctx.getText().equals("logico")){
            return null;
        }
            
        else{
            sp.println("Linha " + ctx.getStart().getLine()+ ": tipo " + ctx.getText() + " nao declarado");
        }
        return null;
    }
    
    
    @Override
    public Void visitCorpo(LAParser.CorpoContext ctx) {
        
        for(LAParser.Declaracao_localContext dl: ctx.declaracao_local()){
            visitDeclaracao_local(dl);  //Verificar se variavel foi declarada anteriormente
        }
        
        for (LAParser.CmdContext cmd:ctx.cmd()){
            visitCmd(cmd);
        }
        return null;
    }
    
    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {
        if(ctx.cmdLeia() != null)
            visitCmdLeia(ctx.cmdLeia());
        else if(ctx.cmdEscreva() != null)
            visitCmdEscreva(ctx.cmdEscreva());
        else if(ctx.cmdSe() != null)
            visitCmdSe(ctx.cmdSe());
        else if(ctx.cmdCaso() != null)
            visitCmdCaso(ctx.cmdCaso());
        else if(ctx.cmdPara() != null)
            visitCmdPara(ctx.cmdPara());
        else if(ctx.cmdEnquanto() != null)
            visit(ctx.cmdEnquanto());
        else if(ctx.cmdEnquanto() != null)
            visitCmdEnquanto(ctx.cmdEnquanto());
        else if(ctx.cmdFaca() != null)
            visitCmdFaca(ctx.cmdFaca());
        else if(ctx.cmdAtribuicao() != null)
            visitCmdAtribuicao(ctx.cmdAtribuicao());
        else if(ctx.cmdChamada() != null)
            visitCmdChamada(ctx.cmdChamada());
        else if(ctx.cmdRetorne() != null)
            if(!pilha.topo().getEscopo().equals("Funcao"))
                sp.println("Linha " + ctx.getStart().getLine() + ": comando retorne nao permitido nesse escopo");
        
        return null;
    }
    
    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        TabelaDeSimbolos escopoAtual = pilha.topo();
        for(LAParser.IdentificadorContext id:ctx.identificador()){
            if(!escopoAtual.existeSimbolo(id.getText())){
                sp.println("Linha " + id.getStart().getLine() + ": identificador " + id.getText() + " nao declarado");
            }
        }
        
        return null;
    }

    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx) { 
        for(LAParser.ExpressaoContext exp:ctx.expressao()){
            visitExpressao(exp);
        }
        return null;
    }
    
    @Override
    public Void visitExpressao(LAParser.ExpressaoContext ctx) {
        for(LAParser.Termo_logicoContext termo:ctx.termo_logico()){
            visitTermo_logico(termo);
        }
        return null;
    }
    
     @Override
    public Void visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        for(LAParser.Fator_logicoContext fator: ctx.fator_logico()){
            visitFator_logico(fator);
        }
        return null;
    }
    
    @Override
    public Void visitFator_logico(LAParser.Fator_logicoContext ctx) {
        visitParcela_logica(ctx.parcela_logica());
        return null;
    }
   
    @Override
    public Void visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        if(ctx.exp_relacional() != null){
            visitExp_relacional(ctx.exp_relacional());
        }
        
        return null;
    }   
    
    @Override
    public Void visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        for(LAParser.Exp_aritmeticaContext expA: ctx.exp_aritmetica()){
            visitExp_aritmetica(expA);
        }
        
        return null;
    }
    
    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        for(LAParser.TermoContext termo: ctx.termo()){
            visitTermo(termo);
        }
        
        return null;
    }
    
    @Override
    public Void visitTermo(LAParser.TermoContext ctx) {
        for(LAParser.FatorContext fator:ctx.fator()){
            visitFator(fator);
        }
        
        return null;
    }
    
    @Override
    public Void visitFator(LAParser.FatorContext ctx) {
        for(LAParser.ParcelaContext parc: ctx.parcela()){
            visitParcela(parc);
        }
        
        return null;
    }

    @Override
    public Void visitParcela(LAParser.ParcelaContext ctx) {
    
        if(ctx.parcela_unario() != null){
            visitParcela_unario(ctx.parcela_unario());
        }
        
        else if(ctx.parcela_nao_unario() != null){
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }        
        return null;
    }

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        return super.visitParcela_nao_unario(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if(ctx.identificador() != null){
            TabelaDeSimbolos escopoAtual = pilha.topo();
            if(!escopoAtual.existeSimbolo(ctx.identificador().getText())){
                sp.println("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.identificador().getText() + " nao declarado");
            }
        }
        
        return null;
    }
    
    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        if(ctx.identificador() != null){
            TabelaDeSimbolos escopoAtual = pilha.topo();
            if(!escopoAtual.existeSimbolo(ctx.identificador().getText())){
                sp.println("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.identificador().getText() + " nao declarado");
            }
            //Verificar tipo 
            else{
                String idTipo = escopoAtual.getTipo(ctx.identificador().getText());   //Tipo do identificador
                String expTipo = "";

                // Definir tipo da expressão
                if(ctx.expressao() != null){
                    
                    // Descer Parser tree até chegar em um identificador ou regra com tipo
                    for(LAParser.Termo_logicoContext tl:ctx.expressao().termo_logico()){
                        for(LAParser.Fator_logicoContext fl:tl.fator_logico()){
                            //Expressão aritmetica
                            if(fl.parcela_logica().exp_relacional().exp_aritmetica() != null ){
                                for(LAParser.Exp_aritmeticaContext expA:fl.parcela_logica().exp_relacional().exp_aritmetica()){
                                    for(LAParser.TermoContext t:expA.termo()){
                                        for(LAParser.FatorContext f:t.fator()){
                                            for(LAParser.ParcelaContext p:f.parcela()){
                                                if(p.parcela_unario() != null){
                                                    if(p.parcela_unario().identificador() != null){
                                                        if(escopoAtual.existeSimbolo(p.getText())){
                                                            if(!idTipo.equals("logico")){
                                                                expTipo = escopoAtual.getTipo(p.parcela_unario().identificador().getText());
                                                                if(idTipo.equals("real") && !expTipo.equals("inteiro") && !expTipo.equals("real")){
                                                                    sp.println("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para " + ctx.identificador().getText());
                                                                }
                                                                else if(!idTipo.equals("real") && !idTipo.equals(expTipo)){
                                                                    sp.println("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para " + ctx.identificador().getText());
                                                                }
                                                            }
                                                        }
                                                        else // Identificador não declarado
                                                            sp.println("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.identificador().getText() + " nao declarado");
                                                    } // Identificador
                                                    if(p.parcela_unario().Num_Int() != null && !idTipo.equals("inteiro") && !idTipo.equals("real"))
                                                        sp.println("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para " + ctx.identificador().getText());
                                                    if(p.parcela_unario().Num_Real() != null && !idTipo.equals("real") )
                                                        sp.println("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para " + ctx.identificador().getText());

                                                } // Parcela unario
                                                else if(p.parcela_nao_unario().Cadeia() != null && !idTipo.equals("literal")){
                                                    if(ctx.getText().contains("^"))
                                                        sp.println("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para ^" + ctx.identificador().getText());                                            
                                                    else
                                                        sp.println("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para " + ctx.identificador().getText());
                                                } // Parcela nao unario
                                            } // Parcela
                                        } //Fator
                                    } // Termo
                                } // For expressao aritmetica
                            } // Expressão aritmetica
                            // Se não é expressao aritmetica é Parcela logica - nao funfou
                            else{
                                expTipo = "logico";
                                if(!idTipo.equals(expTipo)){
                                    sp.println("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para " + ctx.identificador().getText());
                                }
                            }
                        }// Fator logico
                    } // For expressao
                }// End if - determinar tipo da expressao
            } //End else - Verifica tipo da expressao

        }
        return null;
    }
        
    
    @Override
    public Void visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        return super.visitCmdEnquanto(ctx);
    }
    
    //--------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {        
        return null;
    }
    
    @Override
    public Void visitOp_logico_2(LAParser.Op_logico_2Context ctx) {
        return super.visitOp_logico_2(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitOp_logico_1(LAParser.Op_logico_1Context ctx) {
        return super.visitOp_logico_1(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        return super.visitOp_relacional(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    
    

    

    @Override
    public Void visitOp3(LAParser.Op3Context ctx) {
        return super.visitOp3(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitOp2(LAParser.Op2Context ctx) {
        return super.visitOp2(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitOp1(LAParser.Op1Context ctx) {
        return super.visitOp1(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    

    @Override
    public Void visitOp_unario(LAParser.Op_unarioContext ctx) {
        return super.visitOp_unario(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitNumero_intervalo(LAParser.Numero_intervaloContext ctx) {
        return super.visitNumero_intervalo(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitConstantes(LAParser.ConstantesContext ctx) {
        return super.visitConstantes(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitItem_selecao(LAParser.Item_selecaoContext ctx) {
        return super.visitItem_selecao(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitSelecao(LAParser.SelecaoContext ctx) {
        return super.visitSelecao(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        return super.visitCmdRetorne(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        return super.visitCmdChamada(ctx); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public Void visitCmdFaca(LAParser.CmdFacaContext ctx) {
        return super.visitCmdFaca(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    

    @Override
    public Void visitCmdPara(LAParser.CmdParaContext ctx) {
        pilha.empilhar(new TabelaDeSimbolos("para"));
        
        // dasdsadsada
                
        pilha.desempilhar();
        return super.visitCmdPara(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitCmdCaso(LAParser.CmdCasoContext ctx) {
        return super.visitCmdCaso(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitCmdSe(LAParser.CmdSeContext ctx) {
        return super.visitCmdSe(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    

    @Override
    public Void visitParametros(LAParser.ParametrosContext ctx) {
        return super.visitParametros(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitParametro(LAParser.ParametroContext ctx) {
        return super.visitParametro(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    

    @Override
    public Void visitValor_constante(LAParser.Valor_constanteContext ctx) {
        return super.visitValor_constante(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    public Void visitDimensao(LAParser.DimensaoContext ctx) {
        return super.visitDimensao(ctx); //To change body of generated methods, choose Tools | Templates.
    }

       
}