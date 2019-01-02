#include "hashmap.h"
#include "test.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

// Allocates and mallocs memory for a new hashmap with a specified number of buckets
struct hashmap* hm_create(int num_buckets){
	struct hashmap* hm = (struct hashmap*) malloc(sizeof(struct hashmap));
	hm->num_buckets = num_buckets;
	hm->num_elements = 0; // starts with 0 elements
	hm->map = (struct llnode**) malloc(sizeof(struct llnode*) * num_buckets);
  // sets all the buckets to null initially
  for (int i = 0; i < num_buckets; i++) {
		hm->map[i] = NULL;
	}
	return hm;
}

/*
* Takes in the hashmap, word and document_id as parameters and then searches through the entire hashmap
* to see if the node (specified by the word and document_id) exist already, if the node exists then
* return the num_occurances, if it doesn't exist then return -1
*/
int hm_get(struct hashmap* hm, char* word, char* document_id){
	int bucket = hash(hm, word, document_id);
	struct llnode* head = hm->map[bucket]; // the head is the 0th node of the specified bucket

  // head case
	if (head == NULL){
		return -1;
	}
	else {
    // current will iterate through the bucket
		struct llnode* curr = head;
		while (curr != NULL) {
      // word AND document_id have to be equal
			if (strcmp(curr->word, word) == 0 && strcmp(curr->document_id, document_id) == 0) {
				return curr->num_occurrences;
			}
      // get the next node
			curr = curr->next;
		}
		return -1;
	}
}

/*
* Takes in hm, word, document_id and num_occurances as parameters and finds if a identical node already exists.
* If the node does exist then the number of occurances is incremented, if it doesn't exist then the node with
* the given parameters is inserted and the number of occurances is set to 1
*/
void hm_put(struct hashmap* hm, char* word, char* document_id, int num_occurrences){
   // copy will be used instead of directly inserting word
   char *copy = malloc(sizeof(char*));
   strcpy(copy, word);
   int bucket = hash(hm, copy, document_id);

   // current starts at the head of the specified bucket
   struct llnode *curr = hm->map[bucket];
   // temp will be used to iterate through the bucket
   struct llnode *temp;

   // If the bucket is empty
   if(curr == NULL){
      // check to see if there is memory for current to be inserted
      if((curr = (struct llnode*)(malloc(sizeof(struct llnode)))) == NULL){
         return;
      }
      curr->document_id = document_id;
      curr->word = copy;
      curr->num_occurrences = num_occurrences;
      // set the head of the specified bucket to current
      hm->map[bucket] = curr;
      // increment num_elements
      hm->num_elements++;
      return;
   }
   // infinite loop that should be stopped by second if statement
   while(1){
      // check for duplicate nodes
      if(strcmp(curr->word, copy) == 0 && strcmp(curr->document_id, document_id) == 0){
         // num_occurances is incremented and NOT num_elements
         curr->num_occurrences++;
         return;
      }
      // will break the infinite loop when the last element of the bucket is reached
      if(curr->next == NULL){
         temp = (struct llnode*)(malloc(sizeof(struct llnode)));
         temp->word = copy;
         temp->document_id = document_id;
         temp->num_occurrences = num_occurrences;

         // set last element to temp
         curr->next = temp;
         hm->num_elements++;
         return;
      }
      // keep incrementing
      curr = curr->next;
   }
}


/*
* Takes in the hashmap, word and document_id as parameters and iterates through the hashmap,
* looking for the given word and document_id pair. If the pair exists, it removes and frees the node,
* linking the previous and next node to keep the chain unbroken
*/
void hm_remove(struct hashmap* hm, char* word, char* document_id){
	int bucket = hash(hm, word, document_id);
	struct llnode* head = hm->map[bucket];
  // if the bucket it empty then the node CANNOT exist yet
	if (head == NULL) { // empty bucket case
		printf("Key and value pair not initialized\n");
		return;
	}

	// these pointers will be used to iterate through the given bucket
	struct llnode* current = head;
	struct llnode* second = head->next;

  // one element case
	if (second == NULL && ((strcmp(current->word, word) == 0) || (strcmp(current->document_id, document_id) == 0))) {
	  hm->map[bucket] = NULL;
	  hm->num_elements--;
    free(head);
    return;
	}

  // head case
	else if (strcmp(current->word, word) == 0 && strcmp(current->document_id, document_id) == 0) {
	  current->next = NULL;
	  free(current);
	  hm->map[bucket] = second;
		hm->num_elements--;
	  return;
	}

	while (second != NULL) {
	    // If the key and value pair are found, remove and free the node
	    if (strcmp(second->word, word) == 0 && strcmp(second->document_id, document_id) == 0) {
	      current->next = second->next;
	      second->next = NULL;
	      free(second);
		    hm->num_elements--;
	      return;
	    }
	    // increment
	    current = second;
	    second = current->next;
	}
	// Will only occur if the specified key and value pair don't exist
	return;
}

/*
* Takes in the hashmap as a parameter and deallocates all of its elements and then the actual hashmap
*/
void hm_destroy(struct hashmap* hm){
	int i;
	// Frees all of the nodes
	for (i = 0; i < hm->num_buckets; i++) {
		struct llnode* current = hm->map[i];
		struct llnode* temp;
		while (current != NULL) {
			temp = current;
			current = current->next;
			free(temp);
		}
	}
	// Free the map
	free(hm->map);
	// Free the hashmap (everything will be deallocated after this)
	free(hm);
}

/*
* Takes the given word and document ID as parameters and calculates which bucket it should be put into.
* This is done by adding the ASCII codes of all of the characters then taking (sum % number of buckets)
* to choose a value cooresponding to a bucket
*/
int hash(struct hashmap* hm, char* word, char* document_id){
  int sum = 0;
  document_id++;
  document_id--;
  // Goes through all of word's letters
	for(int i = 0; i < (int)strlen(word); i++){
    // Add the ascii value of the current char to sum
    sum += (int)word[i];
   }

  /*
  // Now goes through the document ID
  for(int i = 0; i < (int)strlen(document_id); i++){
    sum += (int)document_id[i];
   }
  */
  // Will determine which bucket the word will go into
	int bucket = sum % hm->num_buckets;
	return bucket;
}

/*
* This is our file input method. The method takes in the number of buckets specified by the user,
* calls hm_create to make our hashmap and then scans through all of the documents and takes in all
* the words in the documents and populates the hashmap
*/
struct hashmap* training(int num_buckets, char* files[]){
  // Initialize the hashmap with a specified number of buckets
	struct hashmap* hm = hm_create(num_buckets);
	// creates memory for a read file
	FILE *readFile;
	char* str = malloc(sizeof (char*));
	int i = 0, check;	// check will hold num_occurances

	// loops through all of the read files to get all string elements
	for (i = 0; i < 3; i++) {
		readFile = fopen(files[i], "r");
    // If the file doesn't exist then the program exits and prints an error message
		if (readFile == NULL) {
      printf("ERROR: File: %s does not exist\n", files[i]);
			break;
		}

    	// loops while fscanf still picks up strings
    	while(fscanf(readFile, "%s", str) != -1){
      	// checks whether a word + key pairing exists
      	check = hm_get(hm, str, files[i]);
      	if (check == -1) {
				hm_put(hm, str, files[i], 1);
			}
			// If str already in hashmap, increment num_occurences and update node
			else {
				hm_put(hm, str, files[i], check + 1);
			}
   	}
		fclose(readFile);
	}
  return hm;
}

/*
** This method takes in the hashmap as a parameter. The method uses the hashmap to determine stop
** words (words that appear in all the documents and have an idf score of 0). These words are then
** removed from the hashmap and hm is returned.
*/
struct hashmap* stop(struct hashmap* hm){
  char *stopWords[100];
  int stopCount = 0;
  int n = 3;  // number of documents being searched
  int df = 0; // number of documents that contain w
  double idfW;
  double idf;

  // This loop will go through every element in hm
  for (int i = 0; i < hm->num_buckets; i++) {
		struct llnode* current = hm->map[i];
		struct llnode* temp;
		while (current != NULL) {
      	int bool = 0;
      	df = 0;
			temp = current;
			current = current->next;

      	// Checks to see if each word occurs in all three text documents
      	if(hm_get(hm, temp->word, "D1.txt") != -1){
        		df++;
      	}
      	if(hm_get(hm, temp->word, "D2.txt") != -1){
        		df++;
      	}
      	if(hm_get(hm, temp->word, "D3.txt") != -1){
        		df++;
      	}
      	// checks in the stop word already exists
      	for(int j = 0; j < stopCount; j++){
        		if(strcmp(temp->word, stopWords[j]) == 0){
          		bool = 1;
        		}
      	}
      	// if the stop word exists already, don't add it again
      	if(bool == 1){
        		continue;
      	}

      	idfW = ((double)n/(double)df);
      	idf = log10(idfW);

      	// If a word has an idf of 0 then it's added to stopwords, all these words will be removed after the while loop
      	if(idf == 0){
        		stopWords[stopCount] = temp->word;
        		stopCount++;
      	}
    	}
  }

  // This loop removes the stop word from each document
  for(int i = 0; i < stopCount; i++){
    hm_remove(hm, stopWords[i], "D1.txt");
    hm_remove(hm, stopWords[i], "D2.txt");
    hm_remove(hm, stopWords[i], "D3.txt");
  }

  return hm;
}
