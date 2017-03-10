/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Assorted routines that interface with IRRSIA00 (initACEE) for creating,
 * deleting, and managing ACEEs and RACOs. 
 */

#include <metal.h>
#include <stdlib.h>

#include "include/common_defines.h"
#include "include/common_mc_defines.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/security_saf_acee.h"
#include "include/security_saf_common.h"
#include "include/security_saf_sandbox.h"

//---------------------------------------------------------------------
// RAS related constants
//---------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_ACEE

//---------------------------------------------------------------------
// Module scoped helper functions and types
//---------------------------------------------------------------------

/**
 * See security_saf_acee.h for function description.
 */
IRRSIA00Parms* __ptr32 allocateIRRSIA00Parms(void) {
    IRRSIA00Parms* __ptr32 parms_p = __malloc31(sizeof(IRRSIA00Parms));

    if (parms_p != NULL) {
        memset(parms_p, 0, sizeof(IRRSIA00Parms));

        // link the pointers in the parm list to the storage for the actual parameter data
        parms_p->Work_area_ptr =             (char* __ptr32) &(parms_p->Work_area);
        parms_p->SAF_return_code_ALET_ptr =                  &(parms_p->SAF_return_code_ALET);
        parms_p->SAF_return_code_ptr =                       &(parms_p->SAF_return_code);
        parms_p->RACF_return_code_ALET_ptr =                 &(parms_p->RACF_return_code_ALET);
        parms_p->RACF_return_code_ptr =                      &(parms_p->RACF_return_code);
        parms_p->RACF_reason_code_ALET_ptr =                 &(parms_p->RACF_reason_code_ALET);
        parms_p->RACF_reason_code_ptr =                      &(parms_p->RACF_reason_code);
        parms_p->Function_code_ptr =                         &(parms_p->Function_code);
        parms_p->Attributes_ptr =                            &(parms_p->Attributes);
        parms_p->RACF_userid_ptr =           (char* __ptr32) &(parms_p->RACF_userid);
        parms_p->ACEE_ptr_ptr =     (acee* __ptr32* __ptr32) &(parms_p->ACEE_ptr);
        parms_p->APPL_id_ptr =               (char* __ptr32) &(parms_p->APPL_id);
        parms_p->Password_ptr =              (char* __ptr32) &(parms_p->Password);
        parms_p->Logstring_ptr =             (char* __ptr32) &(parms_p->Logstring);
        parms_p->Certificate_ptr =           (void* __ptr32) &(parms_p->zero);
        parms_p->ENVR_in_ptr =                               &(parms_p->ENVR_in);
        parms_p->ENVR_out_ptr =                              &(parms_p->ENVR_out);
        parms_p->Output_area_ptr =                           &(parms_p->Output_area);
        parms_p->X500name_ptr =                              &(parms_p->X500name);
        parms_p->Variable_list_ptr =         (void* __ptr32) &(parms_p->zero);
        parms_p->Security_label_ptr =        (char* __ptr32) &(parms_p->Security_label);
        parms_p->SERVAUTH_name_ptr =         (char* __ptr32) &(parms_p->SERVAUTH_name);
        parms_p->Password_phrase_ptr =       (char* __ptr32) &(parms_p->Password_phrase);

        // this sets up storage information that RACF uses to allocate storage for a
        // RACO...  by default, IRRSIA00 does not allocate and return a RACO, so callers
        // need to OR on the INTA_ENVR_RET bit of "Attributes" if a RACO is required
        parms_p->ENVR_in.ENVR_object_storage_area_subpool  = RACO_STORAGE_SUBPOOL;
        parms_p->ENVR_in.ENVR_object_storage_area_key      = RACO_STORAGE_KEY;
        parms_p->ENVR_out.ENVR_object_storage_area_subpool = RACO_STORAGE_SUBPOOL;
        parms_p->ENVR_out.ENVR_object_storage_area_key     = RACO_STORAGE_KEY;
    }

    return parms_p;
}

/**
 * Same parms as populateCommonIRRSIA00Parms, along with a sourceKey from
 * which to copy the char * parm data.
 *
 * @param sourceKey - the source key from which to copy the char * data.
 *
 * @return 0 if all went well.  Non-zero if there was an error due to invalid data.
 */
int populateCommonIRRSIA00Parms_sk(IRRSIA00Parms * __ptr32 irrsia00Parms,
                                   char* username,
                                   int usernameLen,
                                   char* password,
                                   int passwordLen,
                                   char* auditString,
                                   int auditStringLen,
                                   char* applName,
                                   int applNameLen,
                                   RACO* inRaco,
                                   acee* __ptr32 inAcee,
                                   int sourceKey) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_STRING(username, "username"),
                    TRACE_DATA_STRING((password == NULL ? "NULL" : "xxxxxxxx"), "password"),
                    TRACE_DATA_STRING(auditString, "auditString"),
                    TRACE_DATA_STRING(applName, "applName"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    // If IRRSIA00Parms wasn't allocated, bail.
    if (irrsia00Parms == NULL) {
        rc = SECURITY_AUTH_RC_OUT_OF_MEMORY;  
    }

    // Validate and copy each parm into the IRRSIA00Parms struct.
    if (rc == 0 && username != NULL) {
        if (usernameLen > 8) {
            rc = SECURITY_AUTH_RC_INVALID_USERNAME_LENGTH;
        } else {
            irrsia00Parms->RACF_userid[0] = (char) usernameLen;
            memcpy_sk(irrsia00Parms->RACF_userid_ptr+1, username, usernameLen, sourceKey);
        }
    }

    if (rc == 0 && password != NULL) {
        if (passwordLen > 8) {
          if (passwordLen > 100) {
            rc = SECURITY_AUTH_RC_INVALID_PASSWORD_LENGTH;
          } else {
              irrsia00Parms->Password_phrase[0] = (char) passwordLen;
              memcpy_sk(irrsia00Parms->Password_phrase_ptr+1, password, passwordLen, sourceKey);
          }
        } else {
            irrsia00Parms->Password[0] = (char) passwordLen;
            memcpy_sk(irrsia00Parms->Password_ptr+1, password, passwordLen, sourceKey);
        }
    }

    if (rc == 0 && auditString != NULL) {
        if (auditStringLen > 255) {
            rc = SECURITY_AUTH_RC_INVALID_AUDIT_STRING_LENGTH;
        } else {
            irrsia00Parms->Logstring[0] = (char) auditStringLen;
            memcpy_sk(irrsia00Parms->Logstring_ptr+1, auditString, auditStringLen, sourceKey);
        }
    }

    if (rc == 0 && applName != NULL) {
        if (applNameLen > 8) {
            rc = SECURITY_AUTH_RC_INVALID_APPLNAME_LENGTH;
        } else {
            irrsia00Parms->APPL_id[0] = (char) applNameLen;
            memcpy_sk(irrsia00Parms->APPL_id_ptr+1, applName, applNameLen, sourceKey);
        }
    }

    if (rc == 0 && inRaco != NULL) {
        irrsia00Parms->ENVR_in.ENVR_object_length = inRaco->length;
        memcpy(&irrsia00Parms->ENVR_in.ENVR_RACO, inRaco, sizeof(RACO));
    }

    if (rc == 0 && inAcee != NULL) {
        irrsia00Parms->ACEE_ptr = inAcee;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Populate an IRRSIA00_parm_data structure with the given data.
 *
 * IRRSIA00 understands and ignores NULL parameters, so callers of this
 * routine will only need to set values for the exact parameters that
 * are required for a given invocation of IRRSIA00.
 *
 * All input parms are validated to ensure they are of proper size and
 * format for IRRSIA00.  The method also checks the IRRSIA00Parms pointer 
 * against NULL, in the event that the caller failed to allocate storage
 * for it.
 *
 * @return 0 if all went well.  Non-zero if there was an error due to invalid data.
 */
int populateCommonIRRSIA00Parms(IRRSIA00Parms * __ptr32 irrsia00Parms,
                                char* username,
                                int usernameLen,
                                char* password,
                                int passwordLen,
                                char* auditString,
                                int auditStringLen,
                                char* applName,
                                int applNameLen,
                                RACO* inRaco,
                                acee* __ptr32 inAcee) {

    return populateCommonIRRSIA00Parms_sk(irrsia00Parms,
                                          username,
                                          usernameLen,
                                          password,
                                          passwordLen,
                                          auditString,
                                          auditStringLen,
                                          applName,
                                          applNameLen,
                                          inRaco,
                                          inAcee,
                                          8);
}

/**
 * Invokes IRRSIA00 in 31-bit mode using embedded assembler.
 *
 * @param parms A struct containing all of the parameter data for the IRRSIA00 invocation.
 * @param logStr This string is included in trace records written by this function.  It
 *               has no other functional purpose. Use it to indicate the function being 
 *               requested or whatever else is relevant.
 * @param safServiceResult The wasReturnCode and the SAF service rc/rsn codes are passed 
 *               back to the caller via this struct.  The wasReturnCode may indicate a 
 *               penalty box failure, in which case the SAF service is not invoked and the
 *               SAF rc/rsn codes are not populated.
 *
 * @return 0 if IRRSIA00 was invoked;  non-zero otherwise. The rc is also stored in 
 *           safServiceResult.wasReturnCode.
 */
int invokeIRRSIA00(IRRSIA00Parms * __ptr32 parms, 
                   const char * logStr, 
                   SAFServiceResult* safServiceResult) {

    if (TraceActive(trc_level_detailed)) {
        // tracing the exact parms could result in a password showing up in the trace,
        // so create a copy of the parm data with a NULL password
        IRRSIA00Parms temp;
        memcpy(&temp, parms, sizeof(IRRSIA00Parms));
        memset(&(temp.Password), 'x', sizeof(temp.Password));
        if ( (int)temp.Password_phrase[0] >0 ) {
          memset(&(temp.Password_phrase), 'x', sizeof(temp.Password_phrase));
        }

        TraceRecord(trc_level_detailed,
                    TP(3),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_STRING(logStr, "Log string"),
                    TRACE_DATA_PTR32(parms, "IRRSIA00Parms ptr"),
                    TRACE_DATA_RAWDATA(sizeof(IRRSIA00Parms), &temp, "IRRSIA00 parameters"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = checkPenaltyBox(&parms->APPL_id_ptr[1], NULL, NULL); // Skip the length byte in the APPL-ID field.  
    if (rc == 0) {
        // Passed the penalty box check.  Invoke initACEE.
        
        int tempphrase = 0;
        if (parms->Distributed_identity_ptr_ptr != NULL) {
            // setting the high bit to 1 tells IRRSIA00 that this is the last parm
            tempphrase = (int) parms->Distributed_identity_ptr_ptr;
            tempphrase |= 0x80000000;
            parms->Distributed_identity_ptr_ptr = (char * __ptr32) tempphrase;
        } else {
            // setting the high bit to 1 tells IRRSIA00 that this is the last parm
            tempphrase = (int) parms->Password_phrase_ptr;
            tempphrase |= 0x80000000;
            parms->Password_phrase_ptr = (char * __ptr32) tempphrase;
        }

        __asm(" LGR 2,13 Save above bar dynamic area\n"
              " LA 13,%0 Load below bar save area\n"
              " L  1,%1  Load parm list\n"
              AMODE_31(" CALL IRRSIA00")
              " LGR 13,2 Restore above bar dynamic area" :
              :
              "r"(parms->savearea), "m"(parms) :
              "r2");
    }
    
    if (safServiceResult != NULL) {
        safServiceResult->wasReturnCode = rc;
        if (rc == 0) {
            // rc == 0 means we invoked initACEE, so copy back the rc/rsn.
            safServiceResult->safServiceCode = (SAFService) parms->Function_code;
            safServiceResult->safReturnCode = parms->SAF_return_code;
            safServiceResult->racfReturnCode = parms->RACF_return_code;
            safServiceResult->racfReasonCode = parms->RACF_reason_code;
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_STRING(logStr, "Log string"),
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_INT(parms->SAF_return_code,  "SAF return code"),
                    TRACE_DATA_INT(parms->RACF_return_code, "RACF return code"),
                    TRACE_DATA_INT(parms->RACF_reason_code, "RACF reason code"),
                    TRACE_DATA_RAWDATA(((safServiceResult != NULL) ? sizeof(SAFServiceResult) : 0), safServiceResult, "SAFServiceResult"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * See security_saf_acee.h for function description. 
 */
acee* createACEE(IRRSIA00Parms* __ptr32 irrsia00Parms, SAFServiceResult* safServiceResult) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    // Invoke IRRSIA00 with function code CREATE.
    irrsia00Parms->Function_code = IRRSIA00_FUNCTION_CREATE;
    invokeIRRSIA00(irrsia00Parms, "createACEE", safServiceResult);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_RAWDATA(sizeof(acee), irrsia00Parms->ACEE_ptr, "the ACEE"),
                    TRACE_DATA_END_PARMS);
    }

    return (acee*) irrsia00Parms->ACEE_ptr;
}

/**
 * See security_saf_acee.h for function description.
 */
int createACEEAndRACO(IRRSIA00Parms* __ptr32 irrsia00Parms, SAFServiceResult* safServiceResult) {

    // The INTA_ENVR_RET flag tells IRRSIA00 to create the RACO.
    irrsia00Parms->Attributes |= INTA_ENVR_RET;
    createACEE(irrsia00Parms, safServiceResult);

    if (TraceActive(trc_level_detailed)) {
        RACO_CB temp;
        memcpy(&temp, irrsia00Parms->ENVR_out_ptr, sizeof(RACO_CB));     // LE APAR OA37620
        TraceRecord(trc_level_detailed,
                    TP(11),
                    "RACO_CB after create",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_RAWDATA(sizeof(RACO_CB), &temp, "ENVR_out data (temp copy)"),
                    TRACE_DATA_END_PARMS);
    }

    return (safServiceResult != NULL) ? safServiceResult->wasReturnCode : 0;
}

/**
 * See security_saf_acee.h for function description.
 */
void deleteACEE(IRRSIA00Parms* __ptr32 irrsia00Parms, SAFServiceResult* safServiceResult) {
    irrsia00Parms->Function_code = IRRSIA00_FUNCTION_DELETE;
    irrsia00Parms->Attributes = 0;

    // Make sure the RACO output area is cleared out, so that we don't 
    // accidentally delete the RACO too.
    memset(&(irrsia00Parms->ENVR_out), 0, sizeof(RACO_CB)); 

    // Make sure the certificate length is zero'ed out, otherwise we'll
    // get a parameter list error (8/8/4)
    char * __ptr32 savedCertificate_ptr = irrsia00Parms->Certificate_ptr ;
    irrsia00Parms->Certificate_ptr = (void* __ptr32) &(irrsia00Parms->zero);

    invokeIRRSIA00(irrsia00Parms, "deleteACEE", safServiceResult);

    // Restore the certificate_ptr.
    irrsia00Parms->Certificate_ptr = savedCertificate_ptr;
}

/**
 * See security_saf_acee.h for function description.
 */
acee* createACEEFromRACO(RACO* theRaco, SAFServiceResult* safServiceResult) {

    acee* __ptr32 theAcee = NULL;
    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();
    int rc = populateCommonIRRSIA00Parms(irrsia00Parms,
                                         NULL,
                                         0,
                                         NULL,
                                         0,
                                         NULL,
                                         0,
                                         NULL,
                                         0,
                                         theRaco,
                                         NULL);

    if (rc == 0) {
        theAcee = (acee* __ptr32) createACEE(irrsia00Parms, safServiceResult);
    } else {
        safServiceResult->wasReturnCode = rc;
    }

    free(irrsia00Parms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(12),
                    "created ACEE from RACO",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_RAWDATA(sizeof(acee),theAcee,"theAcee"),
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }

    return theAcee;
}

/**
 * See security_saf_acee.h for function description.
 */
void deleteACEEObject(acee* __ptr32 theAcee, SAFServiceResult *safServiceResult) {
    
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(13),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_RAWDATA(sizeof(acee), theAcee, "theAcee"),
                    TRACE_DATA_END_PARMS);
    }

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();
    int rc = populateCommonIRRSIA00Parms(irrsia00Parms,
                                         NULL,
                                         0,
                                         NULL,
                                         0,
                                         NULL,
                                         0,
                                         NULL,
                                         0,
                                         NULL,
                                         theAcee);
    if (rc == 0) {
        deleteACEE(irrsia00Parms, safServiceResult);
    } else {
        safServiceResult->wasReturnCode = rc;
    }

    free(irrsia00Parms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(14),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult), safServiceResult, "safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * See security_saf_acee.h for function description.
 */
void deallocateRACO(RACO* theRaco) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_PTR(theRaco, "theRaco (RACO)"),
                    TRACE_DATA_PTR(theRaco->address, "the actual RACO address"),
                    TRACE_DATA_INT(theRaco->length, "the actual RACO length"),
                    TRACE_DATA_END_PARMS);
    }

    storageReleaseTcb(theRaco->address,
                      theRaco->length,
                      RACO_STORAGE_SUBPOOL,
                      RACO_STORAGE_KEY,
                      TCBJSTCB_ADDR);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(10),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_PTR32(TCBJSTCB_ADDR, "TCBJSTCB"),
                    TRACE_DATA_END_PARMS);
    }
}



