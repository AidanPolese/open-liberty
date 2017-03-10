/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBGZ_ANGEL_CLIENT_PC_H
#define _BBGZ_ANGEL_CLIENT_PC_H
/**@file
 * Defines Angel functions usable by the client.
 */

#include "angel_client_pc_recovery.h"
#include "angel_client_process_data.h"
#include "bbgzarmv.h"
#include "bbgzasvt.h"
#include "bbgzsgoo.h"
#include "mvs_utils.h"

/**
 * Binds a client to a Liberty server.  After successful return from this
 * function, the client can use the clientInvoke service.
 *
 * @param targetServerStoken The stoken of the server to bind to.
 * @param clientFunctionTablePtr_p A pointer to a double word where the pointer
 *                                 to the client function module is copied.
 * @param bindToken_p A pointer to a double word where the bind token is copied.
 *                    The bind token must be supplied on all clientInvoke and
 *                    unbind calls.
 * @param sgoo_p A pointer to the BBGZSGOO control block.
 * @param armv_p A pointer to the current ARMV.
 * @param existingAcpd_p A pointer to the angel client process data, or NULL if no
 *               angel client process data exists yet (a successful bind will
 *               create one).
 * @param recovery_p A pointer to the ARR recovery data for the client.
 *
 * @return 0 on success.
 */
int dynamicReplaceablePC_ClientBind(SToken* targetServerStoken, bbgzasvt_header** clientFunctionTablePtr_p, void** bindToken_p, bbgzsgoo* sgoo_p, bbgzarmv* armv_p, AngelClientProcessData_t* existingAcpd_p, angel_client_pc_recovery* recovery_p);

/**
 * Invoke a service in the common function module (BBGZSCFM) from the client.
 * The client must have bound to the server using the client bind service.
 *
 * @param bindToken_p A pointer to the bind token returned on client bind.
 * @param serviceIndex The index off the SCFM of the service to invoke.
 * @param parm_len The length of the data pointed to by parm_p.
 * @param parm_p A pointer to the parameter struct required by the called
 *               service.
 * @param recovery_p A pointer to the ARR recovery data for the client.
 *
 * @return 0 if the client service was invoked.  The return code from the client
 *         service will be inside parm_p.
 */
int dynamicReplaceablePC_ClientInvoke(void* bindToken_p,
                                      unsigned int serviceIndex,
                                      unsigned int parm_len,
                                      void* parm_p,
                                      angel_client_pc_recovery* recovery_p);

/**
 * Removes the bind between a client and a server.
 *
 * @param bindToken_p A pointer to the bind token returned on client bind.
 * @param recovery_p A pointer to the ARR recovery data for the client.
 * @param cleanup_p A pointer to a byte which is set to a non-zero value if the
 *                  fixed shim module should completely clean up the client
 *                  environment on the way out (free the metal C environment,
 *                  etc).
 *
 * @return 0 on success.
 */
int dynamicReplaceablePC_ClientUnbind(void* bindToken_p, angel_client_pc_recovery* recovery_p, unsigned char* cleanup_p);

/**
 * The ARR which performs cleanup for the client PC routines in the dynamic
 * replaceable module.
 *
 * @param sdwa_p A pointer to the SDWA provided by z/OS for this recovery.
 * @param recovery_p A pointer to a control block that is used by the PC
 *                   routines to keep track of what recovery needs to be
 *                   performed in the ARR.
 */
void dynamicReplaceableClientARR(sdwa* sdwa_p, angel_client_pc_recovery* recovery_p);

/**
 * RESMGR for clients in the dynamic replaceable module.  This is branched to from the
 * fixed shim module during task and address space level recovery.
 *
 * @param rmpl_p The RMPL pointer provided by MVS.
 * @param acpd_p A pointer to the angel client process data control block.
 */
void dynamicReplaceableClientRESMGR(rmpl* rmpl_p, AngelClientProcessData_t* acpd_p);

#endif
