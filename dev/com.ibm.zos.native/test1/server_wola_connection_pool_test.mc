/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <stdlib.h>

#include "include/CuTest.h"

#include "../include/mvs_cell_pool_services.h"
#include "../include/petvet.h"
#include "../include/server_process_data.h"
#include "../include/server_wola_connection_pool.h"
#include "../include/server_wola_registration.h"
#include "../include/server_wola_shared_memory_anchor.h"


extern void println(char * message_p, ...);
extern void * getOuterCellPoolAddrAndSize( WolaSharedMemoryAnchor_t * bboashr_p, long long * outer_cp_size_p );
extern long long buildInnerCellPool( WolaSharedMemoryAnchor_t * wolaAnchor_p, long long cell_size, char * name );
extern long long buildCellPool(void* storage_p, long long storage_len, long long cell_size, char* name_p, buildCellPoolFlags flags);
WolaSharedMemoryAnchor_t* setupSharedMemoryAnchor(void);
struct wolaRegistration setupServerReg(void);
struct wolaRegistration setupReg(WolaRegistration_t * serverReg_p);
struct wolaConnectionHandle* setupHandle(WolaRegistration_t * reg_p);

/** Struct used to compute time difference in seconds (roughly). */
struct timeStruct {
    int seconds;
    int unused;
};

void test_get_free_destroy(CuTest * tc) {
    struct wolaRegistration serverReg = setupServerReg();
    struct wolaRegistration reg = setupReg(&serverReg);
    WolaConnectionHandle_t * handle_p = setupHandle(&reg);
    struct wolaClientConnectionHandle clientHandle;
    memset(&clientHandle, 0, sizeof(WolaClientConnectionHandle_t));

    unsigned long long oldPLO = reg.connPoolPLOCounter;
    unsigned long long oldInstance = handle_p->ploArea.instanceCount;

    // No connections on the free queue, should try to create a new one (and fail because local comm can't actually connect in this environment)
    int rc = getPooledConnection(&reg, reg.stckLastStateChange, 30, &clientHandle, NULL);
    CuAssertIntEquals_Msg(tc, "Expected LocalCommConnectError code", GetConn_RC_LocalCommConnectError, rc);

    // Fake a new connection and put it on the free chain
    reg.activeConnCount = 1;
    reg.allConnHandleListHead_p = handle_p;
    reg.freeConnHandlePoolHead_p = handle_p;

    // This call should see the connection on the free queue and pop it
    rc = getPooledConnection(&reg, reg.stckLastStateChange, 30, &clientHandle, NULL);

    // Call should be successful
    CuAssertIntEquals_Msg(tc, "Failed getting a free connection using getPooledConnection", GetConn_RC_PooledConnectionOK, rc);
    // Client handle we got back should point to the one we dummied up and put on the free queue
    CuAssertPtrEquals(tc, handle_p, clientHandle.handle_p);
    // And its instance count should be accurate
    CuAssertIntEquals(tc, handle_p->ploArea.instanceCount, clientHandle.instanceCount);
    // Free chain should now be empty
    CuAssertPtrIsNull(tc, reg.freeConnHandlePoolHead_p);
    // PLO count for the registration should have changed when we made an update
    CuAssertTrue(tc, oldPLO != reg.connPoolPLOCounter);
    // Handle should have changed from POOLED to READY state
    CuAssertIntEquals(tc, BBOAHDL_READY, handle_p->state);
    // And it should have a new instance count
    CuAssertTrue(tc, oldInstance != handle_p->ploArea.instanceCount);

    // Test freeing the connection
    oldPLO = reg.connPoolPLOCounter;
    oldInstance = handle_p->ploArea.instanceCount;
    rc = freePooledConnection(&reg, &clientHandle);

    // Call should be successful
    CuAssertIntEquals_Msg(tc, "Failed returning an active connection to the free chain", FreeConn_RC_FreeConnectionOK, rc);
    // Free chain head should be the one we freed
    CuAssertPtrEquals(tc, handle_p, reg.freeConnHandlePoolHead_p);
    // And it should have returned to a pooled state
    CuAssertIntEquals(tc, BBOAHDL_POOLED, handle_p->state);
    // PLO value for the reg should be new since we changed the head pointer
    CuAssertTrue(tc, oldPLO < reg.connPoolPLOCounter);
    // And the connection handle should have a new instance counter as well
    CuAssertTrue(tc, oldInstance < handle_p->ploArea.instanceCount);
    // Make doubly sure that the original client handle instance doesn't match the connection handle instance
    CuAssertTrue(tc, clientHandle.instanceCount != handle_p->ploArea.instanceCount);

    // Repeat getting the connection off the free chain so we can destroy it
    oldPLO = reg.connPoolPLOCounter;
    oldInstance = handle_p->ploArea.instanceCount;
    rc = getPooledConnection(&reg, reg.stckLastStateChange, 30, &clientHandle, NULL);

    // Call should be successful
    CuAssertIntEquals_Msg(tc, "Failed getting a free connection using getPooledConnection", GetConn_RC_PooledConnectionOK, rc);
    // Client handle we got back should point to the one we dummied up and put on the free queue
    CuAssertPtrEquals(tc, handle_p, clientHandle.handle_p);
    // And its instance count should be accurate
    CuAssertIntEquals(tc, handle_p->ploArea.instanceCount, clientHandle.instanceCount);
    // Free chain should now be empty
    CuAssertPtrIsNull(tc, reg.freeConnHandlePoolHead_p);
    // PLO count for the registration should have changed when we made an update
    CuAssertTrue(tc, oldPLO < reg.connPoolPLOCounter);
    // Handle should have changed from POOLED to READY state
    CuAssertIntEquals(tc, BBOAHDL_READY, handle_p->state);
    // And it should have a new instance count
    CuAssertTrue(tc, oldInstance < handle_p->ploArea.instanceCount);

    // Test destroying the connection
    oldPLO = reg.connPoolPLOCounter;
    oldInstance = handle_p->ploArea.instanceCount;
    rc = destroyPooledConnection(&reg, reg.stckLastStateChange, handle_p);

    // Call should be successful
    CuAssertIntEquals_Msg(tc, "Failed to destroy a connection", DestroyConn_RC_DestroyConnectionOK, rc);
    // Active connections should be gone
    CuAssertIntEquals(tc, 0, reg.activeConnCount);
    // And the list should be empty
    CuAssertPtrIsNull(tc, reg.allConnHandleListHead_p);
    // PLO value for the reg should be new
    CuAssertTrue(tc, oldPLO < reg.connPoolPLOCounter);
}

void test_waiter (CuTest * tc) {
    struct wolaRegistration serverReg = setupServerReg();
    struct wolaRegistration reg = setupReg(&serverReg);
    WolaConnectionHandle_t * handle_p = setupHandle(&reg);
    struct wolaClientConnectionHandle clientHandle;
    memset(&clientHandle, 0, sizeof(WolaClientConnectionHandle_t));

    struct timeStruct startTime;
    struct timeStruct endTime;

    // put us at our max conns so we'll have to wait
    reg.activeConnCount = 2;

    __stck((unsigned long long*)&startTime);
    int rc = getPooledConnection(&reg, reg.stckLastStateChange, 20, &clientHandle, NULL);
    __stck((unsigned long long*)&endTime);
    int duration = endTime.seconds - startTime.seconds;

    println(__FUNCTION__ ": call to getPooledConnection completed in %d s", duration);

    // Verify we got the "waiter timed out before finding a connection" rc
    CuAssertIntEquals(tc, GetConn_RC_ExceededMaxConnections, rc);
    // And make sure it waited for an appropriate amount of time
    CuAssertTrue(tc, duration < 23);
    CuAssertTrue(tc, duration > 17);
    //  Our waiter should be on the queue
    CuAssertPtrNotNull(tc, reg.connWaitListHead_p);
    CuAssertPtrNotNull(tc, reg.connWaitListTail_p);
    // And be marked as timed out
    WolaConnectionHandleWaiter_t * waiter_p = reg.connWaitListHead_p;
    CuAssertIntEquals(tc, BBOAHDLW_TIMED_OUT, waiter_p->cdsgArea.state);

    // Free a connection. This will go down the path to pull a waiter off the queue, but should
    // discard ours since it timed out.
    handle_p->state = BBOAHDL_READY;
    clientHandle.handle_p = handle_p;
    clientHandle.instanceCount = handle_p->ploArea.instanceCount;
    rc = freePooledConnection(&reg, &clientHandle);

    // This call should succeed
    CuAssertIntEquals(tc, FreeConn_RC_FreeConnectionOK, rc);
    // Our waiter should have popped off the queue
    CuAssertPtrIsNull(tc, reg.connWaitListHead_p);
    CuAssertPtrIsNull(tc, reg.connWaitListTail_p);
    // But since it was timed out, the freed connection should go to the free chain instead
    CuAssertPtrEquals(tc, handle_p, reg.freeConnHandlePoolHead_p);
}

void test_get_error_codes (CuTest * tc) {
    struct wolaRegistration serverReg = setupServerReg();
    struct wolaRegistration reg = setupReg(&serverReg);
    WolaConnectionHandle_t * handle_p = setupHandle(&reg);
    struct wolaClientConnectionHandle clientHandle;
    memset(&clientHandle, 0, sizeof(WolaClientConnectionHandle_t));

    // Give a mismatched STCK value for the registration
    int rc = getPooledConnection(&reg, 9999, 30, &clientHandle, NULL);
    CuAssertIntEquals(tc, GetConn_RC_RgeStckChanged, rc);

    // Put the connpool in a non-ready state
    reg.connPoolState = BBOARGE_CONNPOOL_DESTROYING;
    rc = getPooledConnection(&reg, reg.stckLastStateChange, 30, &clientHandle, NULL);
    CuAssertIntEquals(tc, GetConn_RC_ConnPoolNotReady, rc);
    reg.connPoolState = BBOARGE_CONNPOOL_READY;

    // Try to get a new connection (local comm won't be available to give us one)
    rc = getPooledConnection(&reg, reg.stckLastStateChange, 30, &clientHandle, NULL);
    CuAssertIntEquals(tc, GetConn_RC_LocalCommConnectError, rc);
}

void test_free_error_codes (CuTest * tc) {
    struct wolaRegistration serverReg = setupServerReg();
    struct wolaRegistration reg = setupReg(&serverReg);
    WolaConnectionHandle_t * handle_p = setupHandle(&reg);
    struct wolaClientConnectionHandle clientHandle;
    memset(&clientHandle, 0, sizeof(WolaClientConnectionHandle_t));

    // Use a mismatched instance count on the client handle
    clientHandle.handle_p = handle_p;
    clientHandle.instanceCount = 500;
    int rc = freePooledConnection(&reg, &clientHandle);
    CuAssertIntEquals(tc, FreeConn_RC_InvalidClientHandle, rc);

    // Try to free an already-pooled connection
    clientHandle.instanceCount = handle_p->ploArea.instanceCount;
    handle_p->state = BBOAHDL_POOLED;
    rc = freePooledConnection(&reg, &clientHandle);
    CuAssertIntEquals(tc, FreeConn_RC_ConnectionStateError, rc);
}

void test_destroy_error_codes (CuTest * tc) {
    struct wolaRegistration serverReg = setupServerReg();
    struct wolaRegistration reg = setupReg(&serverReg);
    WolaConnectionHandle_t * handle_p = setupHandle(&reg);
    struct wolaClientConnectionHandle clientHandle;
    memset(&clientHandle, 0, sizeof(WolaClientConnectionHandle_t));

    // Pass in an incorrect STCK value
    int rc = destroyPooledConnection(&reg, 9999, handle_p);
    CuAssertIntEquals(tc, DestroyConn_RC_RgeStckChanged, rc);

    // Give it a connection handle that's not actually on the RGE
    rc = destroyPooledConnection(&reg, reg.stckLastStateChange, handle_p);
    CuAssertIntEquals(tc, DestroyConn_RC_ConnectionNotOnRGE, rc);

}

CuSuite * server_wola_connection_pool_test_suite() {
    CuSuite* suite = CuSuiteNew("server_wola_connection_pool_test");

    SUITE_ADD_TEST(suite, test_get_free_destroy);
    SUITE_ADD_TEST(suite, test_waiter);
    SUITE_ADD_TEST(suite, test_get_error_codes);
    SUITE_ADD_TEST(suite, test_free_error_codes);
    SUITE_ADD_TEST(suite, test_destroy_error_codes);

    return suite;
}

WolaSharedMemoryAnchor_t* setupSharedMemoryAnchor() {
    WolaSharedMemoryAnchor_t * wolaAnchor_p = (WolaSharedMemoryAnchor_t *) malloc(WOLA_SMA_SIZE_MB * 1024 * 1024);
    memset(wolaAnchor_p, 0, sizeof(struct wolaSharedMemoryAnchor));
    wolaAnchor_p->anchorRequestedSize = WOLA_SMA_SIZE_MB * 1024 * 1024;

    long long outer_cp_size;
    void * outer_cp_addr = getOuterCellPoolAddrAndSize( wolaAnchor_p, &outer_cp_size );

    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));
    wolaAnchor_p->outerCellPoolID = buildCellPool(outer_cp_addr,
                                                  outer_cp_size,
                                                  1024 * 1024,
                                                  "BBGZWASP",
                                                  flags);

    wolaAnchor_p->connectionHandleCellPoolID = buildInnerCellPool( wolaAnchor_p, sizeof(WolaConnectionHandle_t), "BBGZHDLP");
    wolaAnchor_p->connectionWaiterCellPoolID = buildInnerCellPool( wolaAnchor_p, sizeof(WolaConnectionHandleWaiter_t), "BBGZWTRP");

    return wolaAnchor_p;
}

struct wolaRegistration setupServerReg() {
    struct wolaRegistration serverReg;
    memset(&serverReg, 0, sizeof(WolaRegistration_t));
    serverReg.stckLastStateChange = 3333;
    return serverReg;
}

struct wolaRegistration setupReg(WolaRegistration_t * serverReg_p) {
    struct wolaRegistration reg;
    memset(&reg, 0, sizeof(WolaRegistration_t));
    reg.connPoolState = BBOARGE_CONNPOOL_READY;
    reg.stckLastStateChange = 1111;
    reg.connPoolPLOCounter = 2222;
    reg.minConns = 1;
    reg.maxConns = 2;
    reg.activeConnCount = 0;
    reg.allConnHandleListHead_p = NULL;
    reg.freeConnHandlePoolHead_p = NULL;
    reg.serverRegistration_p = serverReg_p;
    reg.wolaAnchor_p = setupSharedMemoryAnchor();
    return reg;
}

struct wolaConnectionHandle* setupHandle(WolaRegistration_t * reg_p) {
    WolaConnectionHandle_t * handle_p = getCellPoolCell(reg_p->wolaAnchor_p->connectionHandleCellPoolID);
    memset(handle_p, 0, sizeof(WolaConnectionHandle_t));
    handle_p->ploArea.instanceCount = 4444;
    handle_p->nextHandle_p = NULL;
    handle_p->nextFreeHandle_p = NULL;
    handle_p->wolaServerRegistrationSTCK = 3333;
    handle_p->state = BBOAHDL_POOLED;
    return handle_p;
}
