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

#ifndef SECURITY_SAF_AUTHZ_H_
#define SECURITY_SAF_AUTHZ_H_

#include "security_saf_authorization.h"
#include "security_saf_common.h"
#include "util_registry.h"

/**
 * Parameter structure used by the @c checkAccess routine.
 */
typedef struct {
    RegistryToken*      safCredentialToken; //!< Input - A token associated with the native security credential (RACO) to be authorized.
    char*               resource;           //!< Input - The resource profile to be authorized against.
    int                 resourceLen;        //!< Input - The resource profile length
    char*               className;          //!< Input - The CLASS of the resource profile.
    int                 classNameLen;        //!< Input - The CLASS name length
    char*               applName;           //!< Input - The application name.
    int                 applNameLen;        //!< Input - The application name length
    saf_access_level    accessLevel;        //!< Input - The authorization level (e.g READ, UPDATE, etc).
    saf_log_option      logOption;          //!< Input - The logging option to apply to the SAF service calls.
    int                 msgSuppress;        //!< Input - Indicates whether or not to suppress SAF messages during the authz attempt.
    int                 fastAuth;           //!< Input - Indicates whether or not to perform FASTAUTH (RACROUTE REQUEST=FASTAUTH).
    SAFServiceResult*   safServiceResult;   //!< Output - Contains the SAF return code and RACF return and reason codes.
} CheckAccessParms;

/**
 * PC routine performs a SAF authz check for the given credential against the given
 * CLASS/APPLNAME/RESOURCE, on behalf of non-authorized callers, like Java.
 *
 * The actual authz check is made by a call to checkAuthorization.  This method 
 * handles copying the parameters across storage keys, and retrieving the ACEE from
 * the RACO referenced by the given credential.
 *
 * @param parms The CheckAccessParms structure, containing the native credential token
 *        to authorize and the CLASS, APPLNAME, and RESOURCE in which to authorize
 *        against.  The result of the operation is contained in the safServiceResult
 *        field within the CheckAccessParms.
 */
void checkAccess(CheckAccessParms* parms);

/**
 * Parameter structure used by the @c isSAFClassActive routine.
 */
typedef struct {
    char*               className;          //!< Input - The CLASS of the resource profile.
    int                 classNameLen;       //!< Input - The length of the className.
    int*                rc;                 //!< Output - The rc.
} IsSAFClassActiveParms;

/**
 * PC routine performs a SAF check to see if the given SAF CLASS is active.
 *
 * The actual check is made by a call to isClassActive, which performs a 
 * RACROUTE REQUEST=STAT under the covers.
 *
 * @param parms The IsSAFClassActiveParms structure, containing the SAF CLASS name, 
 *        and an int field for the return code.
 */
void isSAFClassActive(IsSAFClassActiveParms* parms);

#endif /* SECURITY_SAF_AUTHZ_H_ */
