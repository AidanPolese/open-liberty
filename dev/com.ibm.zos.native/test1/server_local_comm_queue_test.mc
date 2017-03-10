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
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/CuTest.h"
#include "../include/server_local_comm_client.h"
#include "../include/server_local_comm_queue.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 *
 */

extern void println(char * messgae_p, ...);

/**
 * Forward declares for functions not declared in server_local_comm_queue.h.
 */
LocalCommWorkQueueElement * initializeWorkQueueElement(LocalCommWorkQueueElement * workElement_p, 
                                                       short requestType,
                                                       LocalCommClientConnectionHandle_t * clientConnHandle_p) ;
LocalCommWorkQueueElement * initializeFFDCWorkQueueElement(LocalCommWorkQueueElement * ffdcWorkElement_p, 
                                                           LocalCommClientConnectionHandle_t* clientConnHandle_p,
                                                           int tp,
                                                           char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE]) ;
int validateClientConnectionHandle(struct localCommClientConnectionHandle* clientConnHandle_p) ;

/**
 * TODO: this test should be in a new suite, server_local_comm_client_test.mc
 */
void test_validateClientConnectionHandle(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Setup a conn handle and client conn handle 
    LocalCommConnectionHandle_t connHandle;
    memset(&connHandle, 0, sizeof(connHandle));

    LocalCommClientConnectionHandle_t clientConnHandle;
    memset(&clientConnHandle, 0, sizeof(clientConnHandle));
    clientConnHandle.handle_p = &connHandle;

    // instanceCounts are both init'ed to 0, so should pass validate
    CuAssertIntEquals(tc, 0, validateClientConnectionHandle(&clientConnHandle));

    clientConnHandle.instanceCount = connHandle.instanceCount = 1;   // they match, should validate
    CuAssertIntEquals(tc, 0, validateClientConnectionHandle(&clientConnHandle));

    clientConnHandle.instanceCount = connHandle.instanceCount + 1; // don't match, should fail 
    CuAssertIntEquals(tc, LOCAL_COMM_STALE_HANDLE, validateClientConnectionHandle(&clientConnHandle));

    clientConnHandle.instanceCount = connHandle.instanceCount;
    clientConnHandle.handle_p = NULL;   // null conn handle, should fail 
    CuAssertIntEquals(tc, LOCAL_COMM_STALE_HANDLE, validateClientConnectionHandle(&clientConnHandle));

    println(__FUNCTION__ ": exit");
}


/**
 * Test.
 */
void test_initializeWorkQueueElement(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Setup a conn handle and client conn handle.
    LocalCommConnectionHandle_t connHandle;
    memset(&connHandle, 0, sizeof(connHandle));

    LocalCommClientConnectionHandle_t clientConnHandle;
    memset(&clientConnHandle, 0, sizeof(clientConnHandle));
    clientConnHandle.handle_p = &connHandle;

    LocalCommWorkQueueElement workElement;
    workElement.requestFlags = 1;    // This should get zero'ed out.

    // Run the code under test.
    initializeWorkQueueElement(&workElement, REQUESTTYPE_CONNECT, &clientConnHandle);

    CuAssertMemEquals(tc, SERVER_LCOM_WRQE_EYECATCHER_INUSE, workElement.eyecatcher, sizeof(workElement.eyecatcher));
    CuAssertIntEquals(tc, LCOMWORKQUEUEELEMENT_CURRENT_VER, workElement.version);
    CuAssertIntEquals(tc, sizeof(LocalCommWorkQueueElement), workElement.length);
    CuAssertIntEquals(tc, REQUESTTYPE_CONNECT, workElement.requestType);
    CuAssertIntEquals(tc, 0, workElement.requestFlags);
    CuAssertMemEquals(tc, &clientConnHandle, workElement.clientConnHandle, sizeof(LocalCommClientConnectionHandle_t));

    println(__FUNCTION__ ": exit");
}

/**
 * Test.
 */
void test_initializeFFDCWorkQueueElement(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Setup a conn handle and client conn handle.
    LocalCommConnectionHandle_t connHandle;
    memset(&connHandle, 0, sizeof(connHandle));

    LocalCommClientConnectionHandle_t clientConnHandle;
    memset(&clientConnHandle, 0, sizeof(clientConnHandle));
    clientConnHandle.handle_p = &connHandle;

    LocalCommWorkQueueElement workElement;
    int tp = 3;
    char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE];
    memcpy(rawData, "abcdefghijklmnopqrstuvwxyz", 26);

    // Run the code under test
    initializeFFDCWorkQueueElement(&workElement, &clientConnHandle, tp, rawData);

    CuAssertMemEquals(tc, SERVER_LCOM_WRQE_EYECATCHER_INUSE, workElement.eyecatcher, sizeof(workElement.eyecatcher));
    CuAssertIntEquals(tc, LCOMWORKQUEUEELEMENT_CURRENT_VER, workElement.version);
    CuAssertIntEquals(tc, sizeof(LocalCommWorkQueueElement), workElement.length);
    CuAssertIntEquals(tc, REQUESTTYPE_FFDC, workElement.requestType);
    CuAssertIntEquals(tc, 0, workElement.requestFlags);
    CuAssertMemEquals(tc, &clientConnHandle, workElement.clientConnHandle, sizeof(LocalCommClientConnectionHandle_t));
    CuAssertIntEquals(tc, tp, workElement.requestSpecificParms.ffdcParms.tp);
    CuAssertMemEquals(tc, rawData, workElement.requestSpecificParms.ffdcParms.rawData, sizeof(rawData));

    println(__FUNCTION__ ": exit");
}

/**
 * Test "initializeWorkQueue"
 * @param tc
 */
void test_initializeWorkQueue(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    LocalCommWorkQueue localWorkQ;
    LocalCommClientAnchor_t* local_LOCL_p = (LocalCommClientAnchor_t*) 0x8765432112345678L;

    LocalComWQE_PLO_CS_Area_t* localPLO_CS_Area_TestValue_p = (LocalComWQE_PLO_CS_Area_t*) 0x1234567887654321L;


    memset(&localWorkQ, 'F', sizeof(localWorkQ));

    initializeWorkQueue(&localWorkQ,
                        local_LOCL_p,
                        localPLO_CS_Area_TestValue_p);

    // Assert results
    CuAssertPtrNotNull(tc, localWorkQ.wrqPLO_CS_p);
    CuAssertPtrEquals_Msg(tc, "LocalCommWorkQueue.wrqPLO_CS_p should be equal to supplied ptr",
                          localWorkQ.wrqPLO_CS_p, localPLO_CS_Area_TestValue_p);
    CuAssert(tc, "LocalCommWorkQueue.wrqeLimit should be non-zero", localWorkQ.wrqeLimit);
    CuAssertPtrEquals_Msg(tc, "LocalCommWorkQueue.LOCL_p should be equal to supplied ptr",
                          localWorkQ.LOCL_p, local_LOCL_p);

    println(__FUNCTION__ ": exit");
}   // end, test_initializeWorkQueue

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_local_comm_queue_test_suite() {

    CuSuite* suite = CuSuiteNew("server_local_comm_queue_test");

    SUITE_ADD_TEST(suite, test_validateClientConnectionHandle);
    SUITE_ADD_TEST(suite, test_initializeWorkQueueElement);
    SUITE_ADD_TEST(suite, test_initializeFFDCWorkQueueElement);
    SUITE_ADD_TEST(suite, test_initializeWorkQueue);

    return suite;
}

