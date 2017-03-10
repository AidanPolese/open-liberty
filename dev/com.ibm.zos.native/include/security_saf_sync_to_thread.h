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

#ifndef SECURITY_SAF_SYNC_TO_THREAD_H_
#define SECURITY_SAF_SYNC_TO_THREAD_H_

#include "security_saf_common.h"
#include "util_registry.h"

/**
 * Parameter structure used by the @c syncToThread routine.
 */
typedef struct {
    RegistryToken*      safCredentialToken; //!< Input - A token associated with the native security credential (RACO) to be authorized.
    char*               profilePrefix;      //!< Input - The profile prefix.
    int                 profilePrefixLen;   //!< Input - The profile prefix length.
    SAFServiceResult*   safServiceResult;   //!< Output - Contains the SAF return code and RACF return and reason codes.
} SyncToThreadParms;

/**
 * This PC routine handles copying the parameters across storage keys,
 * deletes the previous thread security environment, performs surrogate authorization check,
 * and creates the new thread security environment  with the ACEE retrieved from
 * the RACO referenced by the given credential.
 *
 * @param parms The SyncToThreadParms structure, containing the native credential token.
 *        The result of the operation is contained in the safServiceResult
 *        field within the SyncToThreadParms.
 */
void syncToThread(SyncToThreadParms* parms);

/**
 * Parameter structure used by the @c isSyncToThreadEnabled routine.
 */
typedef struct {
    char*               profilePrefix;       //!< Input - The profile prefix.
    int                 profilePrefixLen;    //!< Input - The profile prefix length.
    int*                syncToThreadEnabled; //!< Output - 1 when sync to thread is enabled, 0 otherwise.
} IsSyncToThreadEnabledParms;

/**
 * This PC routine handles copying the parameters across storage keys,
 * and checks to see if the server has READ access to BBG.SYNC.profilePrefix
 * in the FACILITY class. If it does a flag is set in the server process data.
 *
 * BBG.SYNC.profilePrefix CLASS(FACILITY) ID(server user ID) ACC(READ)
 *
 * @param parms_p The IsSyncToThreadEnabledParms structure.
 *        The result of the operation is contained in the syncToThreadEnabled
 *        field within the IsSyncToThreadEnabledParms.
 */
void isSyncToThreadEnabled(IsSyncToThreadEnabledParms* parms_p);

/**
 * Parameter structure used by the @c resetSyncToThreadEnabled routine.
 */
typedef struct {
    int*                returnCode; //!< Output - return code.
} ResetSyncToThreadEnabledParms;

/**
 * This PC routine handles copying the parameters across storage keys,
 * and resets the server process data flag indicating the we checked for
 * READ access to BBG.SYNC.profilePrefix
 *
 * BBG.SYNC.profilePrefix CLASS(FACILITY) ID(server user ID) ACC(READ)
 *
 * @param parms_p The ResetSyncToThreadEnabledParms structure.
 *        The result of the operation is contained in the returnCode
 *        field within the ResetSyncToThreadEnabledParms.
 */
void resetSyncToThreadEnabled(ResetSyncToThreadEnabledParms* parms_p);

#endif /* SECURITY_SAF_SYNC_TO_THREAD_H_ */
