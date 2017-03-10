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

#include <builtins.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>
#include <ieac.h>
#include <string.h>

#include "include/ieantc.h"
#include "include/ras_tracing.h"
#include "include/bbgztrgoo.h"
#include "include/mvs_utils.h"
#include "include/server_logging_jni.h"
#include "include/server_task_data.h"
#include "include/server_trgoo_services.h"
#include "common_tracing_functions_c.c"

#pragma linkage(BPX4WRT,OS_NOSTACK)
void BPX4WRT(int filedes, void* buffer, int alet, int bufsize, int* retval, int* retcode, int* rsnval);

/**
 * Write a formatted trace record element to the trace file.
 *
 * @param message_p a pointer to the formatted trace string to write
 */
static void writeFormattedTraceRecord(const char* message_p) {
    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int filedes = 1; /* stdout */

    BPX4WRT(filedes, &message_p, 0, strlen(message_p), &retval, &retcode, &rsnval);
}

/**
 * Work around variable length array compiler bug by using preprocessor
 * instead of constants
 */
#define MAX_STR 4095

/**
 * Wake up the thread processing Trace requests.
 *
 * @param bbgztrgoo_p pointer to Trace anchor
 * @return
 */
void releaseTraceThread(bbgztrgoo * bbgztrgoo_p) {
    int oldInt, newInt = 0;
    unsigned int* intTarget_Ptr = &(bbgztrgoo_p->bbgztrgoo_threadThreadReleaseLock);

    oldInt = *intTarget_Ptr;
    do {
      if (oldInt == 0) {
          break;
      }
    } while(cs((cs_t *) &oldInt,
               (cs_t *) intTarget_Ptr,
               (cs_t)   newInt
               ));

    if (oldInt == 1) {
        /* We own the wake up call to the thread processing the trace requests. */
        iea_auth_type auth = IEA_UNAUTHORIZED;
        iea_release_code release_release_code;
        int rc;
        char data_str[MAX_STR + 1];

        TraceThreadProcessingData* traceThreadProcessingData_p = (TraceThreadProcessingData*) bbgztrgoo_p->bbgztrgoo_trace_thread_data_p;

        memset(release_release_code, 0, sizeof(release_release_code));
        iea4rls(&rc, auth, traceThreadProcessingData_p->traceThreadProcessingData_pet, release_release_code);
        if (rc) {
            data_str[0] = '\0';
            snprintf(data_str, MAX_STR, "\n  %s: %d", "Tracing encountered error when invoking iea4rls", rc);
            writeFormattedTraceRecord(data_str);
        }
    }
}

/**
 * This routine adds a trace element to the stack, wakes up a thread to process it and
 * waits until the trace is written.
 *
 * @param usr_level    trace level
 * @param trace_point  trace point
 * @param trace_data_p pointer to va_list
 */
void addTraceToStack(enum trc_level usr_level, int trace_point, void * trace_data_p) {
    int rc;
    iea_release_code release_code;
    iea_release_code release_release_code;
    iea_PEToken trace_pet;
    char data_str[MAX_STR + 1];

    iea_auth_type auth = IEA_UNAUTHORIZED;
    /* TODO save pet in thread area. Performance improvement */

    bbgztrgoo * bbgztrgoo_p = getTRGOO();
    if (bbgztrgoo_p != NULL) {
        iea4ape(&rc, auth, trace_pet);
        if (rc) {
            data_str[0] = '\0';
            snprintf(data_str, MAX_STR, "\n  %s: %d", "Tracing encountered error when invoking iea4ape", rc);
            writeFormattedTraceRecord(data_str);
        }
        else {
            server_task_data* std_p = getServerTaskData();
            std_p->trc_element_p = &(std_p->trc_element);
            traceStackElement* localStackElementPtr = std_p->trc_element_p;

            /* initialize trace element */
            memset(localStackElementPtr, 0, sizeof(localStackElementPtr->traceStackElement_header));
            memcpy(localStackElementPtr->traceStackElement_eyecatcher, "BBGZTSE ", sizeof(localStackElementPtr->traceStackElement_eyecatcher));
            memcpy(localStackElementPtr->traceStackElement_pet, trace_pet, sizeof(localStackElementPtr->traceStackElement_pet));
            localStackElementPtr->traceStackElement_tracedata_p = trace_data_p;
            localStackElementPtr->traceStackElement_traceLevel = usr_level;
            localStackElementPtr->traceStackElement_tracePoint = trace_point;
            __stck(&(localStackElementPtr->traceStackElement_createTime));
            localStackElementPtr->traceStackElement_createTcb = *((int*) 0x21c);
            bbgz_psw psw;
            extractPSW(&psw);
            localStackElementPtr->traceStackElement_createState = psw.pbm_state ? 1 : 0;
            localStackElementPtr->traceStackElement_createKey = psw.key;

            /* put trace element on stack */
            push_on_stack(&(bbgztrgoo_p->bbgztrgoo_trace_stack), (concurrent_stack_element*)localStackElementPtr);

            /* Tell the trace processing thread that a trace request is available. */
            releaseTraceThread(bbgztrgoo_p);

            iea4pse(&rc, auth, trace_pet, trace_pet, release_code);
            if (rc) {
                data_str[0] = '\0';
                snprintf(data_str, MAX_STR, "\n  %s: %d", "Tracing encountered error when invoking iea4pse", rc);
                writeFormattedTraceRecord(data_str);
            } else {
                /* free pet */
                iea4dpe(&rc, auth, trace_pet);
                if (rc) {
                    data_str[0] = '\0';
                    snprintf(data_str, MAX_STR, "\n  %s: %d", "Tracing encountered error when invoking iea4dpe", rc);
                    writeFormattedTraceRecord(data_str);
                }
            }
        }
    }
    else {
        data_str[0] = '\0';
        snprintf(data_str, MAX_STR, "\n  %s", "Tracing invoked but no trgoo exists.");
        writeFormattedTraceRecord(data_str);
    }
    return;
} /* end addTraceToStack */

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

    /* If running in a unittest environment there is no java based trace infrastructure
       available. If unittest trace is specified then write the trace records using native facilities only. */

    int unitTestTraceLevel = 0;
    int fileDesc   = 0;
    getUnitTestTraceDataFromNameToken(&unitTestTraceLevel, &fileDesc);

    if (usr_level > 0 && usr_level <= unitTestTraceLevel) {
        int tcbAddress = *((int*) 0x21c);
        bbgz_psw psw;
        extractPSW(&psw);
        int executionState = psw.pbm_state ? 1 : 0;
        int key = psw.key;
        TraceWriteNativeV(usr_level, trace_point, event_desc, usr_var_list, tcbAddress, executionState, key, fileDesc);
        return;
    }

    addTraceToStack(usr_level, trace_point, usr_var_list);
}
