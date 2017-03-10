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

/**
 * @file
 *
 * Assorted routines for creating, obtaining, releasing, and managing native latches.
 *
 */

#ifndef UTIL_LATCH_H_
#define UTIL_LATCH_H_

#define LATCH_SET_NAME_LEN      48  //!< Maximum length for latch_set_name parm.

/**
 * ISGLOB64 access_options.
 */
#pragma enum(4) 
typedef enum {
    ISGLOBT_EXCLUSIVE   = 0,    //!< Exclusive (write) access.
    ISGLOBT_SHARED      = 1     //!< Shared (read) access.
} LatchAccessOption;
#pragma enum(reset)

typedef long long LatchSetToken ;
typedef long long LatchToken ;

/**
 * Create a latch set using the ISGLCR64 service.
 *
 * @param latchSetName      The latch set name. Maximum length LATCH_SET_NAME_LEN.
 * @param numberOfLatches   The number of latches to create in the set.
 * @param latchSetToken     Output parm. Contains the token for the newly created latch set.
 *
 * @return The rc from ISGLCR64.
 *         See http://publib.boulder.ibm.com/infocenter/zos/v1r13/topic/com.ibm.zos.r13.ieaa200/iea2a2c01668.htm#wq2941.
 */
int createLatchSet(const char* latchSetName, int numberOfLatches, LatchSetToken* latchSetToken);

/**
 * Obtain a latch using the ISGLOB64 service.
 *
 * @param latchSetToken     The token for the latch set.
 * @param latchNumber       The index of the latch to obtain within the set.
 * @param accessOption      The access option, either ISGLOBT_EXCLUSIVE or ISGLOBT_SHARED.
 * @param latchToken        Output parm. Contains the token for the obtained latch.
 *
 * @return The rc from ISGLOB64.
 *         See http://publib.boulder.ibm.com/infocenter/zos/v1r13/topic/com.ibm.zos.r13.ieaa200/iea2a2c01690.htm#wq2974.
 */
int obtainLatch(LatchSetToken* latchSetToken, int latchNumber, LatchAccessOption accessOption, LatchToken* latchToken);

/**
 * Release a latch using the ISGLRE64 service.
 *
 * @param latchSetToken     The token for the latch set.
 * @param latchToken        The token for the previously obtained latch.
 *
 * @return The rc from ISGLRE64.
 *         See http://publib.boulder.ibm.com/infocenter/zos/v1r13/topic/com.ibm.zos.r13.ieaa200/iea2a2c01702.htm#wq2992.
 */
int releaseLatch(LatchSetToken* latchSetToken, LatchToken* latchToken);

#endif /* UTIL_LATCH_H_ */
