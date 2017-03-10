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
#ifndef _BBOZ_SERVER_TASK_DATA_H
#define _BBOZ_SERVER_TASK_DATA_H

// Only include if we're compiling a server part
#ifndef ANGEL_COMPILE

#include "bbgzsgoo.h"
#include "common_task_data_anchor.h"
#include "heap_management.h"
#include "server_logging_jni.h"

#include "gen/ihapsa.h"
#include "gen/ikjtcb.h"
#include "gen/ihastcb.h"

/** Number of below-the-bar heap cell chains to allocate. */
#define SERVER_NUM_BELOW_THE_BAR_HEAP_CHAINS 3

/** Number of above-the-bar heap cell chains to allocate. */
#define SERVER_NUM_ABOVE_THE_BAR_HEAP_CHAINS 3


/**@file
 * Dynamic Area Prefix for Liberty server.  AKA TGOO
 */

#pragma pack(1)

// -------------------------------------------------------------
// Dynamic area prefix for metal C parts
//
// Note that items marked with DEP have a dependency in
// another part (probably entry/exit linkage) and cannot be
// moved or deleted without investigation.  A list of known
// dependencies follows:
//
//   eyecatcher - All entry linkage
//   version    - All entry linkage
//   length     - All entry linkage
//   null_byte_for_trace - All entry linkage
//   trclvl_p   - All entry linkage
//   dynamic_area_in_use - All entry linkage
// -------------------------------------------------------------
#define SERVER_TASK_DATA_EYE "BBGZSTD_"
#define SERVER_TASK_DATA_VER0 0

/**
 * Task level structure for a Liberty server task.  This structure is the prefix
 * to the C stack used by this task when invoking authorized metal C code.
 */
typedef struct server_task_data
{
    /**
     * Eye catcher for this control block.
     */
    unsigned char            eyecatcher[8];          /* DEP 0x000*/

    /**
     * Version of this control block.
     */
    int                      version;                /* DEP 0x008*/

    /**
     * Length of storage allocated for this control block.
     */
    int                      length;                 /* DEP 0x00C*/

    /**
     * Pointer to the parameters used to allocate the unauthorized metal C
     * environment stored in unauth_cenv_p, used by unauthorized services.
     */
    struct __csysenv_s*      unauth_sysenv_p;        /*     0x010*/

    /**
     * Pointer to the metal C environment used to run unauthorized services.
     */
    void*                    unauth_cenv_p;          /*     0x018*/

    /**
     * A null byte, pointed to by trclvl_p when the trace level byte cannot be
     * located in the entry linkage.  This effectively disables tracing while
     * trclvl_p points here.
     */
    unsigned char            null_byte_for_trace;    /* DEP 0x020*/

    /**
     * This field is set by the entry linkage if this request could not obtain
     * dynamic area from the cell pool used for dynamic area, because the cell
     * pool did not contain any free cells.
     */
    unsigned char            expandDynAreaCellPool;  /* DEP 0x021*/

    /**
     * This field is set after we've checked for an angel anchor on this task.
     * If this field is set and the angel anchor pointer is null, then we're
     * using the default angel, not a named angel.
     */
    unsigned char            checkedAngelAnchor;     /*     0x022*/

    /**
     * Flags used in task management and cleanup
     */
    unsigned char            taskFlags;              /*     0x023*/

    /**
     * This word is set to 0 when the storage after this control block is
     * available for use as dynamic area by this task.  Any task using this
     * dynamic area should set this word to a non-zero value to prevent
     * an IRB or a recursive PC call from stepping on the dynamic area.  Updates
     * to this word are made via compare and swap cs().
     */
    int                      dynamic_area_in_use;    /* DEP 0x024*/

    /**
     * A pointer to the byte representing the current trace level for this
     * server.
     */
    unsigned char*           trclvl_p;               /* DEP 0x028*/

    /**
     * A pointer to the server process data control block.
     */
    struct server_process_data* spd_p;               /*     0x030*/

    /**
     * A pointer to a traceStackElement which can be used to process trace
     * records for this task.
     */
    traceStackElement*       trc_element_p;          /*     0x038*/

    /**
     * A trace element which can be used to build a trace record in the key
     * of the caller.  This can be used when the caller is running in key 8
     * because the trace writer thread also runs in key 8.  When running in
     * another key, an external trace element must be used.
     */
    traceStackElement        trc_element;            /*     0x040*/

    /**
     * Thread level heap pool cell chains below the bar for authorized callers.
     */
    ThreadLevelHeapCache_t   freeHeapCellsBelow[SERVER_NUM_BELOW_THE_BAR_HEAP_CHAINS]; /* 0x0C0*/

    /**
     * Thread level heap pool cell chains above the bar for authorized callers.
     */
    ThreadLevelHeapCache_t   freeHeapCellsAbove[SERVER_NUM_ABOVE_THE_BAR_HEAP_CHAINS]; /* 0x0F0*/

    /**
     * In a client process, a pointer to the process level data used by the
     * server common function module.
     */
    struct serverCommonFunctionModuleProcessData* scfmProcessData_p; /* 0x120*/

    /**
     * Pointer to the angel anchor for this server.  This contains the angel name and
     * PC number.  This field is only valid when checkedAngelAnchor is nonzero.
     */
    AngelAnchor_t* angelAnchor_p;                                    /* 0x128*/

    /**
     * End of used portion of used portion of prefix.  Macro BBGZSTDI has hard-coded
     * offset to only clear up to here.  If additional fields are added then the
     * macro will need to be updated
     */
                                                                     /* DEP 0x136*/

} server_task_data;                                  /*   0x10000*/

#pragma pack(reset)

/**
 * Mask to use when anding off stack pointer to server task data prefix
 */
#define SERVER_TASK_DATA_PREFIX_MASK 0xFFFFFFFFFFF00000L

/**
 * server_task_data.taskFlags:
 *
 */
// If on for terminating task, then drive server cleanup
// related to a hard failure
#define taskFlags_cleanupForHardFailure 0x01

/**
 * Gets the server task data control block.
 *
 * @return A pointer to the server_task_data control block for this task.
 */
#pragma inline(getServerTaskData)
static server_task_data* getServerTaskData(void) {
    void* dynarea_p;
    __asm(" STG 13,%0" : "=m"(dynarea_p));

    server_task_data* prefix_p = (server_task_data*)
      (((long long)dynarea_p) & SERVER_TASK_DATA_PREFIX_MASK);

    return prefix_p;
}

/**
 * Sets the server task data control block for this task into the
 * common_task_data_anchor control block, hung off the STCBBCBA.
 */
void setServerTaskDataIntoTrustedChain(void);

/**
 * Tells the caller if there is server task data in the common_task_data_anchor,
 * hung off the STCBBCBA.  The task data is stored in the anchor after it has
 * been initialized.  The entry linkage for some of the metal C parts looks for
 * the task data using the STCBBCBA pointer.
 *
 * @return TRUE if there is task data hung off the common_task_data_anchor.
 */
#pragma inline(isServerTaskDataSetInTrustedChain)
static unsigned char isServerTaskDataSetInTrustedChain(void) {
    psa* psa_p = NULL;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    stcb* stcb_p = tcb_p->tcbstcb;
    common_task_data_anchor* ctda_p = (common_task_data_anchor*)stcb_p->stcbbcba;
    void* server_task_data_p = (ctda_p != NULL) ? ctda_p->server_task_data_p : NULL;
    return (server_task_data_p != NULL);
}

/**
 * Gets the server task data for another TCB.  The local lock should be held
 * when this method is called.
 *
 * @param tcb_p A pointer to the TCB representing the task for which the server
 *              task data should be obtained.
 *
 * @return A pointer to the server task data for the requested task, or NULL if
 *         no server task data exists for that task.
 */
server_task_data* getServerTaskDataFromAlternateTCB(void* tcb_p);

/**
 * Gets the trace level byte from the server_task_data for this task.  Since the
 * tracing byte is allocated in key 8 storage, and since this code can run in
 * key 2 or key 8, a MVCSK is used to copy the trace byte into local storage
 * before it is read.  This prevents an unauthorized caller from modifying the
 * pointer to the trace byte, perhaps to some fetch protected key 2 storage,
 * and getting a clue as to what might be stored there by seeing how the
 * tracing code behaves.
 *
 * @return A byte representing the current trace level.
 */
#pragma inline(getTraceLevelFromServerTaskData)
static unsigned char getTraceLevelFromServerTaskData(void) {
    server_task_data* std_p = getServerTaskData();
    unsigned char trace_byte;
    __asm(" LGHI 0,0    Copy one byte\n"
          " LGHI 1,128  Copy from key 8\n"
          " MVCSK %0,%1 Do the copy" : /* No write variables */ :
          "m"(trace_byte),"m"(*(std_p->trclvl_p)) :
          "r0","r1");
    return trace_byte;
}

#endif // ANGEL_COMPILE

#endif
