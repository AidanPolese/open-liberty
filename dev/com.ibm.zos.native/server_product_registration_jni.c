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

/**
 * @file
 *
 * JNI code in support of product registration.
 */
#include <jni.h>
#include "include/mvs_psw.h"
#include <stdio.h>


#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/server_product_registration.h"

//-----------------------------------------------------------------------------
// RAS trace constants.
//-----------------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_SERVER_PRODUCT_REGISTRATION_JNI
#define TP_PROD_MGR_JNI_REGISTER_ENTRY                                  1
#define TP_PROD_MGR_JNI_REGISTER_EXIT                                   2
#define TP_PROD_MGR_JNI_VAL_AND_FETCH_NULL_PARMS                        3
#define TP_PROD_MGR_JNI_VAL_AND_FETCH_STRING_PARM_TO_LONG               4
#define TP_PROD_MGR_JNI_DEREGISTER_ENTRY                                5
#define TP_PROD_MGR_JNI_DEREGISTER_BAD_PC_RC                            6
#define TP_PROD_MGR_JNI_DEREGISTER_EXIT                                 7
#define TP_PROD_MGR_JNI_REGISTER_BAD_PC_RC                              8

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(ntv_registerProduct)
JNIEXPORT jint JNICALL
ntv_registerProduct(JNIEnv* env,
                    jclass jobj,
                    jbyteArray jowner,
                    jbyteArray jname,
                    jbyteArray jversion,
                    jbyteArray jid,
                    jbyteArray jqualifier);

#pragma export(ntv_deregisterProduct)
JNIEXPORT jint JNICALL
ntv_deregisterProduct(JNIEnv* env,
                    jclass jobj,
                    jbyteArray jowner,
                    jbyteArray jname,
                    jbyteArray jversion,
                    jbyteArray jid,
                    jbyteArray jqualifier);

//---------------------------------------------------------------------
// JNI native method structure for the ProductRegistration methods
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod productRegistrationMethods[] = {
    { "ntv_registerProduct",
      "([B[B[B[B[B)I",
      (void *) ntv_registerProduct},
    { "ntv_deregisterProduct",
      "([B[B[B[B[B)I",
      (void *) ntv_deregisterProduct}
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the ProductRegistration
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_registration_internal_ProductRegistrationImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_registration_internal_ProductRegistrationImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(productRegistrationMethods) / sizeof(productRegistrationMethods[0]),
    .nativeMethods = productRegistrationMethods
};

JNIEXPORT jint JNICALL
ntv_registerProduct(JNIEnv* env,
                    jclass jobj,
                    jbyteArray jowner,
                    jbyteArray jname,
                    jbyteArray jversion,
                    jbyteArray jid,
                    jbyteArray jqualifier) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_PROD_MGR_JNI_REGISTER_ENTRY),
                    "ntv_registerProduct. Entry",
                    TRACE_DATA_END_PARMS);
    }

    // Be confident
    int rc = 0;

    // Declare local copy of parameter strings to the required sizes
    char owner[16];
    char name[16];
    char version[8];
    char id[8];
    char qualifier[8];

    // Validate and fetch all our parameters...if anything looks bad, punt.  Tracing done in the function.
    if (validateAndFetchString(env, jowner, owner, sizeof(owner), "owner")!=0) {
        return PRODUCT_REGISTRATION_BAD_OWNER;
    }
    if (validateAndFetchString(env, jname, name, sizeof(name), "name")!=0) {
        return PRODUCT_REGISTRATION_BAD_NAME;
    }
    if (validateAndFetchString(env, jversion, version, sizeof(version), "version")!=0) {
        return PRODUCT_REGISTRATION_BAD_VERSION;
    }
    if (validateAndFetchString(env, jid, id, sizeof(id), "id")!=0) {
        return PRODUCT_REGISTRATION_BAD_ID;
    }
    if (validateAndFetchString(env, jqualifier, qualifier, sizeof(qualifier), "qualifier")!=0) {
        return PRODUCT_REGISTRATION_BAD_QUALIFIER;
    }

    // Set parms
    regParms p = {
                .returnCode_p = &rc,
                .owner_p = owner,
                .name_p = name,
                .version_p = version,
                .id_p = id,
                .qualifier_p = qualifier
            };

    // To find the authorized service
    const server_authorized_function_stubs* auth_stubs_p = 0;
    auth_stubs_p = getServerAuthorizedFunctionStubs();

    // To find the unauthorized service
    const server_unauthorized_function_stubs* unauth_stubs_p = 0;
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();


    // Try to run the authorized register function. If the authrorized stubs are not available or the
    // authorized call fails, run the unauthorized register function. (We have to register as something...).
    if (auth_stubs_p != NULL)
    {
        int pc_rc = auth_stubs_p->pc_registerProduct(&p);
        if (pc_rc != 0) {

            if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(TP_PROD_MGR_JNI_REGISTER_BAD_PC_RC),
                                "ntv_registerProduct. Authorized call failed: Bad PC RC.",
                                TRACE_DATA_INT(pc_rc, "Authorized PC RC"),
                                TRACE_DATA_END_PARMS);
                }
        }

    } else if(unauth_stubs_p != NULL) {

        unauth_stubs_p->registerProduct(&p);

    } else {
        return PRODUCT_REGISTRATION_FUNC_NOT_FOUND;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_PROD_MGR_JNI_REGISTER_EXIT),
                    "ntv_registerProduct. Exit",
                    TRACE_DATA_INT(rc, "Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

JNIEXPORT jint JNICALL
ntv_deregisterProduct(JNIEnv* env,
                    jclass jobj,
                    jbyteArray jowner,
                    jbyteArray jname,
                    jbyteArray jversion,
                    jbyteArray jid,
                    jbyteArray jqualifier) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_PROD_MGR_JNI_DEREGISTER_ENTRY),
                    "ntv_deregisterProduct. Entry",
                    TRACE_DATA_END_PARMS);
    }

    // Init return code and declare local copy of parameter strings to the required sizes.
    int rc = 0;
    char owner[16];
    char name[16];
    char version[8];
    char id[8];
    char qualifier[8];

    // To find the actual service
    const server_authorized_function_stubs* auth_stubs_p = 0;

    // Validate and fetch all our parameters...if anything looks bad, punt.  Tracing done in the function.
    if (validateAndFetchString(env, jowner, owner, sizeof(owner), "owner")!=0) {
        return PRODUCT_REGISTRATION_BAD_OWNER;
    }
    if (validateAndFetchString(env, jname, name, sizeof(name), "name")!=0) {
        return PRODUCT_REGISTRATION_BAD_NAME;
    }
    if (validateAndFetchString(env, jversion, version, sizeof(version), "version")!=0) {
        return PRODUCT_REGISTRATION_BAD_VERSION;
    }
    if (validateAndFetchString(env, jid, id, sizeof(id), "id")!=0) {
        return PRODUCT_REGISTRATION_BAD_ID;
    }
    if (validateAndFetchString(env, jqualifier, qualifier, sizeof(qualifier), "qualifier")!=0) {
        return PRODUCT_REGISTRATION_BAD_QUALIFIER;
    }

    // Find the authorized function.
    auth_stubs_p = getServerAuthorizedFunctionStubs();
    int pc_rc = 0;
    if (auth_stubs_p)
    {
        regParms p = {
            .returnCode_p = &rc,
            .owner_p = owner,
            .name_p = name,
            .version_p = version,
            .id_p = id,
            .qualifier_p = qualifier
        };

        // PC to the authorized metal C code to drive the IFASUSAGE service.
        pc_rc = auth_stubs_p->pc_deregisterProduct(&p);
        if (pc_rc != 0) {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_PROD_MGR_JNI_DEREGISTER_BAD_PC_RC),
                            "ntv_deregisterProduct Bad PC RC",
                            TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                            TRACE_DATA_END_PARMS);
            }

            return PRODUCT_REGISTRATION_DEREG_AUTH_PC_FAILURE;
        }

    } else {
        return PRODUCT_REGISTRATION_FUNC_NOT_FOUND;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_PROD_MGR_JNI_DEREGISTER_EXIT),
                    "ntv_deregisterProduct. Exit",
                    TRACE_DATA_INT(rc, "Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Validate and fetch a parameter
 *
 * @param env The Java ENV
 * @param jBA The parameter as a byte array
 * @param nStr Pointer to a local string where we put the copy of the parm
 * @param targetLength required length for the byte array
 * @param parm Name of the parameter (used for tracing only)
 *
 * @return 0 if we're happy, -1 if we're not
 */
int validateAndFetchString(JNIEnv* env, jbyteArray jBA, char* nStr, int targetLength, char* parm) {

    int len;

    // Make sure we have a string
    if (!jBA) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_PROD_MGR_JNI_VAL_AND_FETCH_NULL_PARMS),
                        "validateAndFetchString, null string for parm",
                        TRACE_DATA_STRING("parm", parm),
                        TRACE_DATA_END_PARMS);
        }
        return -1;
    }

    // Make sure it will fit in the storage we have.  Java code should prevent this but let's not
    // randomly overlay storage if we're wrong.
    len = (*env)->GetArrayLength(env, jBA);
    if (len>targetLength) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_PROD_MGR_JNI_VAL_AND_FETCH_STRING_PARM_TO_LONG),
                        "validateAndFetchString, parm string length exceeds maximum",
                        TRACE_DATA_STRING("parm", parm),
                        TRACE_DATA_INT(len, "length"),
                        TRACE_DATA_INT(targetLength, "required length"),
                        TRACE_DATA_END_PARMS);
        }
        return -1;
    }

    //Blank out the target (thus blank-padding strings that are shorter than required length)
    memset(nStr, '\0', targetLength);

    // Fetch the string
    (*env)->GetByteArrayRegion(env, jBA, 0, len, (jbyte*)(nStr));

    return 0;
}
