# Trabalho 1 de Compiladores 2

## Compilador Linguagem LA

## Grupo
  - Guilherme Nishi Kanashiro - 628298
  - Jorge de Carvalho Kawamura - 595136
  - Rodolfo Krambeck Asbahr - 628042
  
## Requisitos de Software
  - ANTLR (ANother Tool for Language Recognition) - versão 4.7.1
  - NetBeans 8.2
  - Compilador GCC

#### Como gerar a gramática usando o antlr 4.7.1?
A gramática LA.g4 está na pasta: /CompiladorLA/src/compiladorla.
O gerador antlr já está na pasta do projeto junto com a gramática. 

Execute o seguinte comando no terminal para gerar a gramática:

	$ java -jar antlr-4.7.1-complete.jar -package compiladorla LA.g4

O Analisador Semântico foi implementado usando visitor e se encontra no arquivo AnalisadorSemantico.java.
A lógica principal do compilador está implementada no arquivo CompiladorLA.java

#### Como compilar/interpretar o compilador?
Para compilar o compilador desenvolvido, siga os seguintes passos:
	
	1. Abra o projeto do compilador no NetBeans
	2. No menu "Run" selecione a opção "Clean and Build Project"
	3. O executável .jar será gerado na pasta "dist" do projeto CompiladorLA
	
#### Como utilizar o compilador gerado ?
Para compilar e gerar o código fonte de um programa em linguagem LA é preciso passar como parâmetro o nome do programa fonte e o nome do programa objeto :

	$ java -jar CompiladorLA.jar programaFonte programaObjeto
	

#### Como usar o script de Correção automática? 
Para facilitar a correção automática foi gerado um script. Para usá-lo, basta colocar em uma pasta o projeto do CompiladorLA, a pasta do CorretorTrabalho1 e o script corretor.sh.

Adicionar permissão de execução ao script:
	
	$ chmod +x corretor.sh

Executar o script com uma das opções de correção (sintatico, semantico, gerador ou tudo):

	$ ./corretor.sh opcao_de_correcao
