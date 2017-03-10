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

#include "../include/gen/ihapsa.h"
#include "../include/gen/ikjtcb.h"
#include "../include/gen/ihastcb.h"
#include "../include/gen/bpxzotcb.h"

#include "../include/mvs_enq.h"
#include "../include/mvs_user_token_manager.h"
#include "../include/mvs_iarv64.h"
#include "../include/common_defines.h"
#include "../include/server_wola_registration.h"
#include "../include/server_wola_shared_memory_anchor.h"

#include "include/CuTest.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 * TODO: this part currently tests random things not related to server_wola_services,
 *       e.g. test_iarv64, just to demonstrate what such tests might look like.  
 *       Eventually those tests should be moved to their own respective test parts.
 *
 */

extern void println(char * messgae_p, ...);
extern char * buildWolaEnqRname(char * rname, char * wola_group, char * wola_name2, char * wola_name3) ;

/**
 * Test.
 */
void test_buildWolaEnqRname(CuTest * tc) {

    char rname[300];
    buildWolaEnqRname(rname, "WOLAROBX", "LIBERTY ", "SERVER  ") ;
    CuAssertStrEquals(tc, "BBG:AENQ9:LCOM:WOLAROBXLIBERTY SERVER  ", rname);
}

/**
 * Test.
 */
void test_get_enq_exclusive_system(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char rname[300];
    buildWolaEnqRname(rname, "WOLAROB2", "LIBERTY ", "SERVER  ") ;

    println(__FUNCTION__ ": built wola rname: %s", rname);

    // Get the enq.
    // Note: this method will ABEND if it fails.
    enqtoken enq_token;
    get_enq_exclusive_system( BBGZ_ENQ_QNAME, rname, NULL, &enq_token);

    println(__FUNCTION__ ": obtained enq");

    // Release the enq.
    // Note: this method will ABEND if it fails.
    release_enq( &enq_token );

    println(__FUNCTION__ ": exit");
}

/**
 * Test.
 */
void test_iarv64(CuTest * tc) {

    void * addr = getSharedAbove(10, 0, getAddressSpaceSupervisorStateUserToken());
    println(__FUNCTION__ ": obtained iarv64 storage: %x", addr);

    // Make sure we got something back
    CuAssertPtrNotNull(tc, addr);

    detachSharedAbove(addr, getAddressSpaceSupervisorStateUserToken(), 1);
}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_wola_services_test_suite() {

    CuSuite* suite = CuSuiteNew("server_wola_services_test");

    SUITE_ADD_TEST(suite, test_buildWolaEnqRname);
    SUITE_ADD_TEST(suite, test_get_enq_exclusive_system);
    SUITE_ADD_TEST(suite, test_iarv64);

    return suite;
}


