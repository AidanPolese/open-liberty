/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * PC routines for performing SAF authorization using SAF authorized services
 * (e.g. @c RACROUTE, initACEE).
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/security_saf_acee.h"
#include "include/security_saf_authorization.h"
#include "include/security_saf_authz.h"
#include "include/security_saf_common.h"
#include "include/security_saf_sandbox.h"
#include "include/util_registry.h"

#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_AUTHZ

#define CAP_RESOURCE_AREA_LENGTH 255

/**
 * The CheckAccessParms passed into checkAccess are copied from
 * key 8 into this struct, which is in key 2.
 */
typedef struct {
    RegistryToken       safCredentialToken;                     //!< Input - A token associated with the native security credential (RACO) to be authorized.
    char*               resource;                               //!< Input - The resource profile to be authorized against.
    char                className[SAF_CLASSNAME_LENGTH + 1];    //!< Input - The CLASS of the resource profile.
    char                applName[SAF_APPLNAME_LENGTH + 1];      //!< Input - The application name.
    saf_log_option      logOption;                              //!< Input - The SAF service logging option.
    SAFServiceResult    safServiceResult;                       //!< Output - Contains the SAF return code and RACF return and reason codes.
    char                resourceArea[CAP_RESOURCE_AREA_LENGTH + 1];  //!< Storage area for the resource parm. If resource length > 256, it will placed in disjoint storage.
} CheckAccessParmsKey2;

/**
 * Validate CheckAccessParms, then copy them into key 2 storage for use by the
 * metal C routine.  memcpy_sk(8) does the copying.
 *
 * @param key2Parms The parm structure to copy into (in key 2)
 * @param parms     The parm structure to validate and copy from (in key 8).
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int validateCheckAccessParms(CheckAccessParmsKey2* key2Parms, CheckAccessParms* parms) {

    int rc = 0;
    memset(key2Parms, 0, sizeof(CheckAccessParmsKey2));

    if (parms->safCredentialToken != NULL) {
        memcpy_sk(&key2Parms->safCredentialToken, parms->safCredentialToken, sizeof(RegistryToken), 8);
    } else {
        rc = 4;
    }

    if (rc == 0 && parms->applName != NULL) {
        int applNameLen = parms->applNameLen;
        if (applNameLen > SAF_APPLNAME_LENGTH) {
            rc = 8; 
        } else {
            memcpy_sk(key2Parms->applName, parms->applName, applNameLen, 8);
        }
    } else if (rc == 0) {
        rc = 12;
    }

    if (rc == 0 && parms->className != NULL) {
        int classNameLen = parms->classNameLen;
        if (classNameLen > SAF_CLASSNAME_LENGTH) {
            rc = 16;
        } else {
            memcpy_sk(key2Parms->className, parms->className, classNameLen, 8);
        }
    } else if (rc == 0) {
        rc = 20;
    }

    if (rc == 0 && parms->resource != NULL) {
        int resourceLen = parms->resourceLen;
        if (resourceLen <= CAP_RESOURCE_AREA_LENGTH) {
            key2Parms->resource = key2Parms->resourceArea;
        } else {
            key2Parms->resource = malloc(resourceLen + 1);
        }
        memcpy_sk(key2Parms->resource, parms->resource, resourceLen + 1, 8);    // copy null-term too (in case we malloc'ed).
    } else if (rc == 0) {
        rc = 24;
    }

    if (rc == 0) {
        key2Parms->logOption = parms->logOption;
    }

    // If non-zero rc, OR with RAS_MODULE_CONST to distinguish from other RCs.
    rc = (rc == 0) ? rc : rc | RAS_MODULE_CONST;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1), 
                    "copied parms to key 2",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(CheckAccessParmsKey2), key2Parms, "CheckAccessParmsKey2"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Cleanup any storage malloc'ed when copying the parms from key 8 to key 2,
 * and copy back to key 8 any output parms.
 *
 * @param key2Parms The parm structure to clean up and copy back from.
 * @param parms     The parm structure to copy back into.
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int cleanupCheckAccessParms(CheckAccessParmsKey2* key2Parms, CheckAccessParms* parms) {

    // Clean up the resource if we alloc'ed disjoint storage for it.
    if (key2Parms->resource != key2Parms->resourceArea) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(2), 
                        "freeing resource storage",
                        TRACE_DATA_FUNCTION,
                        TRACE_DATA_RAWDATA(sizeof(CheckAccessParmsKey2), key2Parms, "CheckAccessParmsKey2"),
                        TRACE_DATA_END_PARMS);
        }

        free(key2Parms->resource);
    }

    // Copy back the SAF results.
    memcpy_dk(parms->safServiceResult, &key2Parms->safServiceResult, sizeof(SAFServiceResult), 8);  

    return 0;
}

/**
 * Called by CheckAccess for performing a RACROUTE FASTAUTH check.
 *
 * @param parms     The key 8 parms structure
 * @param key2Parms The key 2 parms structure
 * @param raco_cb   The RACO_CB retrieved from the native registry
 */
int performFastAuthz(CheckAccessParms* parms, CheckAccessParmsKey2* key2Parms, RACO_CB* raco_cb) {

    int rc = checkAuthorizationFast(&key2Parms->safServiceResult.safResults,
                                (unsigned char) parms->msgSuppress,
                                key2Parms->logOption,
                                NULL, // requestor
                                raco_cb,
                                NULL, // acee
                                parms->accessLevel,
                                key2Parms->applName,
                                key2Parms->className,
                                key2Parms->resource);
    if (rc == 0) {

        if (key2Parms->safServiceResult.safReturnCode == 4 
            && key2Parms->safServiceResult.racfReturnCode == 4) {
            // 4/4/x possibly means the class is not RACLISTed.  RACLIST it, then re-run
            // the authorization check.  
            rc = raclist(NULL,      // TODO: what about these saf results?
                         NULL, 
                         CREATE,
                         key2Parms->className);

            if (rc == 0) {
                // RE-Perform the FASTAUTH check.
                rc = checkAuthorizationFast(&key2Parms->safServiceResult.safResults,
                                            (unsigned char) parms->msgSuppress,
                                            key2Parms->logOption,
                                            NULL, // requestor
                                            raco_cb,
                                            NULL, // acee
                                            parms->accessLevel,
                                            key2Parms->applName,
                                            key2Parms->className,
                                            key2Parms->resource);
            }
        }

        if (rc == 0) {
            key2Parms->safServiceResult.safServiceCode = RACROUTE_FASTAUTH; // rc == 0 means RACROUTE was invoked.
        }
    }
    return rc;
}

/**
 * Called by CheckAccess for performing a RACROUTE AUTH check.
 *
 * @param parms     The key 8 parms structure
 * @param key2Parms The key 2 parms structure
 * @param raco_cb   The RACO_CB retrieved from the native registry
 */
int performAuthz(CheckAccessParms* parms, CheckAccessParmsKey2* key2Parms, RACO_CB* raco_cb) {
    // REQUEST=AUTH requires an ACEE.  Retrieve the ACEE using the RACO. 
    RACO* theRaco = &raco_cb->ENVR_RACO;
    acee* __ptr32 myAcee = (acee* __ptr32) createACEEFromRACO(theRaco, &key2Parms->safServiceResult);

    // Technically we *could* return the registry element here, since we only needed 
    // it for retrieving the ACEE.  But the structure of the code makes sense for the 
    // caller to return it.  This s l o w authz path shouldn't be called anyway.  We 
    // usually will be using fast authz.

    // Perform the authorization check of the ACEE against the resource (i.e. entity/profile).
    int rc = checkAuthorization(&key2Parms->safServiceResult.safResults,
                                (unsigned char) parms->msgSuppress,
                                key2Parms->logOption,
                                NULL, // requestor
                                myAcee,
                                parms->accessLevel,
                                key2Parms->applName,
                                key2Parms->className,
                                key2Parms->resource);
    if (rc == 0) {
        key2Parms->safServiceResult.safServiceCode = RACROUTE_AUTH; // rc == 0 means RACROUTE was invoked.
    }

    if (myAcee != NULL) {
        deleteACEEObject(myAcee, NULL); //TODO  what about this SAFServiceResult ??
    }

    return rc;
}

/**
 * See security_saf_authz.h for method description.
 */
void checkAccess(CheckAccessParms* parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3), 
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_PTR(parms, "CheckAccessParms ptr"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;

    RegistryDataArea regDataArea;
    CheckAccessParmsKey2 key2Parms;

    // Copy the parms into key 2.
    rc = validateCheckAccessParms(&key2Parms, parms);
    if (rc != 0) {
        // Set the failure rc and exit.
        key2Parms.safServiceResult.wasReturnCode = rc;
        cleanupCheckAccessParms(&key2Parms, parms);
        return;     
    }

    // Verify the server is authorized to check this class/resource.
    rc = checkPenaltyBox(key2Parms.applName, key2Parms.className, key2Parms.resource);  
    if (rc != 0) {
        // Set the failure rc and exit.
        key2Parms.safServiceResult.wasReturnCode = rc;
        cleanupCheckAccessParms(&key2Parms, parms);
        return;     
    }

    // Retrieve SAF NSC data (RACO_CB) from the registry.
    rc = registryGetAndSetUsed(&key2Parms.safCredentialToken, &regDataArea);
    if (rc != 0) {
        // Set the failure rc and exit.
        key2Parms.safServiceResult.wasReturnCode = rc;
        cleanupCheckAccessParms(&key2Parms, parms);
        return;     
    }

    RACO_CB* raco_cb = *((RACO_CB**)&regDataArea);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4), 
                    "retrieved RegistryDataArea, which contains ref to RACO_CB",
                    TRACE_DATA_RAWDATA(sizeof(RegistryDataArea), &regDataArea, "RegistryDataArea"),
                    TRACE_DATA_END_PARMS);
    }

    if (parms->fastAuth) {
        rc = performFastAuthz(parms, &key2Parms, raco_cb);
    } else {
        rc = performAuthz(parms, &key2Parms, raco_cb);
    }

    // Return token to registry.  
    registrySetUnused(&key2Parms.safCredentialToken, TRUE);

    // Copy back the SAF results.
    key2Parms.safServiceResult.wasReturnCode = rc;
    cleanupCheckAccessParms(&key2Parms, parms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5), 
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_PTR(parms, "parms"),
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),&key2Parms.safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * The IsSAFClassActiveParms passed into isSAFClassActive are copied from
 * key 8 into this struct, which is in key 2.
 */
typedef struct {
    char                className[SAF_CLASSNAME_LENGTH + 1];    //!< Input - The CLASS of the resource profile.
} IsSAFClassActiveParmsKey2;

/**
 * Validate IsSAFClassActiveParms, then copy them into key 2 storage for use by the
 * metal C routine.  memcpy_sk(8) does the copying.
 *
 * @param key2Parms The parm structure to copy into (in key 2)
 * @param parms     The parm structure to validate and copy from (in key 8).
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int validateIsSAFClassActiveParms(IsSAFClassActiveParmsKey2* key2Parms, IsSAFClassActiveParms* parms) {
    int rc = 0;
    memset(key2Parms, 0, sizeof(IsSAFClassActiveParmsKey2));

    if (rc == 0 && parms->className != NULL) {
        int classNameLen = parms->classNameLen;
        if (classNameLen > SAF_CLASSNAME_LENGTH) {
            rc = 12;
        } else {
            memcpy_sk(key2Parms->className, parms->className, classNameLen, 8);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6), 
                    "copied parms to key 2",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * See security_saf_authz.h for method description.
 */
void isSAFClassActive(IsSAFClassActiveParms* parms) {
    IsSAFClassActiveParmsKey2 key2Parms;
    int rc = validateIsSAFClassActiveParms(&key2Parms, parms);
    if (rc == 0) {
        rc = isClassActive(key2Parms.className, NULL);
    }
    memcpy_dk(parms->rc, &rc, sizeof(int), 8);  
}

