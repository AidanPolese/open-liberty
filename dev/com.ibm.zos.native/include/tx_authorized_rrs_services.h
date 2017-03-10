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

#ifndef TX_AUTHORIZED_RRS_SERVICES_H_
#define TX_AUTHORIZED_RRS_SERVICES_H_

#include <atrrc.h>
#include <crgc.h>
#include <ctxc.h>

#include "util_registry.h"
#include "server_process_data.h"

/**
 * Performs RRS cleanup. Called during task level termination out of 
 * server_authorized_function_module->serverAuthorizedProcessCleanup
 *
 * @param serverProcessData_p The pointer to the server process data.
 */
void cleanupRRS(server_process_data* serverProcessData_p);

/**
 * Registers resource manager.
 */
struct crg4grm_parms {
    int* returnCode;
    char* resMgrName;
    RegistryToken* resMgrNameRegistryToken;
    char* resMgrToken;
    RegistryToken* resMgrRegistryToken;
    int unregisterOption;
    crg_rm_global_data crgGlobalData;
    char* rmNamePrefix;
    int rmNamePrefixLength;
    char* rmNameSTCK;
    int* internalAuthCheckRc;
    int* safRc;
    int* racfRc;
    int* racfRsn;
};
void crg_register_resource_manager(struct crg4grm_parms*);

/**
 * Unregisters Resource Manager.
 */
struct crg4drm_parms {
    int* returnCode;
    int* resMgrNameRegistryReturnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrNameRegistryToken;
    RegistryToken resMgrToken;
};
void crg_unregister_resource_manager(struct crg4drm_parms*);

/**
 *  Sets exit information.
 */
struct crg4seif_parms {
    int* returnCode;
    int* resMgrNameRegistryReturnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrNameRegistryToken;
    RegistryToken resMgrToken;
    char recovery;
    int* metaDataLoggingAllowed;
};
void atr_set_exit_information(struct crg4seif_parms*);

/**
 * Begins restart.
 */
struct atr4ibrs_parms {
    int* returnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrToken;
};
void atr_begin_restart(struct atr4ibrs_parms*);

/**
 * Ends restart.
 */
struct atr4iers_parms {
    int* returnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrToken;
};
void atr_end_restart(struct atr4iers_parms*);

/**
 * Retrieves log name.
 */
struct atr4irln_parms {
    int* returnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrToken;
    int resMgrLogNameBuffLen;
    int* resMgrLogNameLength;
    char* resMgrLogName;
    int* rrsLogNameLength;
    char* rrsLogName;
};
void atr_retrieve_log_name(struct atr4irln_parms*);

/**
 * Sets log name.
 */
struct atr4isln_parms {
    int* returnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrToken;
    int resMgrLogNameLen;
    char* resMgrLogName;
};
void atr_set_log_name(struct atr4isln_parms*);

/**
 * Retrieves work ID.
 */
struct atr4rwid_parms {
    int* returnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int retrieveOption;
    int generateOption;
    int workIdType;
    int workIdBuffLen;
    int* workIdLength;
    char* workId;
};
void atr_retrieve_work_identifier(struct atr4rwid_parms*);

/**
 * Sets work ID.
 */
struct atr4swid_parms {
    int* returnCode;
    atr_ur_or_uri_token ur_or_uriToken;
    int setOption;
    int workIdType;
    int workIdLen;
    char* workId;
};
void atr_set_work_identifier(struct atr4swid_parms*);

/**
 * Expresses interest.
 */
struct atr4eint_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrToken;
    atr_context_token contextToken;
    char* urInterestToken;
    RegistryToken* urInterestRegistryToken;
    char* ur_token;
    char* currCtxToken;
    char* ur_id;
    int interest_options;
    atr_non_persistent_data nonPersistentData;
    char* currentNonPersistentData;
    int pdataLength;
    char* persistentData;
    int workIdLen;
    char* workId;
    atr_ur_token parent_urToken;
    char* diagnosticArea;
    int* transactionMode;
};
void atr_express_ur_interest(struct atr4eint_parms*);

/**
 * Retrieves side information.
 */
struct atr4rusi_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int infoIdCount;
    int* sideInfoIds;
    int* sideInfoStates;
};
void atr_retrieve_side_information(struct atr4rusi_parms*);

/**
 * Retrieves UR interest.
 */
struct atr4irni_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    int* resMgrNameRegistryReturnCode;
    RegistryToken resMgrToken;
    char* contextToken;
    char* urInterestToken;
    RegistryToken* uriRegistryToken;
    char* urIdentifier;
    int* rmRole;
    int* loggedState;
    int pdataBuffLen;
    int* pdataLen;
    char* persistentData;
};
void atr_retrieve_ur_interest(struct atr4irni_parms*);

/**
 * Sets environment information.
 */
struct atr4senv_parms {
    int* returnCode;
    char* diagnosticArea;
    atr_stoken envStoken;
    int contentCount;
    int* environmentIds;
    int* environmentIdValues;
    int* envProtectionValues;
};
void atr_set_environment(struct atr4senv_parms*);

/**
 * Prepares the UR.
 */
struct atr4aprp_parms {
    int* rrsPrepareReturnCode;      //!< The return code from ATR4APRP
    int* ctxExpressInterestReturnCode; //!< The return code from CTX4EINT
    int* registryReturnCodeUri; //!< The return code from registry services when looking up the URI.
    int* registryReturnCodeContext; //!< The return code from registry services when looking up the context token.
    int* registryReturnCodeContextInterest; //!< The return code from registry services when adding the context interest token.
    int* registryReturnCodeResMgrToken; //!< The return code from the registry service when looking up the resource manager token.
    RegistryToken uriRegistryToken; //!< The registry token used to look up the URI for ATR4APRP.
    RegistryToken contextRegistryToken; //!< The registry token used to look up the context token for CTX4EINT.
    RegistryToken rmToken; //!< The resource manager token.
    RegistryToken* contextInterestRegistryToken_p; //!< A pointer to the registry token used to look up the context interest token if the UR is now in-doubt.
    int logOption; //!< The log option to pass to RRS on ATR4APRP.
};
void atr_prepare_agent_ur(struct atr4aprp_parms*);

/**
 * Commits the UR.
 */
struct atr4acmt_parms {
    int* rrsReturnCode;
    int* registryReturnCodeUri;
    int* registryReturnCodeCi;
    RegistryToken uriRegistryToken;
    RegistryToken ciRegistryToken;
    int logOption;
};
void atr_commit_agent_ur(struct atr4acmt_parms*);

/**
 * Delegates two phase commit processing to RRS.
 */
struct atr4adct_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int logOption;
    int commitOptions;
};
void atr_delegate_commit_agent_ur(struct atr4adct_parms*);

/**
 * Backs out the UR.
 */
struct atr4abak_parms {
    int* rrsReturnCode;
    int* registryReturnCodeUri;
    int* registryReturnCodeCi;
    RegistryToken uriRegistryToken;
    RegistryToken ciRegistryToken;
    int logOption;
};
void atr_backout_agent_ur(struct atr4abak_parms*);

/**
 * Forgets the UR.
 */
struct atr4afgt_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int logOption;
};
void atr_forget_agent_ur_interest(struct atr4afgt_parms*);

/**
 * Responds to a deferred UR exit.
 */
struct atr4pdue_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int exitNumber;
    int completionCode;
};
void atr_post_deferred_ur_exit(struct atr4pdue_parms*);

/**
 * Responds to retrieved interest.
 */
struct atr4irri_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int responseCode;
    atr_non_persistent_data nonPData;
};
void atr_respond_to_retrieved_interest(struct atr4irri_parms*);

/**
 * Sets persistent interest data.
 */
struct atr4spid_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int pdataLength;
    char* pdata;
};
void atr_set_persistent_interest_data(struct atr4spid_parms*);

/**
 * Sets syncpoint controls.
 */
struct atr4sspc_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int prepareExitCode;
    int commitExitCode;
    int backoutExitCode;
    int rmRole;
};
void atr_set_syncpoint_controls(struct atr4sspc_parms*);

/**
 * Sets side information.
 */
struct atr4susi_parms {
    int* rrsReturnCode;
    int* registryReturnCode;
    RegistryToken uriRegistryToken;
    int elementCount;
    int* infoIdsArray;
};
void atr_set_side_information(struct atr4susi_parms*);

/**
 * Sets resource manager metadata.
 */
struct atr4sdta_parms {
    int* returnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrToken;
    int resMgrMetadataLen;
    char* resMgrMetadata;
};
void atr_set_rm_metadata(struct atr4sdta_parms*);

/**
 * Retrieves resource manager metadata.
 */
struct atr4rdta_parms {
    int* returnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken resMgrToken;
    int resMgrMetadataBuffLen;
    int* resMgrMetadataLength;
    char* resMgrMetadata;
};
void atr_retrieve_rm_metadata(struct atr4rdta_parms*);

/**
 * Parameters for RRS exit routine.  Modified from atrrc.h.
 */
typedef struct {
  atr_return_code*            __ptr32 ATRXParmReturnCodePtr;
  int*                        __ptr32 ATRXParmVersionPtr;
  atr_exit_number*            __ptr32 ATRXParmExitNumberPtr;
  atr_resource_manager_token* __ptr32 ATRXParmRMTokenPtr;
  atr_exit_manager_name*      __ptr32 ATRXParmExitMgrNamePtr;
  atr_rm_global_data*         __ptr32 ATRXParmRMGlobalDataPtr;
  atr_uri_token*              __ptr32 ATRXParmURITokenPtr;
  atr_non_persistent_data*    __ptr32 ATRXParmNonPersistentDataPtr;
  ATRXParmExitFlags*          __ptr32 ATRXParmExitFlagsPtr;
  int*                        __ptr32 ATRXParmValue1Ptr;
  int*                        __ptr32 ATRXParmValue2Ptr;
  int*                        __ptr32 ATRXParmValue3Ptr;
  int*                        __ptr32 ATRXParmValue4Ptr;
  int*                        __ptr32 ATRXParmValue5Ptr;
} ATRXParmList64;

/** Parameters for CTX exit routine.  Modified from ctxc.h. */
typedef struct {
  ctx_return_code*            __ptr32 CTXEPReturnCodePtr;
  int*                        __ptr32 CTXEPVersionPtr;
  int*                        __ptr32 CTXEPExitNumberPtr;
  ctx_resource_manager_token* __ptr32 CTXEPRMTokenPtr;
  ctx_exit_manager_name*      __ptr32 CTXEPExitMgrNamePtr;
  ctx_rm_global_data*         __ptr32 CTXEPRMGlobalDataPtr;
  ctx_context_token*          __ptr32 CTXEPCToken;
  ctx_ci_token*               __ptr32 CTXEPCITokenPtr;
  ctx_ci_data*                __ptr32 CTXEPCIDataPtr;
  int*                        __ptr32 CTXEPValue1Ptr;
  int*                        __ptr32 CTXEPValue2Ptr;
  int*                        __ptr32 CTXEPValue3Ptr;
  int*                        __ptr32 CTXEPValue4Ptr;
  int*                        __ptr32 CTXEPValue5Ptr;
} CTXEParameterList64;

/**
 * RRS exit routine.
 */
void atr_exit_routine(ATRXParmList64* parms_p);

/**
 * Creates a new privately managed context.
 */
struct ctx4begc_parms {
    int* returnCode;
    int* resMgrTokenRegistryReturnCode;
    RegistryToken rmRegistryToken;
    char* ctxToken;
    RegistryToken* ctxRegistryToken;
};
void ctx_begin_context(struct ctx4begc_parms*);

/**
 * Switches a privately managed context onto the current thread.
 */
struct ctx4swch_parms {
    int* returnCode;
    RegistryToken inputCtxRegistryToken;
    char* outputCtxToken;
};
void ctx_context_switch(struct ctx4swch_parms*);

/**
 * Ends a privately managed context.
 */
struct ctx4endc_parms {
    int* returnCode;
    RegistryToken inputCtxRegistryToken;
    int completionType;
};
void ctx_end_context(struct ctx4endc_parms*);

#endif /* TX_AUTHORIZED_RRS_SERVICES_H_ */
