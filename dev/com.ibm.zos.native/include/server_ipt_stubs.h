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
#ifndef _BBOZ_SERVER_IPT_STUBS_H
#define _BBOZ_SERVER_IPT_STUBS_H

/**
 * Parameter struct for the IPT utility routine driveAuthorizedServiceOnIPT.
 */
typedef struct driveAuthorizedServiceOnIPTParms {
    int pcReturnCode;    //!< The return code from the authorized PC stub.
    int parmStructSize;  //!< The size of the parm struct passed to the authorized PC stub, as defined in the AUTH_DEF.
    void* authRoutine_p; //!< The address of the authorized routine to call in the server_authorized_function_stubs structure.
    void* parmStruct_p;  //!< The address of the parm struct which is passed to the authorized PC stub.
} DriveAuthorizedServiceOnIPTParms_t;

/**
 * Drives an authorized service on the IPT.  This routine should be driven from
 * LE C.  It will call BPX4IPT to switch to the IPT, invoking an exit which runs
 * as an unauthorized function (in BBGZSUFM).  The exit then looks up the authorized
 * service offset in the authorized services table, and calls the invoke PC to
 * drive the requested authorized service.
 *
 * @param parms_p A pointer to the DriveAuthorizedServiceOnIPTParms structure,
 *                which describes the requested authorized service and its
 *                parameters.
 * @param bpxReturnValue_p A pointer to a full word where the return value from
 *                         BPX4IPT is stored.  A value of 0 indicates that
 *                         BPX4IPT was driven successfully, and nonzero
 *                         indicates there was a problem.
 * @param bpxReturnCode_p A pointer to a full word where the return code from
 *                        BPX4IPT is stored.  The return code is only populated
 *                        if the return value was nonzero.
 * @param bpxReasonCode_p A pointer to a full word where the reason code from
 *                        BPX4IPT is stored.  The reason code is only populated
 *                        if the return value was nonzero.
 *
 * @return Returns 0 if the BPX4IPT service could be invoked, nonzero if BPX4IPT
 *         could not be invoked.  If 0 is returned, the caller should check
 *         bpxReturnValue_p to see if the call to BPX4IPT was successful, and
 *         the return codes referenced in parms_p to see if the requested
 *         service was successful.
 */
int driveAuthorizedServiceOnIPT(DriveAuthorizedServiceOnIPTParms_t* parms_p, int* bpxReturnValue_p, int* bpxReturnCode_p, int* bpxReasonCode_p);

#endif
