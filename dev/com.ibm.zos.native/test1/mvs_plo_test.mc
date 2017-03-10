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
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/CuTest.h"

#include "../include/mvs_plo.h"

/**
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 */

extern void println(char * messgae_p, ...);

/**
 * Test.
 */
void test_ploDoubleCompareAndSwapDoubleWord(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long area1;                                  /* 08 */
        unsigned long long area2;                                  /* 16 */
    } swapCb;

    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloCompareAndSwapAreaDoubleWord_t swapArea2;

    swapCb.area1 = 1;
    swapCb.area2 = 2;

    swapArea.compare_p = &swapCb.area1;
    swapArea.expectedValue = 1;
    swapArea.replaceValue = 160;

    swapArea2.compare_p = &swapCb.area2;
    swapArea2.expectedValue = 2;
    swapArea2.replaceValue = 260;

    // Plo that works
    println(__FUNCTION__ ": Test swap in 160 and 260 works");
    int rc = ploDoubleCompareAndSwapDoubleWord(&swapCb._rsvd, &swapArea, &swapArea2);
    CuAssertIntEquals_Msg(tc, "Return code from ploDoubleCompareAndSwapDoubleWord should be 0", 0, rc);
    CuAssertLongLongEquals_Msg(tc, "area1 should be 160", 160, swapCb.area1);
    CuAssertLongLongEquals_Msg(tc, "area2 should be 260", 260, swapCb.area2);

    // plo that fails because first area does not match expected
    println(__FUNCTION__ ": Test swap fails because first area does not match expected value.");
    swapArea.expectedValue = 1;
    swapArea.replaceValue = 162;
    swapArea2.expectedValue = 260;
    swapArea2.replaceValue = 262;
    rc = ploDoubleCompareAndSwapDoubleWord(&swapCb._rsvd, &swapArea, &swapArea2);
    CuAssertIntEquals_Msg(tc, "Return code from ploDoubleCompareAndSwapDoubleWord should be 1", 1, rc);
    CuAssertLongLongEquals_Msg(tc, "area1 should be 160", 160, swapCb.area1);
    CuAssertLongLongEquals_Msg(tc, "area2 should be 260", 260, swapCb.area2);
    CuAssertLongLongEquals_Msg(tc, "expected value for area 1 should have been updated to be 160", 160, swapArea.expectedValue);
    CuAssertLongLongEquals_Msg(tc, "expected value for area 2 should be 260", 260, swapArea2.expectedValue);

    // plo that fails because second area does not match expected
    println(__FUNCTION__ ": Test swap fails because second area does not match expected value.");
    swapArea.expectedValue = 160;
    swapArea.replaceValue = 162;
    swapArea2.expectedValue = 1;
    swapArea2.replaceValue = 262;
    rc = ploDoubleCompareAndSwapDoubleWord(&swapCb._rsvd, &swapArea, &swapArea2);
    CuAssertIntEquals_Msg(tc, "Return code from ploDoubleCompareAndSwapDoubleWord should be 2", 2, rc);
    CuAssertLongLongEquals_Msg(tc, "area1 should be 160", 160, swapCb.area1);
    CuAssertLongLongEquals_Msg(tc, "area2 should be 260", 260, swapCb.area2);
    CuAssertLongLongEquals_Msg(tc, "expected value for area 2 should have been updated to be 260", 260, swapArea2.expectedValue);
    CuAssertLongLongEquals_Msg(tc, "expected value for area 1 should be 160", 160, swapArea.expectedValue);

    println(__FUNCTION__ ": exit");
}

/**
 * Test.
 */
void test_ploCompareAndLoadDoubleWord(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long area1;                                  /* 08 */
        unsigned long long area2;                                  /* 16 */
    } swapCb;

    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloLoadAreaDoubleWord_t           loadArea;

    swapCb.area1 = 1;
    swapCb.area2 = 2;

    swapArea.compare_p = &swapCb.area1;
    swapArea.expectedValue = 1;
    loadArea.loadLocation_p = &swapCb.area2;
    loadArea.loadValue = 0;

    // Plo that works
    println(__FUNCTION__ ": Test load works");
    int rc = ploCompareAndLoadDoubleWord(&swapCb._rsvd, &swapArea, &loadArea);
    CuAssertIntEquals_Msg(tc, "Return code from ploCompareAndLoadDoubleWord should be 0", 0, rc);
    CuAssertLongLongEquals_Msg(tc, "area1 should be 1", 1, swapCb.area1);
    CuAssertLongLongEquals_Msg(tc, "area2 should be 2", 2, swapCb.area2);
    CuAssertLongLongEquals_Msg(tc, "loaded value should be 2", 2, loadArea.loadValue);

    // plo that fails because area does not match expected
    println(__FUNCTION__ ": Test load fails because area does not match expected value.");
    swapArea.expectedValue = 5;
    rc = ploCompareAndLoadDoubleWord(&swapCb._rsvd, &swapArea, &loadArea);
    CuAssertIntEquals_Msg(tc, "Return code from ploCompareAndLoadDoubleWord should be 1", 1, rc);
    CuAssertLongLongEquals_Msg(tc, "area1 should be 1", 1, swapCb.area1);
    CuAssertLongLongEquals_Msg(tc, "area2 should be 2", 2, swapCb.area2);
    CuAssertLongLongEquals_Msg(tc, "expected value for swapArea should have been updated to be 1", 1, swapArea.expectedValue);

    println(__FUNCTION__ ": exit");
}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * mvs_plo_test_suite() {

    CuSuite* suite = CuSuiteNew("mvs_plo_test");

    SUITE_ADD_TEST(suite, test_ploDoubleCompareAndSwapDoubleWord);
    SUITE_ADD_TEST(suite, test_ploCompareAndLoadDoubleWord);

    return suite;
}


