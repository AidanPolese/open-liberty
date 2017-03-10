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
#ifndef _BBOZ_ANGEL_CLIENT_PC_STUB_H
#define _BBOZ_ANGEL_CLIENT_PC_STUB_H

#include "bbgzasvt.h"
#include "bbgzsgoo.h"
#include "common_defines.h"

/**
 * @file
 * Contains the client side stubs to the PC routines.
 */

/** Bind token */
typedef struct {
    char _token[16];
} LibertyBindToken_t;

// ---------------------------------------------------------------------------
// IMPORTANT NOTE
// The return code range 256 - 511 is used in the PC entry linkage and should
// not be used here for metal C.
// ---------------------------------------------------------------------------

/**
 * The client side stub to the bind PC routine.
 *
 * @param targetServerStoken_p The stoken of the server to bind to.
 * @param clientFunctionTablePtr_p A pointer to a double word where the pointer
 *                                 to the client function module is copied.
 * @param bindToken_p A pointer to a double word where the bind token is copied.
 *                    The bind token must be supplied on all clientInvoke and
 *                    unbind calls.
 * @param angelAnchorPtr_p A pointer to a double word where the pointer to the
 *                         angel anchor is copied.  The angel anchor is supplied
 *                         to future invoke and unbind calls and contains the PC
 *                         number that the client will use.
 *
 * @return 0 on success.
 */
int angelClientBindStub(SToken* targetServerStoken_p, bbgzasvt_header** clientFunctionTablePtr_p, LibertyBindToken_t* bindToken_p, AngelAnchor_t** angelAnchorPtr_p);

#define ANGEL_CLIENT_BIND_OK                               0
#define ANGEL_CLIENT_BIND_NO_BGVT                          1
#define ANGEL_CLIENT_BIND_NO_CGOO                          2
#define ANGEL_CLIENT_BIND_NO_LX                            3
#define ANGEL_CLIENT_BIND_ANGEL_INACTIVE                   4

// Do not use 256 - 511

#define ANGEL_CLIENT_BIND_FSM_INACT_ARMV                 512
#define ANGEL_CLIENT_BIND_FSM_NO_SGOO                    513

#define ANGEL_CLIENT_BIND_DRM_NULL_STOKEN_ADDR          4096
#define ANGEL_CLIENT_BIND_DRM_INV_STOKEN_ADDR           4097
#define ANGEL_CLIENT_BIND_DRM_NULL_TABLE_ADDR           4098
#define ANGEL_CLIENT_BIND_DRM_INV_TABLE_ADDR            4099
#define ANGEL_CLIENT_BIND_DRM_NULL_BIND_TOKEN_ADDR      4100
#define ANGEL_CLIENT_BIND_DRM_INV_BIND_TOKEN_ADDR       4101
#define ANGEL_CLIENT_BIND_DRM_PGOO_NOT_FOUND            4102
#define ANGEL_CLIENT_BIND_DRM_PGOO_GOING_AWAY           4103
#define ANGEL_CLIENT_BIND_DRM_RESMGR_ERROR              4104
#define ANGEL_CLIENT_BIND_DRM_ADD_TO_APD_ERROR          4105
#define ANGEL_CLIENT_BIND_DRM_ADD_TO_ACPD_ERROR         4106
#define ANGEL_CLIENT_BIND_DRM_CREATE_ACPD_ERROR         4107
#define ANGEL_CLIENT_BIND_DRM_CREATE_BIND_DATA_ERROR    4108
#define ANGEL_CLIENT_BIND_DRM_COPY_SCFM_ERROR           4109
#define ANGEL_CLIENT_BIND_DRM_ALLOCATE_TOKEN_ERROR      4110
#define ANGEL_CLIENT_BIND_DRM_INCREMENT_BIND_COUNT_ERROR 4111
#define ANGEL_CLIENT_BIND_DRM_SCFM_INIT_ERROR           4112

#define ANGEL_CLIENT_BIND_UNDEFINED                    65535

/**
 * The client side stub to the invoke PC.
 *
 * @param bindToken_p A pointer to the bind token returned on clientBind.
 * @param serviceIndex The index off the SCFM of the service to invoke.
 * @param parm_len The length of the data pointed to by parm_p.
 * @param parm_p A pointer to the parameter struct required by the called
 *               service.
 * @param angelAnchor_p A pointer to the angel anchor which contains the
 *                      PC number. If null, we'll use the PC number for
 *                      the default angel.
 *
 * @return 0 if the client service was invoked.  The return code from the client
 *         service will be inside parm_p.
 */
int angelClientInvokeStub(LibertyBindToken_t* bindToken_p, unsigned int serviceIndex, int parm_len, void* parm_p, AngelAnchor_t* angelAnchor_p);
#define ANGEL_CLIENT_INVOKE_OK                         0
#define ANGEL_CLIENT_INVOKE_NO_BGVT                    1
#define ANGEL_CLIENT_INVOKE_NO_CGOO                    2
#define ANGEL_CLIENT_INVOKE_NO_LX                      3

// Do not use 256 - 511

#define ANGEL_CLIENT_INVOKE_FSM_NO_SGOO              512
#define ANGEL_CLIENT_INVOKE_FSM_NO_PGOO              513
#define ANGEL_CLIENT_INVOKE_FSM_VALIDATION_ERR       514

#define ANGEL_CLIENT_INVOKE_DRM_FAIL                4096
#define ANGEL_CLIENT_INVOKE_DRM_UNREGISTERED        4097
#define ANGEL_CLIENT_INVOKE_DRM_NO_BINDS            4098
#define ANGEL_CLIENT_INVOKE_DRM_NOMEM               4099
#define ANGEL_CLIENT_INVOKE_DRM_ARGSIZE_INV         4100
#define ANGEL_CLIENT_INVOKE_DRM_SERVICE_NOT_AUTH    4101
#define ANGEL_CLIENT_INVOKE_DRM_INV_SERVICE_INDEX   4102
#define ANGEL_CLIENT_INVOKE_UNDEFINED              65535

/**
 * The client side stub to the unbind PC routine.
 *
 * @param bindToken_p A pointer to the bind token returned on clientBind.
 * @param angelAnchor_p A pointer to the angel anchor which contains the
 *                      PC number. If null, we'll use the PC number for
 *                      the default angel.
 *
 * @return 0 on success.
 */
int angelClientUnbindStub(LibertyBindToken_t* bindToken_p, AngelAnchor_t* angelAnchor_p);
#define ANGEL_CLIENT_UNBIND_OK                0
#define ANGEL_CLIENT_UNBIND_OK_PENDING        4
#define ANGEL_CLIENT_UNBIND_NO_BGVT           8
#define ANGEL_CLIENT_UNBIND_NO_CGOO           9
#define ANGEL_CLIENT_UNBIND_NO_LX            10

// Do not use 256 - 511

#define ANGEL_CLIENT_UNBIND_DRM_TOKINV     4096

#define ANGEL_CLIENT_UNBIND_UNDEFINED     65535


#endif
