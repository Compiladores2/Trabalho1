#include <stdio.h>
#include <stdlib.h>

int main(){
	int i;
	i=1;
	do{
	printf("%d\n", i);
	i=i+1;
	}
	while(!(i==6));
	return 0;
}