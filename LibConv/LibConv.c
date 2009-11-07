#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
	
#define MAX_LEN 80
	
int main() {
	   FILE *ptr_file;
	   char *nome_file = "PCB.fcl.unix";
	   char vettore[MAX_LEN];
	   char *stringa;
		
	   ptr_file = fopen(nome_file,"r");
	
	   printf("Visualizzo una stringa del file: \"%s\"\n", nome_file);
	
	   while((stringa = fgets(vettore, MAX_LEN, ptr_file))!=NULL){
	   		stringa[strlen(stringa)-1]='\0';
	   		printf("\"%s \\n\"+\n", stringa);
	   	}
	
	   fclose(ptr_file);
	
	   return(0);
}