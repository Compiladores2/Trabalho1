package compiladorla;

import java.util.List;

/*
    1 - identificador ja declarado anteriormente
    2 - tipo nao declarado
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
            //return visitDeclaracao_global(ctx.declaracao_global());
        }
        return null;
    }
    
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        
        if(ctx.variavel() != null){
            //TabelaDeSimbolos ts = pilha.topo();
            visitVariavel(ctx.variavel());
        }
        else if(ctx.IDENT() != null){
            TabelaDeSimbolos ts = pilha.topo();
            if(!ts.existeSimbolo(ctx.IDENT().getText())){
                visitTipo_basico(ctx.tipo_basico());
                visitValor_constante(ctx.valor_constante());
                ts.adicionarSimbolo(ctx.getText(), "Constante");
                
            }
        }
        
        return null;    
    }

    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) { 
        TabelaDeSimbolos escopoAtual = pilha.topo();
        for(LAParser.IdentificadorContext id:ctx.identificador()){
            if(escopoAtual.existeSimbolo(id.getText())){
                sp.println("Linha " + ": identificador " + id.getText() + " ja declarado anteriormente");
            }
            else{ // Inserir variavel na tabela de simbolos
                escopoAtual.adicionarSimbolo(id.getText(), ctx.tipo().getText() );
            }
        }
        return null;
    }
    
    @Override
    public Void visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        
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
            visitCmdRetorne(ctx.cmdRetorne());
        
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
        return super.visitExpressao(ctx); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Void visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        // Verificar se o tipo existe
        if(ctx.getText() != "literal" | ctx.getText() != "inteiro" | ctx.getText() != "real" | ctx.getText() != "logico"){
            sp.println("Linha " + ctx.getText() + " tipo nao declarado");
        }
        return null;
    }
    
    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        return super.visitIdentificador(ctx); //To change body of generated methods, choose Tools | Templates.
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
    public Void visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        return super.visitParcela_logica(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitFator_logico(LAParser.Fator_logicoContext ctx) {
        return super.visitFator_logico(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        return super.visitTermo_logico(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    

    @Override
    public Void visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        return super.visitOp_relacional(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        return super.visitExp_relacional(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        return super.visitParcela_nao_unario(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        return super.visitParcela_unario(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitParcela(LAParser.ParcelaContext ctx) {
        return super.visitParcela(ctx); //To change body of generated methods, choose Tools | Templates.
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
    public Void visitFator(LAParser.FatorContext ctx) {
        return super.visitFator(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitTermo(LAParser.TermoContext ctx) {
        return super.visitTermo(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        return super.visitExp_aritmetica(ctx); //To change body of generated methods, choose Tools | Templates.
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
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        return super.visitCmdAtribuicao(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitCmdFaca(LAParser.CmdFacaContext ctx) {
        return super.visitCmdFaca(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        return super.visitCmdEnquanto(ctx); //To change body of generated methods, choose Tools | Templates.
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
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        return super.visitCmdLeia(ctx); //To change body of generated methods, choose Tools | Templates.
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
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        return super.visitDeclaracao_global(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitRegistro(LAParser.RegistroContext ctx) {
        return super.visitRegistro(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitValor_constante(LAParser.Valor_constanteContext ctx) {
        return super.visitValor_constante(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        return super.visitTipo_estendido(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitTipo(LAParser.TipoContext ctx) {
        return super.visitTipo(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitDimensao(LAParser.DimensaoContext ctx) {
        return super.visitDimensao(ctx); //To change body of generated methods, choose Tools | Templates.
    }

       
}
