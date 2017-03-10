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
#ifndef _BBOZ_MVS_RESMGR_H
#define _BBOZ_MVS_RESMGR_H

#include "gen/iharmpl.h"
#include "gen/ikjtcb.h"

#define BBOZRMGR_TYPE_AS   1
#define BBOZRMGR_TYPE_TASK 2
#define BBOZRMGR_TYPE_ALLTASKS 3

/**
 *  Function type that the resmgr implementation should use
 */
typedef void rmgr_entry_t(struct rmpl*, long long*);

/**
 * Add a resource manager using the RESMGR macro
 *
 * @param token_p A returned token that is used to represent the resource
 *                manager.
 * @param param_p A pointer to an 8 byte area that is provided to the RESMGR
 *                when it is driven by z/OS.
 * @param type The type of resource manager to register, BBOZRMGR_TYPE_AS for
 *             an address-space level RESMGR (driven when the address space
 *             terminates), BBOZRMGR_TYPE_TASK which is driven when this task
 *             terminates, or BBOZRMGR_TYPE_ALLTASKS which is driven whenever
 *             any task terminates.
 * @param rmgr_entry A pointer to the function representing the resource
 *                   manager routine.  This is your resource manager
 *                   implementation.
 *
 * @return The return code from the RESMGR macro.
 */
int addResourceManager(int* token_p, void* param_p, int type, rmgr_entry_t* rmgr_entry);

/**
 * Add a resource manager for another task.
 *
 * @param token_p A returned token that is used to represent the resource
 *                manager.
 * @param param_p A pointer to an 8 byte area that is provided to the RESMGR
 *                when it is driven by z/OS.
 * @param tcb_p A pointer to the TCB structure for which the RESMGR should be
 *              created.
 * @param rmgr_entry A pointer to the function representing the resource
 *                   manager routine.  This is your resource manager
 *                   implementation.
 *
 * @return The return code from the RESMGR macro.
 */
int addResourceManagerForAnotherTask(int* token_p, void* param_p, tcb* tcb_p, rmgr_entry_t* rmgr_entry);

/**
 * Delete a resource manager
 *
 * @param token_p A pointer to the token provided when the resource manager
 *                was added.
 * @param type The type of resource manager, BBOZRMGR_TYPE_AS for
 *             an address-space level RESMGR (driven when the address space
 *             terminates), BBOZRMGR_TYPE_TASK which is driven when this task
 *             terminates, or BBOZRMGR_TYPE_ALLTASKS which is driven when any
 *             task terminates.
 *
 * @return The return code from the RESMGR macro.
 */
int deleteResourceManager(int* token_p, int type);

/**
 * Delete a resource manager for another task.  This is used to delete a resource manager
 * created with addResourceManagerForAntoherTask.
 *
 * @param token_p The RESMGR token provided in addResourceManagerForAnotherTask.
 * @param tcb_p A pointer to the TCB structure for where the RESMGR is set.
 *
 * @return The return code from the RESMGR macro.
 */
int deleteResourceManagerForAnotherTask(int* token_p, tcb* tcb_p);

#endif
