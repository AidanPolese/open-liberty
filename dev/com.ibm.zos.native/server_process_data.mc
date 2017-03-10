/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/server_process_data.h"

#include "include/angel_process_data.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/heap_management.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/petvet.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ikjtcb.h"
#include "include/gen/ihastcb.h"

#define SERVER_PROCESS_DATA_EYE_CATCHER "BBGZSPD_"

/**
 * Copies the common function module header into local storage.  A pointer to
 * the local storage will be returned to the caller.
 *
 * @param apd_p A pointer to the angel process data representing the process
 *              that we are binding to.
 * @param recovery_p A pointer to the ARR recovery area.
 *
 * @return A pointer to a copy of the common function module header, for the
 *         server that we are binding to.  This storage is released by calling
 *         free().
 */
static bbgzasvt_header* copyCommonFunctionModule(angel_process_data* apd_p) {
    bbgzasvt_header* header_p = NULL;

    if (apd_p->scfm_function_table_p != NULL) {
        int scfm_size = sizeof(*header_p) +
            ((apd_p->scfm_function_table_p->num_entries) * sizeof(bbgzasve)) +
            strlen(BBGZASVT_EYE_END);

        header_p = malloc(scfm_size);
        if (header_p != NULL) {
            memcpy(header_p, (apd_p->scfm_function_table_p), scfm_size);
        }
    }

    return header_p;
}

server_process_data* createServerProcessData(struct __csysenv_s* auth_csysenv_p, void* auth_cenv_p) {
    server_process_data* spd_p = NULL;

    // -----------------------------------------------------------------------
    // The server process data is an authorized control block.  Don't try to
    // create one unless we're authorized.
    // -----------------------------------------------------------------------
    bbgz_psw psw;
    extractPSW(&psw);
    if ((psw.pbm_state == 0) || (psw.key < 8)) {
        spd_p = getServerProcessData();
        if (spd_p == NULL) {
            int storage_obtain_rc;
            spd_p = storageObtain(sizeof(server_process_data),
                                  SERVER_PROCESS_DATA_SUBPOOL,
                                  SERVER_PROCESS_DATA_KEY,
                                  &storage_obtain_rc);
            if (spd_p != NULL) {
                memset(spd_p, 0, sizeof(server_process_data));
                memcpy(spd_p->eyecatcher, SERVER_PROCESS_DATA_EYE_CATCHER,
                       strlen(SERVER_PROCESS_DATA_EYE_CATCHER));
                spd_p->version = 1;
                spd_p->length = sizeof(server_process_data);
                spd_p->auth_metalc_env_parms_p = auth_csysenv_p;
                spd_p->auth_metalc_env_p = auth_cenv_p;

                // initialize the PetVet,
                PetVet* petvetBoundedStack = (PetVet*)&(spd_p->petvet[0]);
                initializePetVet(petvetBoundedStack, 100, NULL);

                // -----------------------------------------------------------
                // Make a copy of the common (client) function module header
                // that the angel loaded, to be used by OSGi to create services
                // representing the authorized functions.
                // -----------------------------------------------------------
                angel_process_data* apd_p = getAngelProcessDataFromNameToken();
                spd_p->localScfmHeader_p = copyCommonFunctionModule(apd_p);

                // -----------------------------------------------------------
                // Make a name token to keep the address of the process data
                // -----------------------------------------------------------
                char spd_name[16];
                char spd_token[16];
                int home_nametoken_rc = 0;

                memset(spd_name, 0, sizeof(spd_name));
                memcpy(spd_name, SERVER_PROCESS_DATA_TOKEN_NAME,
                       strlen(SERVER_PROCESS_DATA_TOKEN_NAME));

                memset(spd_token, 0, sizeof(spd_token));
                memcpy(spd_token, &spd_p, sizeof(spd_p));

                iean4cr(IEANT_HOME_LEVEL,
                        spd_name,
                        spd_token,
                        IEANT_NOPERSIST,
                        &home_nametoken_rc);

                if (home_nametoken_rc == IEANT_DUP_NAME) {
                    storageRelease(spd_p, sizeof(server_process_data),
                                   SERVER_PROCESS_DATA_SUBPOOL,
                                   SERVER_PROCESS_DATA_KEY);
                    spd_p = getServerProcessData();
                } else if (home_nametoken_rc != IEANT_OK) {
                    storageRelease(spd_p, sizeof(server_process_data),
                                   SERVER_PROCESS_DATA_SUBPOOL,
                                   SERVER_PROCESS_DATA_KEY);
                }
            }
        }
    }

    return spd_p;
}

server_process_data* getServerProcessDataFromNameToken(void) {
    server_process_data* spd_p = NULL;

    char spd_name[16];
    char spd_token[16];

    memset(spd_name, 0, sizeof(spd_name));
    memcpy(spd_name, SERVER_PROCESS_DATA_TOKEN_NAME,
           strlen(SERVER_PROCESS_DATA_TOKEN_NAME));
    int spd_name_token_rc;

    // ---------------------------------------------------------------
    // When checking the name token, if we are running authorized,
    // we want to make sure that the name token was created by an
    // authorized process.
    // ---------------------------------------------------------------
    bbgz_psw psw;
    extractPSW(&psw);
    int level = ((psw.key < 8) || (psw.pbm_state == 0))
                ? IEANT_HOMEAUTH_LEVEL
                    : IEANT_HOME_LEVEL;

    iean4rt(level,
            spd_name,
            spd_token,
            &spd_name_token_rc);

    // ---------------------------------------------------------------
    // If the name token exists, we can get the server process data
    // from it.  Otherwise, no server process data.
    // ---------------------------------------------------------------
    if (spd_name_token_rc == 0) {
        memcpy(&spd_p, spd_token, sizeof(spd_p));
    }

    return spd_p;
}

/**
 * Return the current server_process_data address
 *
 * @param outPGOO_PtrPtr pointer to the pointer variable to update with the PGOO address
 */
int getServerProcessDataUnauth(const server_process_data** outPGOO_PtrPtr){
    if (outPGOO_PtrPtr == NULL) {
        return -1;
    }

    server_process_data *localPGOO_Ptr;

    localPGOO_Ptr = getServerProcessData();

    memcpy_dk(outPGOO_PtrPtr,
              &localPGOO_Ptr,
              sizeof(localPGOO_Ptr),
              8);

    return 0;
}

void destroyServerProcessData(server_process_data* spd_p) {
    bbgz_psw psw;
    extractPSW(&psw);
    if ((psw.pbm_state == 0) || (psw.key < 8)) {
        if (getServerProcessData() == spd_p) {
            // ---------------------------------------------------------------
            // Try to delete the name token.
            // ---------------------------------------------------------------
            char spd_name[16];
            int ieant_rc = 0;

            memset(spd_name, 0, sizeof(spd_name));
            memcpy(spd_name, SERVER_PROCESS_DATA_TOKEN_NAME,
                   strlen(SERVER_PROCESS_DATA_TOKEN_NAME));

            iean4dl(IEANT_HOME_LEVEL, spd_name, &ieant_rc);

            // No need to free malloc'd storage.  The heap is going away.

            // ---------------------------------------------------------------
            // Destroy the PetVet
            // ---------------------------------------------------------------
            PetVet* petvetBoundedStack = (PetVet*)&(spd_p->petvet[0]);
            destroyPetVetPool(petvetBoundedStack);

            // ---------------------------------------------------------------
            // Free the server process data storage.
            // ---------------------------------------------------------------
            storageRelease(spd_p, spd_p->length,
                           SERVER_PROCESS_DATA_SUBPOOL,
                           SERVER_PROCESS_DATA_KEY);
        }
    }
}
