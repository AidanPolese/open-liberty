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
#include <metal.h>
#include <stdarg.h>

#include "include/ras_tracing.h"

/**
 * Trace formatting routine.
 *
 * @param usr_level    trace level
 * @param trace_point  trace point
 * @param event_desc   trace event description
 * @param usr_var_list pointer to va_list
 */
void
TraceWriteV(enum trc_level usr_level, int trace_point, char* event_desc, va_list usr_var_list) {
    // NO-OP.
}

