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
#ifndef _BBOZ_MVS_USER_TOKEN_MANAGER_H
#define _BBOZ_MVS_USER_TOKEN_MANAGER_H

/**
 * @file
 * Creates user tokens that can be used on calls to IARV64, or other MVS
 * services which require 8 byte user tokens at the address space or
 * task level.
 */

/**
 * Gets a user token at the address space level for a problem state program.
 * The user token can be used on a call to IARV64.
 *
 * @return An 8 byte user token suitable for this problem state program.
 */
long long getAddressSpaceProblemStateUserToken(void);

/**
 * Gets a user token at the task level for a problem state program.
 * The user token can be used on a call to IARV64.
 *
 * @return An 8 byte user token suitable for the calling task in a problem
 *         state program.
 */
long long getTaskProblemStateUserToken(void);

/**
 * Gets a user token at the address space level for a supervisor state program.
 *
 * @return An 8 byte user token suitable for this supervisor state program.
 */
long long getAddressSpaceSupervisorStateUserToken(void);

#pragma pack(1)
struct userTokenBias {
    int _rsvd:28,
        bias:4;
};
#pragma pack(reset)

typedef struct userTokenBias UserTokenBias_t;

/**
 * Gets a user token at the address space level for a supervisor state program.
 * Allows the caller to add a 'bias' so that this method can generate a set
 * of user tokens that are loosely related.
 *
 * @param bias A bias that is added to the generated user token.  The bias is
 *             four bits which allows for 16 unique user tokens depending on
 *             which bias is selected.  Note that a bias of all zero bits may
 *             conflict with the user token returned by the non-biased version
 *             of this function.
 *
 * @return An 8 byte user token suitable for this supervisor state program.
 */
long long getAddressSpaceSupervisorStateUserTokenWithBias(UserTokenBias_t* bias_p);

/**
 * Gets a user token at the task level for a supervisor state program
 *
 * @return An 8 byte user token suitable for the calling task in a supervisor
 *         state program.
 */
long long getTaskSupervisorStateUserToken(void);

/**
 * Gets a user token at the system level for a supervisor state program.
 *
 * @return An 8 byte user token suitable for a supervisor state program to use
 *         for shared storage that will be used by multiple address spaces.
 */
long long getSystemSupervisorStateUserToken(void);

#endif
