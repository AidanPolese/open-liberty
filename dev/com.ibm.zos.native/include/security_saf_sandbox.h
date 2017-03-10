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

#ifndef SECURITY_SAF_SANDBOX_H_
#define SECURITY_SAF_SANDBOX_H_

#define EJBROLE_CLASS           "EJBROLE"           //!< EJBROLE CLASS name.

/**
 * Verify that the server has been granted authority to perform authentication
 * and/or authorization checks against the given APPL, CLASS, and RESOURCE PROFILE.
 *
 * @param appl
 * @param className
 * @param resource
 *
 * @return 0 if server has authority; an error-code otherwise.
 */
int checkPenaltyBox(const char* appl, const char* className, char* resource);

/**
 * Parameter structure used by the @c flushPenaltyBoxCache routine.
 */
typedef struct {
    int* returnCodePtr;       //!< Output - return code
} FlushPenaltyBoxParms;

/**
 * Clear out the penalty box cache.
 *
 * This is usually called in response to a server config update that changed
 * the profilePrefix.
 *
 * @param parms the flush penalty box parameter list
 */
void flushPenaltyBoxCache(FlushPenaltyBoxParms* parms);

#endif /* SECURITY_SAF_SANDBOX_H_ */
