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
/*
 * server_wlm_services.h
 *
 *  Created on: Aug 22, 2011
 *      Author: ginnick
 */

#ifndef SERVER_WLM_SERVICES_H_
#define SERVER_WLM_SERVICES_H_


#include "util_registry.h"


#define WLM_SUBSYSTEM_MAX 4
#define WLM_SUBSYSTEM_NAME_MAX 8
#define WLM_COLLECTIONLIMIT 18
#define WLM_TRANSACTION_NAME_MAX 8
#define WLM_TRANSACTION_CLASS_MAX 8
#define WLM_CREATE_FUNCTION_NAME_MAX 8

/*-------------------------------------------------------------------*/
/* Return codes related to Native Registry failures.  And others.    */
/* They are mapped in Java as well (WLMServiceResults.java)          */
/*-------------------------------------------------------------------*/
#define WASRETURNCODE_FAILED_TO_REGISTER -100
#define WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY -101
#define WASRETURNCODE_FAILED_TO_FIND_UNAUTH_FUNCTION_STUBS -102
#define WASRETURNCODE_FAILED_CALLING_BPX4IPT -103

/*-------------------------------------------------------------------*/
/* WLM services                                                      */
/*-------------------------------------------------------------------*/
 typedef struct {
                 unsigned int wlmetok_w1;
                 unsigned int wlmetok_w2;
                } wlmetoken_t;

/*-------------------------------------------------------------------*/
/* Drive IWM4CON WLM Connect                                         */
/*                                                                   */
/* Parms:                                                            */
/*   inSubSysName -- INPUT, Blank padded maximum of 8 characters     */
/*                   passed on SUBSYSNM parm for connect             */
/*   outWLMConnectToken -- OUTPUT, WLM Connection token              */
/*   outrc,outrsn -- OUTPUT, return and reason code returned from    */
/*                   IWM4CON                                         */
/*-------------------------------------------------------------------*/
typedef struct WLM_ConnectParms WLM_ConnectParms;
struct WLM_ConnectParms {
    char * inSubSys;
    char * inSubSysName;
    unsigned int  * outWLMConnectToken;
    int  * outRC;
    int  * outRSN;
};
void wlm_connect(WLM_ConnectParms* parms);


/*-------------------------------------------------------------------*/
/* Drive IWM4DIS WLM Disconnect                                      */
/*                                                                   */
/* Parms:                                                            */
/*   inWLMConnectToken -- INPUT, WLM Connection token                */
/*   outrc,outrsn -- OUTPUT, return and reason code returned from    */
/*                   IWM4DIS                                         */
/*-------------------------------------------------------------------*/
typedef struct WLM_DisconnectParms WLM_DisconnectParms;
struct WLM_DisconnectParms {
    unsigned int  inWLMConnectToken;
    int  * outRC;
    int  * outRSN;
};
void wlm_disconnect(WLM_DisconnectParms* parms);


/*-------------------------------------------------------------------*/
/* IWM4ECRE WLM Create an Enclave                                    */
/*-------------------------------------------------------------------*/
typedef struct WLM_EnclaveCreateParms WLM_EnclaveCreateParms;
struct WLM_EnclaveCreateParms {
    unsigned int  inWLMConnectToken;
    char * inFunctionName;
    char * inCollectionName;
    int    inCollectionNameLen;
    char * inTransactionClass;
    char * inTransactionName;
    unsigned long long inStartTime;
    int    inServiceClassToken;
    RegistryToken * outEnclaveToken;
    int  * outServiceClassToken;
    int  * outRC;
    int  * outRSN;
};
void wlm_enclave_create(WLM_EnclaveCreateParms * parms);

/*-------------------------------------------------------------------*/
/* IWMEJOIN WLM Join an Enclave                                      */
/*-------------------------------------------------------------------*/
typedef struct WLM_EnclaveJoinParms WLM_EnclaveJoinParms;
struct WLM_EnclaveJoinParms {
    RegistryToken * inEnclaveToken;
    int  * outRC;
    int  * outRSN;
};
void wlm_enclave_join(WLM_EnclaveJoinParms * parms);

/*-------------------------------------------------------------------*/
/* IWM4ECRE / IWMEJOIN WLM Create then Join an Enclave               */
/*-------------------------------------------------------------------*/
typedef struct WLM_EnclaveCreateJoinParms WLM_EnclaveCreateJoinParms;
struct WLM_EnclaveCreateJoinParms {
    unsigned int  inWLMConnectToken;
    char * inFunctionName;
    char * inCollectionName;
    int    inCollectionNameLen;
    char * inTransactionClass;
    unsigned long long inStartTime;
    int    inServiceClassToken;
    RegistryToken * outEnclaveToken;
    int  * outServiceClassToken;
    int  * outCreateRC;
    int  * outCreateRSN;
    int  * outJoinRC;
    int  * outJoinRSN;
};
void wlm_enclave_create_join(WLM_EnclaveCreateJoinParms * parms);

/*-------------------------------------------------------------------*/
/* IWMELEAV WLM Leave an Enclave                                     */
/*-------------------------------------------------------------------*/
typedef struct WLM_EnclaveLeaveParms WLM_EnclaveLeaveParms;
struct WLM_EnclaveLeaveParms {
    RegistryToken * inEnclaveToken;
    int  * outRC;
    int  * outRSN;
};
void wlm_enclave_leave(WLM_EnclaveLeaveParms * parms);

typedef struct WLM_EnclaveDeleteData WLM_EnclaveDeleteData;
typedef struct WLM_EnclaveDeleteData {
    unsigned long long enclaveDeleteCPU;
    unsigned long long enclaveDeleteCPUService;
    unsigned long long enclaveDeletezAAPCPU;
    unsigned long long enclaveDeletezAAPService;
    unsigned long long enclaveDeletezIIPCPU;
    unsigned long long enclaveDeletezIIPService;
    unsigned int enclaveDeletezAAPnormFact;
    unsigned int enclaveDeleteRespTimeRatio;
};

typedef struct WLM_EnclaveDeleteDataPlusToken WLM_EnclaveDeleteDataPlusToken;
typedef struct WLM_EnclaveDeleteDataPlusToken {
    WLM_EnclaveDeleteData enclaveDeleteData;
    wlmetoken_t enclaveToken;
};

/*-------------------------------------------------------------------*/
/* IWM4EDEL WLM Delete an Enclave                                    */
/*-------------------------------------------------------------------*/
typedef struct WLM_EnclaveDeleteParms WLM_EnclaveDeleteParms;
struct WLM_EnclaveDeleteParms {
    RegistryToken * inEnclaveToken;
    int  * outRC;
    int  * outRSN;
    WLM_EnclaveDeleteDataPlusToken * outDeleteData;
};
void wlm_enclave_delete(WLM_EnclaveDeleteParms * parms);

/*-------------------------------------------------------------------*/
/* IWMELEAV / IWM4EDEL WLM Leave then Delete an Enclave              */
/*-------------------------------------------------------------------*/
typedef struct WLM_EnclaveLeaveDeleteParms WLM_EnclaveLeaveDeleteParms;
struct WLM_EnclaveLeaveDeleteParms {
    RegistryToken * inEnclaveToken;
    int  * outLeaveRC;
    int  * outLeaveRSN;
    int  * outDeleteRC;
    int  * outDeleteRSN;
    WLM_EnclaveDeleteDataPlusToken * outDeleteData;
};
void wlm_enclave_leave_delete(WLM_EnclaveLeaveDeleteParms * parms);

#endif /* SERVER_WLM_SERVICES_H_ */
