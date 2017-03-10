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
#ifndef MVS_SVCDUMP_SERVICES_H_
#define MVS_SVCDUMP_SERVICES_H_

#define SVCDUMP_MALLOC_FAILURE   -1
#define SVCDUMP_MALLOC31_FAILURE -2
#define SVCDUMP_INVOKE_PC_ERROR  -3
#define SVCDUMP_NO_AUTH_STUBS    -4


/**
 * Parameter structure used by the @c takeSvcDumpAuthorizedPc routine.
 */
typedef struct SvcDumpParms {
    char* id;          //!< Input  - null terminated id of the requester. It is appended to the dump title. Only the first 56 bytes are used.
    int   idLength;    //!< Input  - length of the id not including the null.
    int*  outRC;       //!< Output - takeSvcDumpAuthorizedPc return code. 0 success, < 0 could not allocate storage, > 0 return code returned by sdumpx.
} SvcDumpParms;

/**
 * Authorized PC routine that takes a svcdump.
 *
 * @param parms A @c SvcDumpParms structure containing
 *                the input and output parameters to this function.
 *
 */
void takeSvcDumpAuthorizedPc(SvcDumpParms* parms);

/**
 * Issues a svcdump.
 *
 * @param id A pointer to the null terminated id of the requester. Only the first 56 bytes will end up in the dump title.
 *
 * @return Return code. 0 success, < 0 could not allocate storage, > 0 return code returned by sdumpx.
 *
 */
int takeSvcdump(const char* id);

/**
 * Issues a tdump (similar to an svcdump, for unauthorized callers).
 *
 * @param id A pointer to the null terminated id of the requester. Only the first 56 bytes will end up in the dump title.
 *
 * @return Return code. 0 success, < 0 could not allocate storage, > 0 return code returned by ieatdump.
 *
 */
int takeTDump(const char* id);
#endif /* MVS_SVCDUMP_SERVICES_H_ */
