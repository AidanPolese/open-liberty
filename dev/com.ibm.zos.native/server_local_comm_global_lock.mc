/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <metal.h>
#include <stdlib.h>

#include "include/server_local_comm_global_lock.h"

#include "include/ieantc.h"
#include "include/mvs_abend.h"
#include "include/mvs_enq.h"

#define ENABLE_LOCK 1

/** Temporary ENQ RNAME for local comm processing.
 *  TODO: Remove this and replace it with some other serialization.
 */
#define CLIENT_TEMP_LC_CONNECT_ENQ_RNAME "BBG:CENQX:LCCONNECT"

/**
 * Task level name token name.
 */
#define NAME_TOKEN_NAME "BBGZ_LC_GLOBAL_L"

/**
 * Name token token struct mapping.
 */
typedef struct nameTokenStruct {
    enqtoken* enqToken_p;
    void* available_p;
} NameTokenStruct_t;

/**
 * Obtain the local comm global lock.  A name token will be created on the
 * caller's task which will be required when the lock is freed.
 */
void obtainLocalCommGlobalLock(void) {
#ifdef ENABLE_LOCK
    // Get some storage to store the ENQ token in.
    int nametoken_rc = 0;
    NameTokenStruct_t token;
    token.enqToken_p = malloc(sizeof(enqtoken));
    token.available_p = NULL;

    if (token.enqToken_p != NULL) {
        get_enq_exclusive_system(BBGZ_ENQ_QNAME, CLIENT_TEMP_LC_CONNECT_ENQ_RNAME, NULL, token.enqToken_p);
        iean4cr(IEANT_TASK_LEVEL, NAME_TOKEN_NAME, (char*)(&token), IEANT_NOPERSIST, &nametoken_rc);
        if (nametoken_rc != 0) {
            release_enq(token.enqToken_p);
            free(token.enqToken_p);
            abend_with_data(ABEND_TYPE_SERVER, 0xAAAA0001, &nametoken_rc, NULL);
        }
    } else {
        // Temporary abend code - don't bother documenting it.
        abend(ABEND_TYPE_SERVER, 0xAAAA0000);
    }
#endif
}

/**
 * Release the local comm global lock.  The name token which was created on
 * the caller's task by obtainLocalCommGlobalLock() will be required to
 * release the lock.
 */
void releaseLocalCommGlobalLock(void) {
#ifdef ENABLE_LOCK
    int nametoken_rc = 0;
    NameTokenStruct_t token;

    iean4rt(IEANT_TASK_LEVEL, NAME_TOKEN_NAME, (char*)(&token), &nametoken_rc);
    if (nametoken_rc != 0) {
        abend_with_data(ABEND_TYPE_SERVER, 0xAAAA0002, &nametoken_rc, NULL);
    }

    if (token.enqToken_p == NULL) {
        abend_with_data(ABEND_TYPE_SERVER, 0xAAAA0003, &token, NULL);
    }

    iean4dl(IEANT_TASK_LEVEL, NAME_TOKEN_NAME, &nametoken_rc);
    if (nametoken_rc != 0) {
        abend_with_data(ABEND_TYPE_SERVER, 0xAAAA0004, &nametoken_rc, NULL);
    }

    release_enq(token.enqToken_p);
    free(token.enqToken_p);
#endif
}
