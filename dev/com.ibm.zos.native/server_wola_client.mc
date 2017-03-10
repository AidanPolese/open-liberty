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
#include <builtins.h>
#include <stdlib.h>
#include <string.h>

#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_plo.h"
#include "include/mvs_storage.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/server_local_comm_api.h"
#include "include/server_wola_client.h"
#include "include/server_wola_connection_pool.h"
#include "include/server_wola_message.h"
#include "include/server_wola_nametoken_utility.h"
#include "include/server_wola_registration.h"
#include "include/server_wola_shared_memory_anchor.h"
#include "include/mvs_estae.h"

#include "include/gen/cvt.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"
#include "include/gen/ihapsa.h"

/**
 * Get a null terminated service name.
 *
 * Parameters documented in header
 */
int getServiceName(unsigned int requestServiceNameLength,
                   char* requestServiceName_p,     // need to move with source key
                   unsigned char callerStorageKey,
                   char* outputRequestServiceName_p) {
    unsigned int returnCode = 0;

    if (requestServiceNameLength > BBOA_REQUEST_SERVICE_NAME_MAX) {
        return 4;
    }

    // if requestServiceNameLength is zero input requestServiceName_p should be null terminated.
    if (requestServiceNameLength == 0) {
        memset(outputRequestServiceName_p, 0, 1);
        strncpy_sk(outputRequestServiceName_p, requestServiceName_p, BBOA_REQUEST_SERVICE_NAME_MAX, callerStorageKey);
        // need to null terminate if copied all 256 bytes.
        memset(outputRequestServiceName_p+BBOA_REQUEST_SERVICE_NAME_MAX, 0, 1);
        // if passed a null string get out
        if (strlen(outputRequestServiceName_p) == 0) {
            return 8;
        }
    } else {
        // copy and null terminate
        memcpy_sk(outputRequestServiceName_p, requestServiceName_p, requestServiceNameLength, callerStorageKey);
        memset(outputRequestServiceName_p+requestServiceNameLength, 0, 1);
    }
    return returnCode;
}

/**
 * Checks to see it the client connection handle references a valid connection handle.
 *
 * @param wolaClientConnectionHandle_p client connection handle to validate
 * @return  Returns 1 if the client connection handle is valid, 0 if not.
 */
int connectionHandleValid(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p) {
    int returnCode = 0;
    if ((wolaClientConnectionHandle_p != 0) && (wolaClientConnectionHandle_p->handle_p != 0)) {
        // TODO verify handle_p is in our cell pool
        if (wolaClientConnectionHandle_p->instanceCount == wolaClientConnectionHandle_p->handle_p->ploArea.instanceCount) {
            returnCode = 1;
        }
    }
    return returnCode;
}

/**
 * Use PLO to get local comm connection handle (quad word).
 */
int getLocalCommConnectionHandleFromWolaConnectionHandle(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, OpaqueClientConnectionHandle_t* localCommClientConnectionHandle_p) {
    PloCompareAndSwapAreaDoubleWord_t compareArea;
    PloLoadAreaDoubleWord_t loadArea;

    compareArea.compare_p = &(wolaClientConnectionHandle_p->handle_p->ploArea);
    compareArea.expectedValue = wolaClientConnectionHandle_p->instanceCount;

    loadArea.loadLocation_p = &(wolaClientConnectionHandle_p->handle_p->localCommConnectionHandle[0]);

    int rc = ploCompareAndLoadDoubleWord(&(wolaClientConnectionHandle_p->handle_p->ploArea), &compareArea, &loadArea);

    if (rc == 0) {
        memcpy(localCommClientConnectionHandle_p, &(loadArea.loadValue), sizeof(loadArea.loadValue));
        loadArea.loadLocation_p = &(wolaClientConnectionHandle_p->handle_p->localCommConnectionHandle[8]);
        rc = ploCompareAndLoadDoubleWord(&(wolaClientConnectionHandle_p->handle_p->ploArea), &compareArea, &loadArea);
        if (rc == 0) {
            memcpy(((char*)localCommClientConnectionHandle_p) + 8, &(loadArea.loadValue), sizeof(loadArea.loadValue));
        }
    }

    return rc;
}

/**
 * Use PLO to get connection state.
 *
 * @param clientConnectionHandle_p - The client connection handle.
 * @param connHdlState_p - Pointer to output area to get the connection state.
 *
 * @return zero when connection state was obtained; non-zero otherwise.
 */
int getHandleState(WolaClientConnectionHandle_t* clientConnectionHandle_p, unsigned long long* connHdlState_p) {

    PloCompareAndSwapAreaDoubleWord_t compareArea;
    PloLoadAreaDoubleWord_t loadArea;

    compareArea.compare_p = &(clientConnectionHandle_p->handle_p->ploArea);
    compareArea.expectedValue = clientConnectionHandle_p->instanceCount;

    loadArea.loadLocation_p = &(clientConnectionHandle_p->handle_p->state);

    int rc = ploCompareAndLoadDoubleWord(&(clientConnectionHandle_p->handle_p->ploArea), &compareArea, &loadArea);

    if (rc == 0) {
        *connHdlState_p = loadArea.loadValue;

    }
    return rc;
}

int changeHandleState(WolaClientConnectionHandle_t* clientConnectionHandle_p,
                      unsigned long long oldState,
                      unsigned long long newState) {
    int rc = 0;
    PloCompareAndSwapAreaDoubleWord_t swap1;
    PloCompareAndSwapAreaDoubleWord_t swap2;

    swap1.compare_p = &(clientConnectionHandle_p->handle_p->ploArea);
    swap1.expectedValue = clientConnectionHandle_p->instanceCount;
    swap1.replaceValue = swap1.expectedValue;

    swap2.compare_p = &(clientConnectionHandle_p->handle_p->state);
    swap2.expectedValue = oldState;
    swap2.replaceValue = newState;

    rc = ploDoubleCompareAndSwapDoubleWord(&(clientConnectionHandle_p->handle_p->ploArea), &swap1, &swap2);

    return rc;
}

/**
 * Get the registration for the specified register name.
 *
 * @param registerName_p      pointer to register name
 * @param registrationData_p  pointer to output area that gets updated with registration data when registration is found.
 *
 * @return Pointer to registration. 0 if not found.
 */
WolaRegistration_t* getWolaRegistration(char* registerName_p, GetWolaRegistrationData_t* registrationData_p) {

    WolaRegistration_t* regEntry_p = 0;
    struct register_name_token_map token;
    int rc =  getRegisterNameToken(registerName_p, (char*)&token);
    if(rc == 0) {
        regEntry_p = (WolaRegistration_t*)token.registration_p; //last 8 bytes is the WolaRegistration_t*
        memcpy(&(registrationData_p->registrationflags), &(regEntry_p->flags), sizeof(registrationData_p->registrationflags));
        memcpy(&(registrationData_p->stckLastStateChange), &(regEntry_p->stckLastStateChange), sizeof(registrationData_p->stckLastStateChange));
    }
    return regEntry_p;
}

// TODO verify and move somewhere else
// Local Comm send limit (~2Gig (2**31 - 1))
#define LOCAL_COMM_SEND_MESSAGE_MAXIMUM 0x7FFFFFFF


/**
 * Checks to see if the caller supplied buffer address is
 * good, and that it can be read from or written to.
 *
 * @param buffer_p  Address of buffer to check.
 * @param bufferLen Length of buffer.
 * @param readFlag  Read flag set to one if readonly.
 * @param requestBuffer Request buffer set to one if request buffer, zero if response buffer.
 *
 * @return 0 if the buffer is good, 1 if the buffer start address
 *         was bad, 2 if the buffer end address was bad, 3 if the
 *         buffer is larger than the local comm limit, 4 is we
 *         fail to establish an estae. This call may abend if buffer
 *         is bad, and in that case the ARR will set RC/RSN and exit.
 */
unsigned int checkBuffer(char* buffer_p, unsigned long long bufferLen,
                         unsigned char callerKey,
                         unsigned int readFlag, unsigned int requestBuffer) {
    char* checkAddr;
    volatile unsigned int rc = 0;

    psa* psa_p = 0;
    cvt* cvt_p = (cvt*) psa_p->flccvt;
    unsigned char subpoolOverideSupported = cvt_p->cvtflag1 & cvtover;

    if (bufferLen > 0) {
        rc = 3;
        if (bufferLen <= LOCAL_COMM_SEND_MESSAGE_MAXIMUM) {
            rc = 1;
            if (buffer_p != 0) {

                //---------------------------------------------
                // Establish some recovery in case of an abend.
                //---------------------------------------------
                int estaex_rc = -1;
                int estaex_rsn = -1;
                volatile int already_tried_first_check = 0;
                volatile int already_tried_second_check = 0;
                struct retry_parms retryParms;
                memset(&retryParms, 0, sizeof(retryParms));
                establish_estaex_with_retry(&retryParms,
                                            &estaex_rc,
                                            &estaex_rsn);

                if (estaex_rc == 0) {
                    SET_RETRY_POINT(retryParms);
                    if (already_tried_first_check == 0) {
                        already_tried_first_check = 1;

                        checkAddr = buffer_p;
                        bbgz_ivsk ivskOutput;
                        getStorageKey(checkAddr, &ivskOutput);
                        unsigned char storageKey = ivskOutput.key;

                        if ((storageKey == callerKey) ||
                            ((storageKey == 9) &&
                                (subpoolOverideSupported == cvtover)) ||
                                ((readFlag == 1) && (ivskOutput.fetchProtection == 0))) {
                            rc = 2;
                            //Reset the retry entry point to incorporate new rc
                            SET_RETRY_POINT(retryParms);

                            if(already_tried_second_check == 0){
                                already_tried_second_check = 1;
                                checkAddr = buffer_p + bufferLen - 1;
                                getStorageKey(checkAddr, &ivskOutput);
                                storageKey = ivskOutput.key;

                                if ((storageKey == callerKey) ||
                                    ((storageKey == 9) &&
                                        (subpoolOverideSupported == cvtover)) ||
                                        ((readFlag == 1) && (ivskOutput.fetchProtection == 0))) {
                                    rc = 0;
                                }
                            }
                        }

                    }
                    // -----------------------------------------------------------------------
                    // Remove the ESTAE.
                    // -----------------------------------------------------------------------
                    remove_estaex(&estaex_rc, &estaex_rsn);
                } else {
                    // Couldn't establish an ESTAE
                    rc = 4;
                }
            }
        }
    }
    return rc;
}

/**
 * Use PLO to set the WOLA message and context pointers in the wola client connection handle.
 *
 * @param clientConnectionHandle_p - The client connection handle.
 * @param message_p - Pointer to WOLA message.
 * @param contexts_p - Pointer to WOLA contexts.
 *
 * @return zero when message and context pointers are updated; non-zero otherwise.
 */
unsigned int setMessageAndContextAreas(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, void* message_p, void* contexts_p) {
    unsigned int rc = 0;
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloStoreAreaDoubleWord_t storeArea1;
    PloStoreAreaDoubleWord_t storeArea2;

    swapArea.compare_p = &(wolaClientConnectionHandle_p->handle_p->ploArea);
    swapArea.expectedValue = wolaClientConnectionHandle_p->instanceCount;
    swapArea.replaceValue = swapArea.expectedValue;

    storeArea1.storeLocation_p = &(wolaClientConnectionHandle_p->handle_p->cachedMessage_p);
    storeArea1.storeValue = (unsigned long long) message_p;
    storeArea2.storeLocation_p = &(wolaClientConnectionHandle_p->handle_p->cachedContexts_p);
    storeArea2.storeValue = (unsigned long long) contexts_p;

    rc = ploCompareAndSwapAndDoubleStoreDoubleWord(&(wolaClientConnectionHandle_p->handle_p->ploArea), &swapArea, &storeArea1, &storeArea2);

    return rc;
}

/**
 * Use PLO to validate the input connection handle and return the message and context area pointers.
 *
 * @param wolaClientConnectionHandle_p Client connection handle.
 * @param message_p   Output pointer to get the message area pointer.
 * @param contexts_p  Output pointer to get the context area pointer.
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int getMessageAndContextAreas(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, void** message_p, void** contexts_p) {
    unsigned int rc = 0;
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloStoreAreaDoubleWord_t storeArea1;
    PloStoreAreaDoubleWord_t storeArea2;

    swapArea.compare_p = &(wolaClientConnectionHandle_p->handle_p->ploArea);
    swapArea.expectedValue = wolaClientConnectionHandle_p->instanceCount;
    swapArea.replaceValue = swapArea.expectedValue;

    storeArea1.storeLocation_p = &(wolaClientConnectionHandle_p->handle_p->cachedMessage_p);
    storeArea1.storeValue = 0;
    storeArea2.storeLocation_p = &(wolaClientConnectionHandle_p->handle_p->cachedContexts_p);
    storeArea2.storeValue = 0;

    void* msg_p = wolaClientConnectionHandle_p->handle_p->cachedMessage_p;
    void* ctx_p = wolaClientConnectionHandle_p->handle_p->cachedContexts_p;

    rc = ploCompareAndSwapAndDoubleStoreDoubleWord(&(wolaClientConnectionHandle_p->handle_p->ploArea), &swapArea, &storeArea1, &storeArea2);

    if (rc == 0) {
        *message_p = msg_p;
        *contexts_p = ctx_p;
    }

    return rc;
}
