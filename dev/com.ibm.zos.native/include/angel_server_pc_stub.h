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
#ifndef _BBOZ_ANGEL_SERVER_PC_STUB_H
#define _BBOZ_ANGEL_SERVER_PC_STUB_H

#include "bbgzsgoo.h"

#ifdef __IBM_METAL__
/**
 * @file
 * The server side stub to the register PC routine.  This routine should not
 * be called by client code.  Instead the register routine should be called
 * out of the unauthorized function module table.
 *
 * Note that these return codes are also defined in NativeReturnCodes.java in
 * the com.ibm.ws.zos.core project.
 *
 * @param server_authorized_function_module_name The name of the BBGZSAFM in
 *                                               the file system.
 * @param angelAnchor_p A pointer to the angel anchor, if we are connecting to
 *                      a named angel.  NULL if we are connecting to the
 *                      default angel.
 *
 * @return 0 on success.
 */
int angel_register_pc_client_stub(char* server_authorized_function_module_name, AngelAnchor_t* angelAnchor_p);
#endif

#define ANGEL_REGISTER_OK                               0
#define ANGEL_REGISTER_NO_BGVT                          1
#define ANGEL_REGISTER_NO_CGOO                          2
#define ANGEL_REGISTER_NO_LX                            3
#define ANGEL_REGISTER_ANGEL_INACTIVE                   4
#define ANGEL_REGISTER_ANGEL_NAME_NOT_EXIST             5

#define ANGEL_REGISTER_FSM_INACT_ARMV                 256
#define ANGEL_REGISTER_FSM_ERROR                      257
#define ANGEL_REGISTER_FSM_NO_SGOO                    258
#define ANGEL_REGISTER_FSM_NO_ARR_STORAGE             259
#define ANGEL_REGISTER_FSM_ALREADY_REG                260
#define ANGEL_REGISTER_FSM_ACTIVE_TGOO                261

#define ANGEL_REGISTER_DRM_FAIL                      4096
#define ANGEL_REGISTER_DRM_MARK_PGOO                 4097
#define ANGEL_REGISTER_DRM_ALLOCATE_ASVT             4098
#define ANGEL_REGISTER_DRM_ALLOCATE_TGOO             4099
#define ANGEL_REGISTER_DRM_ALLOCATE_PGOO             4100
#define ANGEL_REGISTER_DRM_ALLOC31_FAIL              4101
#define ANGEL_REGISTER_DRM_RESMGR_FAIL               4102
#define ANGEL_REGISTER_DRM_NOT_AUTHORIZED            4103
#define ANGEL_REGISTER_DRM_NOT_AUTHORIZED_BBGZSAFM   4104
#define ANGEL_REGISTER_DRM_NOT_IN_IPT_CHAIN          4105
#define ANGEL_REGISTER_DRM_IPT_MOVED                 4106
#define ANGEL_REGISTER_DRM_SAFM_NOT_APF_AUTHORIZED   4107

#define ANGEL_REGISTER_UNDEFINED                    65535

#ifdef __IBM_METAL__
/**
 * The server side stub to the Invoke PC routine.  This routine should not
 * be called by client code.  Instead, the authorized service in the
 * authorized service module table should be driven to call this routine.
 *
 * Note that these return codes are also defined in NativeReturnCodes.java in
 * the com.ibm.ws.zos.core project.
 *
 * @param function_index The index of the requested service in the authorized
 *                       function table.
 * @param arg_struct_size The size of the single struct argument which will be
 *                        passed to the invoke target, as defined in AUTH_DEF.
 * @param arg_struct_p A pointer to the single struct argument in the caller's key.
 * @param angelAnchor_p A pointer to the angel anchor if we registered with a
 *                      named angel, NULL if using the default angel.
 *
 * @return 0 if the dispatch was successful, nonzero if error.  Note that a 0
 *         return code does not mean the target service completed successfully,
 *         only that it was dispatched.
 */
int angel_invoke_pc_client_stub(int function_index, int arg_struct_size, void* arg_struct_p, AngelAnchor_t* angelAnchor_p);
#endif
#define ANGEL_INVOKE_OK                         0
#define ANGEL_INVOKE_NO_BGVT                    1
#define ANGEL_INVOKE_NO_CGOO                    2
#define ANGEL_INVOKE_NO_LX                      3
#define ANGEL_INVOKE_NO_PGOO                    4

#define ANGEL_INVOKE_FSM_NO_SGOO              256
#define ANGEL_INVOKE_FSM_NO_PGOO              257

#define ANGEL_INVOKE_DRM_FAIL                4096
#define ANGEL_INVOKE_DRM_UNREGISTERED        4097
#define ANGEL_INVOKE_DRM_NO_TGOO             4098
#define ANGEL_INVOKE_DRM_UNREG_FUNC          4099
#define ANGEL_INVOKE_DRM_UNAUTH_FUNC         4100
#define ANGEL_INVOKE_DRM_BAD_PARM_SIZE       4101
#define ANGEL_INVOKE_DRM_NO_STORAGE_PARMS    4102
#define ANGEL_INVOKE_DRM_NOT_IN_IPT_CHAIN    4103
#define ANGEL_INVOKE_UNDEFINED              65535

#ifdef __IBM_METAL__
/**
 * The server side stub to the Deregister PC routine.  This routine should
 * not be called by client code.  Instead, the deregister service should be
 * driven in the unauthorized function module table.
 *
 * Note that these return codes are also defined in NativeReturnCodes.java in
 * the com.ibm.ws.zos.core project.
 *
 * @param angelAnchor_p A pointer to the angel anchor if we registered with a
 *                      named angel, NULL if using the default angel.
 *
 * @return 0 on success.
 */
int angel_deregister_pc_client_stub(AngelAnchor_t* angelAnchor_p);
#endif

#define ANGEL_DEREGISTER_OK                0
#define ANGEL_DEREGISTER_OK_PENDING        4
#define ANGEL_DEREGISTER_NO_BGVT           8
#define ANGEL_DEREGISTER_NO_CGOO           9
#define ANGEL_DEREGISTER_NO_LX            10

#define ANGEL_DEREGISTER_FSM_NO_SGOO     256
#define ANGEL_DEREGISTER_FSM_NO_PGOO     257

#define ANGEL_DEREGISTER_DRM_FAIL       4096
#define ANGEL_DEREGISTER_DRM_ALR_DEREG  4097
#define ANGEL_DEREGISTER_UNDEFINED     65535

#ifdef __IBM_METAL__
/**
 * Get the version of the Angel DRM that is currently loaded by the
 * angel.  Note that this function is called by the server, and does
 * traverse some angel control block structures.
 */
int getAngelVersion(AngelAnchor_t* angelAnchor_p);

/**
 * Finds the angel anchor for a named angel.
 */
AngelAnchor_t* findAngelAnchor(char* angelName);
#endif

#endif
