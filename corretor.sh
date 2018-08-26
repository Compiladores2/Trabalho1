#!/bin/bash
clear
java -jar ./CorretorTrabalho1/CorretorTrabalho1.jar "java -jar ./CompiladorLA/dist/CompiladorLA.jar" gcc ./CorretorTrabalho1/temp/ ./CompiladorLA/test/casosDeTesteT1/ "628298,628042,595136" $1