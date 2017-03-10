/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/angel_client_pc.h"

#include "include/angel_client_pc_stub.h"
#include "include/angel_process_data.h"
#include "include/angel_sgoo_services.h"
#include "include/bbgzasvt.h"
#include "include/client_dynamic_area_cell_pool.h"
#include "include/common_defines.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_enq.h"
#include "include/mvs_estae.h"
#include "include/mvs_utils.h"

#include "include/gen/isgyquaa.h"

/**
 * Safely verify that we can read the caller's parameter.  The read will occur
 * using the caller's key.
 *
 * @param source_p A pointer to the source address.
 * @param length The length to read from the source address.
 * @param target_p A pointer to the target address (in local storage).
 *
 * @return 0 if the parameter could be read.
 */
static int safelyCopyFromCallerStorage(void* source_p, int length, void* target_p) {
    int estaexRC = -1, estaexRSN = -1;
    volatile unsigned char triedIt = 0;
    volatile unsigned char retried = 0;
    retry_parms retryArea;
    memset(&retryArea, 0, sizeof(retryArea));
    establish_estaex_with_retry(&retryArea,
                                &estaexRC,
                                &estaexRSN);

    if (estaexRC == 0) {
        SET_RETRY_POINT(retryArea);
        if (triedIt == 0) {
            bbgz_psw psw;
            triedIt = 1;
            extractPSWFromLinkageStack(&psw);
            memcpy_sk(target_p, source_p, length, psw.key);
        } else {
            retried = 1;
        }

        remove_estaex(&estaexRC, &estaexRSN);
    } else {
        retried = 2;
    }

    return retried;
}

/**
 * Determine if the caller's parameter can be written to, by attempting to
 * clear the storage to zeros.
 *
 * @param target_p A pointer to the target address.
 * @param length The length to clear, in bytes.
 *
 * @return 0 if the parameter could be cleared.
 */
static int safelyClearCallerStorage(void* target_p, int length) {
    int estaexRC = -1, estaexRSN = -1;
    volatile unsigned char triedIt = 0;
    volatile unsigned char retried = 0;
    retry_parms retryArea;
    memset(&retryArea, 0, sizeof(retryArea));
    establish_estaex_with_retry(&retryArea,
                                &estaexRC,
                                &estaexRSN);

    if (estaexRC == 0) {
        SET_RETRY_POINT(retryArea);
        if (triedIt == 0) {
            bbgz_psw psw;
            triedIt = 1;
            extractPSWFromLinkageStack(&psw);
            void* clearedStorage = malloc(length);
            if (clearedStorage != NULL) {
                memset(clearedStorage, 0, length);
                memcpy_dk(target_p, clearedStorage, length, psw.key);
                free(clearedStorage);
            } else {
                retried = 3;
            }
        } else {
            retried = 1;
        }

        remove_estaex(&estaexRC, &estaexRSN);
    } else {
        retried = 2;
    }

    return retried;
}

/**
 * Function used to free the bind token cell pool.
 */
static void freeBindTokenCellPoolStorage(unsigned char storageType, void* storage_p, long long id) {
    if (storage_p != NULL) {
        free(storage_p);
    }
}

/**
 * Destroys an angel client bind data control block.
 *
 * @param bindData_p A pointer to the bind data to destroy.
 * @param sgoo_p A pointer to the SGOO.
 * @param recovery_p A pointer to the recovery area.
 */
static void destroyBindData(AngelClientBindData_t* bindData_p, bbgzsgoo* sgoo_p, angel_client_pc_recovery* recovery_p) {
    if (bindData_p->bindTokenCellPool != 0L) {
        long long bindTokenCellPool = bindData_p->bindTokenCellPool;
        bindData_p->bindTokenCellPool = 0L;
        destroyCellPool(bindTokenCellPool, freeBindTokenCellPoolStorage);
    }

    if (bindData_p->scfmCopy_p != NULL) {
        void* tempStg_p = bindData_p->scfmCopy_p;
        bindData_p->scfmCopy_p = NULL;
        free(tempStg_p);
    }

    if (recovery_p != NULL) {
        recovery_p->drm_bindData_p = NULL;
    }

    freeCellPoolCell(sgoo_p->bbgzsgoo_clientBindDataPool, bindData_p);
}

/**
 * Create and initialize an angel client bind data control block.
 *
 * @param sgoo_p A pointer to the SGOO.
 * @param apd_p A pointer to the angel process data for the bound server.
 * @param localSCFM_p A pointer to the common function module header.
 * @param recovery_p A pointer to the recovery area.
 * @param stoken_p A pointer to the target server's stoken.
 * @param serverInstanceCount The instance count from the PGOO (angel process data)
 *
 * @return A new angel client bind data control block, if one could be created.
 */
static AngelClientBindData_t* createBindData(bbgzsgoo* sgoo_p, angel_process_data* apd_p, bbgzasvt_header* localSCFM_p, angel_client_pc_recovery* recovery_p, SToken* stoken_p, int serverInstanceCount, unsigned long long clientToken) {
    AngelClientBindData_t* bindData_p = getCellPoolCell(sgoo_p->bbgzsgoo_clientBindDataPool);
    if (bindData_p != NULL) {
        recovery_p->drm_bindData_p = bindData_p;
        recovery_p->drm_bindData_sgoo_p = sgoo_p;

        memset(bindData_p, 0, sizeof(*bindData_p));
        memcpy(bindData_p->eyecatcher, ANGEL_CLIENT_BIND_DATA_EYECATCHER, sizeof(bindData_p->eyecatcher));
        bindData_p->length = sizeof(*bindData_p);
        memcpy(&(bindData_p->serverStoken), stoken_p, sizeof(bindData_p->serverStoken));
        recovery_p->scfmCopy_p = NULL; // Transfer ownership from recovery to bind data
        bindData_p->scfmCopy_p = localSCFM_p;
        bindData_p->apd_p = apd_p;
        bindData_p->serverInstanceCount = serverInstanceCount;
        bindData_p->clientToken = clientToken;

        // -------------------------------------------------------------------
        // Create a unique ID for this bind data instance.
        // -------------------------------------------------------------------
        unsigned int oldCount, newCount;
        oldCount = sgoo_p->clientBindDataInstanceCounter;
        for (int csRC = -1; csRC != 0;) {
            newCount = oldCount + 1;
            csRC = __cs1(&oldCount, &(sgoo_p->clientBindDataInstanceCounter), &newCount);
        }
        bindData_p->instanceCount = oldCount;

        // -------------------------------------------------------------------
        // Create the cell pool that we'll store the bind tokens in.
        // -------------------------------------------------------------------
        buildCellPoolFlags poolFlags;
        memset(&poolFlags, 0, sizeof(poolFlags));
        poolFlags.skipInitialCellAllocation = 1;
        long long poolSize = computeCellPoolStorageRequirement(0, sizeof(AngelClientBindToken_t));
        void* poolStorage_p = malloc(poolSize);
        if (poolStorage_p != NULL) {
            recovery_p->drm_bindDataTokenPool_p = poolStorage_p;
            long long bindTokenCellPool = buildCellPool(poolStorage_p, poolSize, sizeof(AngelClientBindToken_t), "BBGZCBTP", poolFlags);
            if (bindTokenCellPool != 0L) {
                recovery_p->drm_bindDataTokenPool_p = NULL;
                bindData_p->bindTokenCellPool = bindTokenCellPool;
            } else {
                recovery_p->drm_bindDataTokenPool_p = NULL;
                free(poolStorage_p);
                recovery_p->scfmCopy_p = bindData_p->scfmCopy_p; // Transfer back.
                recovery_p->drm_bindData_p = NULL;
                freeCellPoolCell(sgoo_p->bbgzsgoo_clientBindDataPool, bindData_p);
                bindData_p = NULL;
            }
        } else {
            recovery_p->scfmCopy_p = bindData_p->scfmCopy_p; // Transfer back.
            recovery_p->drm_bindData_p = NULL;
            freeCellPoolCell(sgoo_p->bbgzsgoo_clientBindDataPool, bindData_p);
            bindData_p = NULL;
        }
    }

    return bindData_p;
}

/**
 * Adds a client bind to the angel process data list of binds for a server.
 *
 * @param apd_p The angel process data to add the bind to.
 * @param bindData_p The bind to add.
 * @param recovery_p A pointer to the ARR recovery area.
 *
 * @return 0 if the bind was successfully added.
 */
static int addBindToProcessData(angel_process_data* apd_p, AngelClientBindData_t* bindData_p, angel_client_pc_recovery* recovery_p) {
    // -----------------------------------------------------------------------
    // Try to get a node to put the bind data into.
    // -----------------------------------------------------------------------
    bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
    AngelClientBindDataNode_t* node_p = getCellPoolCell(sgoo_p->bbgzsgoo_clientBindDataNodePool);
    if (node_p == NULL) {
        return -1;
    }

    recovery_p->apdBindDataNode_p = node_p;

    // -----------------------------------------------------------------------
    // Get the ENQ for the angel process data bind list.
    // -----------------------------------------------------------------------
    char enqName[255];
    snprintf(enqName, sizeof(enqName), ANGEL_PROCESS_CLIENT_BIND_LIST_ENQ_PATTERN, *((long long*)(&(bindData_p->serverStoken))));
    enqtoken enqToken;
    get_enq_exclusive_system(BBGZ_ENQ_QNAME, enqName, NULL, &enqToken);
    memcpy(&(recovery_p->apdBindDataListEnqToken), &enqToken, sizeof(enqToken));

    // -----------------------------------------------------------------------
    // Update the list.
    // -----------------------------------------------------------------------
    node_p->data_p = bindData_p;
    node_p->next_p = apd_p->clientBindHead_p;
    recovery_p->apdBindDataNode_p = NULL; // Don't recover if put in list.
    apd_p->clientBindHead_p = node_p;
    recovery_p->drm_removeBindDataFromApd = TRUE;

    // -----------------------------------------------------------------------
    // Release the ENQ for the angel process data bind list.
    // -----------------------------------------------------------------------
    memset(&(recovery_p->apdBindDataListEnqToken), 0, sizeof(recovery_p->apdBindDataListEnqToken));
    release_enq(&enqToken);

    return 0;
}


/**
 * Removes a client bind from the angel process data list of binds for a server.
 *
 * @param bindData_p The bind to remove.
 * @param recovery_p A pointer to the ARR recovery area.
 *
 * @return 0 if the bind was successfully removed.
 */
static int removeBindFromProcessData(AngelClientBindData_t* bindData_p, angel_client_pc_recovery* recovery_p) {
    // -----------------------------------------------------------------------
    // Get the ENQ for the angel process data bind list.
    // -----------------------------------------------------------------------
    char enqName[255];
    snprintf(enqName, sizeof(enqName), ANGEL_PROCESS_CLIENT_BIND_LIST_ENQ_PATTERN, *((long long*)(&(bindData_p->serverStoken))));
    enqtoken enqToken;
    get_enq_exclusive_system(BBGZ_ENQ_QNAME, enqName, NULL, &enqToken);
    memcpy(&(recovery_p->apdBindDataListEnqToken), &enqToken, sizeof(enqToken));

    unsigned char removed = FALSE;

    // -----------------------------------------------------------------------
    // Update the list.
    // -----------------------------------------------------------------------
    angel_process_data* apd_p = bindData_p->apd_p;
    AngelClientBindDataNode_t* prevNode_p = NULL;
    AngelClientBindDataNode_t* curNode_p = apd_p->clientBindHead_p;
    while ((curNode_p != NULL) && (removed == FALSE)) {
        if (curNode_p->data_p == bindData_p) {
            recovery_p->drm_removeBindDataFromApd = FALSE;
            if (prevNode_p == NULL) {
                apd_p->clientBindHead_p = curNode_p->next_p;
            } else {
                prevNode_p->next_p = curNode_p->next_p;
            }
            freeCellPoolCell(apd_p->bbgzsgoo_p->bbgzsgoo_clientBindDataNodePool, curNode_p);
            removed = TRUE;
        } else {
            prevNode_p = curNode_p;
            curNode_p = curNode_p->next_p;
        }
    }

    // -----------------------------------------------------------------------
    // Release the ENQ for the angel process data bind list.
    // -----------------------------------------------------------------------
    memset(&(recovery_p->apdBindDataListEnqToken), 0, sizeof(recovery_p->apdBindDataListEnqToken));
    release_enq(&enqToken);

    return (removed == TRUE) ? 0 : -1;
}


/**
 * Establishes the RESMGRs that watch over the client process.  There is one
 * RESMGR which watches over the client address space, and one that watches
 * over the IPT or jobstep task, depending on the client process type.
 *
 * @param acpd_p A pointer to the angel client process data control block.
 *
 * @return 0 if the RESMGRs were created successfully
 */
static int establishClientRESMGRs(AngelClientProcessData_t* acpd_p) {
    int resmgrToken;
    bbgzsgoo* sgoo_p = acpd_p->sgoo_p;
    long long resmgrParms = (long long) sgoo_p->bbgzsgoo_pcLatentParmArea_p;
    int resmgrRC = addResourceManager(&resmgrToken,
                                      &resmgrParms,
                                      BBOZRMGR_TYPE_AS,
                                      acpd_p->sgoo_p->bbgzsgoo_fsm->client_resmgr_stub);

    if (resmgrRC == 0) {
        acpd_p->addressSpaceResmgrToken = resmgrToken;
        resmgrRC = addResourceManagerForAnotherTask(&resmgrToken,
                                                    &resmgrParms,
                                                    acpd_p->tcbForTaskResmgr,
                                                    acpd_p->sgoo_p->bbgzsgoo_fsm->client_resmgr_stub);

        if (resmgrRC == 0) {
            acpd_p->taskResmgrToken = resmgrToken;
        } else {
            resmgrToken = acpd_p->addressSpaceResmgrToken;
            acpd_p->addressSpaceResmgrToken = 0;
            deleteResourceManager(&(resmgrToken), BBOZRMGR_TYPE_AS);
        }
    }

    return resmgrRC;
}

/**
 * Destroyes the RESMGRs watching over the client process.
 *
 * @param acpd_p A pointer to the angel client process data control block,
 *               where the RESMGR tokens are stored.
 */
static void destroyClientRESMGRs(AngelClientProcessData_t* acpd_p) {
    if ((acpd_p->taskResmgrToken != 0) && (acpd_p->tcbForTaskResmgr != NULL)) {
        int token = acpd_p->taskResmgrToken;
        acpd_p->taskResmgrToken = 0;
        tcb* tcb_p = acpd_p->tcbForTaskResmgr;
        acpd_p->tcbForTaskResmgr = NULL;
        deleteResourceManagerForAnotherTask(&token, tcb_p);
    }

    if (acpd_p->addressSpaceResmgrToken != 0) {
        int token = acpd_p->addressSpaceResmgrToken;
        acpd_p->addressSpaceResmgrToken = 0;
        deleteResourceManager(&token, BBOZRMGR_TYPE_AS);
    }
}

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
bbgzasvt_header* copyCommonFunctionModule(angel_process_data* apd_p, angel_client_pc_recovery* recovery_p) {
    bbgzasvt_header* header_p = NULL;

    if (apd_p->scfm_function_table_p != NULL) {
        int scfm_size = sizeof(*header_p) +
            ((apd_p->scfm_function_table_p->num_entries) * sizeof(bbgzasve)) +
            strlen(BBGZASVT_EYE_END);

        header_p = malloc(scfm_size);
        if (header_p != NULL) {
            recovery_p->scfmCopy_p = header_p;
            memcpy(header_p, (apd_p->scfm_function_table_p), scfm_size);
        }
    }

    return header_p;
}

/**
 * Generates a bind token for the caller of bind to use.  The caller should
 * hold the bind ENQ, making this serialized.
 *
 * @param bindData_p A pointer to the bind data representing the target server.
 * @param recovery_p A pointer to the ARR recovery area.
 *
 * @return A pointer to a bind token, if one could be created.
 */
AngelClientBindToken_t* generateBindToken(AngelClientBindData_t* bindData_p, angel_client_pc_recovery* recovery_p) {
    AngelClientBindToken_t* bindToken_p = getCellPoolCell(bindData_p->bindTokenCellPool);
    if (bindToken_p == NULL) {
        long long numCellsPerExtent = 32;
        long long extentSize = computeCellPoolExtentStorageRequirement(numCellsPerExtent);
        void* extentStorage_p = malloc(extentSize);
        if (extentStorage_p != NULL) {
            long long cellStorageSize = numCellsPerExtent * sizeof(AngelClientBindToken_t);
            void* cellStorage_p = malloc(cellStorageSize);
            if (cellStorage_p != NULL) {
                growCellPool(bindData_p->bindTokenCellPool, numCellsPerExtent, extentStorage_p, extentSize, cellStorage_p, cellStorageSize);
                bindToken_p = getCellPoolCell(bindData_p->bindTokenCellPool);
            } else {
                free(extentStorage_p);
            }
        }
    }

    if (bindToken_p != NULL) {
        recovery_p->drm_bindToken_p = bindToken_p; // Save ref to token.
        memset(bindToken_p, 0, sizeof(*bindToken_p));
        memcpy(bindToken_p->eyecatcher,  ANGEL_CLIENT_BIND_TOKEN_EYECATCHER, sizeof(bindToken_p->eyecatcher));
        bindToken_p->length = sizeof(*bindToken_p);
        bindToken_p->clientBindData_p = bindData_p;
        __stck(&(bindToken_p->timestamp));

        // -------------------------------------------------------------------
        // We should consider putting an instance count in the bind token too,
        // to make it unique.
        // -------------------------------------------------------------------
    }

    return bindToken_p;
}

/**
 * The client's representation of the bind token.  This is returned to the
 * caller on bind.
 */
typedef struct {
    AngelClientBindToken_t* bindToken_p;    //!< Pointer to cell pool cell.
    unsigned int            instance;       //!< Unique identifier.
    unsigned char           _available[4];  //!< Available for use.
} ClientBindToken_t;

// Client bind
int dynamicReplaceablePC_ClientBind(SToken* targetServerStoken_p, bbgzasvt_header** clientFunctionTablePtr_p, void** bindToken_p, bbgzsgoo* sgoo_p, bbgzarmv* armv_p, AngelClientProcessData_t* existingAcpd_p, angel_client_pc_recovery* recovery_p) {

    // -----------------------------------------------------------------------
    // Verify that we can read the caller's input parameters.
    // -----------------------------------------------------------------------
    if (targetServerStoken_p == NULL) {
        return ANGEL_CLIENT_BIND_DRM_NULL_STOKEN_ADDR;
    }

    SToken targetServerStoken;
    if (safelyCopyFromCallerStorage(targetServerStoken_p, sizeof(targetServerStoken), &targetServerStoken) != 0) {
        return ANGEL_CLIENT_BIND_DRM_INV_STOKEN_ADDR;
    }

    // -----------------------------------------------------------------------
    // Verify we can write to the caller's output parameters.
    // -----------------------------------------------------------------------
    if (clientFunctionTablePtr_p == NULL) {
        return ANGEL_CLIENT_BIND_DRM_NULL_TABLE_ADDR;
    }

    if (safelyClearCallerStorage(clientFunctionTablePtr_p, sizeof(*clientFunctionTablePtr_p)) != 0) {
        return ANGEL_CLIENT_BIND_DRM_INV_TABLE_ADDR;
    }

    if (bindToken_p == NULL) {
        return ANGEL_CLIENT_BIND_DRM_NULL_BIND_TOKEN_ADDR;
    }

    if (safelyClearCallerStorage(bindToken_p, sizeof(ClientBindToken_t)) != 0) {
        return ANGEL_CLIENT_BIND_DRM_INV_BIND_TOKEN_ADDR;
    }

    // -----------------------------------------------------------------------
    // If an angel client process data object already exists, check it for
    // a bind to the target server.  Since servers can re-use STOKENs if
    // spawned from the shell, we first need to look up the PGOO for the
    // server and get the instance counter.  Then we can check for an existing
    // bind to that instance count + STOKEN.  (Really, the instance count
    // alone should be enough, since each PGOO gets a unique one)
    // -----------------------------------------------------------------------
    unsigned char unbindOnTokenCreateFailure = FALSE;
    int bindReturnCode = ANGEL_CLIENT_BIND_OK;
    AngelClientProcessData_t* acpd_p = existingAcpd_p;

    // -------------------------------------------------------------------
    // Look up the PGOO (angel process data) for the target server.
    // -------------------------------------------------------------------
    angel_process_data* apd_p = getAngelProcessDataByStoken(&targetServerStoken);
    if (apd_p == NULL) {
        return ANGEL_CLIENT_BIND_DRM_PGOO_NOT_FOUND;
    }

    int serverInstanceCount = apd_p->instance_num;
    AngelClientBindData_t* bindData_p = (acpd_p != NULL) ? checkForExistingBind(acpd_p, &targetServerStoken, serverInstanceCount) : NULL;
    if (bindData_p == NULL) {

        // -------------------------------------------------------------------
        // Increment the client bind count in the PGOO.  This will guarantee
        // that the PGOO will not be re-used or deleted.
        // -------------------------------------------------------------------
        if (incrementBindCount(apd_p, &targetServerStoken) != 0) {
            return ANGEL_CLIENT_BIND_DRM_PGOO_GOING_AWAY;
        }

        recovery_p->drm_apdToDecrementBindCount_p = apd_p;

        // -------------------------------------------------------------------
        // Copy the common function module header for the target server into
        // local storage.  This function modifies the recovery area.
        // -------------------------------------------------------------------
        bbgzasvt_header* localCommonFcnModule_p = copyCommonFunctionModule(apd_p, recovery_p);
        if (localCommonFcnModule_p != NULL) {

            // ---------------------------------------------------------------
            // Create the client token, which the server code uses to tell
            // between the different binds to different servers.  Older
            // versions of the server code assume the token is going to be
            // the STOKEN of the server we connected to, but we found that to
            // not be unique enough since the STOKEN can be re-used if the
            // server is running in a BPXAS.  If the server code is new
            // enough (does not depend on the clientToken being the STOKEN),
            // we'll generate a real unique token.
            // ---------------------------------------------------------------
            unsigned long long newClientToken;
            if (localCommonFcnModule_p->flags.generateClientToken == 1) {
                __stck(&newClientToken);
            } else {
                memcpy(&newClientToken, &targetServerStoken, sizeof(newClientToken));
            }

            // ---------------------------------------------------------------
            // Create the bind control block between the client and the server.
            // This function modifies the recovery area.
            // ---------------------------------------------------------------
            bindData_p = createBindData(sgoo_p, apd_p, localCommonFcnModule_p, recovery_p, &targetServerStoken, serverInstanceCount, newClientToken);
            if (bindData_p != NULL) {

                // -----------------------------------------------------------
                // Create the angel client process data, if we don't already
                // have one.
                // -----------------------------------------------------------
                if (acpd_p == NULL) {
                    // -------------------------------------------------------
                    // This function modifies the recovery area.
                    // -------------------------------------------------------
                    acpd_p = createAngelClientProcessData(sgoo_p, armv_p->bbgzarmv_instancecount, recovery_p);

                    // -------------------------------------------------------
                    // Establish the RESMGRs that will watch over the client.
                    // -------------------------------------------------------
                    if (acpd_p != NULL) {
                        if (establishClientRESMGRs(acpd_p) != 0) {
                            destroyAngelClientProcessData(acpd_p, recovery_p);
                            acpd_p = NULL;
                            bindReturnCode = ANGEL_CLIENT_BIND_DRM_RESMGR_ERROR;
                        }
                    }
                }

                // -----------------------------------------------------------
                // Add the new bind to the list of binds in the client process
                // data.
                // -----------------------------------------------------------
                if (acpd_p != NULL) {
                    if (addBindToClientProcessData(acpd_p, bindData_p) == 0) {
                        recovery_p->drm_removeBindDataFromAcpd = TRUE;
                        bindData_p->clientProcessData_p = acpd_p;

                        // ---------------------------------------------------
                        // Call the initialization routine in the SCFM if
                        // there is one.  There is no need to increment the
                        // invoke count when we do this, because we
                        // incremented the bind count in the APD but have not
                        // added the bind data the bind list yet.
                        // ---------------------------------------------------
                        unsigned char initFailure = FALSE;
                        if (localCommonFcnModule_p->process_initialization_routine_ptr != NULL) {
                            recovery_p->drm_callScfmUninitialize = TRUE;
                            void* cenv_p = getenvfromR12();
                            setenvintoR12(NULL);
                            initFailure = (localCommonFcnModule_p->process_initialization_routine_ptr(bindData_p->clientToken) != 0);
                            setenvintoR12(cenv_p);
                        }

                        if (initFailure == FALSE) {
                            // ---------------------------------------------------
                            // Add the new bind to the list of binds in the server's
                            // angel process data.  If this is successful, the bind
                            // portion is complete, but we still need to generate
                            // a bind token so don't remove the recovery information
                            // yet.
                            // ---------------------------------------------------
                            if (addBindToProcessData(apd_p, bindData_p, recovery_p) == 0) {
                                unbindOnTokenCreateFailure = TRUE;
                            } else {
                                if (recovery_p->drm_callScfmUninitialize == TRUE) {
                                    recovery_p->drm_callScfmUninitialize = FALSE;
                                    if (localCommonFcnModule_p->process_cleanup_routine_ptr != NULL) {
                                        void* cenv_p = getenvfromR12();
                                        setenvintoR12(NULL);
                                        localCommonFcnModule_p->process_cleanup_routine_ptr(bindData_p->clientToken);
                                        setenvintoR12(cenv_p);
                                    }
                                }
                                recovery_p->drm_removeBindDataFromAcpd = FALSE;
                                removeBindFromClientProcessData(bindData_p);
                                if (existingAcpd_p == NULL) {
                                    destroyClientRESMGRs(acpd_p);
                                    destroyAngelClientProcessData(acpd_p, recovery_p);
                                    acpd_p = NULL;
                                }
                                destroyBindData(bindData_p, sgoo_p, recovery_p);
                                bindData_p = NULL;
                                recovery_p->drm_apdToDecrementBindCount_p = NULL;
                                decrementBindCount(apd_p, &targetServerStoken);
                                bindReturnCode = ANGEL_CLIENT_BIND_DRM_ADD_TO_APD_ERROR;
                            }
                        } else {
                            recovery_p->drm_removeBindDataFromAcpd = FALSE;
                            removeBindFromClientProcessData(bindData_p);
                            if (existingAcpd_p == NULL) {
                                destroyClientRESMGRs(acpd_p);
                                destroyAngelClientProcessData(acpd_p, recovery_p);
                                acpd_p = NULL;
                            }
                            destroyBindData(bindData_p, sgoo_p, recovery_p);
                            bindData_p = NULL;
                            recovery_p->drm_apdToDecrementBindCount_p = NULL;
                            decrementBindCount(apd_p, &targetServerStoken);
                            bindReturnCode = ANGEL_CLIENT_BIND_DRM_SCFM_INIT_ERROR;
                        }
                    } else {
                        if (existingAcpd_p == NULL) {
                            destroyClientRESMGRs(acpd_p);
                            destroyAngelClientProcessData(acpd_p, recovery_p);
                            acpd_p = NULL;
                        }
                        destroyBindData(bindData_p, sgoo_p, recovery_p);
                        bindData_p = NULL;
                        recovery_p->drm_apdToDecrementBindCount_p = NULL;
                        decrementBindCount(apd_p, &targetServerStoken);
                        bindReturnCode = ANGEL_CLIENT_BIND_DRM_ADD_TO_ACPD_ERROR;
                    }
                } else {
                    destroyBindData(bindData_p, sgoo_p, recovery_p);
                    bindData_p = NULL;
                    recovery_p->drm_apdToDecrementBindCount_p = NULL;
                    decrementBindCount(apd_p, &targetServerStoken);
                    if (bindReturnCode == ANGEL_CLIENT_BIND_OK) {
                        bindReturnCode = ANGEL_CLIENT_BIND_DRM_CREATE_ACPD_ERROR;
                    }
                }
            } else {
                recovery_p->scfmCopy_p = NULL;
                free(localCommonFcnModule_p);
                recovery_p->drm_apdToDecrementBindCount_p = NULL;
                decrementBindCount(apd_p, &targetServerStoken);
                bindReturnCode = ANGEL_CLIENT_BIND_DRM_CREATE_BIND_DATA_ERROR;
            }
        } else {
            recovery_p->drm_apdToDecrementBindCount_p = NULL;
            decrementBindCount(apd_p, &targetServerStoken);
            bindReturnCode = ANGEL_CLIENT_BIND_DRM_COPY_SCFM_ERROR;
        }
    }

    // -----------------------------------------------------------------------
    // If we are bound, generate a bind token for the caller to use.
    // -----------------------------------------------------------------------
    if ((bindData_p != NULL) && (bindReturnCode == ANGEL_CLIENT_BIND_OK)) {
        recovery_p->drm_bindDataToBindTo_p = bindData_p;
        AngelClientBindToken_t* localBindToken_p = generateBindToken(bindData_p, recovery_p);
        if (localBindToken_p != NULL) {
            // ---------------------------------------------------------------
            // Increment the bind count.
            // ---------------------------------------------------------------
            AngelClientDataBindCount_t oldCount, newCount;
            unsigned char errorCase = FALSE;
            memcpy(&oldCount, &(bindData_p->bindCount), sizeof(oldCount));
            for (int csRC = -1; ((csRC != 0) && (errorCase == FALSE));) {
                memcpy(&newCount, &oldCount, sizeof(newCount));
                if (newCount.serverIsEnding != 0) {
                    errorCase = TRUE;
                } else {
                    newCount.bindCount = newCount.bindCount + 1;
                    csRC = __cds1(&oldCount, &(bindData_p->bindCount), &newCount);
                }
            }

            if (errorCase == FALSE) {
                // ---------------------------------------------------------------
                // Copy results back to caller.
                // ---------------------------------------------------------------
                recovery_p->drm_decrementBindCountInBindData = 1;
                ClientBindToken_t clientBindToken;
                clientBindToken.bindToken_p = localBindToken_p;
                clientBindToken.instance = bindData_p->instanceCount;
                memset(clientBindToken._available, 0, sizeof(clientBindToken._available));
                bbgz_psw pswFromLinkageStack;
                extractPSWFromLinkageStack(&pswFromLinkageStack);
                memcpy_dk(clientFunctionTablePtr_p, &(bindData_p->scfmCopy_p), sizeof(*clientFunctionTablePtr_p), pswFromLinkageStack.key);
                memcpy_dk(bindToken_p, &clientBindToken, sizeof(clientBindToken), pswFromLinkageStack.key);
            } else {
                freeCellPoolCell(bindData_p->bindTokenCellPool, bindToken_p);
                if (unbindOnTokenCreateFailure == TRUE) {
                    int removeRC = removeBindFromProcessData(bindData_p, recovery_p);
                    unsigned char driveUninitialize = recovery_p->drm_callScfmUninitialize;
                    recovery_p->drm_callScfmUninitialize = FALSE;
                    if ((removeRC == 0) && (driveUninitialize == TRUE)) {
                        if (bindData_p->scfmCopy_p->process_cleanup_routine_ptr != NULL) {
                            void* cenv_p = getenvfromR12();
                            setenvintoR12(NULL);
                            bindData_p->scfmCopy_p->process_cleanup_routine_ptr(bindData_p->clientToken);
                            setenvintoR12(cenv_p);
                        }
                    }
                    recovery_p->drm_removeBindDataFromAcpd = FALSE;
                    removeBindFromClientProcessData(bindData_p);
                    if (existingAcpd_p == NULL) {
                        destroyClientRESMGRs(acpd_p);
                        destroyAngelClientProcessData(acpd_p, recovery_p);
                        acpd_p = NULL;
                    }
                    angel_process_data* apd_p = bindData_p->apd_p;
                    destroyBindData(bindData_p, sgoo_p, recovery_p);
                    bindData_p = NULL;
                    recovery_p->drm_apdToDecrementBindCount_p = NULL;
                    if (removeRC == 0) {
                        decrementBindCount(apd_p, &targetServerStoken);
                    }
                }

                bindReturnCode = ANGEL_CLIENT_BIND_DRM_INCREMENT_BIND_COUNT_ERROR;
            }
        } else {
            if (unbindOnTokenCreateFailure == TRUE) {
                int removeRC = removeBindFromProcessData(bindData_p, recovery_p);
                unsigned char driveUninitialize = recovery_p->drm_callScfmUninitialize;
                recovery_p->drm_callScfmUninitialize = FALSE;
                if ((removeRC == 0) && (driveUninitialize == TRUE)) {
                    if (bindData_p->scfmCopy_p->process_cleanup_routine_ptr != NULL) {
                        void* cenv_p = getenvfromR12();
                        setenvintoR12(NULL);
                        bindData_p->scfmCopy_p->process_cleanup_routine_ptr(bindData_p->clientToken);
                        setenvintoR12(cenv_p);
                    }
                }
                recovery_p->drm_removeBindDataFromAcpd = FALSE;
                removeBindFromClientProcessData(bindData_p);
                if (existingAcpd_p == NULL) {
                    destroyClientRESMGRs(acpd_p);
                    destroyAngelClientProcessData(acpd_p, recovery_p);
                    acpd_p = NULL;
                }
                angel_process_data* apd_p = bindData_p->apd_p;
                destroyBindData(bindData_p, sgoo_p, recovery_p);
                bindData_p = NULL;
                recovery_p->drm_apdToDecrementBindCount_p = NULL;
                if (removeRC == 0) {
                    decrementBindCount(apd_p, &targetServerStoken);
                }
            }

            bindReturnCode = ANGEL_CLIENT_BIND_DRM_ALLOCATE_TOKEN_ERROR;
        }
    }

    return bindReturnCode;
}

#define INVOKE_ARG_STRUCT_MAX_BYTES 65536

// Client invoke
int dynamicReplaceablePC_ClientInvoke(void* bindToken_p,
                                      unsigned int serviceIndex,
                                      unsigned int parm_len,
                                      void* parm_p,
                                      angel_client_pc_recovery* recovery_p) {

    // ------------------------------------------------------------------------
    // Increment the invoke count on the bind data.
    // ------------------------------------------------------------------------
    angel_task_data* atd_p = getAngelTaskData();
    AngelClientBindData_t* bindData_p = atd_p->validatedClientBindData_p;
    AngelClientDataBindCount_t oldCount, newCount;
    memcpy(&oldCount, &(bindData_p->bindCount), sizeof(oldCount));
    for (int csRC = -1; csRC != 0;) {
        if (oldCount.bindCount <= 0) {
            return ANGEL_CLIENT_INVOKE_DRM_NO_BINDS;
        }
        memcpy(&newCount, &oldCount, sizeof(newCount));
        newCount.invokeCount = newCount.invokeCount + 1;
        csRC = __cds1(&oldCount, &(bindData_p->bindCount), &newCount);
    }

    recovery_p->drm_bindDataToDecrementInvoke_p = bindData_p;
    int invokeRC = ANGEL_CLIENT_INVOKE_OK;

    // -----------------------------------------------------------------------
    // Make sure we've got the correct bind data object.
    // -----------------------------------------------------------------------
    if (((ClientBindToken_t*)bindToken_p)->instance == bindData_p->instanceCount) {
        // -------------------------------------------------------------------
        // Find the index for the function we want to drive, and see if it's
        // available for use.
        // -------------------------------------------------------------------
        bbgzasvt_header* scfmHeader_p = bindData_p->scfmCopy_p;
        if (serviceIndex < scfmHeader_p->num_entries) {
            bbgzasve* firstEntry_p = (bbgzasve*) (scfmHeader_p + 1);
            bbgzasve* desiredEntry_p = firstEntry_p + serviceIndex;

            if (desiredEntry_p->bbgzasve_runtime_bits.authorized_to_use == 1) {
                // -----------------------------------------------------------
                // Copy the caller's structure argument (still in key 8) to
                // local storage.
                // -----------------------------------------------------------
                if ((parm_len != 0) && (parm_len <= INVOKE_ARG_STRUCT_MAX_BYTES)) {
                    void* localInvokeArgStruct_p = malloc(parm_len);
                    if (localInvokeArgStruct_p != NULL) {
                        recovery_p->drm_localInvokeArgStruct_p = localInvokeArgStruct_p;
                        bbgz_psw pswFromLinkageStack;
                        extractPSWFromLinkageStack(&pswFromLinkageStack);
                        memcpy_sk(localInvokeArgStruct_p, parm_p, parm_len, pswFromLinkageStack.key);

                        // TODO: Need to remove metal C environment.

                        // ---------------------------------------------------
                        // Invoke the service.
                        // ---------------------------------------------------
                        void* cenv_p = getenvfromR12();
                        setenvintoR12(NULL);
                        if (scfmHeader_p->setupEnvironmentAndCallInvokableService != NULL) {
                            scfmHeader_p->setupEnvironmentAndCallInvokableService(desiredEntry_p->bbgzasve_fcn_ptr, localInvokeArgStruct_p, bindData_p->clientToken);
                        } else {
                            desiredEntry_p->bbgzasve_fcn_ptr(localInvokeArgStruct_p);
                        }
                        setenvintoR12(cenv_p);

                        recovery_p->drm_localInvokeArgStruct_p = NULL;
                        free(localInvokeArgStruct_p);
                    } else {
                        invokeRC = ANGEL_CLIENT_INVOKE_DRM_NOMEM;
                    }
                } else {
                    invokeRC = ANGEL_CLIENT_INVOKE_DRM_ARGSIZE_INV;
                }
            } else {
                invokeRC = ANGEL_CLIENT_INVOKE_DRM_SERVICE_NOT_AUTH;
            }
        } else {
            invokeRC = ANGEL_CLIENT_INVOKE_DRM_INV_SERVICE_INDEX;
        }
    } else {
        // Bind count is wrong, must have unregistered.
        invokeRC = ANGEL_CLIENT_INVOKE_DRM_UNREGISTERED;
    }

    // -----------------------------------------------------------------------
    // See if any of the cell pools used by the entry linkage need to be
    // grown.
    // -----------------------------------------------------------------------
    if (atd_p->clientTempDynAreaGetFailed != 0) {
        // -------------------------------------------------------------------
        // This is an "auto-grow" pool and will grow if we try to obtain a
        // cell in the usual way (not from the entry linkage).
        // -------------------------------------------------------------------
        AngelClientProcessData_t* acpd_p = bindData_p->clientProcessData_p;
        bbgzsgoo* sgoo_p = acpd_p->sgoo_p;
        void* cell_p = getCellPoolCell(sgoo_p->bbgzsgoo_clientPreDynamicAreaPool);
        freeCellPoolCell(sgoo_p->bbgzsgoo_clientPreDynamicAreaPool, cell_p);
        atd_p->clientTempDynAreaGetFailed = 0;
    }

    if (atd_p->bindTokDynAreaPoolEmpty != 0) {
        growClientDynamicAreaCellPool(&(bindData_p->clientProcessData_p->clientDynAreaPoolInfo));
        atd_p->bindTokDynAreaPoolEmpty = 0;
    }

    // ------------------------------------------------------------------------
    // Decrement the invoke count on the bind data.  This may also result in
    // having to clean up the bind data and/or the angel process data.
    // ------------------------------------------------------------------------
    unsigned char doUnbind = FALSE;
    memcpy(&oldCount, &(bindData_p->bindCount), sizeof(oldCount));
    for (int csRC = -1; csRC != 0;) {
        memcpy(&newCount, &oldCount, sizeof(newCount));
        newCount.invokeCount = newCount.invokeCount - 1;

        // --------------------------------------------------------------------
        // If we would be decrementing the last active invoke and all binds are
        // gone, get the bind/unbind lock so we can clean up.
        // --------------------------------------------------------------------
        doUnbind = ((newCount.invokeCount == 0) && (newCount.bindCount == 0));
        if (doUnbind == TRUE) {
            recovery_p->shr_freeBindEnq = 1;
            get_enq_exclusive_step(BBGZ_ENQ_QNAME, CLIENT_BIND_ENQ_RNAME, &(recovery_p->shr_bindEnqToken));
        }

        recovery_p->drm_bindDataToDecrementInvoke_p = NULL;
        csRC = __cds1(&oldCount, &(bindData_p->bindCount), &newCount);

        if (csRC != 0) {
            recovery_p->drm_bindDataToDecrementInvoke_p = bindData_p;
            if (doUnbind == TRUE) {
                release_enq(&(recovery_p->shr_bindEnqToken));
                recovery_p->shr_freeBindEnq = 0;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Setup the ARR recovery area in case things go badly during cleanup.
    // -----------------------------------------------------------------------
    if (doUnbind == TRUE) {
        recovery_p->drm_bindData_p = bindData_p;
        recovery_p->drm_bindData_sgoo_p = bindData_p->clientProcessData_p->sgoo_p;
        recovery_p->drm_removeBindDataFromApd = TRUE;
        recovery_p->drm_removeBindDataFromAcpd = TRUE;
        recovery_p->drm_apdToDecrementBindCount_p = bindData_p->apd_p;
        recovery_p->drm_callScfmUninitialize = TRUE;

        // -------------------------------------------------------------------
        // If we are doing an unbind, we need to remove the bind from the
        // server's bind list.
        // -------------------------------------------------------------------
        if (removeBindFromProcessData(bindData_p, recovery_p) == 0) {
            recovery_p->drm_callScfmUninitialize = FALSE;
            if (bindData_p->scfmCopy_p->process_cleanup_routine_ptr != NULL) {
                void* cenv_p = getenvfromR12();
                setenvintoR12(NULL);
                bindData_p->scfmCopy_p->process_cleanup_routine_ptr(bindData_p->clientToken);
                setenvintoR12(cenv_p);
            }
            decrementBindCount(bindData_p->apd_p, &(bindData_p->serverStoken));
        }

        // -------------------------------------------------------------------
        // Clean up the bind from the client process data.  We can't go
        // 'all the way' and clean up the client process data because we may
        // be using a dynamic area from it.
        // -------------------------------------------------------------------
        recovery_p->drm_removeBindDataFromAcpd = FALSE;
        removeBindFromClientProcessData(bindData_p);
        destroyBindData(bindData_p, bindData_p->clientProcessData_p->sgoo_p, recovery_p);
        recovery_p->shr_freeBindEnq = 0;
        release_enq(&(recovery_p->shr_bindEnqToken));
    }

    return invokeRC;
}

// Client unbind
int dynamicReplaceablePC_ClientUnbind(void* bindToken_p, angel_client_pc_recovery* recovery_p, unsigned char* cleanup_p) {
    // -----------------------------------------------------------------------
    // We need to verify (again) that the bind token is valid.  Why?  Because
    // when we validated the bind token before, we did not hold the bind lock.
    // We're going to make sure that the bind token is allocated, and that it
    // points to the bind data that we think it does.
    //
    // In the future, we could have an instance counter on the bind token
    // which would prevent a double-free with another task re-acquring that
    // bind token cell in between.
    // -----------------------------------------------------------------------
    angel_task_data* atd_p = getAngelTaskData();
    AngelClientBindData_t* bindData_p = atd_p->validatedClientBindData_p;
    ClientBindToken_t* clientBindToken_p = (ClientBindToken_t*)bindToken_p;
    long long bindTokenAllocated = 0;
    int bindTokenInPool = verifyCellInPool(bindData_p->bindTokenCellPool, clientBindToken_p->bindToken_p, &bindTokenAllocated);

    if ((bindTokenInPool != 1) || (bindTokenAllocated != 1) || (clientBindToken_p->instance != bindData_p->instanceCount)) {
        return ANGEL_CLIENT_UNBIND_DRM_TOKINV;
    }

    // -----------------------------------------------------------------------
    // Decrement the bind count for this client-server connection.
    // -----------------------------------------------------------------------
    AngelClientDataBindCount_t oldCount, newCount;

    memcpy(&oldCount, &(bindData_p->bindCount), sizeof(oldCount));
    for (int csRC = -1; csRC != 0;) {
        memcpy(&newCount, &oldCount, sizeof(newCount));
        newCount.bindCount = newCount.bindCount - 1;
        csRC = __cds1(&oldCount, &(bindData_p->bindCount), &newCount);
    }

    // -----------------------------------------------------------------------
    // Put the bind token back in the pool of bind tokens.
    // -----------------------------------------------------------------------
    freeCellPoolCell(bindData_p->bindTokenCellPool, clientBindToken_p->bindToken_p);

    // -----------------------------------------------------------------------
    // Cleanup if this was the last bind for this client-server connection.
    // -----------------------------------------------------------------------
    if ((newCount.bindCount == 0) && (newCount.invokeCount == 0)) {
        recovery_p->drm_bindData_p = bindData_p;
        recovery_p->drm_bindData_sgoo_p = bindData_p->clientProcessData_p->sgoo_p;
        recovery_p->drm_removeBindDataFromApd = TRUE;
        recovery_p->drm_apdToDecrementBindCount_p = bindData_p->apd_p;
        recovery_p->drm_removeBindDataFromAcpd = TRUE;
        recovery_p->drm_callScfmUninitialize = TRUE;

        // -------------------------------------------------------------------
        // If this was the last unbind for this client-server connection,
        // remove it from the server's bind list.
        // -------------------------------------------------------------------
        if (removeBindFromProcessData(bindData_p, recovery_p) == 0) {
            recovery_p->drm_callScfmUninitialize = FALSE;
            if (bindData_p->scfmCopy_p->process_cleanup_routine_ptr != NULL) {
                void* cenv_p = getenvfromR12();
                setenvintoR12(NULL);
                bindData_p->scfmCopy_p->process_cleanup_routine_ptr(bindData_p->clientToken);
                setenvintoR12(cenv_p);
            }
            recovery_p->drm_apdToDecrementBindCount_p = NULL;
            decrementBindCount(bindData_p->apd_p, &(bindData_p->serverStoken));
        }

        // -------------------------------------------------------------------
        // Remove the bind from the client's list.
        // -------------------------------------------------------------------
        AngelClientProcessData_t* acpd_p = bindData_p->clientProcessData_p;
        recovery_p->drm_removeBindDataFromAcpd = FALSE;
        removeBindFromClientProcessData(bindData_p);
        destroyBindData(bindData_p, acpd_p->sgoo_p, recovery_p);
        bindData_p = NULL;
    }

    return ANGEL_CLIENT_UNBIND_OK;
}

// ARR
void dynamicReplaceableClientARR(sdwa* sdwa_p, angel_client_pc_recovery* recovery_p) {
    // ----------------------------------------------------------------------
    // Retry variable must be volatile so that updates are written directly
    // to memory and not cached in a register.
    // ----------------------------------------------------------------------
    volatile struct {
        int triedToDecrementBindCount : 1;
        int decrementedBindCount : 1;
        int triedToReturnBindToken : 1;
        int returnedBindToken : 1;
        int triedToRemoveBindDataFromApd : 1;
        int removedBindDataFromApd : 1;
        int triedToDecrementBindCountAPD : 1;
        int decrementedBindCountAPD :1;
        int triedToDropAPDBindDataListENQ : 1;
        int droppedAPDBindDataListENQ : 1;
        int triedToRemoveBindDataFromACPD : 1;
        int removedBindDataFromACPD : 1;
        int triedToDestroyClientRESMGRs : 1;
        int destroyedClientRESMGRs : 1;
        int triedToFreeClientProcessData : 1;
        int freedClientProcessData : 1;
        int triedToFreeClientBindData : 1;
        int freedClientBindData : 1;
        int triedToDecrementInvokeCount : 1;
        int decrementedInvokeCount : 1;
        int triedToCallScfmUninitialize : 1;
        int calledScfmUninitialize : 1;
        int _available : 10;
    } retryBits;

    // -----------------------------------------------------------------------
    // Set up an ESTAE in case we run into trouble.
    // -----------------------------------------------------------------------
    memset((void*)(&retryBits), 0, sizeof(retryBits));
    retry_parms angelRetryArea;
    int estaexRC, estaexRSN;
    memset(&angelRetryArea, 0, sizeof(angelRetryArea));
    establish_estaex_with_retry(&angelRetryArea,
                                &estaexRC,
                                &estaexRSN);

    if (estaexRC == 0) {
        // -------------------------------------------------------------------
        // Decrement the invoke count if we need to do that.
        // -------------------------------------------------------------------
        if (recovery_p->drm_bindDataToDecrementInvoke_p != NULL) {
            SET_RETRY_POINT(angelRetryArea);
            if (retryBits.triedToDecrementInvokeCount == 0) {
                retryBits.triedToDecrementInvokeCount = 1;

                AngelClientDataBindCount_t oldCount, newCount;
                memcpy(&oldCount, &(recovery_p->drm_bindDataToDecrementInvoke_p->bindCount), sizeof(oldCount));
                for (int csRC = -1; csRC != 0;) {
                    memcpy(&newCount, &oldCount, sizeof(newCount));
                    newCount.invokeCount = newCount.invokeCount - 1;
                    csRC = __cds1(&oldCount, &(recovery_p->drm_bindDataToDecrementInvoke_p->bindCount), &newCount);
                }

                unsigned char doUnbind = ((newCount.invokeCount == 0) && (newCount.bindCount == 0));

                if (doUnbind == TRUE) {
                    recovery_p->drm_bindData_p = recovery_p->drm_bindDataToDecrementInvoke_p;
                    recovery_p->drm_bindData_sgoo_p = recovery_p->drm_bindData_p->clientProcessData_p->sgoo_p;
                    recovery_p->drm_removeBindDataFromApd = TRUE;
                    recovery_p->drm_apdToDecrementBindCount_p = recovery_p->drm_bindDataToDecrementInvoke_p->apd_p;
                    recovery_p->drm_callScfmUninitialize = TRUE;

                    // -----------------------------------------------------------
                    // For our purposes, the lock was held on entry.  We would
                    // have acquired it if the invoke count had been decremented
                    // to zero.
                    // -----------------------------------------------------------
                    recovery_p->shr_heldBindEnqOnEntry = 1;
                    recovery_p->drm_removeBindDataFromAcpd = TRUE;
                }

                retryBits.decrementedInvokeCount = 1;
            }
        }

        // -------------------------------------------------------------------
        // The rest of our recovery assumes we held the bind ENQ on entry to
        // the ARR.  If we did not hold the ENQ, we can't be sure that the
        // state of things did not change part way through our cleanup.
        // -------------------------------------------------------------------
        if (recovery_p->shr_heldBindEnqOnEntry != 0) {

            // ---------------------------------------------------------------
            // Decrement the bind count in the bind data.
            // ---------------------------------------------------------------
            if (recovery_p->drm_decrementBindCountInBindData != 0) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToDecrementBindCount == 0) {
                    retryBits.triedToDecrementBindCount = 1;
                    AngelClientBindData_t* bindData_p = recovery_p->drm_bindDataToBindTo_p;
                    AngelClientDataBindCount_t oldCount, newCount;
                    memcpy(&oldCount, &(bindData_p->bindCount), sizeof(oldCount));
                    for (int csRC = -1; csRC != 0; ) {
                        memcpy(&newCount, &oldCount, sizeof(newCount));
                        newCount.bindCount = newCount.bindCount - 1;
                        csRC = __cds1(&oldCount, &(bindData_p->bindCount), &newCount);
                    }
                    retryBits.decrementedBindCount = 1;
                }
            }

            // ---------------------------------------------------------------
            // Return the bind token to the pool of bind tokens.
            // ---------------------------------------------------------------
            if (recovery_p->drm_bindToken_p != NULL) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToReturnBindToken == 0) {
                    retryBits.triedToReturnBindToken = 1;
                    AngelClientBindData_t* bindData_p = recovery_p->drm_bindDataToBindTo_p;
                    freeCellPoolCell(bindData_p->bindTokenCellPool, recovery_p->drm_bindToken_p);
                    retryBits.returnedBindToken = 1;
                }
            }

            // ---------------------------------------------------------------
            // We're going to (potentially) get the angel process data bind list
            // ENQ to remove bind data from the list.  So if we already hold the
            // ENQ, drop it.
            // ---------------------------------------------------------------
            enqtoken nullEnqToken;
            memset(&nullEnqToken, 0, sizeof(enqtoken));
            if (memcmp(&nullEnqToken, &(recovery_p->apdBindDataListEnqToken), sizeof(enqtoken)) != 0) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToDropAPDBindDataListENQ == 0) {
                    retryBits.triedToDropAPDBindDataListENQ = 1;
                    release_enq(&(recovery_p->apdBindDataListEnqToken));
                    memset(&(recovery_p->apdBindDataListEnqToken), 0, sizeof(enqtoken));
                    retryBits.droppedAPDBindDataListENQ = 1;
                }
            }

            // ---------------------------------------------------------------
            // Remove the bind data from the list of binds in the angel process
            // data.
            // ---------------------------------------------------------------
            if (recovery_p->drm_removeBindDataFromApd == TRUE) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToRemoveBindDataFromApd == 0) {
                    retryBits.triedToRemoveBindDataFromApd = 1;
                    AngelClientBindData_t* bindData_p = recovery_p->drm_bindData_p;
                    unsigned int removeRC = removeBindFromProcessData(bindData_p, recovery_p);
                    // -------------------------------------------------------
                    // If the server is ending, it may have already decremented
                    // the bind count for us.
                    // -------------------------------------------------------
                    if (removeRC != 0){
                        recovery_p->drm_apdToDecrementBindCount_p = NULL;
                        recovery_p->drm_callScfmUninitialize = FALSE;
                    }
                    retryBits.removedBindDataFromApd = 1;
                }
            }

            // ---------------------------------------------------------------
            // Call uninitialize on the SCFM if we need to do that.  We assume
            // that we're in a state where the SCFM will not go away, because
            // we removed our bind data successfully from the APD, but did not
            // decrement the APD bind count.
            // ---------------------------------------------------------------
            if (recovery_p->drm_callScfmUninitialize == TRUE) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToCallScfmUninitialize == 0) {
                    retryBits.triedToCallScfmUninitialize = 1;
                    AngelClientBindData_t* bindData_p = recovery_p->drm_bindData_p;
                    if (bindData_p->scfmCopy_p->process_cleanup_routine_ptr != NULL) {
                        void* cenv_p = getenvfromR12();
                        setenvintoR12(NULL);
                        bindData_p->scfmCopy_p->process_cleanup_routine_ptr(bindData_p->clientToken);
                        setenvintoR12(cenv_p);
                    }
                    retryBits.calledScfmUninitialize = 1;
                }
            }

            // ---------------------------------------------------------------
            // Decrement the bind count in the angel process data if we
            // incremented it previously.
            // ---------------------------------------------------------------
            if (recovery_p->drm_apdToDecrementBindCount_p != NULL) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToDecrementBindCountAPD == 0) {
                    retryBits.triedToDecrementBindCountAPD = 1;
                    AngelClientBindData_t* bindData_p = recovery_p->drm_bindData_p;
                    decrementBindCount(recovery_p->drm_apdToDecrementBindCount_p, &(bindData_p->serverStoken));
                    retryBits.decrementedBindCountAPD = 1;
                }
            }

            // ---------------------------------------------------------------
            // Remove the bind from the client's list of binds.
            // ---------------------------------------------------------------
            if (recovery_p->drm_removeBindDataFromAcpd == TRUE) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToRemoveBindDataFromACPD == 0) {
                    retryBits.triedToRemoveBindDataFromACPD = 1;
                    removeBindFromClientProcessData(recovery_p->drm_bindData_p);
                    retryBits.removedBindDataFromACPD = 1;
                }
            }

            // ---------------------------------------------------------------
            // Destroy the angel client process data.
            // ---------------------------------------------------------------
            if (recovery_p->drm_clientProcessData_p != NULL) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToDestroyClientRESMGRs == 0) {
                    retryBits.triedToDestroyClientRESMGRs = 1;
                    destroyClientRESMGRs(recovery_p->drm_clientProcessData_p);
                    retryBits.destroyedClientRESMGRs = 1;
                }
                if (retryBits.triedToFreeClientProcessData == 0) {
                    retryBits.triedToFreeClientProcessData = 1;
                    destroyAngelClientProcessData(recovery_p->drm_clientProcessData_p, recovery_p);
                    retryBits.freedClientProcessData = 1;
                }
            }

            // ---------------------------------------------------------------
            // Destroy the client bind data.
            // ---------------------------------------------------------------
            if (recovery_p->drm_bindData_p != NULL) {
                SET_RETRY_POINT(angelRetryArea);
                if (retryBits.triedToFreeClientBindData == 0) {
                    retryBits.triedToFreeClientBindData = 1;
                    bbgzsgoo* sgoo_p = (bbgzsgoo*) recovery_p->drm_bindData_sgoo_p;
                    destroyBindData(recovery_p->drm_bindData_p, sgoo_p, recovery_p);
                    retryBits.freedClientBindData = 1;
                }
            }
        }

        // -----------------------------------------------------------
        // Cancel the ESTAE
        // -----------------------------------------------------------
        remove_estaex(&estaexRC, &estaexRSN);
    }
}

// RESMGR
void dynamicReplaceableClientRESMGR(rmpl* rmpl_p, AngelClientProcessData_t* acpd_p) {
    // -----------------------------------------------------------------------
    // The client RESMGR is driven when the main task ends (IPT or JOBSTEP
    // depending on client type) and when the address space ends.  The cleanup
    // is similar for both cases, with the notable exception being that the
    // private heap is cleaned up when the main task ends, but not when the
    // address space ends (because the private storage is gone by then).
    //
    // Make sure that we have something to clean up.
    // -----------------------------------------------------------------------
    if (acpd_p == NULL) {
        return;
    }

    unsigned char mainTaskCleanup = (((rmpl_p->rmplflg1) & rmplterm) != rmplterm);

    // -----------------------------------------------------------------------
    // Clean up the binds to all servers.  There can't be anyone using the
    // binds anymore so the in-use count checks are not necessary.  We'll get
    // the bind lock for the main task because we're still running in the
    // client address space.
    // -----------------------------------------------------------------------
    enqtoken bindEnqToken;
    if (mainTaskCleanup == TRUE) {
        get_enq_exclusive_step(BBGZ_ENQ_QNAME, CLIENT_BIND_ENQ_RNAME, &bindEnqToken);
    }

    angel_client_pc_recovery fakeRecoveryArea;
    memset(&fakeRecoveryArea, 0, sizeof(fakeRecoveryArea));
    AngelClientBindDataNode_t* curBindDataNode_p = acpd_p->bindHead_p;
    while (curBindDataNode_p != NULL) {
        acpd_p->bindHead_p = curBindDataNode_p->next_p;
        AngelClientBindData_t* curBindData_p = curBindDataNode_p->data_p;
        freeCellPoolCell(acpd_p->sgoo_p->bbgzsgoo_clientBindDataNodePool, curBindDataNode_p);

        if (removeBindFromProcessData(curBindData_p, &fakeRecoveryArea) == 0) {
            if ((mainTaskCleanup == TRUE) && (curBindData_p->scfmCopy_p->process_cleanup_routine_ptr != NULL)) {
                void* cenv_p = getenvfromR12();
                setenvintoR12(NULL);
                curBindData_p->scfmCopy_p->process_cleanup_routine_ptr(curBindData_p->clientToken);
                setenvintoR12(cenv_p);
            }
            decrementBindCount(curBindData_p->apd_p, &(curBindData_p->serverStoken));
        }

        // -------------------------------------------------------------------
        // Only allow storage to be deleted if we're cleaning up for the main
        // task.  The heap only exists when the RESMGR runs in the client AS.
        // -------------------------------------------------------------------
        if (mainTaskCleanup != TRUE) {
            curBindData_p->bindTokenCellPool = 0L;
            curBindData_p->scfmCopy_p = NULL;
        }
        destroyBindData(curBindData_p, acpd_p->sgoo_p, &fakeRecoveryArea);

        curBindDataNode_p = acpd_p->bindHead_p;
    }

    // -----------------------------------------------------------------------
    // Clean up the angel client process data.  Only clean up the dynamic
    // area cell pool hung off of the process data if we're cleaning up for
    // the main task.
    // -----------------------------------------------------------------------
    if (mainTaskCleanup != TRUE) {
        acpd_p->clientDynAreaPool = 0L;
    }
    destroyAngelClientProcessData(acpd_p, &fakeRecoveryArea);

    if (mainTaskCleanup == TRUE) {
        release_enq(&bindEnqToken);
    }
}
