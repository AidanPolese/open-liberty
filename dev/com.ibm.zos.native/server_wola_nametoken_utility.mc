/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/ieantc.h"
#include "include/server_wola_nametoken_utility.h"
#include "include/name_tokens.h"

int lookupPrimaryNameToken(char* name_p, char* token_p, int auth) {
    int rc = -1;
    int level = ((auth == 0) ? IEANT_PRIMARY_LEVEL : IEANT_PRIMARYAUTH_LEVEL);

    struct parms {
        int* level;
        char* token_name;
        char* token;
        int* rc;
    };

    struct parms* p31 = __malloc31(sizeof(struct parms));
    if (p31 != NULL) {
        p31->level = &level;
        p31->token_name = name_p;
        p31->token = token_p;
        p31->rc = &rc;

        void* fnPtr = (void*)iean4rt;

        __asm(" LG 1,%0 Load parameter list \n"
              " LG 15,%1 Load IEAN4RT function address \n"
              " BASR 14,15" : :
              "m"(p31),"m"(fnPtr) : "r0","r1","r14","r15");

        free(p31);
    }

    return rc;
}

/**
 * Get register token name for input register name.
 *
 * @param token_name_p   - Output - will contain the token name (must be at least 16 bytes long)
 * @param registerName_p - Input - Register name.
 *
 */
void getRegisterTokenName(char * token_name_p, char * registerName_p) {
    memcpy(token_name_p, BBOZ_REGISTRATION_NAME_TOKEN_USER_NAME_PREIFX, 4);
    memcpy(token_name_p+4, registerName_p, 12);
}

/**
 * Get the registration for the specified register name.
 *
 * @param registerName_p      pointer to register name
 * @param registrationData_p  pointer to output area that gets updated with registration data when registration is found.
 *
 * @return Pointer to registration. 0 if not found.
 */
int  getRegisterNameToken(char* registerName_p, char* token) {
    char token_name[16];

    getRegisterTokenName(token_name, registerName_p);
    return lookupPrimaryNameToken(token_name, token, 1);
}

