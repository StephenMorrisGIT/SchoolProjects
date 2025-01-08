#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "hashmap.h"
#include "test.h"


int main(void){
  // get bucket input from user
  int buckets = 0;
  printf("How many buckets do you want? (If single digit enter 0 before (i.e.01)) \n");
  // accounts for a 2 digit number of buckets
  buckets += (getchar() - '0') * 10;
  buckets += getchar() - '0';
  getchar();   // accounts for the user hitting enter after the num_buckets

  int num_files = 3; // choose how many files should be entered
  char* files[num_files];
  files[0] = "D1.txt";
  files[1] = "D2.txt";
  files[2] = "D3.txt";

  struct hashmap* hm = training(buckets, files);

  // while loop is to make sure the user enters in a valid char
  while(1){
    printf("Enter 'S' for Search or 'X' for Exit.\n");
    // inputted char has to be 'S' or 'X'
    int searchChar = getchar();
    getchar();
    if(searchChar == (int)'S'){
      printf("Search\n");
      break;
    }
    else if(searchChar == (int)'X'){
      printf("Exit\n");
      exit(0); // exits the entire program if user chooses exit
    }
    else{
      printf("Please enter a valid character.\n");
    }
  }
  hm = stop(hm);  // hm now does not contain stop words
  read_query(hm, files);
  hm_destroy(hm); // de-allocates the entire hashmap
  return 0;
}

/*
** This method takes in the hashmap hm and the files array as parameters. The purpose of the method is to take in a
** string of query words and turn them into individeal query terms and thensort through the entire hashmap, checking
** if the query words exist in the various text documents and then use this information to score and rank the documents
*/
void read_query(struct hashmap* hm, char* files[]){
  char* userQueries[100];  // an array of all the user query words
  char query[256];   // will hold the query string, which is broken into individual query words

  // The end word will be so the last query word goes through correctly
  printf("Enter a single query line with all necessary query words. Type at least 3 words and end line with 'END').\n");
  fgets(query, sizeof(query), stdin);
  // int size = sizeof(userQueries);
  int size = 0;
  int x = 0;
  // breaks the long string into individual words
  userQueries[x] = strtok(query," ");
  while(userQueries[x]!=NULL)
  {
    userQueries[++x] = strtok(NULL," ");
    size ++;
  }

  int n = 3;  // number of documents being searched
  double idfW;
  double idf;
  int tf = 0; // number of times a word appears in a document
  int df = 0;
  double docScore[3] = {0.00000, 0.00000, 0.0000};  // have to initalize double values

  // runs through all of the files
  for(int i = 0; i < 3; i++){
    // runs through all of the query words
    for(int j = 0; j < size; j++){
      df = 0;
      tf = 0;
      // checks to see if a word exists in the hashmap
      if(hm_get(hm, userQueries[j], files[i]) != -1){
        tf = hm_get(hm, userQueries[j], files[i]);
        df++;
      }
      // breaks to the next loop to prevent repeat adding to the document score
      else{
        continue;
      }
      // df cannot be 0, so if it is, set it to 1
      if(df == 0){
        df = 1;
      }
      // calculate idfW and idf
      idfW = ((double)n/(double)df);
      idf = log10(idfW);
      docScore[i] += idf * (double)tf;
    }
  }

  printf("Document ranking: \n");

  int y = 3;
  int i = 0, j = 0;
  double tmp = 0.0000;
  char* tmp2;

  // sorting loops
  for (i = 0; i < y; ++i){
    for (j = i + 1; j < y; ++j){
      if(docScore[i] > docScore[j]){
        // switches around the scores to keep sorting
        tmp =  docScore[i];
        docScore[i] = docScore[j];
        docScore[j] = tmp;

        // sorts the order of the file names to keep in order compared to docScore
        tmp2 = files[i];
        files[i] = files[j];
        files[j] = tmp2;
      }
    }
  }

  int z = 0;
  // prints the document names in correct order
  for(int i = 2; i >= 0; i--, z++){
    printf("%d %s\n", z + 1, files[i]);

    // document contains none of the query words
    if(docScore[i] == (double)0){
      printf("Document %s contains no reference to your query\n", files[i]);
    }
  }

}
