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
#ifndef _BBOZ_ANGEL_SERVER_PC_H
#define _BBOZ_ANGEL_SERVER_PC_H
/**@file
 * Defines Angel functions usable by the server
 */

#include "bbgzarmv.h"
#include "bbgzsgoo.h"
#include "angel_process_data.h"

#include "gen/ihasdwa.h"

#include "angel_server_pc_recovery.h"

/**
 * Registers a server with the angel.  This is called from the fixed shim
 * module.
 *
 * @param usertoken A 8 byte user token created in the fixed shim module
 *                  which can be used as a parameter to z/OS services such
 *                  as IARV64 which require an 8 byte user token.
 * @param sgoo_p A pointer to the SGOO.
 * @param apd_p A pointer to the angel process data if this is a re-registration.
 * @param armv_p A pointer to the current ARMV.
 * @param server_authorized_function_module_name The name of the BBGZSAFM
 *                                               provided by the caller.
 * @param recovery_p A pointer to a recovery area that can be used to keep
 *                   track of cleanup actions to be performed by the ARR in
 *                   case of an ABEND.
 *
 * @return 0 if the register was successful.
 */
int dynamicReplaceablePC_Register(long long usertoken,
                                  bbgzsgoo* sgoo_p,
                                  angel_process_data* apd_p,
                                  bbgzarmv* armv_p,
                                  char* server_authorized_function_module_name,
                                  angel_server_pc_recovery* recovery_p);

/**
 * Invoke a service.  This is called by the fixed shim module to invoke an
 * authorized service on behalf of the client.
 *
 * @param function_index The index of the function/service in the authorized
 *                       functions table (BBGZSAFM).
 * @param arg_struct_size The size of the struct parameter to the service.  This
 *                       is copied from the caller's key into key 2 and passed
 *                       to the target service.
 * @param arg_struct_p A pointer to the struct parameter.
 * @param apd_p A pointer to the angel process data for this process.
 * @param recovery_p A pointer to a recovery area that can be used to keep
 *                   track of cleanup actions to be performed by the ARR in
 *                   case of an ABEND.
 *
 * @return 0 if the target service was dispatched, nonzero on failure.  Note
 *         that a return code 0 does not mean the target service completed
 *         successfully, only that it was dispatched.
 */
int dynamicReplaceablePC_Invoke(unsigned int function_index,
                                unsigned int arg_struct_size,
                                void* arg_struct_p,
                                angel_process_data* apd_p,
                                angel_server_pc_recovery* recovery_p);

/**
 * Deregister a server from the angel
 *
 * @param apd_p A pointer to the angel process data for this process.
 * @param recovery_p A pointer to a recovery area that can be used to keep
 *                   track of cleanup actions to be performed by the ARR in
 *                   case of an ABEND.
 *
 * @return 0 on success.
 */
int dynamicReplaceablePC_Deregister(angel_process_data* apd_p,
                                    angel_server_pc_recovery* recovery_p);

/**
 * The ARR which performs cleanup for the PC routines in the dynamic
 * replaceable module.
 *
 * @param sdwa_p A pointer to the SDWA provided by z/OS for this recovery.
 * @param recovery_p A pointer to a control block that is used by the PC
 *                   routines to keep track of what recovery needs to be
 *                   performed in the ARR.
 */
void dynamicReplaceableARR(sdwa* sdwa_p, angel_server_pc_recovery* recovery_p);

/**
 * RESMGR for the dynamic replaceable module.  This is branched to from the
 * fixed shim module during task and address space level recovery.
 *
 * @param rmpl_p The RMPL pointer provided by MVS.
 * @param apd_p A pointer to the angel process data control block.
 */
void dynamicReplaceableRESMGR(rmpl* rmpl_p, angel_process_data* apd_p);

#endif
