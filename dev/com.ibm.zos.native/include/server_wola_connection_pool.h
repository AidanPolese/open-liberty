/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#ifndef _BBOZ_SERVER_WOLA_CONNECTION_POOL_H
#define _BBOZ_SERVER_WOLA_CONNECTION_POOL_H

#include "gen/bboapc1p.h"
#include "server_wola_connection_handle.h"
#include "server_wola_registration.h"

int getPooledConnection(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK,
                        unsigned int waitTime, WolaClientConnectionHandle_t * clientHandle_p,
                        struct bboapc1p* cicsParms_p);
int freePooledConnection(WolaRegistration_t * clientRGE_p, WolaClientConnectionHandle_t * freeHandle_p);
int destroyPooledConnection(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK,
                            WolaConnectionHandle_t * destroyHandle_p);
int cleanupConnectionPool(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK);
int cleanupConnectionPoolForce(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK);


/**
 * Return codes
 */
#define GetConn_RC_PooledConnectionOK      0
#define GetConn_RC_ConnPoolNotReady        4
#define GetConn_RC_ExceededMaxConnections  8
#define GetConn_RC_LocalCommConnectError  12
#define GetConn_RC_CellPoolError          16
#define GetConn_RC_LocalCommSAFError      20
#define GetConn_RC_RgeStckChanged         24
#define GetConn_RC_PetVetFailure          28
#define GetConn_RC_WaiterPauseFailed      32
#define GetConn_RC_DestroyedRGE           36
#define GetConn_RC_UnexpectedError       256

#define FreeConn_RC_FreeConnectionOK       0
#define FreeConn_RC_ConnectionStateError   4
#define FreeConn_RC_InvalidClientHandle    8
#define FreeConn_RC_RgeStckChanged        12
#define FreeConn_RC_DestroyedRGE          16
#define FreeConn_RC_UnexpectedError      256

#define DestroyConn_RC_DestroyConnectionOK 0
#define DestroyConn_RC_ConnectionNotOnRGE  4
#define DestroyConn_RC_RgeStckChanged      8
#define DestroyConn_RC_DestroyedRGE       12
#define DestroyConn_RC_UnexpectedError   256

#define Cleanup_RC_CleanupOK               0
#define Cleanup_RC_ActiveConnsRemaining    4
#define Cleanup_RC_RgeStckChanged          8

#define CleanupForce_RC_CleanupOK          0
#define CleanupForce_RC_CleanupErr         4


#endif
