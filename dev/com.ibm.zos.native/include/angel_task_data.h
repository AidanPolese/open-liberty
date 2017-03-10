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
#ifndef _BBGZ_ANGEL_TASK_DATA_H
#define _BBGZ_ANGEL_TASK_DATA_H

// Only include if we're compiling an angel part
#ifdef ANGEL_COMPILE

#include "common_task_data_anchor.h"
#include "heap_management.h"

#include "gen/ihapsa.h"
#include "gen/ikjtcb.h"
#include "gen/ihastcb.h"

/** Number of below-the-bar heap cell chains to allocate. */
#define ANGEL_NUM_BELOW_THE_BAR_HEAP_CHAINS 3

/** Number of above-the-bar heap cell chains to allocate. */
#define ANGEL_NUM_ABOVE_THE_BAR_HEAP_CHAINS 3


/** Struct representing compare-and-swap bits used when invoking server code. */
#pragma pack(1)
typedef struct angelCSFlagArea {
    unsigned int invoking : 1, //!< This task is about to call, or is inside, a server method.
                 noMoreInvoke : 1, //!< Set by deregister, this task is no longer allowed to invoke.
                 droveServerCleanup : 1, //!< This task has called the server cleanup routine in BBGZSAFM.
                 _available : 29; //!< Available for use.
} AngelCSFlagArea_t;
#pragma pack(reset)

/**
 * @file
 * The angel task data control block and associated functions.  This
 * control block is hung off the common_task_data_anchor control block and
 * stores the task level storage and metal C stack for the angel portion of the
 * authorized function call path (invoke through the fixed shim and dynamic
 * replaceable modules).
 */

/**
 * The mask to use when 'anding' off the stack pointer to the stack prefix.
 */
#define ANGEL_TASK_DATA_PREFIX_MASK 0xFFFFFFFFFFF00000L

// -------------------------------------------------------------
// Dynamic area prefix for metal C parts
//
// Note that items marked with DEP have a dependency in
// another part (probably entry/exit linkage) and cannot be
// moved or deleted without investigation.  A list of known
// dependencies follows:
//
//   eyecatcher - APCPROL/APCEPIL linkage
//   version    - APCPROL/APCEPIL linkage
//   length     - APCPROL/APCEPIL linkage
//   dynamic_area_in_use - APCPROL/APCEPIL linkage
// -------------------------------------------------------------
#pragma pack(1)
/**
 * The angel task data control block.  The control block is allocated at the
 * begining of the metal C authorized stack for this task.  This struct can
 * grow up to the size of the stack prefix area (currently 64K).
 */
typedef struct angel_task_data {
    /**
     * Eyecatcher for this control block.
     */
    unsigned char            eyecatcher[8];            /* DEP 0x000*/

    /**
     * Version number for this control block.
     */
    unsigned int             version;                  /* DEP 0x008*/

    /**
     * Length of storage allocated for this control block.
     */
    unsigned int             length;                   /* DEP 0x00C*/

    /**
     * A pointer to a byte representing the trace level used by the server.
     */
    unsigned char*           trc_level_p;              /*     0x010*/

    /**
     * A null byte which is used when the byte representing the trace level
     * used by the server cannot be found.  In this case, trc_level_p will
     * point here, and tracing is disabled.
     */
    unsigned char            no_trace;                 /*     0x018*/

    /**
     * A flag which disables the task level heap cell cache for this task.
     * This bit is usually set when a task might have a different task data
     * hung off of R13, such as during task level RESMGR processing, to prevent
     * the second task data from creating a task level cache.
     */
    unsigned char            noTaskLevelHeapCache;     /*     0x019*/

    /**
     * A flag which tells us that we verified that the IPT is in our parent
     * TCB chain.
     */
    unsigned char            iptIsInParentTCBChain;    /*     0x01A*/

    /**
     * A flag which tells us that the caller tried to get a cell from the
     * temporary dynamic area cell pool in the SGOO, but failed.
     */
    unsigned char            clientTempDynAreaGetFailed; /* DEP 0x01B*/

    /**
     * This word is set to 0 when the storage after this control block is
     * available for use as dynamic area by this task.  Any task using this
     * dynamic area should set this word to a non-zero value to prevent
     * an IRB or a recursive PC call from stepping on the dynamic area.  Updates
     * to this word are made via compare and swap cs().
     */
    int                      dynamic_area_in_use;      /* DEP 0x01C*/

    /**
     * Thread level heap pool cell chains below the bar.
     */
    ThreadLevelHeapCache_t freeHeapCellsBelow[ANGEL_NUM_BELOW_THE_BAR_HEAP_CHAINS]; /* 0x020*/

    /**
     * Thread level heap pool cell chains above the bar.
     */
    ThreadLevelHeapCache_t freeHeapCellsAbove[ANGEL_NUM_ABOVE_THE_BAR_HEAP_CHAINS]; /* 0x050*/

    /**
     * Compare-and-swap area used to manage calls into the server-owned code.
     */
    AngelCSFlagArea_t        invokeFlags;                  /* 0x080*/

    /**
     * Flag that is set if the caller tried to get a cell from the dynamic
     * area cell pool hung off of the bind token which was passed on the
     * call, but the cell pool was out of cells.
     */
    unsigned char            bindTokDynAreaPoolEmpty;  /* DEP 0x084*/

    /**
     * Available for use.
     */
    unsigned char            _available1[3];               /* 0x085*/

    /**
     * A pointer to the angel process data.
     */
    struct angel_process_data* apd_p;                      /* 0x088*/

    /**
     * If the entry linkage validated an angel client bind token, this field
     * will point to the angel client bind data that the token represents.
     * This field is only used by the angel client invoke and client unbind PCs.
     */
    struct angelClientBindData* validatedClientBindData_p; /* 0x090*/
} angel_task_data;
#pragma pack(reset)

/**
 * Gets a pointer to the angel_task_data for this task.
 *
 * @return A pointer to the angel_task_data for this task.  If no task data
 *         exists for this task, returns NULL.
 */
#pragma inline(getAngelTaskData)
static angel_task_data* getAngelTaskData(void) {
    angel_task_data* atd_p;
    __asm(" STG 13,%0" : "=m"(atd_p));
    atd_p = (void*)(((long long)atd_p) & ANGEL_TASK_DATA_PREFIX_MASK);
    return atd_p;
}

/**
 * Gets a pointer to the angel_task_data using the trusted control block
 * chain (PSA->TCB->STCB->BCBA).
 *
 * @return A pointer to the angel_task_data for this task.  If no task data
 *         is stored off the STCB, returns NULL.
 */
#pragma inline(getAngelTaskDataFromSTCB)
static angel_task_data* getAngelTaskDataFromSTCB(void) {
    psa* psa_p = NULL;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    stcb* stcb_p = tcb_p->tcbstcb;
    common_task_data_anchor* ctda_p = (common_task_data_anchor*)stcb_p->stcbbcba;
    return ((ctda_p != NULL) ? ((angel_task_data*)ctda_p->angel_task_data_p) : NULL);
}

/**
 * Gets a pointer to the angel_task_data using the control block
 * chain (TCB->STCB->BCBA) for an arbitrary TCB.  The local lock should
 * be held when calling this method.
 *
 * @return A pointer to the angel_task_data for the specified task.  If no task data
 *         is stored off the STCB, returns NULL.
 */
angel_task_data* getAngelTaskDataFromAlternateTCB(void* tcb_p);

/**
 * Initializes the angel_task_data for this task.  The control block itself
 * already exists at the begining of the dynamic area, and its eye catcher and
 * length/version are filled in.  The remainder of required setup is performed
 * here.  The common_task_data_anchor control block is allocated, set into the
 * STCBBCBA, and the angel_task_data is attached to it.
 *
 * @return A pointer to the angel_task_data for this task.  If the task data
 *         could not be anchored to the STCBBCBA, NULL is returned.
 */
angel_task_data* initializeAngelTaskData(void);

/**
 * Clears the STCBBCBA field, and frees the common_task_data_anchor control
 * block.  The server's task data is not freed.  The caller should hold the
 * local lock when calling this routine, unless the process is initializing.
 */
void destroyAngelTaskData(void);

/**
 * Gets the trace level byte from the angel_task_data for this task.  Since the
 * tracing byte is allocated in either key 8 storage (server process) or in
 * non-fetch-protected key 2 storage (angel), and since this code runs in key 2,
 * a MVCSK is used to copy the trace byte into local storage before it is read.
 * This prevents an unauthorized caller from modifying the pointer to the trace
 * byte, perhaps to some fetch protected key 2 storage, and getting a clue as to
 * what might be stored there by seeing how the tracing code behaves.
 *
 * @return A byte representing the current trace level.
 */
#pragma inline(getTraceLevelFromAngelTaskData)
static unsigned char getTraceLevelFromAngelTaskData(void) {
    angel_task_data* atd_p = getAngelTaskData();
    unsigned char trace_byte;
    __asm(" LGHI 0,0    Copy one byte\n"
          " LGHI 1,128  Copy from key 8\n"
          " MVCSK %0,%1 Do the copy" : /* No write variables */ :
          "m"(trace_byte),"m"(*(atd_p->trc_level_p)) :
          "r0","r1");
    return trace_byte;
}

#endif // ANGEL_COMPILE

#endif
