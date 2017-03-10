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
 * Using latch manager services: 
 * http://publib.boulder.ibm.com/infocenter/zos/v1r12/topic/com.ibm.zos.r12.ieaa800/gguide.htm#gguide
 * http://publib.boulder.ibm.com/infocenter/zos/v1r13/topic/com.ibm.zos.r13.ieaa200/isgcr64.htm#isgcr64
 */

#include <metal.h>
#include <stdlib.h>

#include "include/common_defines.h"
#include "include/ras_tracing.h"
#include "include/mvs_latch.h"

#define RAS_MODULE_CONST RAS_MODULE_MVS_LATCH

#define REQUESTOR_ID_LEN    8   //!< Maximum length of the requestor_ID parm.

/**
 * ISGLOB64 obtain_option.
 *
 * The system processes the request synchronously. The system suspends the requestor. 
 * When the latch manager eventually grants ownership of the latch to the requestor, 
 * the system returns control to the requestor.
 */
#define ISGLOB64_SYNC       0 

/**
 * ISGLOB64 obtain_option.
 *
 * The system processes the request conditionally. The system returns control to the 
 * requestor with a return code of ISGLOBT_CONTENTION (value of 4). The latch manager 
 * does not queue the request to obtain the latch.
 */
#define ISGLOB64_COND       1

/**
 * ISGLOB64 obtain_option.
 * The system processes the request asynchronously. The system returns control to the 
 * requestor with a return code of ISGLOBT_CONTENTION (value of 4). When the latch manager 
 * eventually grants ownership of the latch to the requestor, the system posts the ECB 
 * pointed to by the value specified on the ECB_address parameter.
 *
 * When you specify this option, the ECB_address parameter must contain the address of an 
 * initialized ECB that is addressable from the home address space (HASN).
 */
#define ISGLOB64_ASYNC_ECB  2

/**
 * See http://publib.boulder.ibm.com/infocenter/zos/v1r13/topic/com.ibm.zos.r13.ieaa200/iea2a2c01700.htm#wq2990
 */
#define ISGLREL_UNCOND      0

/**
 * See http://publib.boulder.ibm.com/infocenter/zos/v1r13/topic/com.ibm.zos.r13.ieaa200/iea2a2c01700.htm#wq2990
 */
#define ISGLREL_COND        1

/**
 * 512-byte workarea.
 */
typedef struct {
    char w[512];
} Workarea512;

// Setup linkage to ISG LATCH operations.
#pragma linkage(ISGLCR64, OS)
void ISGLCR64(int number_of_latches,
              char* latch_set_name,
              int create_option,
              LatchSetToken* latch_set_token,
              int* return_code);

#pragma linkage(ISGLOB64, OS)
void ISGLOB64(LatchSetToken* latch_set_token,
              int latch_number,
              char* requestor_ID,
              int obtain_option,
              int access_option,
              int ECB_address,
              LatchToken* latch_token,
              Workarea512* work_area,
              int* return_code);

#pragma linkage(ISGLRE64, OS)
void ISGLRE64(LatchSetToken* latch_set_token,
              LatchToken* latch_token,
              int release_option,
              Workarea512* work_area,
              int* return_code);

#pragma linkage(ISGLPR64, OS)
void ISGLPR64(LatchSetToken* latch_set_token,
              char* requestor_ID,
              int* return_code);

/**
 * See mvs_latch.h for function documentation.
 */
int createLatchSet(const char* latchSetName, int numberOfLatches, LatchSetToken* latchSetToken) {

    int rc = -1;
    int createOption = 0;

    char tmpLatchSetName[LATCH_SET_NAME_LEN];
    memset(tmpLatchSetName, ' ', LATCH_SET_NAME_LEN);   // Blank-pad the area.
    int latchSetNameLen = BBGZ_min(strlen(latchSetName),LATCH_SET_NAME_LEN);    // Copy up to LATCH_SET_NAME_LEN bytes.
    memcpy(tmpLatchSetName, latchSetName, latchSetNameLen);     // Null-term is NOT copied. Area is blank-padded.

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_RAWDATA(LATCH_SET_NAME_LEN, tmpLatchSetName, "tmpLatchSetName"),
                    TRACE_DATA_INT(numberOfLatches, "numberOfLatches"),
                    TRACE_DATA_HEX_LONG(*latchSetToken, "latchSetToken"),
                    TRACE_DATA_END_PARMS);
    }

    ISGLCR64(numberOfLatches,
             tmpLatchSetName,
             createOption,
             latchSetToken,
             &rc);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_HEX_LONG(*latchSetToken, "latchSetToken"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * See mvs_latch.h for function documentation.
 */
int obtainLatch(LatchSetToken* latchSetToken, int latchNumber, LatchAccessOption accessOption, LatchToken* latchToken) {

    int rc = -1;
    Workarea512 workarea;
    int ecbAddr = 0;
    char requestorId[REQUESTOR_ID_LEN] = "LIBERTY";

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_HEX_LONG(*latchSetToken, "latchSetToken"),
                    TRACE_DATA_INT(latchNumber, "latchNumber"),
                    TRACE_DATA_INT(accessOption, "accessOption (0=exclusive;1=shared)"),
                    TRACE_DATA_END_PARMS);
    }

    ISGLOB64(latchSetToken,
             latchNumber,
             requestorId,
             ISGLOB64_SYNC,
             (int) accessOption,
             ecbAddr,
             latchToken,
             &workarea,
             &rc);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_HEX_LONG(*latchToken, "latchToken"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}
 
/**
 * See mvs_latch.h for function documentation.
 */
int releaseLatch(LatchSetToken* latchSetToken, LatchToken* latchToken) {

    int rc = -1;
    Workarea512 workarea;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_HEX_LONG(*latchSetToken, "latchSetToken"),
                    TRACE_DATA_HEX_LONG(*latchToken, "latchToken"),
                    TRACE_DATA_END_PARMS);
    }

    ISGLRE64(latchSetToken,
             latchToken,
             ISGLREL_UNCOND,
             &workarea,
             &rc);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

int purgeLatchSet(LatchSetToken* latchSetToken) {

    int rc = -1;
    char requestorId[REQUESTOR_ID_LEN] = "LIBERTY";

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(7),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_HEX_LONG(*latchSetToken, "latchSetToken"),
                    TRACE_DATA_END_PARMS);
    }

    ISGLPR64(latchSetToken,
             requestorId,
             &rc);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_HEX_LONG(*latchSetToken, "latchSetToken"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}


