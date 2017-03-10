/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Provides function for issuing FFDC records from native local comm code.
 *
 * The FFDC record is issued from the server's java code.  The native code 
 * queues a REQUESTTYPE_FFDC work request on the server's localcomm "black"/work
 * queue containing the FFDC data.  The work request is handled in Java which
 * writes the FFDC record.
 *
 * This is a hackish way to report FFDC-type failures from the native code
 * (especially the localcomm native code, which is barren of trace/error
 * reporting, especially on the client side, because there's nowhere really
 * to log the error.
 *
 * TODO: make this a generic native code utility (as opposed to attached to
 *       the localcomm code). Could piggy back on the tracing code.  One thing
 *       to keep in mind tho is we'd want to also issue FFDCs from a localcomm
 *       client, who would not have access to our trace stuff so we'd need a
 *       solution for that.
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdarg.h>

#include "include/server_local_comm_ffdc.h"
#include "include/server_local_comm_client.h"
#include "include/server_local_comm_queue.h"

/**
 * Queue an FFDC request up to the server for issuing an FFDC record.
 *
 * NOTE: The string msg, after all substitutions, must not exceed REQUESTTYPE_FFDC_RAW_DATA_SIZE.
 * Anything beyond that length will be trimmed off.
 *
 * @param clientConnHandle_p Pointer to the client's LocalCommClientConnectionHandle_t
 *        (Note: this is declared as void * to accommodate OpaqueClientConnectionHandle_t).
 * @param tp The trace point identifying the FFDC record
 * @param msg The FFDC error message
 * @param ... va_args for the msg parm (a la vsnprintf).
 *
 * @return 0 if all went well;
 *         otherwise the failing RC from issueFFDCRequest
 */
int localCommFFDC(void * clientConnHandle_p, int tp, char * msg, ...) {

    va_list argp;
    char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE];
    va_start(argp, msg);
    vsnprintf(rawData, REQUESTTYPE_FFDC_RAWDATA_SIZE, msg, argp);
    va_end(argp);

    return issueFFDCRequest( (LocalCommClientConnectionHandle_t *) clientConnHandle_p, tp, rawData);
}


