# Trabalho 1 de Compiladores 2

## Compilador Linguagem LA

## Grupo
  - Guilherme Nishi Kanashiro - 628298
  - Jorge de Carvalho Kawamura - 595136
  - Rodolfo Krambeck Asbahr - 628042
  
## Requisitos de Software
  - ANTLR (ANother Tool for Language Recognition) - versão 4.7.1
  - NetBeans 8.2

#### Como gerar a gramática usando o antlr 4.7.1?
	java -jar antlr-4.7.1-complete.jar -package compiladorla LA.g4

#### Como usar o Corretor Automático?
	java -jar CorretorTrabalho1.jar "java -jar compilador/CompiladorLA.jar" gcc temp/ casosDeTesteT1/ "RA" sintatico


#### Como usar o script?
Adicionar permissão de execução ao script
	
	chmod +x corretor.sh

Executar o script com uma das opções de correção (sintatico, semantico, gerador ou tudo)

	./script.sh opcao_de_correcao
