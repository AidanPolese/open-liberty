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
#ifndef _BBGZ_COMMON_TASK_DATA_ANCHOR_H
#define _BBGZ_COMMON_TASK_DATA_ANCHOR_H

/**
 * @file
 * A mapping of the structure hung off the STCBBCBA on a Liberty task.
 * The structure holds pointers to the Angel and Server task level control
 * block, and authorized metal C stack.
 */

#define COMMON_TASK_DATA_ANCHOR_SUBPOOL 230 /* High private, TCB, Not FP */
#define COMMON_TASK_DATA_ANCHOR_KEY 2
#define COMMON_TASK_DATA_ANCHOR_EYE "BBGZCTDA"

#pragma pack(1)
typedef struct common_task_data_anchor common_task_data_anchor;
/**
 * This control block is hung off the STCBBCBA on a Liberty task.  It contains
 * pointers to the Angel and Server task level data, including the authorized
 * metal C stacks.  The control block is allocated by the Angel the first time
 * a task invokes an authorized service.  The angel will fill in the header
 * information and the pointer to the angel task data, and set the control block
 * into the STCBBCBA.  Later, the server will fill in the pointer to the server
 * task data.
 *
 * This control block is shared between the angel and server processes, and
 * CANNOT change in a way that would make it incompatible with any angel and
 * server process combination.
 */
struct common_task_data_anchor {
    unsigned char eyecatcher[8]; /**< Eye catcher used for block validation */
    unsigned int version;     /**< Version number of control block */
    unsigned int length;      /**< Length of control block */
    void* angel_task_data_p;  /**< Pointer to angel task level data */
    void* server_task_data_p; /**< Pointer to server task level data */
    void* server_task_data_unauth_p; /**< Pointer to unauthorized server task level data */
    unsigned char available[216]; /**< Available for expansion */
};
#pragma pack(reset)

#endif
