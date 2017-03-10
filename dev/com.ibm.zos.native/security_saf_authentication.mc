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
 * Assorted routines that interface with z/OS security products via
 * IRRSIA00 to perform authentication tasks.
 */

#include <stdlib.h>

#include "include/gen/ihaacee.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/security_saf_acee.h"
#include "include/security_saf_authentication.h"
#include "include/security_saf_authorization.h"
#include "include/security_saf_common.h"
#include "include/util_registry.h"

//---------------------------------------------------------------------
// RAS related constants
//---------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_AUTHENTICATION

//---------------------------------------------------------------------
// Error codes.
//---------------------------------------------------------------------
#define EXTRACT_UTOKEN_NULL_SAF_CREDENTIAL_TOKEN_PARM (RAS_MODULE_SECURITY_SAF_AUTHENTICATION + 1)
#define EXTRACT_UTOKEN_NULL_UTOKEN_PARM               (RAS_MODULE_SECURITY_SAF_AUTHENTICATION + 2)
#define EXTRACT_UTOKEN_NULL_RESULT_PARM               (RAS_MODULE_SECURITY_SAF_AUTHENTICATION + 3)

//---------------------------------------------------------------------
// Module scoped helper functions and types
//---------------------------------------------------------------------

/**
 * Allocate a RegistryToken from the native token registry for the RACO referenced 
 * in the given IRRSIA00Parms struct.  The allocated token is copied into the given 
 * RegistryToken parm using memcpy_dk(8), for use by unauthorized callers.
 *
 * The RegistryToken references a RegistryDataArea, which in turn references a RACO_CB, 
 * which in turn references the RACO.
 *
 * Since the RegistryDataArea is in Key 2 fetch-protected storage, users of the 
 * RegistryToken cannot directly access the RACO or even figure out where it is.
 *
 * @param fillMeIn The RegistryToken structure into which the allocated token is copied.
 *                 The token is copied using memcpy_dk(8).
 * @param irrsia00parms A struct containing all of the parameter data for IRRSIA00.
 *
 * @return 0 if the token was successfully created or @c SECURITY_AUTH_RC_OUT_OF_MEMORY
 *           otherwise
 */
int allocTokenFromRegistry(RegistryToken* fillMeIn, IRRSIA00Parms* __ptr32 irrsia00Parms) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    RegistryToken token;
    RegistryDataArea dataArea;
    memset(&dataArea, 0, sizeof(RegistryDataArea));

    // Allocate a RACO_CB in above the bar storage, copy the ENVR_out from initACEE
    // into it, then put a ref to the RACO_CB in the registry element.
    RACO_CB* raco_cb = (RACO_CB*) malloc(sizeof(RACO_CB));
    memcpy(raco_cb, &irrsia00Parms->ENVR_out, sizeof(RACO_CB));
    *((RACO_CB**)&dataArea) = raco_cb;

    // Put the RACO_CB in the registry, for retieval later during authz or other tasks.
    int rc = registryPut(SAFNSC, &dataArea, &token);

    if (rc == 0) {
        memcpy_dk(fillMeIn, &token, sizeof(RegistryToken), 8);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Allocates and initializes an IRRSIA00 parameter structure for creating a
 * password credential.
 *
 * @param parms Contains the parameters needed to create the credential.
 * @param rc    An output parameter that will contain the return code of this function.
 *
 * @return An IRRSIA00 parameter structure that is properly initialized for creating a
 *         password credential, or NULL if there was a problem.
 */
IRRSIA00Parms* allocatePasswordCredentialParms(CreatePasswordCredentialParms* parms, int* rc) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(7),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int localrc = 0;
    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();

    if (irrsia00Parms == NULL) {
        localrc = SECURITY_AUTH_RC_OUT_OF_MEMORY;
    }
    else if (parms->usernamePtr == NULL || parms->usernameLen == 0) {
        localrc = SECURITY_AUTH_RC_INVALID_USERNAME_LENGTH;
    }
    else if (parms->passwordPtr == NULL || parms->passwordLen == 0) {
        localrc = SECURITY_AUTH_RC_INVALID_PASSWORD_LENGTH;
    }
    else {
        localrc = populateCommonIRRSIA00Parms(irrsia00Parms,
                                              parms->usernamePtr,
                                              parms->usernameLen,
                                              parms->passwordPtr,
                                              parms->passwordLen,
                                              parms->auditStringPtr,
                                              parms->auditStringLen,
                                              parms->applNamePtr,
                                              parms->applNameLen,
                                              NULL,
                                              NULL);
    }

    if (localrc != 0 && irrsia00Parms != NULL) {
        free(irrsia00Parms);
        irrsia00Parms = NULL;
    }

    *rc = localrc;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(localrc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return irrsia00Parms;
}

/**
 * Allocates and initializes an IRRSIA00 parameter structure for creating an
 * asserted credential.
 *
 * @param parms Contains the parameters needed to create the credential.
 * @param rc    An output parameter that will contain the return code of this function.
 *
 * @return An IRRSIA00 parameter structure that is properly initialized for creating an
 *         asserted credential, or NULL if there was a problem.
 */
IRRSIA00Parms* allocateAssertedCredentialParms(CreateAssertedCredentialParms* parms, int* rc) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int localrc = 0;
    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();

    if (irrsia00Parms == NULL) {
        localrc = SECURITY_AUTH_RC_OUT_OF_MEMORY;
    }
    else if (parms->usernamePtr == NULL || parms->usernameLen == 0) {
        localrc = SECURITY_AUTH_RC_INVALID_USERNAME_LENGTH;
    }
    else {
        localrc = populateCommonIRRSIA00Parms(irrsia00Parms,
                                              parms->usernamePtr,
                                              parms->usernameLen,
                                              NULL,
                                              0,
                                              parms->auditStringPtr,
                                              parms->auditStringLen,
                                              parms->applNamePtr,
                                              parms->applNameLen,
                                              NULL,
                                              NULL);
    }

    if (localrc != 0 && irrsia00Parms != NULL) {
        free(irrsia00Parms);
        irrsia00Parms = NULL;
    }

    *rc = localrc;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(10),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(localrc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return irrsia00Parms;
}

/**
 * Allocates and initializes an IRRSIA00 parameter structure for creating a
 * certificate credential.
 *
 * @param parms Contains the parameters needed to create the credential.
 * @param rc    An output parameter that will contain the return code of this function.
 *
 * @return An IRRSIA00 parameter structure that is properly initialized for creating a
 *         certificate credential, or NULL if there was a problem.
 */
IRRSIA00Parms* allocateCertificateCredentialParms(CreateCertificateCredentialParms* parms, int* rc) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int localrc = 0;
    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();
    int certificateLength = parms->certificatePtr == NULL ? 0 : parms->certificateLen;

    if (irrsia00Parms == NULL) {
        localrc = SECURITY_AUTH_RC_OUT_OF_MEMORY;
    }
    else if ((irrsia00Parms->Certificate_ptr = __malloc31(sizeof(int) + certificateLength)) == NULL) {
        localrc = SECURITY_AUTH_RC_OUT_OF_MEMORY;
    }
    else {
        memcpy(irrsia00Parms->Certificate_ptr, &certificateLength, sizeof(int));
        memcpy_sk((irrsia00Parms->Certificate_ptr + sizeof(int)), parms->certificatePtr, certificateLength, 8);
        localrc = populateCommonIRRSIA00Parms(irrsia00Parms,
                                              NULL,
                                              0,
                                              NULL,
                                              0,
                                              parms->auditStringPtr,
                                              parms->auditStringLen,
                                              parms->applNamePtr,
                                              parms->applNameLen,
                                              NULL,
                                              NULL);
    }

    if (localrc != 0 && irrsia00Parms != NULL) {
        if (irrsia00Parms->Certificate_ptr != NULL) {
            free(irrsia00Parms->Certificate_ptr);
            irrsia00Parms->Certificate_ptr = NULL;
        }
        free(irrsia00Parms);
        irrsia00Parms = NULL;
    }

    *rc = localrc;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(12),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(localrc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return irrsia00Parms;
}

/**
 * Creates a native security credential of any type, given an already initialized
 * parameter list for the desired credential type.
 *
 * @param irrsia00Parms     An IRRSIA00 parameter structure that contains whatever parameters are needed
 *                            for the desired credential type.
 * @param fillMeIn          An output parameter that will contain a token representing the underlying native
 *                            credential.
 * @param safServiceResults An output parameter that will contain several return codes relating to the
 *                            creation of the credential.
 * @param deleteTheAcee     An boolean input parameter that indicates whether or not to delete the ACEE.
 *
 * @return 0 if all is well; non-zero otherwise. safServiceResults contains SAF RCs.
 */
int createCredentialCommon(IRRSIA00Parms* __ptr32 irrsia00Parms, 
                            RegistryToken* fillMeIn, 
                            SAFServiceResult* safServiceResult,
                            int deleteTheAcee ) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(13),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_INT(deleteTheAcee, "deleteTheAcee"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0; // The rc is passed back via the SAFServiceResult.

    SAFServiceResult tmpSafResults;
    memset(&tmpSafResults, 0, sizeof(SAFServiceResult));

    // Create the RACO (and the ACEE too... which we'll delete later).
    createACEEAndRACO(irrsia00Parms, &tmpSafResults);

    if (tmpSafResults.wasReturnCode == 0 && tmpSafResults.safReturnCode == 0) {
        // Success!  Now create the double-walled protective wrapper around the RACO
        // (RegistryToken -> RegistryElement -> RACO).
        rc = allocTokenFromRegistry(fillMeIn, irrsia00Parms);

        // We don't want the actual ACEE; we just want the RACO.  Turn around and use the
        // same parm data struct ACEE because RACF set the ACEE pointer into the appropriate
        // output parm during the first invocation
        if (deleteTheAcee) {
            deleteACEE(irrsia00Parms, NULL);     // TODO: what about this SAFServiceResult?
        }
    } else {
        // Fail! Set a non-zero failure rc.
        rc = SECURITY_AUTH_RC_IRRSIA00_FAILED; 
    }

    // Copy back the SAF and RACF return and reason codes.
    // The wasReturnCode may have already been set. If it hasn't, set it to rc.
    if (tmpSafResults.wasReturnCode == 0) {
        tmpSafResults.wasReturnCode = rc;
    }
    memcpy_dk(safServiceResult, &tmpSafResults, sizeof(SAFServiceResult), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(14),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult), &tmpSafResults, "SAF results"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

//---------------------------------------------------------------------
// Externally exposed functions.
//---------------------------------------------------------------------
/*
 * Documented in security_saf_authentication.h.
 */
void createPasswordCredential(CreatePasswordCredentialParms* parms) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(15),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocatePasswordCredentialParms(parms, &rc);
    if (irrsia00Parms != NULL) {
        createCredentialCommon(irrsia00Parms,
                               parms->outputToken,
                               parms->safServiceResult,
                               1);
        free(irrsia00Parms);
    } else {
        // Copy back the failure rc.
        memcpy_dk(&parms->safServiceResult->wasReturnCode, &rc, sizeof(int), 8);  
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(16),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }
}

/*
 * Documented in security_saf_authentication.h. 
 */
void createAssertedCredential(CreateAssertedCredentialParms* parms) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(17),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateAssertedCredentialParms(parms, &rc);
    if (irrsia00Parms != NULL) {
        createCredentialCommon(irrsia00Parms,
                               parms->outputToken,
                               parms->safServiceResult,
                               1);
        free(irrsia00Parms);
    } else {
        // Copy back the failure rc.
        memcpy_dk(&parms->safServiceResult->wasReturnCode, &rc, sizeof(int), 8);  
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(18),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }
}

/*
 * Documented in security_saf_authentication.h.
 */
void createCertificateCredential(CreateCertificateCredentialParms* parms) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(19),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateCertificateCredentialParms(parms, &rc);

    if (irrsia00Parms != NULL) {
        rc = createCredentialCommon(irrsia00Parms,
                                    parms->outputToken,
                                    parms->safServiceResult,
                                    0);  // Don't delete the ACEE - we need it to retrieve the userId.

        if (rc == 0) {
            // Copy back the userId from the ACEE
            acee* ACEE = (acee*) irrsia00Parms->ACEE_ptr;
            int usernameLength = ACEE->aceeuser._aceeusrl;
            memcpy_dk(parms->outputUsernamePtr, ACEE->aceeuser._aceeusri, usernameLength, 8);

            // Now safe to delete the ACEE.
            deleteACEE(irrsia00Parms, NULL);     // TODO: what about this SAFServiceResult?
        }

        // Delete the certificate.
        free(irrsia00Parms->Certificate_ptr);
        irrsia00Parms->Certificate_ptr = (void* __ptr32) &(irrsia00Parms->zero);
        free(irrsia00Parms);

    } else {
        // Copy back the failure rc.
        memcpy_dk(&parms->safServiceResult->wasReturnCode, &rc, sizeof(int), 8);  
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(20),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * The DeleteCredentialParms passed into deleteCredential are copied from
 * key 8 into this struct, which is in key 2.
 */
typedef struct {
    RegistryToken  inputToken; //!< Input  - Pointer to the NSCToken associated with the native credential to delete.
    int            returnCode; //!< Output - The return code from the @c deleteCredential routine.
} DeleteCredentialParmsKey2;

/**
 * Validate DeleteCredentialParms, then copy them into key 2 storage for use by the
 * metal C routine.  memcpy_sk(8) does the copying.
 *
 * @param DeleteCredentialParmsKey2 The parm structure to copy into (in key 2)
 * @param DeleteCredentialParms The parm structure to validate and copy from (in key 8).
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int validateDeleteCredentialParms(DeleteCredentialParmsKey2* key2Parms, DeleteCredentialParms* parms) {
    int rc = 0;
    memset(key2Parms, 0, sizeof(DeleteCredentialParmsKey2));

    if (parms->inputToken != NULL) {
        memcpy_sk(&key2Parms->inputToken, parms->inputToken, sizeof(RegistryToken), 8);
    } else {
        rc = 4;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(25), 
                    "copied parms to key 2",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(DeleteCredentialParmsKey2), key2Parms, "DeleteCredentialParmsKey2"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Cleanup any storage malloc'ed when copying the parms from key 8 to key 2,
 * and copy back to key 8 any output parms.
 *
 * @param DeleteCredentialParmsKey2 The parm structure to clean up and copy back from.
 * @param DeleteCredentialParms The parm structure to copy back into.
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int cleanupDeleteCredentialParms(DeleteCredentialParmsKey2* key2Parms, DeleteCredentialParms* parms) {

    // Copy back the return code.
    memcpy_dk(parms->returnCode, &key2Parms->returnCode, sizeof(int), 8);

    return 0;
}

/*
 * Documented in security_saf_authentication.h.
 */
void deleteCredential(DeleteCredentialParms* parms) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(21),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    DeleteCredentialParmsKey2 key2Parms;

    // Copy the parms into key 2.
    key2Parms.returnCode = validateDeleteCredentialParms(&key2Parms, parms);

    if (key2Parms.returnCode == 0) {
        key2Parms.returnCode = registryFree(&key2Parms.inputToken, FALSE);
    }

    cleanupDeleteCredentialParms(&key2Parms, parms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(22),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(key2Parms.returnCode, "return code"),
                    TRACE_DATA_END_PARMS);
    }
}

/*
 * Documented in security_saf_authentication.h.
 */
void safExtractRealm(SafExtractRealmParms* serp) {
    racf_extract_results extractResults;
    extractResults.length = RACF_EXTRACT_RESULTS_MAX_LENGTH;  // init with max capacity of the data buffer.

    SAFServiceResult safServiceResult;
    memset(&safServiceResult, 0, sizeof(SAFServiceResult));

    int rc = safExtract(&safServiceResult.safResults,
                        NULL,
                        "REALM",
                        "SAFDFLT",
                        "APPLDATA",
                        &extractResults);
    if (!rc) {
        // The EXTRACT call was made.  Copy back the extractResults to the key8 caller.
        safServiceResult.safServiceCode = RACROUTE_EXTRACT; // safe to set this now, since we know it was definitely invoked.
        memcpy_dk(serp->extractResults, &extractResults, sizeof(extractResults.length) + extractResults.length, 8); 
    }

    // Copy back the SAF results to the key8 caller.
    safServiceResult.wasReturnCode = rc;
    memcpy_dk(serp->safServiceResult, &safServiceResult, sizeof(SAFServiceResult), 8);
}

/*
 * Documented in util_registry.h.
 */
void destroySAFNSCDataArea(RegistryDataArea dataArea) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(23),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_RAWDATA(sizeof(RegistryDataArea), &dataArea, "RegistryDataArea"),
                    TRACE_DATA_END_PARMS);
    }

    RACO_CB* raco_cb = *((RACO_CB**)&dataArea);
    deallocateRACO(&raco_cb->ENVR_RACO);  

    free(raco_cb);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(24),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * The IsRESTRICTEDParms passed into isRESTRICTED are copied from
 * key 8 into this struct, which is in key 2.
 */
typedef struct {
    RegistryToken       safCredentialToken;                     //!< Input - A token associated with the native security credential (RACO) to be authorized.
    int                 isRestrictedBit;                        //!< Output - The value of the aceeraui bit (or -1 if an error occurred).
    SAFServiceResult    safServiceResult;                       //!< Output - Contains the SAF return code and RACF return and reason codes.
} IsRESTRICTEDParmsKey2;

/**
 * Validate IsRESTRICTEDParms, then copy them into key 2 storage for use by the
 * metal C routine.  memcpy_sk(8) does the copying.
 *
 * @param IsRESTRICTEDParmsKey2 The parm structure to copy into (in key 2)
 * @param IsRESTRICTEDParms The parm structure to validate and copy from (in key 8).
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int validateIsRESTRICTEDParms(IsRESTRICTEDParmsKey2* key2Parms, IsRESTRICTEDParms* parms) {

    int rc = 0;
    memset(key2Parms, 0, sizeof(IsRESTRICTEDParmsKey2));

    if (parms->safCredentialToken != NULL) {
        memcpy_sk(&key2Parms->safCredentialToken, parms->safCredentialToken, sizeof(RegistryToken), 8);
    } else {
        rc = 4;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(25), 
                    "copied parms to key 2",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(IsRESTRICTEDParmsKey2), key2Parms, "IsRESTRICTEDParmsKey2"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Cleanup any storage malloc'ed when copying the parms from key 8 to key 2,
 * and copy back to key 8 any output parms.
 *
 * @param IsRESTRICTEDParmsKey2 The parm structure to clean up and copy back from.
 * @param IsRESTRICTEDParms The parm structure to copy back into.
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int cleanupIsRESTRICTEDParms(IsRESTRICTEDParmsKey2* key2Parms, IsRESTRICTEDParms* parms) {

    // Copy back the isRestrictedBit
    memcpy_dk(parms->isRestrictedBit, &key2Parms->isRestrictedBit, sizeof(int), 8);  

    // Copy back the SAF results.
    memcpy_dk(parms->safServiceResult, &key2Parms->safServiceResult, sizeof(SAFServiceResult), 8);  

    return 0;
}

/**
 * See security_saf_authentication.h for method description.
 */
void isRESTRICTED(IsRESTRICTEDParms* parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(26), 
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_PTR(parms, "IsRESTRICTEDParms ptr"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    RegistryDataArea regDataArea;
    IsRESTRICTEDParmsKey2 key2Parms;

    // Copy the parms into key 2.
    rc = validateIsRESTRICTEDParms(&key2Parms, parms);
    if (rc != 0) {
        // Set the failure rc and exit.
        key2Parms.safServiceResult.wasReturnCode = rc;
        cleanupIsRESTRICTEDParms(&key2Parms, parms);
        return;     
    }

    // Retrieve SAF NSC data (RACO_CB) from the registry.
    rc = registryGetAndSetUsed(&key2Parms.safCredentialToken, &regDataArea);
    if (rc != 0) {
        // Set the failure rc and exit.
        key2Parms.safServiceResult.wasReturnCode = rc;
        cleanupIsRESTRICTEDParms(&key2Parms, parms);
        return;     
    }

    // Get the RACO out of the RegistryDataArea.  
    RACO_CB* raco_cb = *((RACO_CB**)&regDataArea);
    RACO* theRaco = &raco_cb->ENVR_RACO;

    // Get the ACEE using the RACO. 
    acee* __ptr32 myAcee = (acee* __ptr32) createACEEFromRACO(theRaco, &key2Parms.safServiceResult);

    // Return token to registry.  We needed it only to retrieve the ACEE.
    registrySetUnused(&key2Parms.safCredentialToken, TRUE);

    if (rc == 0) {
        // All is well. Check the RESTRICTED bit.
        key2Parms.isRestrictedBit = ((myAcee->aceeflg6 & aceeraui) != 0);
    }

    // Copy back the SAF results.
    key2Parms.safServiceResult.wasReturnCode = rc;
    cleanupIsRESTRICTEDParms(&key2Parms, parms);

    if (myAcee != NULL) {
        deleteACEEObject(myAcee, NULL); 
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(27), 
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(key2Parms.isRestrictedBit, "isRestrictedBit"),
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),&key2Parms.safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * The ExtractUtokenParms passed into extractUtoken are copied from
 * key 8 into this struct, which is in key 2.
 */
typedef struct {
    RegistryToken       safCredentialToken;                     //!< Input - A token associated with the native security credential (RACO) to be authorized.
    ExtractedUtoken     safExtractedUtoken;                     //!< Output - The extracted UTOKEN.
    SAFServiceResult    safServiceResult;                       //!< Output - Contains the SAF return code and RACF return and reason codes.
} ExtractUtokenParmsKey2;

/**
 * Validate ExtractUtokenParms, then copy them into key 2 storage for use by the
 * metal C routine.  memcpy_sk(8) does the copying.
 *
 * @param key2Parms The parm structure to copy into (in key 2)
 * @param parms The parm structure to validate and copy from (in key 8).
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int validateExtractUtokenParms(ExtractUtokenParmsKey2* key2Parms, ExtractUtokenParms* parms) {

    int rc = 0;
    memset(key2Parms, 0, sizeof(ExtractUtokenParmsKey2));

    if (parms->safCredentialToken != NULL) {
        memcpy_sk(&key2Parms->safCredentialToken, parms->safCredentialToken, sizeof(RegistryToken), 8);
    } else {
        rc = EXTRACT_UTOKEN_NULL_SAF_CREDENTIAL_TOKEN_PARM;
    }

    if (parms->safExtractedUtoken == NULL) {
        rc = EXTRACT_UTOKEN_NULL_UTOKEN_PARM;
    }

    if (parms->safServiceResult == NULL) {
        rc = EXTRACT_UTOKEN_NULL_RESULT_PARM;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(28),
                    "copied parms to key 2",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(ExtractUtokenParmsKey2), key2Parms, "ExtractUtokenParmsKey2"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Cleanup any storage malloc'ed when copying the parms from key 8 to key 2,
 * and copy back to key 8 any output parms.
 *
 * @param key2Parms The parm structure to clean up and copy back from.
 * @param parms The parm structure to copy back into.
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int cleanupExtractUtokenParms(ExtractUtokenParmsKey2* key2Parms, ExtractUtokenParms* parms) {

    // Copy back the UTOKEN
    memcpy_dk(parms->safExtractedUtoken, &key2Parms->safExtractedUtoken, sizeof(ExtractedUtoken), 8);

    // Copy back the SAF results.
    memcpy_dk(parms->safServiceResult, &key2Parms->safServiceResult, sizeof(SAFServiceResult), 8);

    return 0;
}
/**
 * See security_saf_authentication.h for method description.
 */
void extractUtoken(ExtractUtokenParms* parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(29),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_PTR(parms, "ExtractUtokenParms ptr"),
                    TRACE_DATA_END_PARMS);
    }

    RegistryDataArea       regDataArea;
    ExtractUtokenParmsKey2 key2Parms;

    // Copy the parms into key 2.
    key2Parms.safServiceResult.wasReturnCode = validateExtractUtokenParms(&key2Parms, parms);
    if (key2Parms.safServiceResult.wasReturnCode != 0) {
        cleanupExtractUtokenParms(&key2Parms, parms);
        return;
    }

    // Retrieve SAF NSC data (RACO_CB) from the registry.
    key2Parms.safServiceResult.wasReturnCode = registryGetAndSetUsed(&key2Parms.safCredentialToken, &regDataArea);
    if (key2Parms.safServiceResult.wasReturnCode != 0) {
        cleanupExtractUtokenParms(&key2Parms, parms);
        return;
    }

    // Get the RACO out of the RegistryDataArea.
    RACO_CB* raco_cb = *((RACO_CB**)&regDataArea);
    RACO* theRaco = &raco_cb->ENVR_RACO;

    // Get the ACEE using the RACO.
    acee* __ptr32 myAcee_p = (acee* __ptr32) createACEEFromRACO(theRaco, &key2Parms.safServiceResult);

    // Return token to registry.  We needed it only to retrieve the ACEE.
    registrySetUnused(&key2Parms.safCredentialToken, TRUE);

    if (myAcee_p != NULL) {
        // get utoken
        key2Parms.safServiceResult.wasReturnCode = safUtokenExtract(&(key2Parms.safServiceResult.safResults), myAcee_p, &key2Parms.safExtractedUtoken);
        if (key2Parms.safServiceResult.wasReturnCode != 0) {
            // Set service
            key2Parms.safServiceResult.safServiceCode = RACROUTE_TOKENXTR;
        }
        deleteACEEObject(myAcee_p, NULL);
    }

    // Copy back the SAF results.
    cleanupExtractUtokenParms(&key2Parms, parms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(30),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_RAWDATA(sizeof(ExtractedUtoken),&key2Parms.safExtractedUtoken,"safExtractedUtoken"),
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),&key2Parms.safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Allocates and initializes an IRRSIA00 parameter structure for creating a
 * mapped identity credential.
 *
 * @param parms Contains the parameters needed to create the credential.
 * @param rc    An output parameter that will contain the return code of this function.
 *
 * @return An IRRSIA00 parameter structure that is properly initialized for creating a
 *         mapped identity credential, or NULL if there was a problem.
 */
IRRSIA00Parms* allocateMappedCredentialParms(CreateMappedCredentialParms* parms, int* rc) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(31),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int localrc = 0;
    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();

    unsigned int storageSize = sizeof(idid) + sizeof(ididsec1) + sizeof(idid1usr) + parms->userNameLen + sizeof(idid1reg) + parms->registryNameLen;

    if (irrsia00Parms == NULL) {
        localrc = SECURITY_AUTH_RC_OUT_OF_MEMORY;
    }
    else if ((irrsia00Parms->Distributed_identity_ptr = __malloc31(sizeof(int) + storageSize)) == NULL) {
        localrc = SECURITY_AUTH_RC_OUT_OF_MEMORY;
    }
    else {
        irrsia00Parms->Distributed_identity_ptr_ptr = (char*) &(irrsia00Parms->Distributed_identity_ptr);
        char * char_ptr = (char*) irrsia00Parms->Distributed_identity_ptr;
        memset(char_ptr, 0, storageSize);
        idid* idid_ptr = (idid*) char_ptr;
        char_ptr = char_ptr + sizeof(idid);

        // build distributed identity area
        memcpy(idid_ptr->ididid, "IDID", sizeof(idid_ptr->ididid));
        memset(&(idid_ptr->ididvers), 1, sizeof(idid_ptr->ididvers));
        memset(&(idid_ptr->ididoffn), 5, sizeof(idid_ptr->ididoffn));
        *((unsigned int*)(&(idid_ptr->ididlen))) = storageSize - 2; // subtract 2 because idid1usr and idid1reg each have an extra byte.
        *((unsigned int*)(&(idid_ptr->ididoff1))) = sizeof(idid);

        // build section one
        ididsec1* ididsec1_ptr = (ididsec1*) char_ptr;
        //ididsec1_ptr->idid1nmf =  0=format is undetermined   1=format is straight string   2=format is x.500
        *((unsigned int*)(&(ididsec1_ptr->idid1of1))) = sizeof(ididsec1);
        *((unsigned int*)(&(ididsec1_ptr->idid1of2))) = sizeof(ididsec1) + sizeof(idid1usr) - 1 + parms->userNameLen;

        // build user name section
        char_ptr = char_ptr + sizeof(ididsec1);
        idid1usr* idid1usr_ptr = (idid1usr*) char_ptr;
        *((unsigned short*)(&(idid1usr_ptr->idid1udl))) = parms->userNameLen;
        memcpy_sk(&(idid1usr_ptr->idid1udn), parms->userNamePtr, parms->userNameLen, 8);

        // build registry name section
        char_ptr = char_ptr + sizeof(idid1usr) - 1 + parms->userNameLen;
        idid1reg* idid1reg_ptr = (idid1reg*) char_ptr;
        *((unsigned short*)(&(idid1reg_ptr->idid1rl))) = parms->registryNameLen;
        memcpy_sk(&(idid1reg_ptr->idid1rn), parms->registryNamePtr, parms->registryNameLen, 8);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(32),
                        "Built distributed identity area",
                        TRACE_DATA_FUNCTION,
                        TRACE_DATA_RAWDATA(storageSize, idid_ptr, "Distributed identity area"),
                        TRACE_DATA_END_PARMS);
        }
        localrc = populateCommonIRRSIA00Parms(irrsia00Parms,
                                              NULL, // twas did not set it when IDID area is used
                                              0,
                                              NULL,
                                              0,
                                              parms->auditStringPtr,
                                              parms->auditStringLen,
                                              parms->applNamePtr,
                                              parms->applNameLen,
                                              NULL,
                                              NULL);
    }

    if (localrc != 0 && irrsia00Parms != NULL) {
        if (irrsia00Parms->Distributed_identity_ptr != NULL) {
            free(irrsia00Parms->Distributed_identity_ptr);
            irrsia00Parms->Distributed_identity_ptr = NULL;
        }
        free(irrsia00Parms);
        irrsia00Parms = NULL;
    }

    *rc = localrc;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(33),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(localrc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return irrsia00Parms;
}
/*
 * Documented in security_saf_authentication.h.
 */
void createMappedCredential(CreateMappedCredentialParms* parms) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(34),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateMappedCredentialParms(parms, &rc);
    if (irrsia00Parms != NULL) {
        rc = createCredentialCommon(irrsia00Parms,
                                    parms->outputToken,
                                    parms->safServiceResult,
                                    0);  // Don't delete the ACEE - we need it to retrieve the userId.
        if (rc == 0) {
            // Copy back the userId from the ACEE
            acee* ACEE = (acee*) irrsia00Parms->ACEE_ptr;
            int usernameLength = ACEE->aceeuser._aceeusrl;
            memcpy_dk(parms->outputUserNamePtr, ACEE->aceeuser._aceeusri, usernameLength, 8);
            // Now safe to delete the ACEE.
            deleteACEE(irrsia00Parms, NULL);
        }

        // Delete the certificate.
        free(irrsia00Parms->Distributed_identity_ptr);
        irrsia00Parms->Distributed_identity_ptr = (void* __ptr32) &(irrsia00Parms->zero);
        free(irrsia00Parms);
    } else {
        // Copy back the failure rc.
        memcpy_dk(&parms->safServiceResult->wasReturnCode, &rc, sizeof(int), 8);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(35),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }
}


/**
 * Key2 parm structure used for @c safRacrouteExtract.
 *
 * This struct mirrors SafRacrouteExtractParms (built in key 8),
 * except it allocates storage for strings and objects rather 
 * than just pointers to strings and objects.
 */
typedef struct {
    char                    className[SAF_CLASSNAME_LENGTH + 1];                //!< Input - The CLASS of the resource profile.
    char                    fieldName[SAF_FIELD_LENGTH + 1];                    //!< Input - The field to extract
    SAFServiceResult        safServiceResult;                                   //!< Output - SAF RC/RSN
    racf_extract_results    racfExtractResults;                                 //!< Output - extracted data
    char                    profileName[SAF_PROFILENAME_MAX_LENGTH + 1];        //!< Input - The resource profile name (at the bottom cuz it's big)
} SafRacrouteExtractParmsKey2;

/**
 * @return true (non-zero) if p == NULL; zero otherwise
 */
int saf_isNull( const char * p ) { 
    return (p == NULL); 
}

/**
 * @return true if  x >= lowerBound && x <= upperBound 
 */
int saf_isWithinBounds( int x, int lowerBound, int upperBound ) {
    return ( x >= lowerBound && x <= upperBound );
}

/**
 * @return true if  x < lowerBound || x > upperBound 
 */
int saf_isOutOfBounds( int x, int lowerBound, int upperBound ) {
    return ! saf_isWithinBounds(x, lowerBound, upperBound);
}

/**
 * @return true if the parms are OK, false otherwise.
 */
int safRacrouteExtract_areParmsValid(SafRacrouteExtractParms * parms) {
    return ! ( saf_isNull( parms->className )
                || saf_isOutOfBounds(parms->classNameLen, 1, SAF_CLASSNAME_LENGTH) 
                || saf_isNull( parms->fieldName )
                || saf_isOutOfBounds( parms->fieldNameLen, 1, SAF_FIELD_LENGTH)
                || saf_isNull( parms->profileName )
                || saf_isOutOfBounds( parms->profileNameLen, 1, SAF_PROFILENAME_MAX_LENGTH ) );
}

/**
 *
 * @param key2Parms The parm structure to copy into (in key 2)
 * @param parms     The parm structure to validate and copy from (in key 8).
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int safRacrouteExtract_copyParms(SafRacrouteExtractParmsKey2 * key2Parms, SafRacrouteExtractParms * parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(41), 
                    "entry",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_RAWDATA(sizeof(SafRacrouteExtractParms), parms, "SafRacrouteExtractParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Initialize key2 area
    memset(key2Parms, 0, sizeof(SafRacrouteExtractParmsKey2));
    key2Parms->racfExtractResults.length = RACF_EXTRACT_RESULTS_MAX_LENGTH;  // init with max capacity of the data buffer.

    // Verify the parms..
    if ( ! safRacrouteExtract_areParmsValid( parms ) ) {
        return RAS_MODULE_CONST + 4;
    }

    // Copy parms from key 8.
    memcpy_sk(key2Parms->className, parms->className, parms->classNameLen, 8);
    memcpy_sk(key2Parms->profileName, parms->profileName, parms->profileNameLen, 8);
    memcpy_sk(key2Parms->fieldName, parms->fieldName, parms->fieldNameLen, 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(42), 
                    "exit",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_RAWDATA(sizeof(SafRacrouteExtractParmsKey2), key2Parms, "SafRacrouteExtractParmsKey2"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
 *
 * @param key2Parms The parm structure to clean up and copy back from.
 * @param parms     The parm structure to copy back into.
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int safRacrouteExtract_cleanupParms(SafRacrouteExtractParmsKey2 * key2Parms, SafRacrouteExtractParms * parms) {

    // Copy back the SAF results.
    if (parms->safServiceResult != NULL) {
        memcpy_dk(parms->safServiceResult, &key2Parms->safServiceResult, sizeof(SAFServiceResult), 8);  
    }

    // Copy back extracted data
    if (parms->racfExtractResults != NULL) {
        memcpy_dk(parms->racfExtractResults, &key2Parms->racfExtractResults, sizeof(racf_extract_results), 8);  
    }

    return 0;
}

/**
 * Extract data from a field in a SAF resource profile.
 *
 * Documented in security_saf_authentication.h.
 */
void safRacrouteExtract(SafRacrouteExtractParms * parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(43), 
                    "entry",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_END_PARMS);
    }

    SafRacrouteExtractParmsKey2 key2Parms;

    int rc = safRacrouteExtract_copyParms(&key2Parms, parms);

    if (rc != 0) {
        // Copy failed.  Exit.
        key2Parms.safServiceResult.wasReturnCode = rc;
        safRacrouteExtract_cleanupParms(&key2Parms, parms);
        return;     
    }

    rc = safExtract(&key2Parms.safServiceResult.safResults,
                    NULL,
                    key2Parms.className,
                    key2Parms.profileName,
                    key2Parms.fieldName,
                    &key2Parms.racfExtractResults);

    if (rc == 0) {
        // The EXTRACT call was made.  
        key2Parms.safServiceResult.safServiceCode = RACROUTE_EXTRACT; 
    }
    
    key2Parms.safServiceResult.wasReturnCode = rc;
    safRacrouteExtract_cleanupParms(&key2Parms, parms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(44), 
                    "exit",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(SafRacrouteExtractParmsKey2), &key2Parms, "SafRacrouteExtractParmsKey2"),
                    TRACE_DATA_END_PARMS);
    }
}


