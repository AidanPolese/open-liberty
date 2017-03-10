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
#include <stdio.h>
#include <ieac.h>

// Skip code inclusion from server_process_data.h.  This is so we don't have to drag additional .o's 
// into some dlls that only need to look at server_process_data struct fields.
#define _BBOZ_SERVER_PROCESS_DATA_NOCODE_H

#include "include/ras_tracing.h"
#include "include/bbgztrgoo.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/server_logging_jni.h"
#include "include/server_process_data.h"
#include "include/server_task_data.h"
#include "include/server_trgoo_services.h"
#include "common_tracing_functions_c.c"

#pragma linkage(BPX4WRT,OS_NOSTACK)
void BPX4WRT(int filedes, void* buffer, int alet, int bufsize, int* retval, int* retcode, int* rsnval);

/**
 * Forward declares
 */
void releaseAllWaiters(void);

/**
 * Write a formatted trace record element to the trace file.
 *
 * @param message_p a pointer to the formatted trace string to write
 */
static void writeFormattedTraceRecord(const char* message_p) {
    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int filedes = 1; // stdout

    BPX4WRT(filedes, &message_p, 0, strlen(message_p), &retval, &retcode, &rsnval);
}

//
// Work around variable length array compiler bug by using preprocessor
// instead of constants
//
#define MAX_STR 4095

/**
 * Set up the task data with a trace element that is in a key appropriate for
 * tracing (key 8).
 *
 * @return the address of the trace stack element.
 */
static traceStackElement* getExternalTraceElementPtrFromStackPrefix(void) {
    traceStackElement* tse_p = NULL;

    server_task_data* std_p = getServerTaskData();
    if (std_p != NULL) {
        if (std_p->trc_element_p == NULL) {
            int* rc_p = 0;
            std_p->trc_element_p = (traceStackElement *) storageObtain(sizeof(traceStackElement),
                                                                       230, /* Subpool */
                                                                       8,  /* Key */
                                                                       rc_p); /* No return code */
        }

        tse_p = std_p->trc_element_p;
    }

    return tse_p;
}

/**
 * Wake up the thread processing Trace requests.
 */
static void releaseTraceThread(bbgztrgoo * bbgztrgoo_p) {
    int zeroInt = 0;
    unsigned int *newIntKey8_p;
    unsigned int *oldIntKey8_p;

    unsigned int* intTarget_Ptr = &(bbgztrgoo_p->bbgztrgoo_threadThreadReleaseLock);

    // Use the key 8 storage anchored to this test for tracing as working storage for the cs()
    traceStackElement* traceStackElement_p = getExternalTraceElementPtrFromStackPrefix();

    newIntKey8_p = &(traceStackElement_p->traceStackElement_new_cs_area);
    oldIntKey8_p = &(traceStackElement_p->traceStackElement_old_cs_area);


    memcpy_dk(newIntKey8_p,
              &zeroInt,
              sizeof(*newIntKey8_p),
              8);

    memcpy_dk(oldIntKey8_p,
              intTarget_Ptr,
              sizeof(*oldIntKey8_p),
              8);
    do {
       // If currently not requesting a "release", bail.
      if (*oldIntKey8_p == 0) {
          break;
      }

      unsigned char old_key;
      // Store the original key
      __asm(" IPK\n"
            " STC 2,%0\n" :
            "=m"(old_key) : :
            "r2");

      // Switch to key 8
      unsigned char new_key = 0x80;
      __asm(" MODESET KEYADDR=%0,WORKREG=11" : :
            "m"(new_key) : "r0","r1","r11","r14","r15");

      // Make updates to key 8 storage with compare and swap.
      traceStackElement_p->traceStackElement_csRetCode = __cs(oldIntKey8_p,      // old
                                                              intTarget_Ptr,     // current
                                                              *newIntKey8_p);    // new

      // Return to original key
      __asm(" MODESET KEYADDR=%0,WORKREG=11" : :
            "m"(old_key) : "r0","r1","r11","r14","r15");

    } while(traceStackElement_p->traceStackElement_csRetCode);

    if (*oldIntKey8_p == 1) {
        // We own the wake up call to the thread processing the trace requests.
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

    return;
}

/**
 * This routine adds a trace element to the stack, wakes up a thread to process it and
 * waits until the trace is written.
 *
 * @param usr_level    trace level
 * @param trace_point  trace point
 * @param trace_data_p pointer to va_list
 *
 */
void addTraceToStack(enum trc_level usr_level, int trace_point, void * trace_data_p) {
    int rc;
    iea_release_code release_code;
    iea_release_code release_release_code;
    iea_PEToken trace_pet;
    char data_str[MAX_STR + 1];

    iea_auth_type auth = IEA_UNAUTHORIZED;
    /* TODO save pet in thread area. Performance improvement */

    // If the server is dying hard, just return.
    server_task_data* std_p = getServerTaskData();
    server_process_data* spd_p = (std_p != NULL) ? std_p->spd_p : NULL;
    if (spd_p && spd_p->serializedFlags.serverHardFailureDetected) {
        return;
    }

    bbgztrgoo * bbgztrgoo_p = getTRGOO();
    if (bbgztrgoo_p != NULL) {
        iea4ape(&rc, auth, trace_pet);
        if (rc) {
            data_str[0] = '\0';
            snprintf(data_str, MAX_STR, "\n  %s: %d", "Tracing encountered error when invoking iea4ape", rc);
            writeFormattedTraceRecord(data_str);
        }
        else {
            traceStackElement * taskStackElementPtr = getExternalTraceElementPtrFromStackPrefix();
            if (taskStackElementPtr == 0) {
                data_str[0] = '\0';
                snprintf(data_str, MAX_STR, "\n  %s", "Tracing encountered error when trying to obtain storage for a stack element.");
                writeFormattedTraceRecord(data_str);
                /* free pet */
                iea4dpe(&rc, auth, trace_pet);
                if (rc) {
                    data_str[0] = '\0';
                    snprintf(data_str, MAX_STR, "\n  %s: %d", "Tracing encountered error when invoking iea4dpe", rc);
                    writeFormattedTraceRecord(data_str);
                }
                return;
            }
            /* initialize trace element */
            void * nullPtr = 0;
            traceStackElement localStackElement;
            memset(&localStackElement, 0, sizeof(localStackElement));
            memcpy(localStackElement.traceStackElement_eyecatcher, "BBGZTSE ", sizeof(localStackElement.traceStackElement_eyecatcher));
            memcpy(localStackElement.traceStackElement_pet, trace_pet, sizeof(localStackElement.traceStackElement_pet));
            localStackElement.traceStackElement_tracedata_p = trace_data_p;
            localStackElement.traceStackElement_traceLevel = usr_level;
            localStackElement.traceStackElement_tracePoint = trace_point;
            __stck(&(localStackElement.traceStackElement_createTime));
            int createTcb = *((int*) 0x21c);
            localStackElement.traceStackElement_createTcb = createTcb;
            bbgz_psw psw;
            extractPSW(&psw);
            int createState = psw.pbm_state ? 1 : 0;
            localStackElement.traceStackElement_createState = createState;
            int createKey = psw.key;
            localStackElement.traceStackElement_createKey = createKey;

            memcpy_dk(taskStackElementPtr, &localStackElement, sizeof(*taskStackElementPtr), 8);

            /* put trace element on stack */
            push_on_key8_stack(&(bbgztrgoo_p->bbgztrgoo_trace_stack), (concurrent_stack_element*)taskStackElementPtr);

            // Tell the trace processing thread that a trace request is available.
            releaseTraceThread(bbgztrgoo_p);

            // If the server is dying hard, just return.  Don't pause, there is likely no thread to process the
            // trace element we just pushed.
            if (spd_p && spd_p->serializedFlags.serverHardFailureDetected) {
                return;
            }

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

    //If running in a unittest environment there is no java based trace infrastructure
    //available. If unittest trace is specified then write the trace records using native facilities only.

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

/**
 * Disable tracing on from this thread.
 * @return 0.
 */
int disableTraceLevelInServerTaskData() {
    server_task_data* std_p = getServerTaskData();
    std_p->trclvl_p = &(std_p->null_byte_for_trace);
    return 0;
}

/**
 * Cleanup routine driven from resmgr for a Task termination of a specially
 * marked thread.  This thread was started from Java during server startup to
 * cover a hard failure of the server (ex. a "kill -9") which bypassed normal
 * server shutdown.
 *
 * This routine will check for and release the any PAUSED thread that
 * is currently in an MVS PAUSED state waiting to have its trace request handled.
 * These PAUSED thread will prevent LE from terminating the server (hung server).
 *
 */
void trace_hardFailureRegisteredCleanupRtn(void) {

    // Release any waiters found on the trace stack.
    releaseAllWaiters();

    return;
}

void releaseAllWaiters(void) {

    bbgztrgoo * bbgztrgoo_p = getTRGOO();
    if (bbgztrgoo_p != NULL) {
        int rc;
        iea_release_code release_code;
        iea_release_code release_release_code;
        iea_auth_type auth = IEA_UNAUTHORIZED;

        traceStackElement * currentStackElementPtr;
        traceStackElement * nextStackElementPtr = NULL;

        iea_PEToken nullPET[16] = {{0}};

        currentStackElementPtr = (traceStackElement *) bbgztrgoo_p->bbgztrgoo_trace_stack.concurrent_stack_stack_element_p;
        do {
            if (currentStackElementPtr) {
                // Get next
                nextStackElementPtr = (traceStackElement *) currentStackElementPtr->traceStackElement_header.stack_element_next_p;

                // Release the current waiter
                memset(release_release_code, 0, sizeof(release_release_code));
                iea4rls(&rc, auth, currentStackElementPtr->traceStackElement_pet, release_release_code);

            }

            currentStackElementPtr = nextStackElementPtr;
        } while (currentStackElementPtr != NULL);

        // --------------------------------------------------------------------
        // Release authorized threads related to trace requests that the trace
        // thread was/is currently processing.
        // --------------------------------------------------------------------
        TraceThreadProcessingData* traceThreadProcessingData_p = (TraceThreadProcessingData*) bbgztrgoo_p->bbgztrgoo_trace_thread_data_p;
        concurrent_stack * threadStackPtr = &(traceThreadProcessingData_p->traceThreadProcessingData_traceStack);

        currentStackElementPtr = (traceStackElement *) threadStackPtr->concurrent_stack_stack_element_p;
        do {
            if (currentStackElementPtr) {
               // Get next
                nextStackElementPtr = (traceStackElement *) currentStackElementPtr->traceStackElement_header.stack_element_next_p;

                // Release the current waiter
                memset(release_release_code, 0, sizeof(release_release_code));
                iea4rls(&rc, auth, currentStackElementPtr->traceStackElement_pet, release_release_code);
            }

            currentStackElementPtr = nextStackElementPtr;
        } while (currentStackElementPtr != NULL);

        // If the Trace thread died while processing a request, then release waiting trace requestor
        if (memcmp(traceThreadProcessingData_p->traceThreadProcessingData_requestor_pet, nullPET, sizeof(traceThreadProcessingData_p->traceThreadProcessingData_requestor_pet)) != 0) {
            memset(release_release_code, 0, sizeof(release_release_code));
            iea4rls(&rc, auth, traceThreadProcessingData_p->traceThreadProcessingData_requestor_pet, release_release_code);
        }
    }

    return;
} /* end releaseAllWaiters */
