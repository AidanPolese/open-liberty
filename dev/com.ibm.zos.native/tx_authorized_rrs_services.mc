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

#include <atrrc.h>
#include <crgc.h>
#include <ctxc.h>
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/common_defines.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/security_saf_authorization.h"
#include "include/tx_authorized_rrs_services.h"

#include "include/gen/cvt.h"
#include "include/gen/ihaecvt.h"

//-----------------------------------------------------------------------------
// Disable the optimizer until the compiler fixes show up
//-----------------------------------------------------------------------------
#ifdef __OPTIMIZE__
#pragma option_override(atr_set_exit_information, "OPT(LEVEL, 0)")
#endif

//-----------------------------------------------------------------------------
// RAS trace constants.
//-----------------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_TX_AUTHORIZED_RRS_SERVICES
#define TP_TX_RRS_AUTH_CRG4GRM_ENTRY                                1
#define TP_TX_RRS_AUTH_CRG4GRM_EXIT                                 2
#define TP_TX_RRS_AUTH_CRG4DRM_ENTRY                                3
#define TP_TX_RRS_AUTH_CRG4DRM_EXIT                                 4
#define TP_TX_RRS_AUTH_CRG4SEIF_ENTRY                               5
#define TP_TX_RRS_AUTH_CRG4SEIF_EXIT                                6
#define TP_TX_RRS_AUTH_ATR4IBRS_ENTRY                               7
#define TP_TX_RRS_AUTH_ATR4IBRS_EXIT                                8
#define TP_TX_RRS_AUTH_ATR4IERS_ENTRY                               9
#define TP_TX_RRS_AUTH_ATR4IERS_EXIT                                10
#define TP_TX_RRS_AUTH_ATR4IRLN_ENTRY                               11
#define TP_TX_RRS_AUTH_ATR4IRLN_EXIT                                12
#define TP_TX_RRS_AUTH_ATR4ISLN_ENTRY                               13
#define TP_TX_RRS_AUTH_ATR4ISLN_EXIT                                14
#define TP_TX_RRS_AUTH_ATR4RWID_ENTRY                               15
#define TP_TX_RRS_AUTH_ATR4RWID_EXIT                                16
#define TP_TX_RRS_AUTH_ATR4SWID_ENTRY                               17
#define TP_TX_RRS_AUTH_ATR4SWID_EXIT                                18
#define TP_TX_RRS_AUTH_ATR4EINT_ENTRY                               19
#define TP_TX_RRS_AUTH_ATR4EINT_EXIT                                20
#define TP_TX_RRS_AUTH_ATR4RUSI_ENTRY                               21
#define TP_TX_RRS_AUTH_ATR4RUSI_EXIT                                22
#define TP_TX_RRS_AUTH_ATR4IRNI_ENTRY                               23
#define TP_TX_RRS_AUTH_ATR4IRNI_EXIT                                24
#define TP_TX_RRS_AUTH_ATR4SENV_ENTRY                               25
#define TP_TX_RRS_AUTH_ATR4SENV_EXIT                                26
#define TP_TX_RRS_AUTH_ATR4APRP_ENTRY                               27
#define TP_TX_RRS_AUTH_ATR4APRP_EXIT                                28
#define TP_TX_RRS_AUTH_ATR4ACMT_ENTRY                               29
#define TP_TX_RRS_AUTH_ATR4ACMT_EXIT                                30
#define TP_TX_RRS_AUTH_ATR4ADCT_ENTRY                               31
#define TP_TX_RRS_AUTH_ATR4ADCT_EXIT                                32
#define TP_TX_RRS_AUTH_ATR4ABAK_ENTRY                               33
#define TP_TX_RRS_AUTH_ATR4ABAK_EXIT                                34
#define TP_TX_RRS_AUTH_ATR4AFGT_ENTRY                               35
#define TP_TX_RRS_AUTH_ATR4AFGT_EXIT                                36
#define TP_TX_RRS_AUTH_ATR4PDUE_ENTRY                               37
#define TP_TX_RRS_AUTH_ATR4PDUE_EXIT                                38
#define TP_TX_RRS_AUTH_ATR4IRRI_ENTRY                               39
#define TP_TX_RRS_AUTH_ATR4IRRI_EXIT                                40
#define TP_TX_RRS_AUTH_ATR4SPID_ENTRY                               41
#define TP_TX_RRS_AUTH_ATR4SPID_EXIT                                42
#define TP_TX_RRS_AUTH_ATR4SSPC_ENTRY                               43
#define TP_TX_RRS_AUTH_ATR4SSPC_EXIT                                44
#define TP_TX_RRS_AUTH_ATR4SUSI_ENTRY                               45
#define TP_TX_RRS_AUTH_ATR4SUSI_EXIT                                46
#define TP_TX_RRS_AUTH_ATR4BEGC_ENTRY                               47
#define TP_TX_RRS_AUTH_ATR4BEGC_EXIT                                48
#define TP_TX_RRS_AUTH_ATR4SWCH_ENTRY                               49
#define TP_TX_RRS_AUTH_ATR4SWCH_EXIT                                50
#define TP_TX_RRS_AUTH_ATR4ENDC_ENTRY                               51
#define TP_TX_RRS_AUTH_ATR4ENDC_EXIT                                52
#define TP_TX_RRS_AUTH_ATRSRV_DELETERM_ENTRY                        53
#define TP_TX_RRS_AUTH_ATRSRV_DELETERM_EXIT                         54
#define TP_TX_RRS_AUTH_ATR4ISLN_BAD_LOG_NAME_LENGTH_INPUT           55
#define TP_TX_RRS_AUTH_ATR4SWID_BAD_XID_LENGTH_INPUT                56
#define TP_TX_RRS_AUTH_ATR4EINT_BAD_XID_LENGTH_INPUT                57
#define TP_TX_RRS_AUTH_ATR4RUSI_BAD_INFO_ID_COUNT_INPUT             58
#define TP_TX_RRS_AUTH_ATR4SENV_BAD_CONTENT_COUNT_INPUT             59
#define TP_TX_RRS_AUTH_ATR4SPID_BAD_PDATA_LENGTH_INPUT              60
#define TP_TX_RRS_AUTH_ATR4SUSI_BAD_ELEMENT_COUNT_INPUT             61
#define TP_TX_RRS_AUTH_BUILD_RMNAME_ENTRY                           62
#define TP_TX_RRS_AUTH_BUILD_RMNAME_EXIT                            63
#define TP_TX_RRS_AUTH_CRG4GRM_BAD_RMNAME_PREFIX_LENGTH_INPUT       64
#define TP_TX_RRS_AUTH_CRG4GRM_FAILED_PROFILE_SEC_CHECK             65
#define TP_TX_RRS_AUTH_CRG4GRM_BAD_STCK_INPUT                       66
#define TP_TX_RRS_AUTH_ATR4SDTA_ENTRY                               67
#define TP_TX_RRS_AUTH_ATR4SDTA_EXIT                                68
#define TP_TX_RRS_AUTH_ATR4SDTA_BAD_METADA_BUFF_LENGTH_INPUT        69
#define TP_TX_RRS_AUTH_ATR4RDTA_ENTRY                               70
#define TP_TX_RRS_AUTH_ATR4RDTA_EXIT                                71
#define TP_TX_RRS_AUTH_ATR4SDTA_USER_NOT_AUTH                       72
#define TP_TX_RRS_AUTH_CRG4SEIF_DEFAULT_TO_NO_METADATA_LOGGING      73
#define TP_TX_RRS_AUTH_IS_META_DATA_ACTIVE_BAD_RRS_RC               74
#define TP_TX_RRS_AUTH_CRG4GRM_UPDATE_REGISRTY_FAILURE              75
#define TP_TX_RRS_AUTH_CRG4GRM_UPDATE_REGISRTY_FAILURE2             76

//-----------------------------------------------------------------------------
// Generic constants. Must in sync with tx_rrs_services_jni.c
//-----------------------------------------------------------------------------
#define MAX_ELEMENT_COUNT                                           32
#define MAX_RMNAME_PREFIX_LENGTH                                     8
#define RMNAME_STCK_LENGTH                                          16

//-----------------------------------------------------------------------------
// Invalid RC constant. It must be in sync with RegisterResMgrReturnType.java
//-----------------------------------------------------------------------------
#define RMPREFIX_FAILED_AUTHORIZATION_CHECK                        -10

//-----------------------------------------------------------------------------
// ATRQUERY CODES:
//-----------------------------------------------------------------------------
#define ATRQUERY_AREA_FULL                                         3
#define ATRQUERY_REMOTE_WARNING                                   17
#define ATRQUERY_RMMETADATALOGUNAVAILABLE                         29

//-----------------------------------------------------------------------------
// RRS service calls that require key 0-7 or supervisor state.
//-----------------------------------------------------------------------------
/**
 * RRS exit routine.  Note that this routine runs on an SRB and has no stack
 * prefix area, so it can't call anything that needs the stack prefix, such
 * as issuing traces.  It also only has about 32K of stack area so it can't
 * nest calls too deeply.  It also has no metal C environment hung off R12 so
 * it can't call some of the metal C runtime library functions.
 */
#pragma prolog(atr_exit_routine,"SATRXPRL")
#pragma epilog(atr_exit_routine,"SATRXEPL")
void atr_exit_routine(ATRXParmList64* parms_p) {
    *(parms_p->ATRXParmReturnCodePtr) = ATRX_OK;
}

/**
 * Context services exit routine.  Note that this routine runs on an SRB and has
 * no stack prefix area, so it can't call anything that needs the stack prefix,
 * such as issuing traces.  It also only has about 32K of stack area so it can't
 * nest calls too deeply.  It also has no metal C environment hung off R12 so
 * it can't call some of the metal C runtime library functions.
 */
#pragma prolog(ctx_exit_routine,"SATRXPRL")
#pragma epilog(ctx_exit_routine,"SATRXEPL")
void ctx_exit_routine(CTXEParameterList64* parms_p) {
    switch (*(parms_p->CTXEPExitNumberPtr)) {
        // -------------------------------------------------------------------
        // We only express interest in the contexts which currently have a UR
        // in them which we will want RRS to complete (ie. an in-doubt UR).
        // So, if called for this exit, we'll always disassociate the UR from
        // the context.
        // -------------------------------------------------------------------
        case CTX_PRIVATE_CONTEXT_OWNER:
            *(parms_p->CTXEPReturnCodePtr) = CTX_DIS_PVT_CONTEXT;
            break;
        // -------------------------------------------------------------------
        // The logic in this exit is so simple that a failure is unlikely.  If
        // a failure does occur, just unset the RM.
        // -------------------------------------------------------------------
        case CTX_EXIT_FAILED_EXIT:
            *(parms_p->CTXEPReturnCodePtr) = CTX_EXIT_UNSET_RM;
            break;
        // -------------------------------------------------------------------
        // We should not get driven for this exit.
        // -------------------------------------------------------------------
        default:
            *(parms_p->CTXEPReturnCodePtr) = 0;
    }
}

/**
 * Adds an item to the native registry.
 *
 * @param data_p A pointer to the data to put in the registry data area.
 * @param dataLen The length of the data pointed to by data_p, in bytes.
 * @param type The type of registry entry to create.
 * @param registryToken_p A pointer to the registry token, which will be filled
 *                        in if the call is successful.
 *
 * @return the return code from the registry service.  0 indicates success.
 */
static int addToNativeRegistry(void* data_p, int dataLen, RegistryDataType type, RegistryToken* registryToken_p) {
    RegistryDataArea dataArea;
    memset(&dataArea, 0, sizeof(dataArea));
    memcpy(&dataArea, data_p, dataLen);
    return registryPut(type, &dataArea, registryToken_p);
}

/**
 * Adds a URI token to the native registry.
 *
 * @param uriToken The URI token to add to the registry.
 * @param uriRegistryToken_p A pointer to the registry token which will be
 *                           filled in and used to look up the URI token in
 *                           the registry.
 *
 * @return The return code from the registry service.  0 indicates success.
 */
static int addUriTokenToRegistry(atr_uri_token uriToken, RegistryToken* uriRegistryToken_p) {
    return addToNativeRegistry(uriToken, sizeof(atr_uri_token), RRSURI, uriRegistryToken_p);
}

/**
 * Adds a context interest token to the native registry.
 *
 * @param ciToken The context interest token to add to the registry.
 * @param contextInterestRegistryToken_p A pointer to the registry token which will be
 *                                       filled in and used to look up the CI token in
 *                                       the registry.
 *
 * @return The return code from the registry service.  0 indicates success.
 */
static int addContextInterestTokenToRegistry(ctx_ci_token ciToken, RegistryToken* contextInterestRegistryToken_p) {
    return addToNativeRegistry(ciToken, sizeof(ctx_ci_token), RRSCTXI, contextInterestRegistryToken_p);
}

/**
 * Gets an object from the native registry.
 *
 * @param registryToken_p A pointer to the registry token to use to look up the object.
 * @param data_p A pointer to the area where the data from the registry data should be copied.
 * @param dataLen The length of the data to copy.
 *
 * @return The return code from registry services.  0 is success.
 */
static int getFromNativeRegistry(RegistryToken* registryToken_p, void* data_p, int dataLen) {
    RegistryDataArea dataArea;
    int registryRC = registryGetAndSetUsed(registryToken_p, &dataArea);
    if (registryRC == 0) {
        memcpy(data_p, &dataArea, dataLen);
        registrySetUnused(registryToken_p, TRUE);
    }
    return registryRC;
}

/**
 * Gets a context token from the native registry.
 *
 * @param contextRegistryToken_p A pointer to the registry token used to look up the context token.
 * @param contextToken_p A pointer to the context token, which will be filled in.
 *
 * @return The return code from registry services.  0 is success.
 */
static int getContextTokenFromRegistry(RegistryToken* contextRegistryToken_p, ctx_context_token* contextToken_p) {
    return getFromNativeRegistry(contextRegistryToken_p, contextToken_p, sizeof(ctx_context_token));
}

/**
 * Gets a URI token from the native registry.
 *
 * @param uriRegistryToken_p A pointer to the registry token used to look up the URI token.
 * @param uriToken_p A pointer to the URI token, which will be filled in.
 *
 * @return The return code from registry services.  0 is success.
 */
static int getUriTokenFromRegistry(RegistryToken* uriRegistryToken_p, atr_uri_token* uriToken_p) {
    return getFromNativeRegistry(uriRegistryToken_p, uriToken_p, sizeof(atr_uri_token));
}

/**
 * Gets the resource manager name from the native registry.
 *
 * @param resMgrNameRegistryToken_p A pointer to the registry token used to look up the resource manager name.
 * @param resMgrName_p A pointer to the resource manager name, which will be filled in.
 *
 * @return The return code from registry services.  0 is success.
 */
static int getResMgrNameFromRegistry(RegistryToken* resMgrNameRegistryToken_p, crg_resource_manager_name* resMgrName_p) {
    return getFromNativeRegistry(resMgrNameRegistryToken_p, resMgrName_p, sizeof(crg_resource_manager_name));
}

/**
 * Builds the resource manager name:
 * BBG.DEFAULT.[STCK].IBM or BBG.[USER_PREFIX][STCK].IBM or BBG.[USER_PREFIX].[STCK].IBM
 * where [USER_PREFIX] is the user configured prefix to be added to the resource
 * manager name for security purposes.
 * The resource manager name is "guaranteed" to be unique across the system.
 * @param rmNamePrefix The resource manager name prefix.
 * @param rmNamePrefixLen the resource manager name prefix length.
 * @param rmNameSTCK The resource manager timestamp (STCK).
 * @param resMgrName The resource manager name.
 *
 * @return The return code. Currently not used.
 */
static void buildResourceManagerName(char* rmNamePrefix, int rmNamePrefixLen, char* rmNameSTCK, crg_resource_manager_name* resMgrName) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_AUTH_BUILD_RMNAME_ENTRY),
                    "tx_authorized_rrs_services.buildResourceManagerName. Entry",
                    TRACE_DATA_END_PARMS);
    }

    char* internalPrefix_p = "BBG.";
    char* internalSuffix_p = ".IBM";

    int rmNameLength = sizeof(atr_resource_manager_name);

    // Clear area.
    memset(resMgrName, 0x40, rmNameLength);
    char* curpos_p = *resMgrName;
    // Add internal BBG. prefix.
    int stringLen = strlen(internalPrefix_p);
    memcpy(curpos_p, internalPrefix_p, stringLen);
    curpos_p += stringLen;

    // Append the input prefix.
    memcpy(curpos_p, rmNamePrefix, rmNamePrefixLen);
    curpos_p += rmNamePrefixLen;

    // Append a DOT to that name if there is enough room.
    if (rmNamePrefixLen < MAX_RMNAME_PREFIX_LENGTH) {
        memcpy(curpos_p, ".", 1);
        curpos_p += 1;
    }

    // Append the input variable data.
    memcpy(curpos_p, rmNameSTCK, RMNAME_STCK_LENGTH);
    curpos_p += RMNAME_STCK_LENGTH;

    // Add the internal IBM suffix: BBG.DEFAULT.[STCK].IBM or
    // BBG.[USER_PREFIX][STCK].IBM or BBG.[USER_PREFIX].[STCK].IBM
    stringLen = strlen(internalSuffix_p);
    memcpy(curpos_p, internalSuffix_p, stringLen);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_AUTH_BUILD_RMNAME_EXIT),
                    "tx_authorized_rrs_services.buildResourceManagerName. Exit",
                    TRACE_DATA_RAWDATA(rmNameLength, resMgrName, "RMName"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Gets a context interest token from the native registry.
 *
 * @param ciRegistryToken_p A pointer to the registry token used to look up the CI token.
 * @param ciToken The context interest token, which will be filled in.
 *
 * @return The return code from registry services.  0 is success.
 */
static int getContextInterestTokenFromRegistry(RegistryToken* ciRegistryToken_p, ctx_ci_token ciToken) {
    return getFromNativeRegistry(ciRegistryToken_p, ciToken, sizeof(ctx_ci_token));
}

/**
 * Register a Resource manager with registration services.
 * UnregisterOption = 2 requires callers to be authorized.
 *
 * @param p The structure holding crg4grm input parameters.
 */
void crg_register_resource_manager(struct crg4grm_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_CRG4GRM_ENTRY),
            "tx_authorized_rrs_services.crg_register_resource_manager. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct crg4grm_parms), p, "crg4grm_parms"),
            TRACE_DATA_END_PARMS);
    }

    if (p->returnCode == NULL) { return; }

    crg_return_code localRc = -1;
    crg_resource_manager_token localResMgrToken;
    crg_resource_manager_name localResMgrName;

    // Validate prefix input length. It must always be greater than 0.
    int localRMNamePrefixLen = p->rmNamePrefixLength;
    if (localRMNamePrefixLen < 1 || localRMNamePrefixLen > MAX_RMNAME_PREFIX_LENGTH) {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_TX_RRS_AUTH_CRG4GRM_BAD_RMNAME_PREFIX_LENGTH_INPUT),
                            "tx_authorized_rrs_services.crg_register_resource_manager. The RM name prefix length input is invalid",
                            TRACE_DATA_INT(localRMNamePrefixLen, "RMNamePrefixLen"),
                            TRACE_DATA_RAWDATA(sizeof(struct crg4grm_parms), p, "crg4grm_parms"),
                            TRACE_DATA_END_PARMS);
            }
            memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
            return;

    }

    char localRMNamePrefix[localRMNamePrefixLen];
    memcpy_sk(localRMNamePrefix, p->rmNamePrefix, localRMNamePrefixLen, 8);
    char nullTermPrefix[localRMNamePrefixLen + 1];
    memcpy(nullTermPrefix, localRMNamePrefix, localRMNamePrefixLen);
    nullTermPrefix[localRMNamePrefixLen] = '\0';

    if (strncmp(nullTermPrefix, "DEFAULT", localRMNamePrefixLen) != 0) {
        char racfProfileName[64];
        snprintf(racfProfileName, sizeof(racfProfileName), "BBG.RMNAME.%s.RRS", nullTermPrefix);

        saf_results results;
        int rc = checkAuthorization(&results,
                                    1,                          // Suppress messages
                                    ASIS,                       // Log option
                                    "BBGZSRV",                  // Requestor
                                    NULL,                       // ACEE
                                    READ,                       // Access level
                                    "BBGZSRV",                  // Application name
                                    SAF_SERVER_CLASS,           // Class
                                    racfProfileName);           // Profile

        // If there was an internal failure or the SAF return code is other than
        // zero, we fail the registration. Note that we do not distinguish a SAF RC of 4
        // (No RACF decision) because the user specified a prefix to be authenticated.
        if (rc != 0 || results.safReturnCode != 0) {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_TX_RRS_AUTH_CRG4GRM_FAILED_PROFILE_SEC_CHECK),
                            "tx_authorized_rrs_services.crg_register_resource_manager. RACF profile name failed to pass security check",
                            TRACE_DATA_STRING(racfProfileName, "RACFProfileName"),
                            TRACE_DATA_INT(rc, "CheckAuthorizationReturnCode"),
                            TRACE_DATA_INT(results.safReturnCode, "SAFReturnCode"),
                            TRACE_DATA_INT(results.racfReturnCode, "RACFReturnCode"),
                            TRACE_DATA_INT(results.racfReasonCode, "RACFReasonCode"),
                            TRACE_DATA_RAWDATA(sizeof(struct crg4grm_parms), p, "crg4grm_parms"),
                            TRACE_DATA_END_PARMS);
            }

            int failureRc = RMPREFIX_FAILED_AUTHORIZATION_CHECK;
            memcpy_dk(p->returnCode, &failureRc, sizeof(int), 8);
            memcpy_dk(p->internalAuthCheckRc, &rc, sizeof(int), 8);
            memcpy_dk(p->safRc, &results.safReturnCode, sizeof(int), 8);
            memcpy_dk(p->racfRc, &results.racfReturnCode, sizeof(int), 8);
            memcpy_dk(p->racfRsn, &results.racfReasonCode, sizeof(int), 8);
            return;
        }
    }

    // Preset the security related return codes. At this point we either did not care
    // to check for authorization (DEFAULT) or the authorization passed.
    int zeroRC = 0;
    memcpy_dk(p->internalAuthCheckRc, &zeroRC, sizeof(int), 8);
    memcpy_dk(p->safRc, &zeroRC, sizeof(int), 8);
    memcpy_dk(p->racfRc, &zeroRC, sizeof(int), 8);
    memcpy_dk(p->racfRsn, &zeroRC, sizeof(int), 8);

    // Get the STCK.
    char localRMNameSTCK[RMNAME_STCK_LENGTH];
    if (p->rmNameSTCK == NULL) {
        unsigned long long currentTime;
        __stck(&currentTime);
        char stckBuf[17];
        snprintf(stckBuf, sizeof(stckBuf), "%16.16llX", currentTime);
        memcpy(localRMNameSTCK, stckBuf, RMNAME_STCK_LENGTH);
    } else {
        memcpy_sk(localRMNameSTCK, p->rmNameSTCK, RMNAME_STCK_LENGTH, 8);

        // Validate the input STCK.
        char nullTermStck[RMNAME_STCK_LENGTH + 1];
        nullTermStck[RMNAME_STCK_LENGTH] = '\0';
        char NullTermScanCopy[RMNAME_STCK_LENGTH + 1];
        NullTermScanCopy[RMNAME_STCK_LENGTH] = '\0';

        memcpy(nullTermStck, localRMNameSTCK, RMNAME_STCK_LENGTH);

        if (sscanf(nullTermStck, "%16[0123456789ABCDEF]", NullTermScanCopy) == 0 ||
            strncmp(nullTermStck, NullTermScanCopy, RMNAME_STCK_LENGTH) != 0) {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_TX_RRS_AUTH_CRG4GRM_BAD_STCK_INPUT),
                            "tx_authorized_rrs_services.crg_register_resource_manager. The input STCK is invalid.",
                            TRACE_DATA_RAWDATA(RMNAME_STCK_LENGTH, localRMNameSTCK, "STCK"),
                            TRACE_DATA_RAWDATA(sizeof(nullTermStck), nullTermStck, "NullTermSTCK"),
                            TRACE_DATA_RAWDATA(sizeof(NullTermScanCopy), NullTermScanCopy, "NullTermScanCopy"),
                            TRACE_DATA_END_PARMS);
            }
            memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
            return;
        }
    }

    buildResourceManagerName(localRMNamePrefix, localRMNamePrefixLen, localRMNameSTCK, &localResMgrName);
    RegistryToken localResMgrTokenRegistryToken;
    RegistryToken localResMgrNameRegistryToken;
    int registryRC = addToNativeRegistry(localResMgrName, sizeof(crg_resource_manager_name), RRSRMNAME, &localResMgrNameRegistryToken);
    if (registryRC != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_CRG4GRM_UPDATE_REGISRTY_FAILURE),
                        "tx_authorized_rrs_services.crg_register_resource_manager. Add resource manager name to registry failed",
                        TRACE_DATA_INT(registryRC, "registry return code"),
                        TRACE_DATA_RAWDATA(sizeof(struct crg4grm_parms), p, "crg4grm_parms"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    crg4grm(&localRc, localResMgrName, localResMgrToken, p->unregisterOption, p->crgGlobalData);

    if (localRc == ATR_OK) {
        // Add the rmToken and rmName to the server_process_data so that the RM can be
        // deregistered/deleted from RRS as part of task termination in case of hard failures.
        server_process_data* spd_p = getServerProcessData();
        memcpy(spd_p->resMgrToken, localResMgrToken, sizeof(crg_resource_manager_token));
        memcpy(spd_p->resMgrName, localResMgrName, sizeof(crg_resource_manager_name));
                    
        // Add the registered resource manager token to the registry.
        registryRC = addToNativeRegistry(localResMgrToken, sizeof(crg_resource_manager_token), RRSRMTKN, &localResMgrTokenRegistryToken);
        if (registryRC != 0) {
            int localDrmRc;
            crg4drm(&localDrmRc, localResMgrToken);
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_TX_RRS_AUTH_CRG4GRM_UPDATE_REGISRTY_FAILURE2),
                            "tx_authorized_rrs_services.crg_register_resource_manager. Add resource manager token to registry failed",
                            TRACE_DATA_INT(registryRC, "registry return code"),
                            TRACE_DATA_RAWDATA(sizeof(struct crg4grm_parms), p, "crg4grm_parms"),
                            TRACE_DATA_END_PARMS);
            }
            localRc = -1;
        } else {
            memcpy_dk(p->resMgrName, &localResMgrName, sizeof(crg_resource_manager_name), 8);
            memcpy_dk(p->resMgrNameRegistryToken, &localResMgrNameRegistryToken, sizeof(localResMgrNameRegistryToken), 8);
            memcpy_dk(p->resMgrToken, &localResMgrToken, sizeof(crg_resource_manager_token), 8);
            memcpy_dk(p->resMgrRegistryToken, &localResMgrTokenRegistryToken, sizeof(RegistryToken), 8);
        }
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(int), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_CRG4GRM_EXIT),
                    "tx_authorized_rrs_services.crg_register_resource_manager. Exit",
                    TRACE_DATA_INT(localRc, "Registration Services Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_RAWDATA(sizeof(crg_resource_manager_name), &localResMgrName, "Resource Manager Name"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), &localResMgrNameRegistryToken, "Resource Manager Name Registry Token"),
                    TRACE_DATA_RAWDATA(sizeof(crg_resource_manager_token), &localResMgrToken, "Resource Manager Token"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), &localResMgrTokenRegistryToken, "Resource Manager Token Registry Token"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Deletes a resource manager from the RRS logs.
 *
 * @param rmName The resource manager name to delete.  The name should be
 *               blank padded on the right if it's smaller than 32 bytes.
 * @param rsnCode_p A pointer to a full word where the reason code from the
 *                  ATRSRV macro will be stored.  If this pointer is NULL, the
 *                  reason code will not be stored.
 *
 * @return The return code from the ATRSRV service.
 */
static int atrsrv_deleteRM(crg_resource_manager_name rmName, int* rsnCode_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATRSRV_DELETERM_ENTRY),
            "tx_authorized_rrs_services.atrsrv_deleteRM. Entry",
            TRACE_DATA_RAWDATA(sizeof(rmName), rmName, "RM Name"),
            TRACE_DATA_PTR(rsnCode_p, "Reason code ptr"),
            TRACE_DATA_END_PARMS);
    }

    struct parm31 {
        crg_resource_manager_name rmName;
        char executeAtrsrv[256];
    };

    int rc = -1;
    int rsn = -1;

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p != NULL) {
        memcpy(parm_p->rmName, rmName, sizeof(parm_p->rmName));
        memset(parm_p->executeAtrsrv, 0, sizeof(parm_p->executeAtrsrv));

        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " ATRSRV REQUEST=REMOVRM,RMNAME=(%2),PLISTVER=3,MF=(E,(%3),COMPLETE)\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64\n"
              " ST 15,%0\n"
              " ST 0,%1" : "=m"(rc),"=m"(rsn) :
              "r"(parm_p->rmName),"r"(parm_p->executeAtrsrv) :
              "r0","r1","r14","r15");

        free(parm_p);
    }

    if (rsnCode_p != NULL) {
        *rsnCode_p = rsn;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATRSRV_DELETERM_EXIT),
                    "tx_authorized_rrs_services.atrsrv_deleteRM. Exit",
                    TRACE_DATA_INT(rc, "ATRSRV Return code"),
                    TRACE_DATA_INT(rsn, "ATRSRV Reason code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Checks if the caller is authorized to call ATRSRV on this system in the plex.
 *
 * @return TRUE if authorized, or if the FACILITY class is inactive.  FALSE if
 *         not authorized.
 */
static unsigned char isCallerAuthorizedATRSRV(void) {
    // -------------------------------------------------------------------
    // See if we have access to the RRS macro ATRSRV.  We need the sysplex
    // name and system name to build the string which we'll pass to RACF.
    // -------------------------------------------------------------------
    char* SAF_SERVER_REQUESTOR_NAME = "BBGZSRV";
    char* SAF_SERVER_APPLICATION_NAME = "BBGZSRV";
    char* SAF_FACILITY_CLASS = "FACILITY";
    char plexName[9];
    char sysName[9];
    char racfProfileName[64];
    psa* psa_p = 0;
    cvt* cvt_p = (cvt*) psa_p->flccvt;
    ecvt* ecvt_p = (ecvt*) cvt_p->cvtecvt;

    memset(plexName, 0, sizeof(plexName));
    memset(sysName, 0, sizeof(sysName));

    memcpy(plexName, ecvt_p->ecvtsplx, sizeof(ecvt_p->ecvtsplx));
    memcpy(sysName, cvt_p->cvtsname, sizeof(cvt_p->cvtsname));

    for (int x = 0; x < sizeof(plexName); x++) {
        if (plexName[x] == ' ') {
            plexName[x] = 0;
        }
    }

    for (int x = 0; x < sizeof(sysName); x++) {
        if (sysName[x] == ' ') {
            sysName[x] = 0;
        }
    }

    snprintf(racfProfileName, sizeof(racfProfileName), "MVSADMIN.RRS.COMMANDS.%s.%s", plexName, sysName);
    saf_results safReturnCodes;
    int rc = checkAuthorization(&safReturnCodes,  /* Return codes */
                                TRUE,             /* Supress messages */
                                NOFAIL,           /* Don't log on failure */
                                SAF_SERVER_REQUESTOR_NAME, /* Our name */
                                NULL,             /* No ACEE */
                                UPDATE,           /* Update access requested */
                                SAF_SERVER_APPLICATION_NAME, /* Our name */
                                SAF_FACILITY_CLASS, /* Check the facility class */
                                racfProfileName); /* Check the RRS services */

    // Good return codes, we're good to go.
    if ((rc == 0) && (safReturnCodes.safReturnCode == 0)) {
        return TRUE;
    }

    // Bad return codes, not so good to go.
    if ((rc != 0) || (safReturnCodes.safReturnCode > 4)) {
        return FALSE;
    }

    // SAF can't figure it out.  Allow if the FACILITY class is inactive.
    if ((safReturnCodes.safReturnCode == 4) && (safReturnCodes.racfReturnCode == 4)) {
        if (isClassActive(SAF_FACILITY_CLASS, SAF_SERVER_REQUESTOR_NAME) == FALSE) {
            return TRUE;
        }
    }

    return FALSE;
}

/**
 * Performs RRS cleanup. Called during task level termination out of 
 * server_authorized_function_module->serverAuthorizedProcessCleanup
 *
 * @param serverProcessData_p The pointer to the server process data.
 */
void cleanupRRS(server_process_data* serverProcessData_p) {

    // Resource manager cleanup.
    char nullResMgrToken[16] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    if (memcmp(&(serverProcessData_p->resMgrToken),
               &nullResMgrToken,
               sizeof(nullResMgrToken)) != 0) {
        crg_return_code localRc = 0;
        crg_resource_manager_token localResMgrToken;
        memcpy(localResMgrToken, serverProcessData_p->resMgrToken, sizeof(localResMgrToken));

        // Deregister the resource manager.
        crg4drm(&localRc, localResMgrToken);

        // If deregistration went well.
        if (localRc == 0) {
            memset(serverProcessData_p->resMgrToken, 0, sizeof(crg_resource_manager_token));

            // Delete the RM name if authorized.
            if (isCallerAuthorizedATRSRV()) {
                crg_resource_manager_name local_resMgrName;
                memcpy(local_resMgrName, serverProcessData_p->resMgrName, sizeof(crg_resource_manager_name));
                int atrsrvRsn = -1;
                atrsrv_deleteRM(local_resMgrName, &atrsrvRsn);
                memset(serverProcessData_p->resMgrName, 0, sizeof(crg_resource_manager_name));
            }
        }
    }
}

/**
 * Unregister a Resource manager with registration services.
 * Caller is required to be authorized if the resource manager
 * registered with unregisterOption = 2.
 *
 * @param p The structure holding crg4drm input parameters.
 */
void crg_unregister_resource_manager(struct crg4drm_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_CRG4DRM_ENTRY),
            "tx_authorized_rrs_services.crg_unregister_resource_manager. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct crg4drm_parms), p, "crg4drm_parms"),
            TRACE_DATA_END_PARMS);
    }

    if (p->returnCode == NULL) { return; }

    crg_return_code localRc = -1;

    crg_resource_manager_name local_resMgrName;
    int resMgrTokenRegistryRC = 0;
    int resMgrNameRegistryRC = getResMgrNameFromRegistry(&(p->resMgrNameRegistryToken), &local_resMgrName);
    if (p->resMgrNameRegistryReturnCode != NULL) {memcpy_dk(p->resMgrNameRegistryReturnCode, &resMgrNameRegistryRC, sizeof(int), 8);}
    if (resMgrNameRegistryRC == 0) {
        crg_resource_manager_token localResMgrToken;
        resMgrTokenRegistryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(crg_resource_manager_token));
        if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &resMgrTokenRegistryRC, sizeof(int), 8);}
        if (resMgrTokenRegistryRC == 0) {
            crg4drm(&localRc, localResMgrToken);
                    
            // We drove deregistration. Regardless of the outcome, there is no reason 
            // to drive it again. Clear the rmToken and rmName from the process data.
            server_process_data* spd_p = getServerProcessData();
            memset(spd_p->resMgrToken, 0, sizeof(crg_resource_manager_token));
            memset(spd_p->resMgrName, 0, sizeof(crg_resource_manager_name));
                    
            // -----------------------------------------------------------------------
            // If we deregistered, try to delete the RM from the RM log, to prevent
            // lognames from accumulating.  RRS will not allow us to delete the log
            // name if there are URs requiring resolution.
            // -----------------------------------------------------------------------
            if ((localRc == ATR_OK) && (isCallerAuthorizedATRSRV())) {
                int atrsrvRsn = -1;
                int atrsrvRc = atrsrv_deleteRM(local_resMgrName, &atrsrvRsn);
            }
            registryFree(&(p->resMgrNameRegistryToken), TRUE);
            registryFree(&(p->resMgrToken), TRUE);
        }
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(int), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_CRG4DRM_EXIT),
                    "tx_authorized_rrs_services.crg_unregister_resource_manager. Exit",
                    TRACE_DATA_INT(resMgrNameRegistryRC, "Resource manager name registry return code"),
                    TRACE_DATA_INT(resMgrTokenRegistryRC, "Resource manager token registry return code"),
                    TRACE_DATA_INT(localRc, "Registration Services Return Code"),
                    TRACE_DATA_END_PARMS);
    }
}


/**
 * Determines if RRS's METADATA logstream is active.
 * @param rmName The resource manager name to be used for this call.
 * @return True if active. False otherwise.
 */
static unsigned char isMETADATALogstreamActive(crg_resource_manager_name rmName) {

    int retry = FALSE;
    unsigned char active = FALSE;
    int newStorageSize = 4096;

    do {
        int traceLevel = -1;
        int rc = -1;
        int rsn = -1;
        int storageSize = newStorageSize;
        retry = FALSE;

        struct parm31 {
            crg_resource_manager_name rmName;
            char parmList[1024];
            char* __ptr32 queryStorageArea_p;
            int count;
            int areaLen;
        };

        struct parm31* parm_p = __malloc31(sizeof(struct parm31));
        parm_p->queryStorageArea_p = __malloc31(storageSize);

        if (parm_p != NULL) {
            parm_p->areaLen = storageSize;
            memcpy(parm_p->rmName, rmName, sizeof(parm_p->rmName));
            memset(parm_p->queryStorageArea_p, 0, storageSize);
            memset(parm_p->parmList, 0, sizeof(parm_p->parmList));

            __asm(" SAM31\n"
                " SYSSTATE AMODE64=NO\n"
                " ATRQUERY REQUEST=RMINFO,METADATA,RMNAME=(%2),AREAADDR=(%3),COUNT=(%4),AREALEN=(%5),PLISTVER=MAX,MF=(E,(%6),COMPLETE)\n"
                " SYSSTATE AMODE64=YES\n"
                " SAM64\n"
                " ST 15,%0\n"
                " ST 0,%1" : "=m"(rc),"=m"(rsn) :
                "r"(parm_p->rmName),"r"(&(parm_p->queryStorageArea_p)),"r"(&(parm_p->count)),"r"(&(parm_p->areaLen)),"r"(parm_p->parmList) :
                "r0","r1","r14","r15");

            switch(rc) {
                case 0:
                    // Make sure we got something. If so, then we can be confident that we also
                    // got METADATA info.
                    if (parm_p->count > 0) {
                        active = TRUE;
                    }
                    break;
                case 4:
                    switch (rsn) {
                        case ATRQUERY_AREA_FULL:
                            // Stop at 10K because it should be more than enough storage
                            // (2K RM Info, 8K METADATA). Otherwise increase the storage area
                            // by 3K each time. Note that fall through to set the trace level.
                            if (storageSize <= 10240) {
                                retry = TRUE;
                                newStorageSize += 3072;
                            }
                        case ATRQUERY_REMOTE_WARNING:
                        case ATRQUERY_RMMETADATALOGUNAVAILABLE:
                        default:
                            traceLevel = trc_level_detailed;
                            break;
                    }
                    // Fall through to print trace.
                case 8:
                    if (traceLevel == -1) {
                        traceLevel = trc_level_exception;
                    }
                    // Fall through to print trace.
                default:
                    if (TraceActive(traceLevel)) {
                        TraceRecord(traceLevel, TP(TP_TX_RRS_AUTH_IS_META_DATA_ACTIVE_BAD_RRS_RC),
                                    "tx_authorized_rrs_services.isMETADATALogstreamActive. ATRQUERY invalid return code",
                                    TRACE_DATA_RAWDATA(32,parm_p->rmName,"RMName"),
                                    TRACE_DATA_INT(rc, "ATRQUERY Return code"),
                                    TRACE_DATA_INT(rsn, "ATRQUERY Reason code"),
                                    TRACE_DATA_INT(parm_p->count, "Count"),
                                    TRACE_DATA_INT(storageSize, "ATRQUERY storage area size"),
                                    TRACE_DATA_RAWDATA(storageSize, parm_p->queryStorageArea_p, "Storage area"),
                                    TRACE_DATA_END_PARMS);
                    }
                    break;
            }

            free(parm_p);
        }
    } while (retry == TRUE);
    return active;
}

/**
 * Sets exit information.
 * Using authorized RMs requires callers to be authorized.
 *
 * @param p The structure holding crg4seif input parameters.
 */
void atr_set_exit_information(struct crg4seif_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_CRG4SEIF_ENTRY),
            "tx_authorized_rrs_services.atr_set_exit_information. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct crg4seif_parms), p, "crg4seif_parms"),
            TRACE_DATA_END_PARMS);
    }

    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;
    int localMetaDataLoggingAllowed = FALSE;

    int registryRMTokenRC = 0;
    crg_resource_manager_name local_resMgrName;

    int registryRMNameRC = getResMgrNameFromRegistry(&(p->resMgrNameRegistryToken), &local_resMgrName);
    if (p->resMgrNameRegistryReturnCode != NULL) {memcpy_dk(p->resMgrNameRegistryReturnCode, &registryRMNameRC, sizeof(int), 8);}
    if (registryRMNameRC == 0) {
        crg_resource_manager_token localResMgrToken;
        registryRMTokenRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(crg_resource_manager_token));
        if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRMTokenRC, sizeof(int), 8);}
        if (registryRMTokenRC == 0) {
            char* ctxExitMgr = "CTX.EXITMGR.IBM ";
            char* rrsExitMgr = "ATR.EXITMGR.IBM ";

            int notificationExitType = CRG_EXIT_TYPE_NONE;
            long long notificationExitEntry = 0L;
            crg_exit_manager_name exitManagerName;

            memcpy(&exitManagerName, ctxExitMgr, sizeof(exitManagerName));
            crg_array_index exitCount = 0;
            int* exitNumbers_p = NULL;
            long* exitEntries_p = NULL;
            int* exitTypes_p = NULL;

            int local_varData1 = 0x00000000;

            struct CtxVarData1 {
                int length;
                int version;
                char rmName[32];
            };

            struct CtxVarData1* ctxVarData1_p = __malloc31(sizeof(struct CtxVarData1));
            if (ctxVarData1_p != NULL) {
                memset(ctxVarData1_p, 0, sizeof(struct CtxVarData1));
                ctxVarData1_p->length = sizeof(struct CtxVarData1);
                ctxVarData1_p->version = 1;
                memcpy(ctxVarData1_p->rmName, "ATR.RESOURCEMANAGER.IBM         ", sizeof(ctxVarData1_p->rmName));

                int varData1 = (int)ctxVarData1_p;
                int varData2 = 0;
                int varData3 = 0;

                int numberOfCtxExits = 2;
                exitCount = numberOfCtxExits;
                int ctxExitNumbers[numberOfCtxExits];
                long ctxExitEntries[numberOfCtxExits];
                int ctxExitTypes[numberOfCtxExits];
                ctxExitNumbers[0] = CTX_EXIT_FAILED_EXIT;
                ctxExitNumbers[1] = CTX_PRIVATE_CONTEXT_OWNER;
                for (int x = 0; x < numberOfCtxExits; x++) {
                    ctxExitEntries[x] = (long)ctx_exit_routine;
                    ctxExitTypes[x] = CTX_EXIT_TYPE_SRB;
                }

                exitNumbers_p = ctxExitNumbers;
                exitEntries_p = ctxExitEntries;
                exitTypes_p = ctxExitTypes;

                // Set exits with context services
                crg4seif(&localRc,
                         localResMgrToken,
                         notificationExitType,
                         notificationExitEntry,
                         exitManagerName,
                         exitCount,
                         (void*)exitNumbers_p,
                         (void*)exitEntries_p,
                         (void*)exitTypes_p,
                         varData1,
                         varData2,
                         varData3);

                free(ctxVarData1_p);

                if (localRc == CRG_OK) {
                    memcpy(&exitManagerName, rrsExitMgr, sizeof(exitManagerName));
                    exitCount = 4;
                    int rrsExitNumbers[4];
                    long rrsExitEntries[4];
                    int rrsExitTypes[4];
                    rrsExitNumbers[0] = ATR_PREPARE_EXIT;
                    rrsExitNumbers[1] = ATR_COMMIT_EXIT;
                    rrsExitNumbers[2] = ATR_BACKOUT_EXIT;
                    rrsExitNumbers[3] = ATR_EXIT_FAILED_EXIT;
                    for (int x = 0; x < 4; x++) {
                        rrsExitEntries[x] = (long)atr_exit_routine;
                        rrsExitTypes[x] = ATR_EXIT_TYPE_SRB;
                    }
                    exitNumbers_p = rrsExitNumbers;
                    exitEntries_p = rrsExitEntries;
                    exitTypes_p = rrsExitTypes;

                    // By default clear varData1 and varData2 and set exit information with RRS before
                    // we QUERY RRS to see if the METADATA logstream is active. We have to do this
                    // because the RM must be in the active state (after SEIF) to use the ATRQUERY service.
                    varData1 = 0;
                    varData2 = 0;

                    crg4seif(&localRc,
                             localResMgrToken,
                             notificationExitType,
                             notificationExitEntry,
                             exitManagerName,
                             exitCount,
                             (void*)exitNumbers_p,
                             (void*)exitEntries_p,
                             (void*)exitTypes_p,
                             varData1,
                             varData2,
                             varData3);

                    // Now check if we need to set the ATR_8K_RM_METADATA_REQUESTED bit to enable metadata logging.
                    // We will only log in non-recovery cases, if authorization to use ATRSRV is in place, and if we
                    // deem that the metadata logstream is active. The first 2 conditions are straight forward; however,
                    // that last one requires some explanation.
                    // We deem that the metadata logstream is available/active by using RRS ATRQUERY routine to retrieve
                    // RMINFO with the METADATA option. The metadata logstream is deemed available only when the query
                    // is successful. In any other cases we cannot tell. Having said that, we do this because a
                    // pre-requisite to logging is to define ATR_8K_RM_METADATA_REQUESTED bit in varData2. If we set
                    // the bit blindly and the logstream is NOT active, RRS will issue message ATR173E to the console stating:
                    // "OPTIONAL LOGSTREAM <...> IS NOW REQUIRED. AN RM HAS REQUESTED THE USE OF THE LOGSTREAM."
                    // Needles to say, there is no easy way to check in the logstream is active/available, so we had to
                    // resort to querying RRS as a best effort approach.
                    if (p->recovery == FALSE && isCallerAuthorizedATRSRV() && isMETADATALogstreamActive(local_resMgrName)) {
                        varData1 = 0;
                        varData2 = 0x00400000;

                        // Set exits with RRS
                        crg4seif(&localRc,
                                 localResMgrToken,
                                 notificationExitType,
                                 notificationExitEntry,
                                 exitManagerName,
                                 exitCount,
                                 (void*)exitNumbers_p,
                                 (void*)exitEntries_p,
                                 (void*)exitTypes_p,
                                 varData1,
                                 varData2,
                                 varData3);
                        localMetaDataLoggingAllowed = TRUE;
                    }
                }
            } else {
                localRc = -2;
            }
        }
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
    if (p->metaDataLoggingAllowed != NULL) {memcpy_dk(p->metaDataLoggingAllowed, &localMetaDataLoggingAllowed, sizeof(int), 8);}

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_CRG4SEIF_EXIT),
                    "tx_authorized_rrs_services.atr_set_exit_information. Exit",
                    TRACE_DATA_INT(registryRMNameRC, "Resource Manager Name Registry Return Code"),
                    TRACE_DATA_INT(registryRMTokenRC, "Resource Manager Token Registry Return Code"),
                    TRACE_DATA_INT(localRc, "RRS Return Code"),
                    TRACE_DATA_INT(localMetaDataLoggingAllowed, "MetaDataLoggingAllowed"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Begin restart with RRS.
 *
 * @param p The structure holding atr4ibrs input parameters.
 */
void atr_begin_restart(struct atr4ibrs_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IBRS_ENTRY),
            "tx_authorized_rrs_services.atr_begin_restart. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4ibrs_parms), p, "atr4ibrs_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;

    atr_resource_manager_token localResMgrToken;
    int registryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRC, sizeof(int), 8);}
    if (registryRC == 0) {
        atr4ibrs(&localRc, localResMgrToken);
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IBRS_EXIT),
            "tx_authorized_rrs_services.atr_begin_restart. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * End restart with RRS.
 *
 * @param p The structure holding atr4iers input parameters.
 */
void atr_end_restart(struct atr4iers_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IERS_ENTRY),
            "tx_authorized_rrs_services.atr_end_restart. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4iers_parms), p, "atr4iers_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;

    atr_resource_manager_token localResMgrToken;
    int registryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRC, sizeof(int), 8);}
    if (registryRC == 0) {
        atr4iers(&localRc, localResMgrToken);
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IERS_EXIT),
            "tx_authorized_rrs_services.atr_end_restart. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Retrieve log name from RRS.
 *
 * @param p The structure holding atr4irln input parameters.
 */
void atr_retrieve_log_name(struct atr4irln_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IRLN_ENTRY),
            "tx_authorized_rrs_services.atr_retrieve_log_name. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4irln_parms), p, "atr4irln_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;
    int local_resMgrLogNameLength = 0;
    char local_resMgrLogName[ATR_MAX_RM_LOGNAME_LENGTH];
    int local_rrsLogNameLength = 0;
    char local_rrsLogName[ATR_MAX_RM_LOGNAME_LENGTH];

    if (p->resMgrLogNameBuffLen != ATR_MAX_RM_LOGNAME_LENGTH) {
        memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    atr_resource_manager_token localResMgrToken;
    int registryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRC, sizeof(int), 8);}
    if (registryRC == 0) {
        atr4irln(&localRc,
                 localResMgrToken,
                 p->resMgrLogNameBuffLen,
                 &local_resMgrLogNameLength,
                 &local_resMgrLogName,
                 &local_rrsLogNameLength,
                 &local_rrsLogName);
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

    if (localRc == ATR_OK) {
        if (p->resMgrLogNameLength != NULL) {memcpy_dk(p->resMgrLogNameLength, &local_resMgrLogNameLength, sizeof(int), 8);}
        if (p->resMgrLogName != NULL) {memcpy_dk(p->resMgrLogName, &local_resMgrLogName, local_resMgrLogNameLength, 8);}
        if (p->rrsLogNameLength != NULL) {memcpy_dk(p->rrsLogNameLength, &local_rrsLogNameLength, sizeof(int), 8);}
        if (p->rrsLogName != NULL) {memcpy_dk(p->rrsLogName, &local_rrsLogName, local_rrsLogNameLength, 8);}
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IRLN_EXIT),
            "tx_authorized_rrs_services.atr_retrieve_log_name. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(local_resMgrLogNameLength, "Resource Manager Log Name Length"),
            TRACE_DATA_RAWDATA(local_resMgrLogNameLength, local_resMgrLogName, "Resource Manager Log Name"),
            TRACE_DATA_INT(local_rrsLogNameLength, "RRS Log Name Length"),
            TRACE_DATA_RAWDATA(local_rrsLogNameLength, local_rrsLogName, "RRS Log Name"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Sets the log name with RRS.
 *
 * @param p The structure holding atr4isln input parameters.
 */
void atr_set_log_name(struct atr4isln_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ISLN_ENTRY),
            "tx_authorized_rrs_services.atr_set_log_name. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4isln_parms), p, "atr4isln_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;
    int local_resMgrLogNameLen = p->resMgrLogNameLen;

    if (local_resMgrLogNameLen < 0 || local_resMgrLogNameLen > ATR_MAX_RM_LOGNAME_LENGTH) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4ISLN_BAD_LOG_NAME_LENGTH_INPUT),
                        "tx_authorized_rrs_services.atr_set_log_name. The log Name length input is invalid",
                        TRACE_DATA_INT(local_resMgrLogNameLen, "ResMgrLogNameLen"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4isln_parms), p, "atr4isln_parms"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    char local_resMgrLogName[local_resMgrLogNameLen];
    memcpy_sk(local_resMgrLogName, p->resMgrLogName, local_resMgrLogNameLen, 8);

    atr_resource_manager_token localResMgrToken;
    int registryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRC, sizeof(int), 8);}
    if (registryRC == 0) {
        atr4isln(&localRc, localResMgrToken, local_resMgrLogNameLen, local_resMgrLogName);
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ISLN_EXIT),
            "tx_authorized_rrs_services.atr_set_log_name. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Retrieve the work ID (XID) for the specified UR.
 * NOTE: the caller of ATR4RWID needs to be abuthorized (supervisor or Key 0-7)
 * only when the specified UR is not currently on the thread (i.e. recovery).
 *
 * @param p The structure holding atr4rwid input parameters.
 */
void atr_retrieve_work_identifier(struct atr4rwid_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4RWID_ENTRY),
            "tx_authorized_rrs_services.atr_retrieve_work_identifier. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4rwid_parms), p, "atr4rwid_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;
    int local_workIdLength = 0;
    char local_workId[ATR_MAX_XID_LENGTH];

    // Make sure the input URI token is in the registry.
    atr_uri_token local_uriToken;
    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &local_uriToken);
    if (registryRC == 0) {
        atr4rwid(&localRc,
                 local_uriToken,
                 p->retrieveOption,
                 p->generateOption,
                 p->workIdType,
                 p->workIdBuffLen,
                 &local_workIdLength,
                 &local_workId);

        memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

        if (localRc == ATR_OK) {
            if (p->workIdLength != NULL) {memcpy_dk(p->workIdLength, &local_workIdLength, sizeof(int), 8);}
            if (p->workId != NULL) {memcpy_dk(p->workId, &local_workId, local_workIdLength, 8);}
        } else if ((localRc == ATR_URI_TOKEN_INV) || (localRc == ATR_UR_TOKEN_INV)) {
            // If the URI token is invalid, free it from the registry.
            registryFree(&(p->uriRegistryToken), TRUE);
        }
    }

    if (p->registryReturnCode != NULL) {memcpy_dk(p->registryReturnCode, &registryRC, sizeof(registryRC), 8);}

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4RWID_EXIT),
            "tx_authorized_rrs_services.atr_retrieve_work_identifier. Exit",
            TRACE_DATA_INT(registryRC, "URI registry return code"),
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(local_workIdLength, "Work ID Length"),
            TRACE_DATA_RAWDATA(local_workIdLength, local_workId, "Work ID"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Set the work ID (XID) for the specified UR.
 *
 * @param p The structure holding atr4swid input parameters.
 */
void atr_set_work_identifier(struct atr4swid_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SWID_ENTRY),
            "tx_authorized_rrs_services.atr_set_work_identifier. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4swid_parms), p, "atr4swid_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;
    int local_workIdLen = p->workIdLen;

    if (local_workIdLen < 0 || local_workIdLen > ATR_MAX_XID_LENGTH) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4SWID_BAD_XID_LENGTH_INPUT),
                        "tx_authorized_rrs_services.atr_set_work_identifier. The XID length input is invalid",
                        TRACE_DATA_INT(local_workIdLen, "XidLength"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4swid_parms), p, "atr4swid_parms"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    char local_workId[local_workIdLen];
    memcpy_sk(local_workId, p->workId, local_workIdLen, 8);

    atr4swid(&localRc, p->ur_or_uriToken, p->setOption, p->workIdType, local_workIdLen, local_workId);

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SWID_EXIT),
            "tx_authorized_rrs_services.atr_set_work_identifier. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Express interest in the UR.
 *
 * @param p The structure holding atr4eint input parameters.
 */
void atr_express_ur_interest(struct atr4eint_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4EINT_ENTRY),
            "tx_authorized_rrs_services.atr_express_ur_interest. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4eint_parms), p, "atr4eint_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->rrsReturnCode == NULL) { return; }

    RegistryToken registryToken;
    int registryRC = -1;

    atr_return_code localRc = -1;
    atr_uri_token local_urInterestToken;
    atr_ur_token local_ur_token;
    atr_context_token local_currCtxToken;
    atr_urid local_ur_id;

    atr_non_persistent_data local_currentNonPersistentData;

    char local_persistentData[ATR_MAX_PDATA_LENGTH];
    memcpy_sk(local_persistentData, p->persistentData, p->pdataLength, 8);
    int local_workIdLen = p->workIdLen;

    if (local_workIdLen < 0 || local_workIdLen > ATR_MAX_XID_LENGTH) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4EINT_BAD_XID_LENGTH_INPUT),
                        "tx_authorized_rrs_services.atr_express_ur_interest. The XID length input is invalid",
                        TRACE_DATA_INT(local_workIdLen, "XidLength"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4eint_parms), p, "atr4eint_parms"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    char local_workId[local_workIdLen];
    memcpy_sk(local_workId, p->workId, local_workIdLen, 8);

    atr_diag_area local_diagnosticAreaArea;
    atr_transaction_mode local_transactionMode;

    atr_resource_manager_token localResMgrToken;
    int resourceManagerTokenRegistryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &resourceManagerTokenRegistryRC, sizeof(int), 8);}
    if (resourceManagerTokenRegistryRC == 0) {
        atr4eint(&localRc,
                 localResMgrToken,
                 p->contextToken,
                 local_urInterestToken,
                 local_ur_token,
                 local_currCtxToken,
                 local_ur_id,
                 p->interest_options,
                 p->nonPersistentData,
                 local_currentNonPersistentData,
                 p->pdataLength,
                 local_persistentData,
                 local_workIdLen,
                 local_workId,
                 p->parent_urToken,
                 local_diagnosticAreaArea,
                 &local_transactionMode);
    }

    memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);
    if (p->diagnosticArea != NULL) {memcpy_dk(p->diagnosticArea, &local_diagnosticAreaArea, sizeof(atr_diag_area), 8);}

    if (localRc == ATR_OK) {
        // Try to put the URI token into the registry.
        registryRC = addUriTokenToRegistry(local_urInterestToken, &registryToken);
        if (p->registryReturnCode != NULL) {memcpy_dk(p->registryReturnCode, &registryRC, sizeof(registryRC), 8);}
        if (registryRC == 0) {
            if (p->urInterestToken != NULL) {memcpy_dk(p->urInterestToken, &local_urInterestToken, sizeof(atr_uri_token), 8);}
            if (p->ur_token != NULL) {memcpy_dk(p->ur_token, &local_ur_token, sizeof(atr_ur_token), 8);}
            if (p->currCtxToken != NULL) {memcpy_dk(p->currCtxToken, &local_currCtxToken, sizeof(atr_context_token), 8);}
            if (p->ur_id != NULL) {memcpy_dk(p->ur_id, &local_ur_id, sizeof(atr_urid), 8);}
            if (p->currentNonPersistentData != NULL) {memcpy_dk(p->currentNonPersistentData, &local_currentNonPersistentData, sizeof(atr_non_persistent_data), 8);}
            if (p->transactionMode != NULL) {memcpy_dk(p->transactionMode, &local_transactionMode, sizeof(atr_transaction_mode), 8);}
            if (p->urInterestRegistryToken != NULL) {memcpy_dk(p->urInterestRegistryToken, &registryToken, sizeof(registryToken), 8);}
        } else {
            int dintRC;
            atr4dint(&dintRC, local_urInterestToken);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4EINT_EXIT),
            "tx_authorized_rrs_services.atr_express_ur_interest. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Native Registry Return Code"),
            TRACE_DATA_INT(resourceManagerTokenRegistryRC, "Resource manager token Registry Return Code"),
            TRACE_DATA_RAWDATA(sizeof(atr_uri_token), local_urInterestToken, "URI Token"),
            TRACE_DATA_RAWDATA(sizeof(RegistryToken), &registryToken, "URI Registry Token"),
            TRACE_DATA_RAWDATA(sizeof(atr_ur_token), local_ur_token, "UR Token"),
            TRACE_DATA_RAWDATA(sizeof(atr_context_token), local_currCtxToken, "Current Context Token"),
            TRACE_DATA_RAWDATA(sizeof(atr_urid), local_ur_id, "URID"),
            TRACE_DATA_RAWDATA(sizeof(atr_non_persistent_data), local_currentNonPersistentData, "Current Non Persistent Interest Data"),
            TRACE_DATA_RAWDATA(sizeof(atr_diag_area), local_diagnosticAreaArea, "Diagnostic Data"),
            TRACE_DATA_INT(local_transactionMode, "Transaction Mode"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Retrieve side information for the given interest in the UR.
 *
 * @param p The structure holding atr4rusi input parameters.
 */
void atr_retrieve_side_information(struct atr4rusi_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4RUSI_ENTRY),
            "tx_authorized_rrs_services.atr_retrieve_side_information. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4rusi_parms), p, "atr4rusi_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->rrsReturnCode == NULL) { return; }

    atr_return_code localRc = -1;
    int local_infoIdCount = p->infoIdCount;

    if (local_infoIdCount < 0 || local_infoIdCount > MAX_ELEMENT_COUNT) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4RUSI_BAD_INFO_ID_COUNT_INPUT),
                        "tx_authorized_rrs_services.atr_retrieve_side_information. The info ID count input is invalid",
                        TRACE_DATA_INT(local_infoIdCount, "infoIdCount"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4rusi_parms), p, "atr4rusi_parms"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    int local_sideInfoIds[local_infoIdCount];
    memcpy_sk(local_sideInfoIds, p->sideInfoIds, (sizeof(int) * local_infoIdCount), 8);

    int local_sideInfoStates[local_infoIdCount];

    atr_uri_token uriToken;
    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    if (registryRC == 0) {
        atr4rusi(&localRc, uriToken, local_infoIdCount, local_sideInfoIds, &local_sideInfoStates);

        if (localRc == ATR_OK) {
            if (p->sideInfoStates != NULL) {memcpy_dk(p->sideInfoStates, local_sideInfoStates, (sizeof(int) * local_infoIdCount), 8);}
        } else if ((localRc == ATR_URI_TOKEN_INV) || (localRc == ATR_UR_TOKEN_INV)) {
            // If the URI token is invalid, free it from the registry.
            registryFree(&(p->uriRegistryToken), TRUE);
        }
    }

    if (p->registryReturnCode != NULL) {memcpy_dk(p->registryReturnCode, &registryRC, sizeof(registryRC), 8);}
    memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4RUSI_EXIT),
            "tx_authorized_rrs_services.atr_retrieve_side_information. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_RAWDATA((sizeof(int) * local_infoIdCount),local_sideInfoStates, "Side Info States"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Retrieves a UR interest during restart.
 *
 * @param p The structure holding atr4irni input parameters.
 */
void atr_retrieve_ur_interest(struct atr4irni_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IRNI_ENTRY),
            "tx_authorized_rrs_services.atr_retrieve_ur_interest. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4irni_parms), p, "atr4irni_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->rrsReturnCode == NULL) { return; }

    int registryRC = -1;
    RegistryToken uriRegistryToken;

    atr_return_code localRc = -1;
    atr_pdata_length local_pdataLen = 0;
    atr_context_token local_contextToken;
    atr_uri_token local_urInterestToken;
    atr_urid local_urIdentifier;
    atr_role local_rmRole;
    atr_ur_state local_loggedState;

    char local_persistentData[ATR_MAX_PDATA_LENGTH];

    atr_resource_manager_token localResMgrToken;
    int resourceManagerTokenRegistryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrNameRegistryReturnCode != NULL) {memcpy_dk(p->resMgrNameRegistryReturnCode, &resourceManagerTokenRegistryRC, sizeof(resourceManagerTokenRegistryRC), 8);}
    if (resourceManagerTokenRegistryRC == 0) {
        atr4irni(&localRc,
                 localResMgrToken,
                 local_contextToken,
                 local_urInterestToken,
                 local_urIdentifier,
                 &local_rmRole,
                 &local_loggedState,
                 p->pdataBuffLen,
                 &local_pdataLen,
                 local_persistentData);
    }

    memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);

    if (localRc == ATR_OK) {
        registryRC = addUriTokenToRegistry(local_urInterestToken, &uriRegistryToken);
        if (p->registryReturnCode != NULL) {memcpy_dk(p->registryReturnCode, &registryRC, sizeof(registryRC), 8);}

        if (registryRC == 0) {
            if (p->contextToken != NULL) {memcpy_dk(p->contextToken, &local_contextToken, sizeof(atr_context_token), 8);}
            if (p->urInterestToken != NULL) {memcpy_dk(p->urInterestToken, &local_urInterestToken, sizeof(atr_uri_token), 8);}
            if (p->uriRegistryToken != NULL) {memcpy_dk(p->uriRegistryToken, &uriRegistryToken, sizeof(uriRegistryToken), 8);}
            if (p->urIdentifier != NULL) {memcpy_dk(p->urIdentifier, &local_urIdentifier, sizeof(atr_urid), 8);}
            if (p->rmRole != NULL) {memcpy_dk(p->rmRole, &local_rmRole, sizeof(atr_role), 8);}
            if (p->loggedState != NULL) {memcpy_dk(p->loggedState, &local_loggedState, sizeof(atr_ur_state), 8);}
            if (p->pdataLen != NULL) {memcpy_dk(p->pdataLen, &local_pdataLen, sizeof(atr_pdata_length), 8);}
            if (p->persistentData != NULL) {memcpy_dk(p->persistentData, &local_persistentData, local_pdataLen, 8);}
        } else {
            // ----------------------------------------------------------------
            // Not a lot we can do except hope that we can continue with the
            // next URI.
            // ----------------------------------------------------------------
            atr_non_persistent_data local_currentNonPersistentData;
            memset(local_currentNonPersistentData, 0, sizeof(atr_non_persistent_data));
            atr4irri(&localRc,
                     local_urInterestToken,
                     ATR_RESPOND_CONTINUE,
                     local_currentNonPersistentData);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IRNI_EXIT),
            "tx_authorized_rrs_services.atr_retrieve_ur_interest. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_INT(resourceManagerTokenRegistryRC, "Resource manager token registry Return Code"),
            TRACE_DATA_RAWDATA(sizeof(atr_context_token), local_contextToken, "Context Token"),
            TRACE_DATA_RAWDATA(sizeof(atr_uri_token), local_urInterestToken, "URI Token"),
            TRACE_DATA_RAWDATA(sizeof(RegistryToken), &uriRegistryToken, "URI Registry Token"),
            TRACE_DATA_RAWDATA(sizeof(atr_urid),local_urIdentifier, "URID"),
            TRACE_DATA_INT(local_rmRole, "Resource Manager Role"),
            TRACE_DATA_INT(local_loggedState, "Logged State"),
            TRACE_DATA_INT(local_pdataLen, "PDATA Length"),
            TRACE_DATA_RAWDATA(local_pdataLen, local_persistentData, "PDATA"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Set Environment.
 *
 * @param p The structure holding atr4senv input parameters.
 */
void atr_set_environment(struct atr4senv_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SENV_ENTRY),
            "tx_authorized_rrs_services.atr_set_environment. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4senv_parms), p, "atr4senv_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;
    atr_diag_area local_diagnosticArea;
    int local_contentCount = p->contentCount;

    // The maximum count is currently 2.
    if (local_contentCount < 0 || local_contentCount > MAX_ELEMENT_COUNT) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4SENV_BAD_CONTENT_COUNT_INPUT),
                        "tx_authorized_rrs_services.atr_set_environment. The content count input is invalid",
                        TRACE_DATA_INT(local_contentCount, "contentCount"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4senv_parms), p, "atr4senv_parms"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    int local_environmentIds[local_contentCount];
    memcpy_sk(local_environmentIds, p->environmentIds, (sizeof(int) * local_contentCount), 8);

    int local_environmentIdValues[local_contentCount];
    memcpy_sk(local_environmentIdValues, p->environmentIdValues, (sizeof(int) * local_contentCount), 8);

    int local_envProtectionValues[local_contentCount];
    memcpy_sk(local_envProtectionValues, p->envProtectionValues, (sizeof(int) * local_contentCount), 8);

    atr_context_token contextToken;
    memset(contextToken, 0x00, sizeof(atr_context_token));
    int envScope = 1;

    atr4senv(&localRc,
             local_diagnosticArea,
             envScope,
             contextToken,
             p->envStoken,
             local_contentCount,
             local_environmentIds,
             local_environmentIdValues,
             local_envProtectionValues);

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
    if (p->diagnosticArea != NULL) {memcpy_dk(p->diagnosticArea, &local_diagnosticArea, sizeof(atr_diag_area), 8);}

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SENV_EXIT),
            "tx_authorized_rrs_services.atr_set_environment. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_RAWDATA(sizeof(atr_diag_area), local_diagnosticArea, "Diagnostic Area"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Expresses an interest in a context.  We express an interest in a context
 * when we want to be notified when the context is ending with a UR attached
 * to it.
 *
 * @param ctxToken The context token representing the context where the interest will be expressed.
 * @param rmToken Our resource manager token.
 * @param ctxInterestToken The context interest token, which will be filled in on return.
 *
 * @return The return code from CTX4EINT.
 */
static ctx_return_code expressContextInterest(ctx_context_token ctxToken, ctx_resource_manager_token rmToken, ctx_ci_token ctxInterestToken) {
    ctx_return_code returnCode = -1;
    ctx_option memtermOption = CTX_ALL_TERMINATIONS;
    ctx_option multipleInterestOption = CTX_CONDITIONAL;
    ctx_ci_data contextInterestData;
    ctx_context_token ignoreCurrentCtxToken;
    ctx_ci_data ignoreCurrentContextInterestData;
    ctx_resource_manager_name workManagerName;

    memset(contextInterestData, 0, sizeof(contextInterestData));

    ctx4eint(&returnCode, // Output, the return code from the service.
             rmToken, // Input, our resource manager token.
             ctxToken, // Input, our context token.
             memtermOption,  // Input, the memterm option.
             contextInterestData,  // Input, the context interest data we supply.
             ignoreCurrentCtxToken,  // Output, the current context token if ctxToken was 0s.
             ctxInterestToken,  // Output, the context interest token.
             ignoreCurrentContextInterestData,  // Output, the current context interest data if there was already an interest for our RM.
             multipleInterestOption, // Input, multiple interest option, tells context services not to make a 2nd interest.
             workManagerName); // Output, the work manager name for the context.

    return returnCode;
}

/**
 * Prepares the UR associated with the given UR interest token.
 *
 * @param p The structure holding atr4aprp input parameters.
 */
void atr_prepare_agent_ur(struct atr4aprp_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4APRP_ENTRY),
            "tx_authorized_rrs_services.atr_prepare_agent_ur. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4aprp_parms), p, "atr4aprp_parms"),
            TRACE_DATA_END_PARMS);
    }

    atr_return_code localRc = -1;

    atr_uri_token uriToken;
    ctx_return_code expressInterestReturnCode = 0;
    ctx_return_code deleteInterestReturnCode = 0;
    int registryReturnCodeContext = 0;
    int registryReturnCodeContextInterest = 0;
    int registryReturnCodeRMToken = 0;
    int registryReturnCodeUri = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    if (p->registryReturnCodeUri != NULL) {memcpy_dk(p->registryReturnCodeUri, &registryReturnCodeUri, sizeof(int), 8);}
    if (registryReturnCodeUri == 0) {
        ctx_context_token ctxToken;
        registryReturnCodeContext = getContextTokenFromRegistry(&(p->contextRegistryToken), &ctxToken);
        if (p->registryReturnCodeContext != NULL) {memcpy_dk(p->registryReturnCodeContext, &registryReturnCodeContext, sizeof(int), 8);}
        if (registryReturnCodeContext == 0) {
            ctx_resource_manager_token localResMgrToken;
            registryReturnCodeRMToken = getFromNativeRegistry(&(p->rmToken), &localResMgrToken, sizeof(ctx_resource_manager_token));
            if (p->registryReturnCodeResMgrToken != NULL) {memcpy_dk(p->registryReturnCodeResMgrToken, &registryReturnCodeRMToken, sizeof(int), 8);}
            if (registryReturnCodeRMToken == 0) {
                // ---------------------------------------------------------------
                // First express context interest so our exit gets control if the
                // UR goes in-doubt and the server abends.
                // ---------------------------------------------------------------
                ctx_ci_token ctxInterestToken;
                expressInterestReturnCode = expressContextInterest(ctxToken, localResMgrToken, ctxInterestToken);
                if (p->ctxExpressInterestReturnCode != NULL) {memcpy_dk(p->ctxExpressInterestReturnCode, &expressInterestReturnCode, sizeof(expressInterestReturnCode), 8);}
                if (expressInterestReturnCode == CTX_OK) {
                    // -----------------------------------------------------------
                    // Gotta put the CI token in the native registry.  It's a
                    // protected resource.
                    // -----------------------------------------------------------
                    RegistryToken ciRegistryToken;
                    registryReturnCodeContextInterest = addContextInterestTokenToRegistry(ctxInterestToken, &ciRegistryToken);
                    if (p->registryReturnCodeContextInterest != NULL) {memcpy_dk(p->registryReturnCodeContextInterest, &registryReturnCodeContextInterest, sizeof(int), 8);}
                    if (registryReturnCodeContextInterest == 0) {
                        // -------------------------------------------------------
                        // Finally... prepare.
                        // -------------------------------------------------------
                        atr4aprp(&localRc, uriToken, p->logOption);
                        if (p->rrsPrepareReturnCode != NULL) {memcpy_dk(p->rrsPrepareReturnCode, &localRc, sizeof(atr_return_code), 8);}

                        // If the UR is finished, remove the URI token from the registry.
                        switch(localRc) {
                            case ATR_OK:
                                // Copy the context interest token back if the UR is now in-doubt.
                                if (p->contextInterestRegistryToken_p != NULL) {memcpy_dk(p->contextInterestRegistryToken_p, &ciRegistryToken, sizeof(ciRegistryToken), 8);}
                                break;
                            case ATR_FORGET:
                            case ATR_BACKED_OUT:
                            case ATR_BACKED_OUT_OUTCOME_PENDING:
                            case ATR_BACKED_OUT_OUTCOME_MIXED:
                            case ATR_URI_TOKEN_INV:
                                registryFree(&(p->uriRegistryToken), TRUE);
                                // Drop into next statements
                            default:
                                // Delete our context interest if we're not in-doubt.
                                ctx4dint(&deleteInterestReturnCode, ctxInterestToken);
                        }
                    } else {
                        // Delete our context interest if we couldn't add it to the registry.
                        ctx4dint(&deleteInterestReturnCode, ctxInterestToken);
                    }
                }
            }
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4APRP_EXIT),
            "tx_authorized_rrs_services.atr_prepare_agent_ur. Exit",
            TRACE_DATA_INT(localRc, "ATR4APRP Return Code"),
            TRACE_DATA_INT(registryReturnCodeUri, "Registry services URI return code"),
            TRACE_DATA_INT(registryReturnCodeContext, "Registry services context return code"),
            TRACE_DATA_INT(registryReturnCodeContextInterest, "Registry services add context interest return code"),
            TRACE_DATA_INT(registryReturnCodeRMToken, "Registry services resource manager token return code"),
            TRACE_DATA_INT(expressInterestReturnCode, "CTX4EINT return code"),
            TRACE_DATA_INT(deleteInterestReturnCode, "CTX4DINT return code (if ATR4APRP failed)"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Commits the UR associated with the given UR interest token.
 *
 * @param p The structure holding atr4acmt input parameters.
 */
void atr_commit_agent_ur(struct atr4acmt_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ACMT_ENTRY),
            "tx_authorized_rrs_services.atr_commit_agent_ur. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4acmt_parms), p, "atr4acmt_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCodeUri == NULL) { return; }

    atr_uri_token uriToken;
    ctx_ci_token ciToken;
    ctx_return_code dintRc = 0;
    atr_return_code localRc = -1;
    int getCiRegistryRC = 0;
    RegistryToken nullRegistryToken;
    unsigned char gotCiRegistryToken = FALSE;

    memset(&nullRegistryToken, 0, sizeof(RegistryToken));
    int getUriRegistryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCodeUri, &getUriRegistryRC, sizeof(int), 8);
    if (getUriRegistryRC == 0) {
        // -------------------------------------------------------------------
        // If there is a context interest registry token, try to look up the
        // token.  We would have had a token if the UR was in-doubt.
        // -------------------------------------------------------------------
        if (memcmp(&nullRegistryToken, &(p->ciRegistryToken), sizeof(nullRegistryToken)) != 0) {
            getCiRegistryRC = getContextInterestTokenFromRegistry(&(p->ciRegistryToken), ciToken);
            if (p->registryReturnCodeCi != NULL) {memcpy_dk(p->registryReturnCodeCi, &getCiRegistryRC, sizeof(int), 8);}
            gotCiRegistryToken = (getCiRegistryRC == 0);
        }

        // -------------------------------------------------------------------
        // Proceed with the commit unless a CI registry token existed and we
        // failed to get it.
        // -------------------------------------------------------------------
        if (getCiRegistryRC == 0) {
            atr4acmt(&localRc, uriToken, p->logOption);
            if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

            // Remove the URI token from the registry if the UR is finished.
            switch (localRc) {
                case ATR_OK:
                case ATR_COMMITTED_OUTCOME_PENDING:
                case ATR_COMMITTED_OUTCOME_MIXED:
                    // Remove the URI token from the registry if the UR is finished.
                    if (p->logOption == ATR_DEFER_IMPLICIT) {
                        registryFree(&(p->uriRegistryToken), TRUE);
                    }
                    // Also remove the context interest, if there was one.
                    if (gotCiRegistryToken == TRUE) {
                        ctx4dint(&dintRc, ciToken);
                        registryFree(&(p->ciRegistryToken), TRUE);
                    }
                    break;
                case ATR_URI_TOKEN_INV:
                    registryFree(&(p->uriRegistryToken), TRUE);
                    if (gotCiRegistryToken == TRUE) {
                        ctx4dint(&dintRc, ciToken);
                        registryFree(&(p->ciRegistryToken), TRUE);
                    }
                    break;
            }
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ACMT_EXIT),
            "tx_authorized_rrs_services.atr_commit_agent_ur. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(dintRc, "CTX4DINT Return Code"),
            TRACE_DATA_INT(getUriRegistryRC, "Get URI token from registry return code"),
            TRACE_DATA_INT(getCiRegistryRC, "Get CI token from registry return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Delegates the 2 phase commit process to RRS.
 *
 * @param p The structure holding atr4adct input parameters.
 */
void atr_delegate_commit_agent_ur(struct atr4adct_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ADCT_ENTRY),
            "tx_authorized_rrs_services.atr_delegate_commit_agent_ur. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4adct_parms), p, "atr4adct_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCode == NULL) { return; }

    atr_uri_token uriToken;
    atr_return_code localRc = -1;
    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCode, &registryRC, sizeof(int), 8);
    if (registryRC == 0) {
        atr4adct(&localRc, uriToken, p->logOption, p->commitOptions);
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

        // Remove the URI token from the registry if the UR is finished.
        switch (localRc) {
            case ATR_OK:
            case ATR_COMMITTED_OUTCOME_PENDING:
            case ATR_COMMITTED_OUTCOME_MIXED:
                if (p->logOption == ATR_DEFER_IMPLICIT) {
                    registryFree(&(p->uriRegistryToken), TRUE);
                }
                break;
            case ATR_FORGET:
            case ATR_BACKED_OUT:
            case ATR_BACKED_OUT_OUTCOME_PENDING:
            case ATR_BACKED_OUT_OUTCOME_MIXED:
            case ATR_URI_TOKEN_INV:
                registryFree(&(p->uriRegistryToken), TRUE);
                break;
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ADCT_EXIT),
            "tx_authorized_rrs_services.atr_delegate_commit_agent_ur. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Rolls back the UR associated with the given UR interest token.
 *
 * @param p The structure holding atr4abak input parameters.
 */
void atr_backout_agent_ur(struct atr4abak_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ABAK_ENTRY),
            "tx_authorized_rrs_services.atr_backout_agent_ur. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4abak_parms), p, "atr4abak_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCodeUri == NULL) { return; }

    atr_uri_token uriToken;
    ctx_ci_token ciToken;
    ctx_return_code dintRc = 0;
    atr_return_code localRc = -1;
    int getCiRegistryRC = 0;
    RegistryToken nullRegistryToken;
    unsigned char gotCiRegistryToken = FALSE;

    memset(&nullRegistryToken, 0, sizeof(RegistryToken));
    int getUriRegistryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCodeUri, &getUriRegistryRC, sizeof(int), 8);
    if (getUriRegistryRC == 0) {
        // -------------------------------------------------------------------
        // If there is a context interest registry token, try to look up the
        // token.  We would have had a token if the UR was in-doubt.
        // -------------------------------------------------------------------
        if (memcmp(&nullRegistryToken, &(p->ciRegistryToken), sizeof(nullRegistryToken)) != 0) {
            getCiRegistryRC = getContextInterestTokenFromRegistry(&(p->ciRegistryToken), ciToken);
            if (p->registryReturnCodeCi != NULL) {memcpy_dk(p->registryReturnCodeCi, &getCiRegistryRC, sizeof(int), 8);}
            gotCiRegistryToken = (getCiRegistryRC == 0);
        }

        // -------------------------------------------------------------------
        // Proceed with the backout unless a CI registry token existed and we
        // failed to get it.
        // -------------------------------------------------------------------
        if (getCiRegistryRC == 0) {
            atr4abak(&localRc, uriToken, p->logOption);
            if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

            switch (localRc) {
                case ATR_OK:
                case ATR_BACKED_OUT_OUTCOME_PENDING:
                case ATR_BACKED_OUT_OUTCOME_MIXED:
                    // Remove the URI token from the registry if the UR is finished.
                    if (p->logOption == ATR_DEFER_IMPLICIT) {
                        registryFree(&(p->uriRegistryToken), TRUE);
                    }
                    // Also remove the context interest, if there was one.
                    if (gotCiRegistryToken == TRUE) {
                        ctx4dint(&dintRc, ciToken);
                        registryFree(&(p->ciRegistryToken), TRUE);
                    }
                    break;
                case ATR_URI_TOKEN_INV:
                    registryFree(&(p->uriRegistryToken), TRUE);
                    if (gotCiRegistryToken == TRUE) {
                        ctx4dint(&dintRc, ciToken);
                        registryFree(&(p->ciRegistryToken), TRUE);
                    }
                    break;
            }
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ABAK_EXIT),
            "tx_authorized_rrs_services.atr_backout_agent_ur. Exit",
            TRACE_DATA_INT(localRc, "ATR4ABAK Return Code"),
            TRACE_DATA_INT(dintRc, "CTX4DINT Return Code"),
            TRACE_DATA_INT(getUriRegistryRC, "Get URI token from registry return code"),
            TRACE_DATA_INT(getCiRegistryRC, "Get CI token from registry return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Forgets the UR interest associated with the given UR interest token.
 *
 * @param p The structure holding atr4afgt input parameters.
 */
void atr_forget_agent_ur_interest(struct atr4afgt_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4AFGT_ENTRY),
            "tx_authorized_rrs_services.atr_forget_agent_ur_interest. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4afgt_parms), p, "atr4afgt_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCode == NULL) { return; }

    atr_return_code localRc = -1;
    atr_uri_token uriToken;
    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCode, &registryRC, sizeof(int), 8);
    if (registryRC == 0) {
        atr4afgt(&localRc, uriToken, p->logOption);
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

        // If the UR is finished, remove the URI token from the registry.
        switch (localRc) {
            case ATR_OK:
            case ATR_OK_NO_CONTEXT:
            case ATR_FORGET_NOT_REQUIRED:
            case ATR_URI_TOKEN_INV:
                registryFree(&(p->uriRegistryToken), TRUE);
                break;
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4AFGT_EXIT),
            "tx_authorized_rrs_services.atr_forget_agent_ur_interest. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Post Deferred UR exit reply.
 *
 * @param p The structure holding atr4pdue input parameters.
 */
void atr_post_deferred_ur_exit(struct atr4pdue_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4PDUE_ENTRY),
            "tx_authorized_rrs_services.atr_post_deferred_ur_exit. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4pdue_parms), p, "atr4pdue_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCode == NULL) { return; }

    atr_return_code localRc = -1;
    atr_uri_token uriToken;
    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCode, &registryRC, sizeof(int), 8);
    if (registryRC == 0) {
        // Check p->exitNumber.  Can't be outside what RRS expects (Dec 1-11. Hex 1-B)
        if (p->exitNumber > 11) {
            if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}
            return;
        }

        atr4pdue(&localRc, uriToken, p->exitNumber, p->completionCode);
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

        // If the UR is finished, remove the URI token from the registry.
        if (localRc == ATR_URI_TOKEN_INV) {
            registryFree(&(p->uriRegistryToken), TRUE);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4PDUE_EXIT),
            "tx_authorized_rrs_services.atr_post_deferred_ur_exit. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Respond to retrieved interest.
 *
 * @param p The structure holding atr4irri input parameters.
 */
void atr_respond_to_retrieved_interest(struct atr4irri_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IRRI_ENTRY),
            "tx_authorized_rrs_services.atr_respond_to_retrieved_interest. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4irri_parms), p, "atr4irri_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCode == NULL) { return; }

    atr_return_code localRc = -1;
    atr_uri_token uriToken;
    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCode, &registryRC, sizeof(int), 8);
    if (registryRC == 0) {
        // Check that the responseCode is valid (must be either 0 or 1)
        if( p->responseCode < 0 || p->responseCode > 1 ) {
            if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}
            return;
        }
        atr4irri(&localRc, uriToken, p->responseCode, p->nonPData);
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

        // If the UR is finished, remove the URI from the registry.
        if (localRc == ATR_URI_TOKEN_INV) {
            registryFree(&(p->uriRegistryToken), TRUE);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4IRRI_EXIT),
            "tx_authorized_rrs_services.atr_respond_to_retrieved_interest. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Sets the persistent interest data for the UR associated with input URI token.
 *
 * @param p The structure holding atr4spid input parameters.
 */
void atr_set_persistent_interest_data(struct atr4spid_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SPID_ENTRY),
            "tx_authorized_rrs_services.atr_set_persistent_interest_data. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4spid_parms), p, "atr4spid_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCode == NULL) { return; }

    atr_return_code localRc = -1;
    atr_uri_token uriToken;

    int local_pdataLenth = p->pdataLength;

    if (local_pdataLenth < 0 || local_pdataLenth> ATR_MAX_PDATA_LENGTH) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4SPID_BAD_PDATA_LENGTH_INPUT),
                        "tx_authorized_rrs_services.atr_set_persistent_interest_data. The pdata length input is invalid",
                        TRACE_DATA_INT(local_pdataLenth, "pdataLenth"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4spid_parms), p, "atr4spid_parms"),
                        TRACE_DATA_END_PARMS);
        }
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}
        return;
    }

    char local_pdata[local_pdataLenth];
    memcpy_sk(local_pdata, p->pdata, local_pdataLenth, 8);

    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCode, &registryRC, sizeof(int), 8);
    if (registryRC == 0) {
        atr4spid(&localRc, uriToken, local_pdataLenth, local_pdata);
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

        // If the UR is gone, remove the URI token from the registry.
        if (localRc == ATR_URI_TOKEN_INV) {
            registryFree(&(p->uriRegistryToken), TRUE);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SPID_EXIT),
            "tx_authorized_rrs_services.atr_set_persistent_interest_data. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Sets the persistent interest data for the UR associated with input URI token.
 *
 * @param p The structure holding atr4sspc input parameters.
 */
void atr_set_syncpoint_controls(struct atr4sspc_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SSPC_ENTRY),
            "tx_authorized_rrs_services.atr_set_syncpoint_controls. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4sspc_parms), p, "atr4sspc_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCode == NULL) { return; }

    atr_return_code localRc = -1;
    atr_uri_token uriToken;
    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCode, &registryRC, sizeof(int), 8);
    if (registryRC == 0) {
        atr4sspc(&localRc, uriToken, p->prepareExitCode, p->commitExitCode, p->backoutExitCode, p->rmRole);
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

        // If the UR is gone, remove the URI token from the registry.
        if (localRc == ATR_URI_TOKEN_INV) {
            registryFree(&(p->uriRegistryToken), TRUE);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SSPC_EXIT),
            "tx_authorized_rrs_services.atr_set_syncpoint_controls. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Sets side information for an interest in an UR.
 *
 * @param p The structure holding atr4susi input parameters.
 */
void atr_set_side_information(struct atr4susi_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SUSI_ENTRY),
            "tx_authorized_rrs_services.atr_set_side_information. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4susi_parms), p, "atr4susi_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->registryReturnCode == NULL) { return; }

    atr_return_code localRc = -1;
    atr_uri_token uriToken;

    int local_elementCount = p->elementCount;

    // The maximum count is currently 8.
    if (local_elementCount < 0 || local_elementCount > MAX_ELEMENT_COUNT) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4SUSI_BAD_ELEMENT_COUNT_INPUT),
                        "tx_authorized_rrs_services.atr_set_side_information. The element count input is invalid",
                        TRACE_DATA_INT(local_elementCount, "elementCount"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4susi_parms), p, "atr4susi_parms"),
                        TRACE_DATA_END_PARMS);
        }
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}
        return;
    }

    int local_infoIdsArray[local_elementCount];
    memcpy_sk(local_infoIdsArray, p->infoIdsArray, (sizeof(int) * local_elementCount), 8);

    int registryRC = getUriTokenFromRegistry(&(p->uriRegistryToken), &uriToken);
    memcpy_dk(p->registryReturnCode, &registryRC, sizeof(int), 8);
    if (registryRC == 0) {
        atr4susi(&localRc, uriToken, local_elementCount, local_infoIdsArray);
        if (p->rrsReturnCode != NULL) {memcpy_dk(p->rrsReturnCode, &localRc, sizeof(atr_return_code), 8);}

        // If the UR is gone, remove the URI token from the registry.
        if ((localRc == ATR_URI_TOKEN_INV) || (localRc == ATR_UR_TOKEN_INV)) {
            registryFree(&(p->uriRegistryToken), TRUE);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SUSI_EXIT),
            "tx_authorized_rrs_services.atr_set_side_information. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Sets resource manager metadata.
 *
 * @param p The structure holding atr4sdat input parameters.
 */
void atr_set_rm_metadata(struct atr4sdta_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SDTA_ENTRY),
            "tx_authorized_rrs_services.atr_set_rm_metadata. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4sdta_parms), p, "atr4isln_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    // If the called is not authorized to remove RM, return normally. That is because
    // entries in the METADA logstream are only deleted if the user is authorized.
    if (isCallerAuthorizedATRSRV() == FALSE) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SDTA_USER_NOT_AUTH),
                "tx_authorized_rrs_services.atr_set_rm_metadata. Caller not authorized to log to METADATA logstream.",
                TRACE_DATA_END_PARMS);
        }
    
        int zeroReturnCode = 0;
        memcpy_dk(p->returnCode, &zeroReturnCode, sizeof(atr_return_code), 8);
        return;
    }
    
    atr_return_code localRc = -1;
    int local_resMgrMetadataLen = p->resMgrMetadataLen;

    if (local_resMgrMetadataLen < 0 || local_resMgrMetadataLen > ATR_MAX_RM_METADATA_LENGTH) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_AUTH_ATR4SDTA_BAD_METADA_BUFF_LENGTH_INPUT),
                        "tx_authorized_rrs_services.atr_set_rm_metadata. The metadata length input is invalid",
                        TRACE_DATA_INT(local_resMgrMetadataLen, "ResMgrMetadataLen"),
                        TRACE_DATA_RAWDATA(sizeof(struct atr4sdta_parms), p, "atr4sdta_parms"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);
        return;
    }

    char local_resMgrMetadata[local_resMgrMetadataLen];
    memcpy_sk(local_resMgrMetadata, p->resMgrMetadata, local_resMgrMetadataLen, 8);

    atr_resource_manager_token localResMgrToken;
    int registryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRC, sizeof(int), 8);}
    if (registryRC == 0) {
        atr4sdta(&localRc, localResMgrToken, local_resMgrMetadataLen, local_resMgrMetadata);
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SDTA_EXIT),
            "tx_authorized_rrs_services.atr_set_rm_metadata. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Retrieves resource manager metadata.
 *
 * @param p The structure holding atr4rdta input parameters.
 */
void atr_retrieve_rm_metadata(struct atr4rdta_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4RDTA_ENTRY),
            "tx_authorized_rrs_services.atr_retrieve_rm_metadata. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct atr4rdta_parms), p, "atr4rdta_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    atr_return_code localRc = -1;
    int local_resMgrMetadataLength = 0;
    char local_resMgrMetadata[ATR_MAX_RM_METADATA_LENGTH];

    atr_resource_manager_token localResMgrToken;
    int registryRC = getFromNativeRegistry(&(p->resMgrToken), &localResMgrToken, sizeof(atr_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRC, sizeof(int), 8);}
    if (registryRC == 0) {
        atr4rdta(&localRc,
                 localResMgrToken,
                 p->resMgrMetadataBuffLen,
                 &local_resMgrMetadataLength,
                 &local_resMgrMetadata);
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(atr_return_code), 8);

    if (localRc == ATR_OK) {
        if (p->resMgrMetadataLength != NULL) {memcpy_dk(p->resMgrMetadataLength, &local_resMgrMetadataLength, sizeof(int), 8);}
        if (p->resMgrMetadata != NULL) {memcpy_dk(p->resMgrMetadata, &local_resMgrMetadata, local_resMgrMetadataLength, 8);}
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4RDTA_EXIT),
            "tx_authorized_rrs_services.atr_retrieve_rm_metadata. Exit",
            TRACE_DATA_INT(localRc, "RRS Return Code"),
            TRACE_DATA_INT(registryRC, "Registry Return Code"),
            TRACE_DATA_INT(local_resMgrMetadataLength, "Resource Manager Metadata Length"),
            TRACE_DATA_RAWDATA(local_resMgrMetadataLength, local_resMgrMetadata, "Resource Manager Metadata"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Creates a new privately managed context.
 *
 * @param p The structure holding ctx4begc input parameters.
 */
void ctx_begin_context(struct ctx4begc_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4BEGC_ENTRY),
            "tx_authorized_rrs_services.ctx_begin_context. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct ctx4begc_parms), p, "ctx4begc_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    ctx_return_code localRc = -1;
    ctx_context_token local_ctxToken;

    int registryRC = -1;

    ctx_resource_manager_token localResMgrToken;
    registryRC = getFromNativeRegistry(&(p->rmRegistryToken), &localResMgrToken, sizeof(ctx_resource_manager_token));
    if (p->resMgrTokenRegistryReturnCode != NULL) {memcpy_dk(p->resMgrTokenRegistryReturnCode, &registryRC, sizeof(int), 8);}
    if (registryRC == 0) {
        ctx4begc(&localRc, localResMgrToken, local_ctxToken);
    }

    if (localRc == ATR_OK) {

        // -------------------------------------------------------------------
        // Put the new context in the registry.
        // -------------------------------------------------------------------
        RegistryDataType dataType = RRSCTX;
        RegistryToken registryToken;
        RegistryDataArea dataArea;
        memset(&dataArea, 0, sizeof(dataArea));
        memcpy(&dataArea, local_ctxToken, sizeof(local_ctxToken));
        registryRC = registryPut(dataType, &dataArea, &registryToken);
        if (registryRC == 0) {
            if (p->ctxRegistryToken != NULL) {memcpy_dk(p->ctxRegistryToken, &registryToken, sizeof(registryToken), 8);}
            if (p->ctxToken != NULL) {memcpy_dk(p->ctxToken, &local_ctxToken, sizeof(ctx_context_token), 8);}
        } else {
            localRc = -1;
            ctx4endc(&localRc, local_ctxToken, CTX_NORMAL_TERMINATION);
        }
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(ctx_return_code), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4BEGC_EXIT),
            "tx_authorized_rrs_services.ctx_begin_context. Exit",
            TRACE_DATA_INT(localRc, "Context Services Return Code"),
            TRACE_DATA_INT(registryRC, "Registry services return code"),
            TRACE_DATA_RAWDATA(sizeof(ctx_context_token), local_ctxToken, "Context Token Created"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Switches the current thread's context with the one specified.
 *
 * @param p The structure holding ctx4swch input parameters.
 */
void ctx_context_switch(struct ctx4swch_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SWCH_ENTRY),
            "tx_authorized_rrs_services.ctx_context_switch. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct ctx4swch_parms), p, "ctx4swch_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    ctx_return_code localRc = -1;
    ctx_context_token local_outputCtxToken;

    // -----------------------------------------------------------------------
    // Make sure the input context token is in the registry.  If we're
    // switching to the native context, the token will be all zeros.
    // -----------------------------------------------------------------------
    ctx_context_token  local_ctxToken;
    RegistryToken      native_ctx_registry_token;
    int                registryRC = 0;

    memset(&native_ctx_registry_token, 0, sizeof(native_ctx_registry_token));
    if (memcmp(&native_ctx_registry_token, &(p->inputCtxRegistryToken), sizeof(native_ctx_registry_token)) == 0) {
        memset(&local_ctxToken, 0, sizeof(local_ctxToken));
        ctx4swch(&localRc, local_ctxToken, local_outputCtxToken);
    } else {
        registryRC = getContextTokenFromRegistry(&(p->inputCtxRegistryToken), &local_ctxToken);
        if (registryRC == 0) {
            ctx4swch(&localRc, local_ctxToken, local_outputCtxToken);
        }
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(ctx_return_code), 8);

    if (localRc == ATR_OK) {
        if (p->outputCtxToken != NULL) {memcpy_dk(p->outputCtxToken, &local_outputCtxToken, sizeof(ctx_context_token), 8);}
    }
    
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4SWCH_EXIT),
            "tx_authorized_rrs_services.ctx_context_switch. Exit",
            TRACE_DATA_INT(localRc, "Context Services Return Code"),
            TRACE_DATA_INT(registryRC, "Registry services return code"),
            TRACE_DATA_RAWDATA(sizeof(ctx_context_token), local_outputCtxToken, "Output Context Token"),
            TRACE_DATA_END_PARMS);
    }
}

/**
 * Switches the current thread's context with the one specified.
 *
 * @param p The structure holding ctx4endc input parameters.
 */
void ctx_end_context(struct ctx4endc_parms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ENDC_ENTRY),
            "tx_authorized_rrs_services.ctx_end_context. Entry",
            TRACE_DATA_RAWDATA(sizeof(struct ctx4endc_parms), p, "ctx4endc_parms"),
            TRACE_DATA_END_PARMS);
    }
    if (p->returnCode == NULL) { return; }

    ctx_return_code localRc = -1;

    // -----------------------------------------------------------------------
    // Make sure the input context token is in the registry. If we are ending
    // a native context, the token will be all zeros
    // -----------------------------------------------------------------------
    ctx_context_token local_ctxToken;
    RegistryToken      native_ctx_registry_token;
    int                registryRC = 0;

    memset(&native_ctx_registry_token, 0, sizeof(native_ctx_registry_token));
    if (memcmp(&native_ctx_registry_token, &(p->inputCtxRegistryToken), sizeof(native_ctx_registry_token)) == 0) {
        memset(&local_ctxToken, 0, sizeof(local_ctxToken));
        ctx4endc(&localRc, local_ctxToken, p->completionType);
    } else {
        registryRC = getContextTokenFromRegistry(&(p->inputCtxRegistryToken), &local_ctxToken);
        if (registryRC == 0) {
            ctx4endc(&localRc, local_ctxToken, p->completionType);
            registryFree(&(p->inputCtxRegistryToken), TRUE);
        }
    }

    memcpy_dk(p->returnCode, &localRc, sizeof(ctx_return_code), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_AUTH_ATR4ENDC_EXIT),
            "tx_authorized_rrs_services.ctx_end_context. Exit",
            TRACE_DATA_INT(localRc, "Context Services Return Code"),
            TRACE_DATA_INT(registryRC, "Registry services return code"),
            TRACE_DATA_RAWDATA(sizeof(ctx_context_token), local_ctxToken, "Context Token Ended"),
            TRACE_DATA_END_PARMS);
    }
}

#pragma insert_asm(" CVT DSECT=YES")
#pragma insert_asm(" IHAECVT")
#pragma insert_asm(" ATRFZSRV")
#pragma insert_asm(" ATRFZQRY")
