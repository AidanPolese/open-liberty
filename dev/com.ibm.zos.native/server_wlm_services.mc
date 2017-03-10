/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Assorted routines that interface with z/OS Workload Management Services
 * which require an authorized caller.
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#include "include/gen/ihapsa.h"
#include "include/gen/cvt.h"
#include "include/gen/ikjtcb.h"
#include "include/gen/ihastcb.h"

#include "include/common_defines.h"
#include "include/common_mc_defines.h"
#include "include/mvs_utils.h"
#include "include/server_wlm_services.h"
#include "include/ras_tracing.h"


#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WLM_SERVICES
#define TP_SERVER_WLM_SERVICES_WLM_CONNECT_ENTRY          1
#define TP_SERVER_WLM_SERVICES_WLM_CONNECT_RESULTS        2
#define TP_SERVER_WLM_SERVICES_WLM_CONNECT_EXIT           3
#define TP_SERVER_WLM_SERVICES_WLM_CONNECT_PRE_IWM4CON    4
#define TP_SERVER_WLM_SERVICES_WLM_DISCONNECT_ENTRY       5
#define TP_SERVER_WLM_SERVICES_WLM_DISCONNECT_RESULTS     6
#define TP_SERVER_WLM_SERVICES_WLM_DISCONNECT_EXIT        7
#define TP_SERVER_WLM_SERVICES_ENCLAVE_CREATE_ENTRY       8
#define TP_SERVER_WLM_SERVICES_CREATE_PRE_IWM4ECRE        9
#define TP_SERVER_WLM_SERVICES_CREATE_POST_IWM4ECRE      10
#define TP_SERVER_WLM_SERVICES_ENCLAVE_CREATE_EXIT       11
#define TP_SERVER_WLM_SERVICES_ENCLAVE_JOIN_ENTRY        12
#define TP_SERVER_WLM_SERVICES_JOIN_POST_IWMEJOIN        13
#define TP_SERVER_WLM_SERVICES_ENCLAVE_JOIN_EXIT         14
#define TP_SERVER_WLM_SERVICES_ENCLAVE_LEAVE_ENTRY       15
#define TP_SERVER_WLM_SERVICES_LEAVE_POST_IWMELEAV       16
#define TP_SERVER_WLM_SERVICES_ENCLAVE_LEAVE_EXIT        17
#define TP_SERVER_WLM_SERVICES_ENCLAVE_DELETE_ENTRY      18
#define TP_SERVER_WLM_SERVICES_ENCLAVE_DELETE_EXIT       19
#define TP_SERVER_WLM_SERVICES_ENCLAVE_L_DELETE_ENTRY    20
#define TP_SERVER_WLM_SERVICES_ENCLAVE_L_DELETE_EXIT     21
#define TP_SERVER_WLM_SERVICES_CREATEJ_ENTRY             22
#define TP_SERVER_WLM_SERVICES_CREATEJ_PRE_IWM4ECRE      23
#define TP_SERVER_WLM_SERVICES_CREATEJ_POST_IWM4ECRE     24
#define TP_SERVER_WLM_SERVICES_CREATEJ_POST_IWMEJOIN     25
#define TP_SERVER_WLM_SERVICES_CREATEJ_EXIT              26
#define TP_SERVER_WLM_SERVICES_LEAVEDELETE_ENTRY         27
#define TP_SERVER_WLM_SERVICES_LEAVEDELETE_POST_IWMELEAV 28
#define TP_SERVER_WLM_SERVICES_LEAVEDELETE_EXIT          29

static const int BAD_PARMS = 8;

/**
 * Gets a WLM Enclave token from the native registry.
 *
 * @param enclaveRegistryToken_p A pointer to the registry token used to look up the WLM Enclave token.
 * @param enclaveToken_p A pointer to the Enclave token, which will be filled in.
 *
 * @return The return code from registry services.  0 is success.
 */
static int getEnclaveTokenFromRegistry(RegistryToken* enclaveRegistryToken_p, wlmetoken_t* enclaveToken_p) {
    RegistryDataArea dataArea;
    int registryRC = registryGetAndSetUsed(enclaveRegistryToken_p, &dataArea);
    if (registryRC == 0) {
        memcpy(enclaveToken_p, &dataArea, sizeof(*enclaveToken_p));
        registrySetUnused(enclaveRegistryToken_p, TRUE);
    }
    return registryRC;
}

/**
 * Adds a WLM Enclave token to the native registry.
 *
 * @param enclaveToken The WLM Enclave token to add to the registry.
 * @param enclaveRegistryToken_p A pointer to the registry token which will be
 *                           filled in and used to look up the WLM Enclave token in
 *                           the registry.
 *
 * @return The return code from the registry service.  0 indicates success.
 */
static int addEnclaveTokenToRegistry(wlmetoken_t* enclaveToken_p, RegistryToken* enclaveRegistryToken_p) {
    RegistryDataType dataType = ENCLAVE;
    RegistryDataArea dataArea;
    memset(&dataArea, 0, sizeof(dataArea));
    memcpy(&dataArea, enclaveToken_p, sizeof(*enclaveToken_p));
    return registryPut(dataType, &dataArea, enclaveRegistryToken_p);
}


__asm(" IWM4EDEL PLISTVER=MAX,MF=(L,AUTO)" :
      "DS"(deletePlistArea));

typedef struct {
    wlmetoken_t localEnclaveToken31;
    int  wlmRC;
    int  wlmRSN;
    WLM_EnclaveDeleteData deleteData;
    char deletePlistArea31[sizeof(deletePlistArea)];
} localDelete_parms31;

static void local_wlm_enclave_delete(localDelete_parms31* parms);

/**
 * Drive IWM4CON WLM Connect
 *
 * @param parms pointer to <code>WLM_ConnectParms</code>
 *
 * @return void
*/
void wlm_connect(WLM_ConnectParms *parms)
{
    int localRC, localRSN;

    __asm(" IWM4CON PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(list_plistArea));


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_WLM_CONNECT_ENTRY),
                    "wlm_connect, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((parms->inSubSys == 0)                ||
        (parms->inSubSysName == 0)            ||
        (parms->outWLMConnectToken == 0)      ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    struct parm31 {
        unsigned int connectionToken;
        char subSys[WLM_SUBSYSTEM_MAX];
        char subSysNam[WLM_SUBSYSTEM_NAME_MAX];
        int rc;
        int rsn;
        char execute_plistArea[sizeof(list_plistArea)];
    };


    // ----------------------------------------------------------------
    // Setup and Call WLM Connect Service (IWMCONN)
    // ----------------------------------------------------------------
    struct parm31* parms31_p = __malloc31(sizeof(struct parm31));
    if (parms31_p != NULL) {
        strncpy_sk(parms31_p->subSys,
                   parms->inSubSys,
                   WLM_SUBSYSTEM_MAX,
                   8);
        strncpy_sk(parms31_p->subSysNam,
                   parms->inSubSysName,
                   WLM_SUBSYSTEM_NAME_MAX,
                   8);
        memcpy(parms31_p->execute_plistArea, &list_plistArea, sizeof(list_plistArea));

        if (TraceActive(trc_level_detailed)) {
            struct parm31 temp;
            memcpy(&temp, parms31_p, sizeof(temp)); // LE APAR OA37620

            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_WLM_SERVICES_WLM_CONNECT_PRE_IWM4CON),
                        "wlm_connect, 31bit parmarea for IWM4CON",
                        TRACE_DATA_RAWDATA(sizeof(*parms31_p),
                                           &temp,  // OA37620, change &temp to: parms31_p
                                           "parms31_p"),
                                           TRACE_DATA_END_PARMS);
        }

        __asm(" IWM4CON SUBSYS=((%3)),SUBSYSNM=((%4)),WORK_MANAGER=YES,"
              "CONNTKNKEYP=PSWKEY,SERVER_MANAGER=NO,EWLM=YES,"
              "CONNTKN=%0,RETCODE=%1,RSNCODE=%2,MF=(E,(%5),COMPLETE)" :
              "=m"(parms31_p->connectionToken),"=m"(parms31_p->rc),"=m"(parms31_p->rsn) :
              "r"(parms31_p->subSys),"r"(parms31_p->subSysNam),"r"(parms31_p->execute_plistArea):
              "r0","r1","r14","r15");

        // ----------------------------------------------------------------
        // Push output parms back to caller
        // ----------------------------------------------------------------
        memcpy_dk(parms->outWLMConnectToken,
                  &parms31_p->connectionToken,
                  sizeof(parms31_p->connectionToken),
                  8);
        memcpy_dk(parms->outRC,
                  &parms31_p->rc,
                  sizeof(parms31_p->rc),
                  8);
        memcpy_dk(parms->outRSN,
                  &parms31_p->rsn,
                  sizeof(parms31_p->rsn),
                  8);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_WLM_SERVICES_WLM_CONNECT_RESULTS),
                        "wlm_connect, IWM4CON results",
                        TRACE_DATA_HEX_INT(parms31_p->connectionToken, "parms31_p->connectionToken"),
                        TRACE_DATA_HEX_INT(parms31_p->rc, "parms31_p->rc"),
                        TRACE_DATA_HEX_INT(parms31_p->rsn, "parms31_p->rsn"),
                        TRACE_DATA_END_PARMS);
        }

        free(parms31_p);
        parms31_p = 0;
    } else {
        memcpy_dk(parms->outRC,
                  &localRC,
                  sizeof(localRC),
                  8);
        memcpy_dk(parms->outRSN,
                  &localRSN,
                  sizeof(localRSN),
                  8);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_WLM_CONNECT_EXIT),
                    "wlm_connect, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_connect

/**
 * Drive IWMDISC WLM Disconnect
 *
 * @param parms pointer to <code>WLM_DisconnectParms</code>
 *
 * @return void
*/
void wlm_disconnect(WLM_DisconnectParms* parms)
{
    int localRC, localRSN;
    __asm(" IWMDISC PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(discPlistArea));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_WLM_DISCONNECT_ENTRY),
                    "wlm_disconnect, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    localRC = -1;
    localRSN = -1;

    if ((parms->inWLMConnectToken == 0)       ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) ) {

        localRSN = BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }
        return;
    }

    struct parms31 {
        int  localConnectToken;
        int  localRC;
        int  localRSN;
        char discPlistArea31[sizeof(discPlistArea)];
    };

    struct parms31* parms31_p = __malloc31(sizeof(struct parms31));

    if (parms31_p != NULL) {
        memcpy(parms31_p->discPlistArea31,
               &discPlistArea,
               sizeof(discPlistArea));

        parms31_p->localConnectToken = parms->inWLMConnectToken;

        // ----------------------------------------------------------------
        // Setup and Call WLM Disconnect Service (IWMDISC).
        // ----------------------------------------------------------------
        __asm(AMODE_31(" IWMDISC CONNTKN=%2,RETCODE=%0,RSNCODE=%1,MF=(E,(%3),COMPLETE)") :
              "=m"(parms31_p->localRC),"=m"(parms31_p->localRSN) :
              "m"(parms31_p->localConnectToken),"r"(parms31_p->discPlistArea31):
              "r0","r1","r14","r15");

        // ----------------------------------------------------------------
        // Push output parms back to caller
        // ----------------------------------------------------------------
        memcpy_dk(parms->outRC,
                  &(parms31_p->localRC),
                  sizeof(parms31_p->localRC),
                  8);
        memcpy_dk(parms->outRSN,
                  &(parms31_p->localRSN),
                  sizeof(parms31_p->localRSN),
                  8);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_WLM_SERVICES_WLM_DISCONNECT_RESULTS),
                        "wlm_disconnect, IWMDISC results",
                        TRACE_DATA_HEX_INT(parms->inWLMConnectToken, "parms->inWLMConnectToken"),
                        TRACE_DATA_HEX_INT(parms31_p->localRC, "parms31_p->localRC"),
                        TRACE_DATA_HEX_INT(parms31_p->localRSN, "parms31_p->localRSN"),
                        TRACE_DATA_END_PARMS);
        }

        free(parms31_p);
        parms31_p = 0;
    } else {
        memcpy_dk(parms->outRC,
                  &localRC,
                  sizeof(localRC),
                  8);
        memcpy_dk(parms->outRSN,
                  &localRSN,
                  sizeof(localRSN),
                  8);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_WLM_DISCONNECT_EXIT),
                    "wlm_disconnect, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_disconnect

/**
 * Drive IWM4ECRE WLM Enclave Create
 *
 * @param parms pointer to <code>WLM_EnclaveCreateParms</code>
 *
 * @return void
 *
 **/
void wlm_enclave_create(WLM_EnclaveCreateParms* parms)
{

    __asm(" IWMCLSFY PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(classifyPlistArea));
    __asm(" IWM4ECRE PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(createPlistArea));

    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_CREATE_ENTRY),
                    "wlm_enclave_create, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    localRC = -1;
    localRSN = 0;

    if ((parms->inWLMConnectToken == 0)       ||
        (parms->inFunctionName == 0)          ||
        (parms->inCollectionName == 0)        ||
        (parms->inCollectionNameLen == 0)     ||
        (parms->inCollectionNameLen > WLM_COLLECTIONLIMIT) ||
        (parms->inTransactionClass == 0)      ||
        (parms->inTransactionName == 0)       ||
        (parms->inStartTime == 0)             ||
        (parms->outEnclaveToken == 0)         ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) ) {

        localRSN = BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // Below the Bar parms
    struct parm31 {
        int rc;
        int rsn;
        int inservcls;
        int servcls;
        char localFunctionName[WLM_CREATE_FUNCTION_NAME_MAX];
        char localCollectionName[WLM_COLLECTIONLIMIT];
        char localTransactionClass[WLM_TRANSACTION_CLASS_MAX];
        char localTransactionName[WLM_TRANSACTION_NAME_MAX];
        wlmetoken_t localEnclaveToken;
        unsigned long long localStartTime;
        char classifyPlistArea31[sizeof(classifyPlistArea)];
        char createPlistArea31[sizeof(createPlistArea)];
        // Below Parms for delete if register fails
        localDelete_parms31 localDeleteParms;
    };

    // ----------------------------------------------------------------
    // Setup and Call WLM Connect Service (IWMCONN)
    // ----------------------------------------------------------------
    struct parm31* parms31_p = __malloc31(sizeof(struct parm31));
    if (parms31_p != NULL) {
        RegistryToken localRegisteredEnclaveToken;

        // Note: that caller blank-pads CollectionName, TransactionClass,
        // and TransactionName.
        memcpy_sk(parms31_p->localFunctionName,
                  parms->inFunctionName,
                  WLM_CREATE_FUNCTION_NAME_MAX,
                  8);
        memcpy_sk(parms31_p->localCollectionName,
                  parms->inCollectionName,
                  parms->inCollectionNameLen,
                  8);
        memcpy_sk(parms31_p->localTransactionClass,
                  parms->inTransactionClass,
                  WLM_TRANSACTION_CLASS_MAX,
                  8);
        memcpy_sk(parms31_p->localTransactionName,
                  parms->inTransactionName,
                  WLM_TRANSACTION_NAME_MAX,
                  8);

        memcpy(parms31_p->classifyPlistArea31, &classifyPlistArea, sizeof(classifyPlistArea));
        // ----------------------------------------------------------------
        // Build classify list prior to creation of enclave
        // ----------------------------------------------------------------
        __asm(" IWMCLSFY TRXNAME=%1,TRXCLASS=%2,COLLECTION=%3,"
            "COLLECTION_LEN=%4,CONNTKN=%0,MF=(M,(%5),COMPLETE)" :
              "=m"(parms->inWLMConnectToken) :
              "m"(parms31_p->localTransactionName),"m"(parms31_p->localTransactionClass),
              "m"(parms31_p->localCollectionName),"m"(parms->inCollectionNameLen),
              "r"(parms31_p->classifyPlistArea31) :
              "r0","r1","r14","r15");

        // ----------------------------------------------------------------
        // Create a WLM enclave using the Classify info
        // ----------------------------------------------------------------
        parms31_p->localStartTime = parms->inStartTime;
        memcpy(parms31_p->createPlistArea31, &createPlistArea, sizeof(createPlistArea));

        if (TraceActive(trc_level_detailed)) {
            struct parm31 temp;
            memcpy(&temp, parms31_p, sizeof(temp)); // LE APAR OA37620
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_WLM_SERVICES_CREATE_PRE_IWM4ECRE),
                        "wlm_enclave_create, 31bit parmarea for IWM4ECRE",
                        TRACE_DATA_RAWDATA(sizeof(*parms31_p),
                                           &temp,  // OA37620, change &temp to: parms31_p
                                           "parms31_p"),
                                           TRACE_DATA_END_PARMS);
        }

        psa*  psa_p = NULL;
        cvt*  cvt_p = (cvt* __ptr32) psa_p->flccvt;
        int localServiceClassToken = 0;
        parms31_p->servcls = 0;
        parms31_p->inservcls = 0;
        // Make sure on at least z/OS 2.1 before using INSERVCLS and SERVCLS
        //if ((cvt_p->cvtoslv6 & cvth7790) == cvth7790 ) { // cvth7790 is defined as  0x80
        if ((cvt_p->cvtoslv6 & 0x80) == 0x80 ) { // cvth7790 is defined as  0x80
            parms31_p->inservcls = parms->inServiceClassToken;
            __asm(" IWM4ECRE TYPE=INDEPENDENT,CLSFY=((%5)),ARRIVALTIME=%6,"
                "FUNCTION_NAME=%4,ETOKEN=%0,EXSTARTDEFER=YES,"
                "INSERVCLS=%7,SERVCLS=%3,"
                "RETCODE=%1,RSNCODE=%2,MF=(E,(%8),COMPLETE)" :
                  "=m"(parms31_p->localEnclaveToken),"=m"(parms31_p->rc),
                  "=m"(parms31_p->rsn),"=m"(parms31_p->servcls) :
                  "m"(parms31_p->localFunctionName),"r"(parms31_p->classifyPlistArea31),
                  "m"(parms31_p->localStartTime),"m"(parms31_p->inservcls),"r"(parms31_p->createPlistArea31):
                  "r0","r1","r14","r15");
        } else {
            __asm(" IWM4ECRE TYPE=INDEPENDENT,CLSFY=((%4)),ARRIVALTIME=%5,"
                "FUNCTION_NAME=%3,ETOKEN=%0,EXSTARTDEFER=YES,"
                "RETCODE=%1,RSNCODE=%2,MF=(E,(%6),COMPLETE)" :
                  "=m"(parms31_p->localEnclaveToken),"=m"(parms31_p->rc),
                  "=m"(parms31_p->rsn) :
                  "m"(parms31_p->localFunctionName),"r"(parms31_p->classifyPlistArea31),
                  "m"(parms31_p->localStartTime),"r"(parms31_p->createPlistArea31):
                  "r0","r1","r14","r15");
        }

        if (TraceActive(trc_level_detailed)) {
            wlmetoken_t temp;   // LE APAR OA37620
            memcpy(&temp, &(parms31_p->localEnclaveToken), sizeof(temp)); // LE APAR OA37620
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_WLM_SERVICES_CREATE_POST_IWM4ECRE),
                        "wlm_enclave_create, IWM4ECRE results",
                        TRACE_DATA_RAWDATA(sizeof(parms31_p->localEnclaveToken),
                                           &temp, // OA37620 change &temp to &(parms31_p->localRegisteredEnclaveToken)
                                           "parms31_p->localEnclaveToken"),
                        TRACE_DATA_HEX_INT(parms31_p->inservcls, "parms31_p->inservcls"),
                        TRACE_DATA_HEX_INT(parms31_p->servcls, "parms31_p->servcls"),
                        TRACE_DATA_HEX_INT(parms31_p->rc, "parms31_p->rc"),
                        TRACE_DATA_HEX_INT(parms31_p->rsn, "parms31_p->rsn"),
                        TRACE_DATA_END_PARMS);
        }

        // Check IWM4ECRE return code and reason code to see if we need to save the service class token.
        // xxxx044E    Name: IwmRsnCodeNewServcls
        // Meaning: Input service class token is not valid.  A new one has been assigned and returned.
        if ((parms31_p->rc == 4) && ((parms31_p->rsn & 0x0000FFFF) == 0x0000044E)) {
            localServiceClassToken = parms31_p->servcls;
            parms31_p->rc = 0;
        }
        if (parms31_p->rc == 0) {
            // ------------------------------------------------------------
            // Register the returned WLM Enclave token and return a
            // registry token to the caller
            // ------------------------------------------------------------
            int registryRC = addEnclaveTokenToRegistry(&(parms31_p->localEnclaveToken),
                                                       &localRegisteredEnclaveToken);

            if (registryRC != 0) {
                // Delete the Enclave and return a failure to register with
                // native registry
                memcpy(parms31_p->localDeleteParms.deletePlistArea31,
                       &deletePlistArea,
                       sizeof(deletePlistArea));

                memcpy(&(parms31_p->localDeleteParms.localEnclaveToken31),
                       &(parms31_p->localEnclaveToken),
                       sizeof(parms31_p->localEnclaveToken));

                // --------------------------------------------------------
                // Delete a WLM enclave
                // --------------------------------------------------------
                local_wlm_enclave_delete(&(parms31_p->localDeleteParms));

                parms31_p->rc = -1;
                parms31_p->rsn = WASRETURNCODE_FAILED_TO_REGISTER;
            }
        }

        // ----------------------------------------------------------------
        // Push output parms back to caller
        // ----------------------------------------------------------------
        memcpy_dk(parms->outEnclaveToken,
                  &localRegisteredEnclaveToken,
                  sizeof(localRegisteredEnclaveToken),
                  8);
        memcpy_dk(parms->outServiceClassToken,
                  &(localServiceClassToken),
                  sizeof(localServiceClassToken),
                  8);
        memcpy_dk(parms->outRC,
                  &(parms31_p->rc),
                  sizeof(parms31_p->rc),
                  8);
        memcpy_dk(parms->outRSN,
                  &(parms31_p->rsn),
                  sizeof(parms31_p->rsn),
                  8);

        free(parms31_p);
        parms31_p = 0;
    } else {
        memcpy_dk(parms->outRC,
                  &localRC,
                  sizeof(localRC),
                  8);
        memcpy_dk(parms->outRSN,
                  &localRSN,
                  sizeof(localRSN),
                  8);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_CREATE_EXIT),
                    "wlm_enclave_create, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_enclave_create

/**
 * Drive IWMEJOIN WLM Enclave Join
 *
 * @param parms pointer to <code>WLM_EnclaveJoinParms</code>
 *
 * @return void
 *
 **/
void wlm_enclave_join(WLM_EnclaveJoinParms* parms)
{

    RegistryToken localRegisteredEnclaveToken;

    __asm(" IWMEJOIN PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(joinPlistArea));


    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_JOIN_ENTRY),
                    "wlm_enclave_join, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    localRC = -1;
    localRSN = -1;

    if ((parms->inEnclaveToken == 0)         ||
        (parms->outRC == 0)                  ||
        (parms->outRSN == 0) ) {
        localRSN = BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }
        return;
    }


    struct parms31 {
        wlmetoken_t localEnclaveToken31;
        int  localRC;
        int  localRSN;
        char joinPlistArea31[sizeof(joinPlistArea)];
    };

    struct parms31* parms31_p = __malloc31(sizeof(struct parms31));

    if (parms31_p != NULL) {
        memcpy(parms31_p->joinPlistArea31,
               &joinPlistArea,
               sizeof(joinPlistArea));

        // Retrieve the WLM Enclave token using the registered Token
        memcpy_sk(&(localRegisteredEnclaveToken),
                  parms->inEnclaveToken,
                  sizeof(localRegisteredEnclaveToken),
                  8);

        localRC = getEnclaveTokenFromRegistry(&(localRegisteredEnclaveToken),
                                              &(parms31_p->localEnclaveToken31) );

        if (localRC == 0) {

            // ------------------------------------------------------------
            // Join a WLM enclave
            // ------------------------------------------------------------
            __asm(AMODE_31(" IWMEJOIN ETOKEN=%2,"
                "RETCODE=%0,RSNCODE=%1,MF=(E,(%3),COMPLETE)") :
                "=m"(parms31_p->localRC),"=m"(parms31_p->localRSN) :
                "m"(parms31_p->localEnclaveToken31),
                "r"(parms31_p->joinPlistArea31) :
                "r0","r1","r14","r15");

            if (TraceActive(trc_level_detailed)) {
                wlmetoken_t temp;   // LE APAR OA37620
                memcpy(&temp, &(parms31_p->localEnclaveToken31), sizeof(temp)); // LE APAR OA37620
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_WLM_SERVICES_JOIN_POST_IWMEJOIN),
                            "wlm_enclave_join, IWMEJOIN results",
                            TRACE_DATA_RAWDATA(sizeof(parms31_p->localEnclaveToken31),
                                               &temp, // OA37620 change &temp to &(parms31_p->localEnclaveToken31)
                                               "parms31_p->localEnclaveToken31"),
                            TRACE_DATA_HEX_INT(parms31_p->localRC, "parms31_p->localRC"),
                            TRACE_DATA_HEX_INT(parms31_p->localRSN, "parms31_p->localRSN"),
                            TRACE_DATA_END_PARMS);
            }

            localRC = parms31_p->localRC;
            localRSN = parms31_p->localRSN;
        } else {
            localRC = -1;
            localRSN = WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY;
        }


        free(parms31_p);
    }

    // ----------------------------------------------------------------
    // Push output parms back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_JOIN_EXIT),
                    "wlm_enclave_join, exit",
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_enclave_join

/**
 * Drive IWM4ECRE WLM Enclave Create then IWMEJOIN to Join
 *
 * @param parms pointer to <code>WLM_EnclaveCreateParms</code>
 *
 * @return void
 *
 **/
void wlm_enclave_create_join(WLM_EnclaveCreateJoinParms* parms)
{

    __asm(" IWMCLSFY PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(classifyPlistArea));
    __asm(" IWM4ECRE PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(createPlistArea));
    __asm(" IWMEJOIN PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(joinPlistArea));

    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_CREATEJ_ENTRY),
                    "wlm_enclave_create_join, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    localRC = -1;
    localRSN = 0;

    if ((parms->inWLMConnectToken == 0)       ||
        (parms->inFunctionName == 0)          ||
        (parms->inCollectionName == 0)        ||
        (parms->inCollectionNameLen == 0)     ||
        (parms->inCollectionNameLen > WLM_COLLECTIONLIMIT) ||
        (parms->inTransactionClass == 0)      ||
        (parms->inStartTime == 0)             ||
        (parms->outEnclaveToken == 0)         ||
        (parms->outCreateRC == 0)             ||
        (parms->outCreateRSN == 0)            ||
        (parms->outJoinRC == 0)               ||
        (parms->outJoinRSN == 0) ) {

        localRSN = BAD_PARMS;

        if (parms->outCreateRC != 0) {
            memcpy_dk(parms->outCreateRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outCreateRSN != 0) {
            memcpy_dk(parms->outCreateRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // Below the Bar parms
    struct parm31 {
        int rc;
        int rsn;
        int inservcls;
        int servcls;
        char localFunctionName[WLM_CREATE_FUNCTION_NAME_MAX];
        char localCollectionName[WLM_COLLECTIONLIMIT];
        char localTransactionClass[WLM_TRANSACTION_CLASS_MAX];
        char localTransactionName[WLM_TRANSACTION_NAME_MAX];
        wlmetoken_t localEnclaveToken;
        unsigned long long localStartTime;
        char classifyPlistArea31[sizeof(classifyPlistArea)];
        char createPlistArea31[sizeof(createPlistArea)];
        char joinPlistArea31[sizeof(joinPlistArea)];
        // Below Parms for delete if register fails
        localDelete_parms31 localDeleteParms;
    };

    // ----------------------------------------------------------------
    // Setup and Call WLM Classify (IWMCLSFY) and Create (IWM4ECRE)
    // ----------------------------------------------------------------
    struct parm31* parms31_p = __malloc31(sizeof(struct parm31));
    if (parms31_p != NULL) {
        RegistryToken localRegisteredEnclaveToken = {{0}};

        // Note: that caller blank-pads CollectionName, TransactionClass,
        // and TransactionName.
        memcpy_sk(parms31_p->localFunctionName,
                  parms->inFunctionName,
                  WLM_CREATE_FUNCTION_NAME_MAX,
                  8);
        memcpy_sk(parms31_p->localCollectionName,
                  parms->inCollectionName,
                  parms->inCollectionNameLen,
                  8);
        memcpy_sk(parms31_p->localTransactionClass,
                  parms->inTransactionClass,
                  WLM_TRANSACTION_CLASS_MAX,
                  8);


        memcpy(parms31_p->classifyPlistArea31, &classifyPlistArea, sizeof(classifyPlistArea));
        // ----------------------------------------------------------------
        // Build classify list prior to creation of enclave
        // ----------------------------------------------------------------
        __asm(" IWMCLSFY TRXCLASS=%1,COLLECTION=%2,"
            "COLLECTION_LEN=%3,CONNTKN=%0,MF=(M,(%4),COMPLETE)" :
              "=m"(parms->inWLMConnectToken) :
              "m"(parms31_p->localTransactionClass),
              "m"(parms31_p->localCollectionName),"m"(parms->inCollectionNameLen),
              "r"(parms31_p->classifyPlistArea31) :
              "r0","r1","r14","r15");

        // ----------------------------------------------------------------
        // Create a WLM enclave using the Classify info
        // ----------------------------------------------------------------
        parms31_p->localStartTime = parms->inStartTime;
        memcpy(parms31_p->createPlistArea31, &createPlistArea, sizeof(createPlistArea));

        if (TraceActive(trc_level_detailed)) {
            struct parm31 temp;
            memcpy(&temp, parms31_p, sizeof(temp)); // LE APAR OA37620
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_WLM_SERVICES_CREATEJ_PRE_IWM4ECRE),
                        "wlm_enclave_create_join, 31bit parmarea for IWM4ECRE",
                        TRACE_DATA_RAWDATA(sizeof(*parms31_p),
                                           &temp,  // OA37620, change &temp to: parms31_p
                                           "parms31_p"),
                                           TRACE_DATA_END_PARMS);
        }

        psa*  psa_p = NULL;
        cvt*  cvt_p = (cvt* __ptr32) psa_p->flccvt;
        int localServiceClassToken = 0;
        parms31_p->servcls = 0;
        parms31_p->inservcls = 0;
        // Make sure on at least z/OS 2.1 before using INSERVCLS and SERVCLS
        //if ((cvt_p->cvtoslv6 & cvth7790) == cvth7790 ) { // cvth7790 is defined as  0x80
        if ((cvt_p->cvtoslv6 & 0x80) == 0x80 ) { // cvth7790 is defined as  0x80
            parms31_p->inservcls = parms->inServiceClassToken;
            __asm(" IWM4ECRE TYPE=INDEPENDENT,CLSFY=((%5)),ARRIVALTIME=%6,"
                "FUNCTION_NAME=%4,ETOKEN=%0,EXSTARTDEFER=YES,"
                "INSERVCLS=%7,SERVCLS=%3,"
                "RETCODE=%1,RSNCODE=%2,MF=(E,(%8),COMPLETE)" :
                  "=m"(parms31_p->localEnclaveToken),"=m"(parms31_p->rc),
                  "=m"(parms31_p->rsn),"=m"(parms31_p->servcls) :
                  "m"(parms31_p->localFunctionName),"r"(parms31_p->classifyPlistArea31),
                  "m"(parms31_p->localStartTime),"m"(parms31_p->inservcls),"r"(parms31_p->createPlistArea31):
                  "r0","r1","r14","r15");
        } else {
            __asm(" IWM4ECRE TYPE=INDEPENDENT,CLSFY=((%4)),ARRIVALTIME=%5,"
                "FUNCTION_NAME=%3,ETOKEN=%0,EXSTARTDEFER=YES,"
                "RETCODE=%1,RSNCODE=%2,MF=(E,(%6),COMPLETE)" :
                  "=m"(parms31_p->localEnclaveToken),"=m"(parms31_p->rc),
                  "=m"(parms31_p->rsn) :
                  "m"(parms31_p->localFunctionName),"r"(parms31_p->classifyPlistArea31),
                  "m"(parms31_p->localStartTime),"r"(parms31_p->createPlistArea31):
                  "r0","r1","r14","r15");
        }

        if (TraceActive(trc_level_detailed)) {
            wlmetoken_t temp;   // LE APAR OA37620
            memcpy(&temp, &(parms31_p->localEnclaveToken), sizeof(temp)); // LE APAR OA37620
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_WLM_SERVICES_CREATEJ_POST_IWM4ECRE),
                        "wlm_enclave_create_join, IWM4ECRE results",
                        TRACE_DATA_RAWDATA(sizeof(parms31_p->localEnclaveToken),
                                           &temp, // OA37620 change &temp to &(parms31_p->localRegisteredEnclaveToken)
                                           "parms31_p->localEnclaveToken"),
                        TRACE_DATA_HEX_INT(parms31_p->inservcls, "parms31_p->inservcls"),
                        TRACE_DATA_HEX_INT(parms31_p->servcls, "parms31_p->servcls"),
                        TRACE_DATA_HEX_INT(parms31_p->rc, "parms31_p->rc"),
                        TRACE_DATA_HEX_INT(parms31_p->rsn, "parms31_p->rsn"),
                        TRACE_DATA_END_PARMS);
        }

        // Check IWM4ECRE return code and reason code to see if we need to save the service class token.
        // xxxx044E    Name: IwmRsnCodeNewServcls
        // Meaning: Input service class token is not valid.  A new one has been assigned and returned.
        if ((parms31_p->rc == 4) && ((parms31_p->rsn & 0x0000FFFF) == 0x0000044E)) {
            localServiceClassToken = parms31_p->servcls;
            parms31_p->rc = 0;
        }
        if (parms31_p->rc == 0) {
            // ------------------------------------------------------------
            // Register the returned WLM Enclave token and return a
            // registry token to the caller
            // ------------------------------------------------------------
            int registryRC = addEnclaveTokenToRegistry(&(parms31_p->localEnclaveToken),
                                                       &localRegisteredEnclaveToken);

            if (registryRC != 0) {
                // --------------------------------------------------------
                // Delete the Enclave and return a failure to register with
                // native registry
                // --------------------------------------------------------
                memcpy(parms31_p->localDeleteParms.deletePlistArea31,
                       &deletePlistArea,
                       sizeof(deletePlistArea));

                memcpy(&(parms31_p->localDeleteParms.localEnclaveToken31),
                       &(parms31_p->localEnclaveToken),
                       sizeof(parms31_p->localEnclaveToken));

                // Delete a WLM enclave
                local_wlm_enclave_delete(&(parms31_p->localDeleteParms));

                parms31_p->rc = -1;
                parms31_p->rsn = WASRETURNCODE_FAILED_TO_REGISTER;
            }
        }

        // ----------------------------------------------------------------
        // Push Create output RC/RSN parms back to caller
        // ----------------------------------------------------------------
        memcpy_dk(parms->outCreateRC,
                  &(parms31_p->rc),
                  sizeof(parms31_p->rc),
                  8);
        memcpy_dk(parms->outCreateRSN,
                  &(parms31_p->rsn),
                  sizeof(parms31_p->rsn),
                  8);

        // ----------------------------------------------------------------
        // Attempt to Join the enclave if Create was successful
        // ----------------------------------------------------------------
        if (parms31_p->rc == 0) {
            // ------------------------------------------------------------
            // Join a WLM enclave
            // ------------------------------------------------------------
            __asm(AMODE_31(" IWMEJOIN ETOKEN=%2,"
                "RETCODE=%0,RSNCODE=%1,MF=(E,(%3),COMPLETE)") :
                "=m"(parms31_p->rc),"=m"(parms31_p->rsn) :
                "m"(parms31_p->localEnclaveToken),
                "r"(parms31_p->joinPlistArea31) :
                "r0","r1","r14","r15");

            if (TraceActive(trc_level_detailed)) {
                wlmetoken_t temp;   // LE APAR OA37620
                memcpy(&temp, &(parms31_p->localEnclaveToken), sizeof(temp)); // LE APAR OA37620
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_WLM_SERVICES_CREATEJ_POST_IWMEJOIN),
                            "wlm_enclave_create_join, IWMEJOIN results",
                            TRACE_DATA_RAWDATA(sizeof(parms31_p->localEnclaveToken),
                                               &temp, // OA37620 change &temp to &(parms31_p->localEnclaveToken)
                                               "parms31_p->localEnclaveToken"),
                            TRACE_DATA_HEX_INT(parms31_p->rc, "parms31_p->rc"),
                            TRACE_DATA_HEX_INT(parms31_p->rsn, "parms31_p->rsn"),
                            TRACE_DATA_END_PARMS);
            }

            memcpy_dk(parms->outJoinRC,
                      &(parms31_p->rc),
                      sizeof(parms31_p->rc),
                      8);
            memcpy_dk(parms->outJoinRSN,
                      &(parms31_p->rsn),
                      sizeof(parms31_p->rsn),
                      8);

            // ------------------------------------------------------------
            // Deregister Enclave Token and delete the Enclave on a Join
            // failure
            // ------------------------------------------------------------
            if (parms31_p->rc != 0) {
                // Deregister the Enclave Token
                registryFree(&(localRegisteredEnclaveToken), TRUE);
                memset(&localRegisteredEnclaveToken, 0, sizeof(localRegisteredEnclaveToken));

                // --------------------------------------------------------
                // Delete the Enclave and return a failure to register with
                // native registry
                // --------------------------------------------------------
                memcpy(parms31_p->localDeleteParms.deletePlistArea31,
                       &deletePlistArea,
                       sizeof(deletePlistArea));

                memcpy(&(parms31_p->localDeleteParms.localEnclaveToken31),
                       &(parms31_p->localEnclaveToken),
                       sizeof(parms31_p->localEnclaveToken));

                // Delete a WLM enclave
                local_wlm_enclave_delete(&(parms31_p->localDeleteParms));
            }
        }

        // Push back service class Token or null
        memcpy_dk(parms->outServiceClassToken,
                  &(localServiceClassToken),
                  sizeof(localServiceClassToken),
                  8);
        // Push back Enclave Token or null token
        memcpy_dk(parms->outEnclaveToken,
                  &localRegisteredEnclaveToken,
                  sizeof(localRegisteredEnclaveToken),
                  8);

        free(parms31_p);
        parms31_p = 0;
    } else {
        // Just setting the Create RC/RSN codes to a failure.
        memcpy_dk(parms->outCreateRC,
                  &localRC,
                  sizeof(localRC),
                  8);
        memcpy_dk(parms->outCreateRSN,
                  &localRSN,
                  sizeof(localRSN),
                  8);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_CREATEJ_EXIT),
                    "wlm_enclave_create_join, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_enclave_create_join


/**
 * Drive IWMELEAV WLM Enclave Leave
 *
 * @param parms pointer to <code>WLM_EnclaveLeaveParms</code>
 *
 * @return void
 *
 **/
void wlm_enclave_leave(WLM_EnclaveLeaveParms* parms)
{

    RegistryToken localRegisteredEnclaveToken;

    __asm(" IWMELEAV PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(leavePlistArea));


    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_LEAVE_ENTRY),
                    "wlm_enclave_leave, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    localRC = -1;
    localRSN = -1;

    if ((parms->inEnclaveToken == 0)         ||
        (parms->outRC == 0)                  ||
        (parms->outRSN == 0) ) {
        localRSN = BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    struct parms31 {
        wlmetoken_t localEnclaveToken31;
        int  wlmRC;
        int  wlmRSN;
        char leavePlistArea31[sizeof(leavePlistArea)];
    };

    struct parms31* parms31_p = __malloc31(sizeof(struct parms31));

    if (parms31_p != NULL) {
        memcpy(parms31_p->leavePlistArea31,
               &leavePlistArea,
               sizeof(leavePlistArea));

        // Retrieve the WLM Enclave token using the registered Token
        memcpy_sk(&(localRegisteredEnclaveToken),
                  parms->inEnclaveToken,
                  sizeof(localRegisteredEnclaveToken),
                  8);

        localRC = getEnclaveTokenFromRegistry(&(localRegisteredEnclaveToken),
                                              &(parms31_p->localEnclaveToken31) );

        if (localRC == 0) {

            // --------------------------------------------------------
            // Leave a WLM enclave
            // --------------------------------------------------------
            __asm(AMODE_31(" IWMELEAV ETOKEN=%2,"
                "RETCODE=%0,RSNCODE=%1,MF=(E,(%3),COMPLETE)") :
                "=m"(parms31_p->wlmRC),"=m"(parms31_p->wlmRSN) :
                "m"(parms31_p->localEnclaveToken31),
                "r"(parms31_p->leavePlistArea31) :
                "r0","r1","r14","r15");

            if (TraceActive(trc_level_detailed)) {
                wlmetoken_t temp;   // LE APAR OA37620
                memcpy(&temp, &(parms31_p->localEnclaveToken31), sizeof(temp)); // LE APAR OA37620
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_WLM_SERVICES_LEAVE_POST_IWMELEAV),
                            "wlm_enclave_leave, IWMELEAV results",
                            TRACE_DATA_RAWDATA(sizeof(parms31_p->localEnclaveToken31),
                                               &temp, // OA37620 change &temp to &(parms31_p->localEnclaveToken31)
                                               "parms31_p->localEnclaveToken31"),
                            TRACE_DATA_HEX_INT(parms31_p->wlmRC, "parms31_p->wlmRC"),
                            TRACE_DATA_HEX_INT(parms31_p->wlmRSN, "parms31_p->wlmRSN"),
                            TRACE_DATA_END_PARMS);
            }

            localRC = parms31_p->wlmRC;
            localRSN = parms31_p->wlmRSN;

        } else {
            localRC = -1;
            localRSN = WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY;
        }

        free(parms31_p);
    }

    // ----------------------------------------------------------------
    // Push output parms back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_LEAVE_EXIT),
                    "wlm_enclave_leave, exit",
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_enclave_leave

static void local_wlm_enclave_delete(localDelete_parms31* parms)
{
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_L_DELETE_ENTRY),
                    "local_wlm_enclave_delete, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Delete a WLM enclave
    // ----------------------------------------------------------------

/* TODO do not hard code offsets off of register 2.
 *      CPUTIME=0(2),CPUSERVICE=8(2),"ZAAPTIME=16(2),ZAAPSERVICE=24(2),"ZIIPTIME=32(2)
 *
 *      MF=(M did not compile
 *      add a dsect and using? angel_pc_initialization.mc has a using for ETDEF
 *
 */
    __asm(AMODE_31(" LA 2,%5\n"
        " IWM4EDEL ETOKEN=%6,"
        "RETCODE=%0,RSNCODE=%1,CPUTIME=0(2),CPUSERVICE=8(2),"
        "ZAAPTIME=16(2),ZAAPSERVICE=24(2),ZIIPTIME=32(2),"
        "ZIIPSERVICE=%2,ZAAPNFACTOR=%3,RESPTIME_RATIO=%4,"
        "MF=(E,(%7),COMPLETE)\n"):
        "=m"(parms->wlmRC),"=m"(parms->wlmRSN),
         "=m"(parms->deleteData.enclaveDeletezIIPService),
        "=m"(parms->deleteData.enclaveDeletezAAPnormFact),
        "=m"(parms->deleteData.enclaveDeleteRespTimeRatio) :
        "m"(parms->deleteData),
        "m"(parms->localEnclaveToken31),
        "r"(parms->deletePlistArea31) :
        "r0","r1","r2","r14","r15");

    if (TraceActive(trc_level_detailed)) {
        wlmetoken_t temp;   // LE APAR OA37620
        memcpy(&temp, &(parms->localEnclaveToken31), sizeof(temp)); // LE APAR OA37620
        WLM_EnclaveDeleteData temp2;
        memcpy(&temp2, &(parms->deleteData), sizeof(temp2));
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_L_DELETE_EXIT),
                    "local_wlm_enclave_delete, IWM4EDEL results",
                    TRACE_DATA_RAWDATA(sizeof(parms->localEnclaveToken31),
                                       &temp, // OA37620 change &temp to &(parms->localEnclaveToken31)
                                       "parms31_p->localEnclaveToken31"),
                    TRACE_DATA_RAWDATA(sizeof(parms->deleteData),
                                       &temp2,
                                       "parms->deleteData"),
                    TRACE_DATA_HEX_INT(parms->wlmRC, "parms->wlmRC"),
                    TRACE_DATA_HEX_INT(parms->wlmRSN, "parms->wlmRSN"),
                    TRACE_DATA_END_PARMS);
    }

} // end, local_wlm_enclave_delete

/**
 * Drive IWM4EDEL WLM Enclave Delete
 *
 * @param parms pointer to <code>WLM_EnclaveDeleteParms</code>
 *
 * @return void
 *
 **/
void wlm_enclave_delete(WLM_EnclaveDeleteParms* parms)
{
    RegistryToken localRegisteredEnclaveToken;
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_DELETE_ENTRY),
                    "wlm_enclave_delete, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    localRC = -1;
    localRSN = -1;

    if ((parms->inEnclaveToken == 0)         ||
        (parms->outRC == 0)                  ||
        (parms->outRSN == 0)                 ||
        (parms->outDeleteData == 0)) {
        localRSN = BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }
        return;
    }

    localDelete_parms31* parms31_p = __malloc31(sizeof(localDelete_parms31));

    if (parms31_p != NULL) {
        memcpy(parms31_p->deletePlistArea31,
               &deletePlistArea,
               sizeof(deletePlistArea));

        // Retrieve the WLM Enclave token using the registered Token
        memcpy_sk(&(localRegisteredEnclaveToken),
                  parms->inEnclaveToken,
                  sizeof(localRegisteredEnclaveToken),
                  8);

        localRC = getEnclaveTokenFromRegistry(&(localRegisteredEnclaveToken),
                                              &(parms31_p->localEnclaveToken31) );

        if (localRC == 0) {
            // ----------------------------------------------------------------
            // Delete a WLM enclave
            // ----------------------------------------------------------------
            registryFree(&(localRegisteredEnclaveToken), TRUE);

            local_wlm_enclave_delete(parms31_p);

            // copy back times from delete
            memcpy_dk(parms->outDeleteData,
                      &(parms31_p->deleteData),
                      sizeof(parms31_p->deleteData),
                      8);
            // copy back enclave token
            memcpy_dk(&(parms->outDeleteData->enclaveToken),
                      &(parms31_p->localEnclaveToken31),
                      sizeof(parms31_p->localEnclaveToken31),
                      8);
            localRC = parms31_p->wlmRC;
            localRSN = parms31_p->wlmRSN;

        } else {
             localRC = -1;
             localRSN = WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY;
        }

        free(parms31_p);
    }

    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_ENCLAVE_DELETE_EXIT),
                    "wlm_enclave_delete, exit",
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_enclave_delete


/**
 * Drive IWMELEAV WLM Enclave Leave then IWM4EDEL to delete
 *
 * @param parms pointer to <code>WLM_EnclaveLeaveDeleteParms</code>
 *
 * @return void
 *
 **/
void wlm_enclave_leave_delete(WLM_EnclaveLeaveDeleteParms* parms)
{

    RegistryToken localRegisteredEnclaveToken;

    __asm(" IWMELEAV PLISTVER=MAX,MF=(L,AUTO)" :
          "DS"(leavePlistArea));


    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_LEAVEDELETE_ENTRY),
                    "wlm_enclave_leave_delete, entered",
                    TRACE_DATA_END_PARMS);
    }

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    localRC = -1;
    localRSN = -1;

    if ((parms->inEnclaveToken == 0)         ||
        (parms->outLeaveRC == 0)             ||
        (parms->outLeaveRSN == 0)            ||
        (parms->outDeleteRC == 0)            ||
        (parms->outDeleteRSN == 0)           ||
        (parms->outDeleteData == 0)) {
        localRSN = BAD_PARMS;

        if (parms->outLeaveRC != 0) {
            memcpy_dk(parms->outLeaveRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outLeaveRSN != 0) {
            memcpy_dk(parms->outLeaveRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    struct parms31 {
        wlmetoken_t localEnclaveToken31;
        int  wlmRC;
        int  wlmRSN;
        char leavePlistArea31[sizeof(leavePlistArea)];
        localDelete_parms31 localDeleteParms;
    };

    struct parms31* parms31_p = __malloc31(sizeof(struct parms31));

    if (parms31_p != NULL) {
        memcpy(parms31_p->leavePlistArea31,
               &leavePlistArea,
               sizeof(leavePlistArea));

        // Retrieve the WLM Enclave token using the registered Token
        memcpy_sk(&(localRegisteredEnclaveToken),
                  parms->inEnclaveToken,
                  sizeof(localRegisteredEnclaveToken),
                  8);

        localRC = getEnclaveTokenFromRegistry(&(localRegisteredEnclaveToken),
                                              &(parms31_p->localEnclaveToken31) );

        if (localRC == 0) {

            // ------------------------------------------------------------
            // Leave a WLM enclave
            // ------------------------------------------------------------
            __asm(AMODE_31(" IWMELEAV ETOKEN=%2,"
                "RETCODE=%0,RSNCODE=%1,MF=(E,(%3),COMPLETE)") :
                "=m"(parms31_p->wlmRC),"=m"(parms31_p->wlmRSN) :
                "m"(parms31_p->localEnclaveToken31),
                "r"(parms31_p->leavePlistArea31) :
                "r0","r1","r14","r15");

            if (TraceActive(trc_level_detailed)) {
                wlmetoken_t temp;   // LE APAR OA37620
                memcpy(&temp, &(parms31_p->localEnclaveToken31), sizeof(temp)); // LE APAR OA37620
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_WLM_SERVICES_LEAVEDELETE_POST_IWMELEAV),
                            "wlm_enclave_leave_delete, IWMELEAV results",
                            TRACE_DATA_RAWDATA(sizeof(parms31_p->localEnclaveToken31),
                                               &temp, // OA37620 change &temp to &(parms31_p->localEnclaveToken31)
                                               "parms31_p->localEnclaveToken31"),
                            TRACE_DATA_HEX_INT(parms31_p->wlmRC, "parms31_p->wlmRC"),
                            TRACE_DATA_HEX_INT(parms31_p->wlmRSN, "parms31_p->wlmRSN"),
                            TRACE_DATA_END_PARMS);
            }

            // Save Leave RC/RSN
            memcpy_dk(parms->outLeaveRC,
                      &(parms31_p->wlmRC),
                      sizeof(parms31_p->wlmRC),
                      8);

            memcpy_dk(parms->outLeaveRSN,
                      &(parms31_p->wlmRSN),
                      sizeof(parms31_p->wlmRSN),
                      8);

            // ------------------------------------------------------------
            // Deregister and Delete the WLM enclave
            // ------------------------------------------------------------
            if (parms31_p->wlmRC == 0) {
                // Deregister the Enclave Token
                registryFree(&(localRegisteredEnclaveToken), TRUE);

                // --------------------------------------------------------
                // Delete the Enclave and return a failure to register with
                // native registry
                // --------------------------------------------------------
                memcpy(parms31_p->localDeleteParms.deletePlistArea31,
                       &deletePlistArea,
                       sizeof(deletePlistArea));

                memcpy(&(parms31_p->localDeleteParms.localEnclaveToken31),
                       &(parms31_p->localEnclaveToken31),
                       sizeof(parms31_p->localEnclaveToken31));

                // Delete a WLM enclave
                local_wlm_enclave_delete(&(parms31_p->localDeleteParms));

                // copy back times from delete
                memcpy_dk(parms->outDeleteData,
                          &(parms31_p->localDeleteParms.deleteData),
                          sizeof(parms31_p->localDeleteParms.deleteData),
                          8);
                // copy back enclave token
                memcpy_dk(&(parms->outDeleteData->enclaveToken),
                          &(parms31_p->localEnclaveToken31),
                          sizeof(parms31_p->localEnclaveToken31),
                          8);

                memcpy_dk(parms->outDeleteRC,
                          &(parms31_p->localDeleteParms.wlmRC),
                          sizeof(parms31_p->localDeleteParms.wlmRC),
                          8);

                memcpy_dk(parms->outDeleteRSN,
                          &(parms31_p->localDeleteParms.wlmRSN),
                          sizeof(parms31_p->localDeleteParms.wlmRSN),
                          8);
            }
        } else {
            localRC = -1;
            localRSN = WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY;
            memcpy_dk(parms->outLeaveRC,
                      &(localRC),
                      sizeof(localRC),
                      8);

            memcpy_dk(parms->outLeaveRSN,
                      &(localRSN),
                      sizeof(localRSN),
                      8);
        }

        free(parms31_p);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_WLM_SERVICES_LEAVEDELETE_EXIT),
                    "wlm_enclave_leave_delete, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, wlm_enclave_leave_delete


#pragma insert_asm(" CVT DSECT=YES")
#pragma insert_asm(" IWMYCON")
