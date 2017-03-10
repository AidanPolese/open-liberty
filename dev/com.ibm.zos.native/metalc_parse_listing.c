#define  _XOPEN_SOURCE_EXTENDED 1
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define TRUE 1
#define FALSE 0

/* This is a line of the module map in the listing. */
struct labelLine{
  char _reserved1;
  char sectionOffset[8];
  char _reserved2;
  char classOffset[8];
  char _reserved3[5];
  char methodShortName[16];
  char _reserved4[3];
  char labelConst[5];
};

#define LABEL_CONST "LABEL"

/* This is a line of the abbreviation table in the listing. */
struct abbrevLine{
  char _reserved[7];
  char methodShortName[16];
  char divider[4];
  char methodLongName[16]; /* Actual length ??? */
};

/* This is a linked list representing the data going into the SCANC file. */
struct methodInfo{
  char* methodName;
  int offset;
  struct methodInfo* next_p;
};

void printCurrent(struct methodInfo* cur_p){
  if (cur_p != NULL){
    printCurrent(cur_p->next_p);
    printf("F %8.8x %s\n", cur_p->offset, cur_p->methodName);
  }
}

/* Apparently we don't have an implementation of this in string.h */
char* strndup(char* s, size_t n){
  char* newString = NULL;
  if (memchr(s, 0, n) == NULL){
    newString = malloc(n + 1);
    if (newString != NULL){
      memcpy(newString, s, n);
      *(newString + n) = 0;
    }
  } else{
    newString = strdup(s);
  }
  return newString;
}

int main(int argc, char** argv) {

  /* Make sure the caller told us what the module entry name is. */
  if (argc != 2){
    printf("Usage: %s MODULE\n", argv[0]);
    exit(1);
  }

  char* moduleName = argv[1];
  
  char buffer[256];
  struct methodInfo* head_p = NULL;

  /* Lets read the whole input, one line at a time */
  while(fgets(buffer, sizeof(buffer), stdin) != NULL){

    /* Look for the "module map" header in the listing. */
    if (strstr(buffer, "*** M O D U L E  M A P ***") != NULL){

      /* Read lines from the module map to the end */
      unsigned char endModuleMap = FALSE;
      while ((endModuleMap == FALSE) &&
             (fgets(buffer, sizeof(buffer), stdin) != NULL)){

        if (strstr(buffer, "*** E N D  O F  M O D U L E  M A P ***") != NULL){
          endModuleMap = TRUE;
        } else {
          
          /* We are looking for LABEL lines.  They will have a LABEL */
          /* in columns 42-46.                                       */
          if (strlen(buffer) >= sizeof(struct labelLine)){
            struct labelLine* curLine_p = (struct labelLine*)buffer;
            if (memcmp(LABEL_CONST, curLine_p->labelConst,
                       sizeof(curLine_p->labelConst)) == 0){
              
              /* We've got one, read out the class offset and name. */
              struct methodInfo* cur_p = malloc(sizeof(*cur_p));
              if (cur_p != NULL){
                cur_p->methodName = strndup(curLine_p->methodShortName, 
                                           sizeof(curLine_p->methodShortName));
                cur_p->offset = strtol(curLine_p->classOffset, NULL, 16);
                cur_p->next_p = head_p;
                head_p = cur_p;
              } else{
                printf("Error: out of memory.\n");
                exit(1);
              }
            }
          }
        }
      }
    }
    
    /* Look for the long symbol abbreviation table. */
    if (strstr(buffer, "*** L O N G  S Y M B O L  A B B R E V I A T I O N  "
               "T A B L E ***") != NULL){
      unsigned char endAbbrevTable = FALSE;
      while ((endAbbrevTable == FALSE) &&
             (fgets(buffer, sizeof(buffer), stdin) != NULL)){

        if (strstr(buffer, "*** E N D  O F  L O N G  S Y M B O L  A B B R "
                   "E V .  T A B L E ***") != NULL){
          endAbbrevTable = TRUE;
        } else {
          /* We are looking for abbrev lines.  They will have " := " */
          /* in columns 23-26.                                       */
          if (strlen(buffer) >= sizeof(struct abbrevLine)){
            struct abbrevLine* curLine_p = (struct abbrevLine*)buffer;
            if (memcmp(" := ", curLine_p->divider,
                       sizeof(curLine_p->divider)) == 0){
              
              /* We've got a name mapping.  Get the long name. */
              char* longName = strdup(curLine_p->methodLongName);
              char* whiteSpace = strstr(longName, " ");
              if (whiteSpace != NULL){
                *whiteSpace = 0; /* Shorten the string. */
              }

              /* See if anything in our map matches this long name. */
              struct methodInfo* cur_p = head_p;
              while (cur_p != NULL){
                if ((memcmp(curLine_p->methodShortName, cur_p->methodName,
                            sizeof(curLine_p->methodShortName)) == 0) &&
                    (strlen(cur_p->methodName) == 
                     sizeof(curLine_p->methodShortName))){
                  
                  /* We have a match. */
                  char* temp_p = cur_p->methodName;
                  cur_p->methodName = strdup(longName);
                  free(temp_p);
                }
                cur_p = cur_p->next_p;
              }

              free(longName);
            }
          }
        }
      }
    }    
  }

  /* Print our results. */
  printf("M %s\n", moduleName);
  printCurrent(head_p);
}





