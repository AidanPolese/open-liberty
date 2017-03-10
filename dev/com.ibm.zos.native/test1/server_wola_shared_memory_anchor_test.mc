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

#include "../include/ieantc.h"
#include "../include/server_wola_shared_memory_anchor_server.h"
#include "../include/mvs_cell_pool_services.h"

#include "include/CuTest.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 */

extern void println(char * messgae_p, ...);
extern void * getOuterCellPoolAddrAndSize( WolaSharedMemoryAnchor_t * bboashr_p, long long * outer_cp_size_p );
extern void * nextQuadWord( void * fromHere ) ;


/**
 * Test.
 */
void test_getBboashrTokenName(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char token_name[16];
    getBboashrTokenName(token_name, "WOLAROBX");
    CuAssertMemEquals(tc, "BBGZSHR_WOLAROBX", token_name, 16);

    // Test blank padding.
    getBboashrTokenName(token_name, "WOLA");
    CuAssertMemEquals(tc, "BBGZSHR_WOLA    ", token_name, 16);

    println(__FUNCTION__ ": exit");
}

/**
 * Test.
 */
void test_getBboashrForWolaGroup_notExist(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Create the token name.
    char token_name[16];
    getBboashrTokenName(token_name, "WOLAROBZ");
    CuAssertMemEquals(tc, "BBGZSHR_WOLAROBZ", token_name, 16);

    // BBOASHR_WOLAROBZ shouldn't exist
    int iean4rt_rc = 0;
    void * bboashr_p = getBboashrForWolaGroup( "WOLAROBZ", &iean4rt_rc ) ;
    CuAssertPtrIsNull(tc,bboashr_p);
    CuAssertIntEquals(tc, 4, iean4rt_rc);

    println(__FUNCTION__ ": exit");
}


/**
 * Test.
 */
void test_createBboashrInNameToken(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Create the token name.
    char token_name[16];
    getBboashrTokenName(token_name, "WOLAROBY");
    CuAssertMemEquals(tc, "BBGZSHR_WOLAROBY", token_name, 16);

    // BBOASHR_WOLAROBY initially shouldn't exist
    int iean4rt_rc = -1;
    void * bboashr_p = getBboashrForWolaGroup("WOLAROBY", &iean4rt_rc);
    CuAssertIntEquals(tc, 4, iean4rt_rc);
    CuAssertPtrIsNull(tc,bboashr_p);

    // Create it
    int iean4cr_rc = createBboashrNameToken("WOLAROBY", (void *) 0x1234);
    CuAssertIntEquals(tc, 0, iean4cr_rc);

    // Lookup should work now.
    bboashr_p = getBboashrForWolaGroup("WOLAROBY", &iean4rt_rc);
    CuAssertIntEquals(tc, 0, iean4rt_rc);
    CuAssertPtrEquals(tc, (void *)0x1234, bboashr_p);

    // Delete the name token
    int iean4dl_rc = -1;
    iean4dl(IEANT_SYSTEM_LEVEL,
            token_name,
            &iean4dl_rc);
    CuAssertIntEquals(tc, 0, iean4dl_rc);

    println(__FUNCTION__ ": exit");
}


/**
 * Test.
 */
void test_nextQuadWord( CuTest * tc) {

    void * p = (void *) 0x04; 
    CuAssertPtrEquals(tc, (void *)0x10, nextQuadWord(p) );

    p = (void *) 0x07;
    CuAssertPtrEquals(tc, (void *)0x10, nextQuadWord(p) );

    p = (void *) 0x0b;
    CuAssertPtrEquals(tc, (void *)0x10, nextQuadWord(p) );

    p = (void *) 0x10;
    CuAssertPtrEquals(tc, (void *)0x10, nextQuadWord(p) );

    p = (void *) 0x11;
    CuAssertPtrEquals(tc, (void *)0x20, nextQuadWord(p) );

    p = (void *) 0xffffffffffff0001;
    CuAssertPtrEquals(tc, (void *)0xffffffffffff0010, nextQuadWord(p) );

}


/**
 * Test.
 */
void test_getOuterCellPoolAddrAndSize( CuTest * tc) {

    WolaSharedMemoryAnchor_t bboashr;
    WolaSharedMemoryAnchor_t * bboashr_p = &bboashr;
    memset(bboashr_p, 0, sizeof(WolaSharedMemoryAnchor_t));

    bboashr_p->anchorRequestedSize = WOLA_SMA_SIZE_MB * 1024 * 1024; 

    long long outer_cp_size = 0;
    void * outer_cp_addr = getOuterCellPoolAddrAndSize( bboashr_p, &outer_cp_size );

    void * expected_addr = nextQuadWord( (void *) ((char *)bboashr_p + sizeof(WolaSharedMemoryAnchor_t)) );
    long long expected_size = WOLA_SMA_SIZE_MB * 1024 * 1024 - ((char *) expected_addr - (char *)bboashr_p);

    CuAssertPtrEquals(tc, expected_addr, outer_cp_addr );
    CuAssertLongLongEquals(tc, expected_size, outer_cp_size);
    CuAssert(tc, "outer_cp_size should be smaller than anchorRequestedSize", outer_cp_size < bboashr_p->anchorRequestedSize);
    CuAssert(tc, "outer_cp_size should be larger than bboashr anchor", outer_cp_size > sizeof(WolaSharedMemoryAnchor_t));
}


/**
 * Test.
 */
void test_buildCellPools( CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Grab a BIG chunk of memory
    WolaSharedMemoryAnchor_t * bboashr_p = (WolaSharedMemoryAnchor_t *) malloc(WOLA_SMA_SIZE_MB * 1024 * 1024); 

    initializeBboashr(bboashr_p, "WOLAROBY");

    CuAssertTrue(tc, bboashr_p->outerCellPoolID != 0);

    println(__FUNCTION__ ": bboashr_p: %x, bboashr_p->outerCellPoolID: %x, sizeof(bboashr): %d", 
            bboashr_p, bboashr_p->outerCellPoolID, sizeof(WolaSharedMemoryAnchor_t));

    CuAssertTrue(tc, bboashr_p->registationCellPoolID != 0);
    CuAssertTrue(tc, bboashr_p->availServiceCellPoolID != 0);
    CuAssertTrue(tc, bboashr_p->waitServiceCellPoolID != 0);
    CuAssertTrue(tc, bboashr_p->connectionHandleCellPoolID != 0);
    CuAssertTrue(tc, bboashr_p->connectionWaiterCellPoolID != 0);

    free(bboashr_p);

    println(__FUNCTION__ ": exit");
}


/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_wola_shared_memory_anchor_test_suite() {

    CuSuite* suite = CuSuiteNew("server_wola_shared_memory_anchor_test");

    SUITE_ADD_TEST(suite, test_getBboashrTokenName);
    SUITE_ADD_TEST(suite, test_getBboashrForWolaGroup_notExist);
    SUITE_ADD_TEST(suite, test_createBboashrInNameToken);
    SUITE_ADD_TEST(suite, test_nextQuadWord);
    SUITE_ADD_TEST(suite, test_getOuterCellPoolAddrAndSize);
    SUITE_ADD_TEST(suite, test_buildCellPools);

    return suite;
}


