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
#include "../include/mvs_stimerm.h"
#include "../include/mvs_utils.h"
#include "../include/common_defines.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 *
 * Native unit tests for mvs_stimerm.
 *
 */

extern void println(char * message_p, ...);

/** Struct used to compute time difference in seconds (roughly). */
struct timeStruct {
    int seconds;
    int unused;
};

/** Exit routine for test_setAndCancelTimer. */
void exitRoutine_doNothing(void* parms_p) {
}

/** Exit routine that sets the time it was driven. */
void exitRoutine_setTimestamp(void* parms_p) {
    __stck(parms_p);
}

/** Exit routine that sets the time it was driven and waits 5 seconds. */
void exitRoutine_setTimestampAndWait(void* parms_p) {
    __stck(parms_p);
    sleep(5);
}

/**
 * Test timer set/cancel.
 */
void test_setAndCancelTimer(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    MvsTimerID_t timerID;
    PetVet test_petvet __attribute__((aligned(16))) ;
    initializePetVet(&test_petvet, 5, NULL);

    int rc = setTimer(exitRoutine_doNothing, NULL, 60, &test_petvet, FALSE, &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);

    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not zero", 0, rc);

    destroyPetVetPool(&test_petvet);

    println(__FUNCTION__ ": exit");
}

/**
 * Test timer pop after 5 seconds.
 */
void test_timerPopFiveSeconds(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    MvsTimerID_t timerID;
    PetVet test_petvet __attribute__((aligned(16))) ;
    struct timeStruct startingTime __attribute__((aligned(8)));
    struct timeStruct alarmTime __attribute__((aligned(8)));

    initializePetVet(&test_petvet, 5, NULL);
    __stck((unsigned long long*)&startingTime);

    // Set a timer to pop in 5 seconds.
    int rc = setTimer(exitRoutine_setTimestamp, &alarmTime, 5, &test_petvet, FALSE, &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);

    // Wait for some time longer than 5 seconds.
    sleep(7);

    // Clean up the timer.  RC=1 means the timer popped.
    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not one", 1, rc);

    // Make sure the timer popped roughly when it was supposed to.
    int elapsedSeconds = alarmTime.seconds - startingTime.seconds;
    println("%s: Timer popped in %i seconds", __FUNCTION__, elapsedSeconds);
    CuAssertTrue(tc, ((elapsedSeconds >= 4) && (elapsedSeconds <= 6)));

    destroyPetVetPool(&test_petvet);

    println(__FUNCTION__ ": exit");
}

/**
 * Test timer pop after 5 seconds.
 */
void test_timerPopWaitForExitToRun(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    MvsTimerID_t timerID;
    PetVet test_petvet __attribute__((aligned(16))) ;
    struct timeStruct startingTime __attribute__((aligned(8)));
    struct timeStruct alarmTime __attribute__((aligned(8)));
    struct timeStruct endTime __attribute__((aligned(8)));

    initializePetVet(&test_petvet, 5, NULL);
    __stck((unsigned long long*)&startingTime);

    // Set a timer to pop in 5 seconds.
    int rc = setTimer(exitRoutine_setTimestampAndWait, &alarmTime, 5, &test_petvet, FALSE, &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);

    // Wait for some time longer than 5 seconds.
    sleep(7);

    // Clean up the timer.  RC=1 means the timer popped.  Record our end time.
    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not one", 1, rc);
    __stck((unsigned long long*)&endTime);

    // Make sure the timer popped roughly when it was supposed to.
    int elapsedSeconds = alarmTime.seconds - startingTime.seconds;
    println("%s: Timer popped in %i seconds", __FUNCTION__, elapsedSeconds);
    CuAssertTrue(tc, ((elapsedSeconds >= 4) && (elapsedSeconds <= 6)));

    elapsedSeconds = endTime.seconds - alarmTime.seconds;
    println("%s: Alarm ran for %i seconds", __FUNCTION__, elapsedSeconds);
    CuAssertTrue(tc, ((elapsedSeconds >= 4) && (elapsedSeconds <= 6)));

    destroyPetVetPool(&test_petvet);

    println(__FUNCTION__ ": exit");
}

/**
 * Test zero second timer.
 */
void test_zeroSecondTimer(CuTest * tc) {
    println(__FUNCTION__ ": entry");
    
    MvsTimerID_t timerID = 1L;
    PetVet test_petvet __attribute__((aligned(16))) ;
    long long exitParm = 0L;

    initializePetVet(&test_petvet, 5, NULL);

    // A 0 timeout value means no timer will actually be set
    // and the exitRoutine will not be called.
    int rc = setTimer(exitRoutine_setTimestamp, &exitParm, 0, &test_petvet, FALSE, &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);

    // Give a little time in case the exitRoutine were to get called for some reason.
    sleep(3);

    // Verify the exitRoutine wasn't called (because the exitParm wasn't set).
    CuAssertLongLongEquals(tc, 0, exitParm); 

    // Verify the timerId is 0.
    CuAssertLongLongEquals(tc, 0, (long long) timerID);

    // Cancel should still return 0.
    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not zero", 0, rc);

    println(__FUNCTION__ ": exit");
}

/**
 * Test negative timeout value.
 */
void test_negativeTimeoutValue(CuTest * tc) {
    println(__FUNCTION__ ": entry");
    
    MvsTimerID_t timerID = 1L;
    PetVet test_petvet __attribute__((aligned(16))) ;
    long long exitParm = 0L;

    initializePetVet(&test_petvet, 5, NULL);

    // A negative timeout value means no timer will actually be set
    // and the exitRoutine will not be called.
    int rc = setTimer(exitRoutine_setTimestamp, &exitParm, -1, &test_petvet, FALSE, &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);

    // Give a little time in case the exitRoutine were to get called for some reason.
    sleep(3);

    // Verify the exitRoutine wasn't called (because the exitParm wasn't set).
    CuAssertLongLongEquals(tc, 0, exitParm); 

    // Verify the timerId is 0.
    CuAssertLongLongEquals(tc, 0, (long long) timerID);

    // Cancel should still return 0.
    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not zero", 0, rc);

    println(__FUNCTION__ ": exit");
}

/**
 * Test MAX_INT 
 */
void test_BBGZ_minWithMaxInt(CuTest * tc) {

    CuAssertIntEquals(tc, 0x7FFFFFFF, (int) BBGZ_min( (long long) 0x7FFFFFFF * 100, 0x7FFFFFFFL ) );
    CuAssertIntEquals(tc, 30 * 100, (int) BBGZ_min( (long long) 30 * 100, 0x7FFFFFFFL ) );
}

/**
 * Test set timer with MAX_INT timeout
 */
void test_setTimerWithMaxInt(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    MvsTimerID_t timerID;
    PetVet test_petvet __attribute__((aligned(16))) ;
    initializePetVet(&test_petvet, 5, NULL);

    int rc = setTimer(exitRoutine_doNothing, NULL, 0x7FFFFFFF, &test_petvet, FALSE, &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);
    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not 4", 4, rc);

    // 111848 is the max timeout value you can use without causing cancelTimer 
    // to return 4.  STIMERM CANCEL returns 4 because the 'remaining time' output parm, 
    // which is computed as 26.04166 microseconds per bit, cannot fit in the alloted 
    // 4 byte area.
    rc = setTimer(exitRoutine_doNothing, NULL, 111848, &test_petvet, FALSE &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);
    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not zero", 0, rc);

    // 111849 is the minimum timeout value you can use to cause cancelTimer 
    // to return 4.  STIMERM CANCEL returns 4 because the 'remaining time' output parm, 
    // which is computed as 26.04166 microseconds per bit, cannot fit in the alloted 
    // 4 byte area.
    rc = setTimer(exitRoutine_doNothing, NULL, 111849, &test_petvet, FALSE, &timerID);
    CuAssertIntEquals_Msg(tc, "Return code from setTimer was not zero", 0, rc);
    rc = cancelTimer(&timerID);
    CuAssertIntEquals_Msg(tc, "Return code from cancelTimer was not 4", 4, rc);

    destroyPetVetPool(&test_petvet);

    println(__FUNCTION__ ": exit");
}


/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * mvs_stimerm_test_suite() {

    CuSuite* suite = CuSuiteNew("mvs_stimerm_test");

    SUITE_ADD_TEST(suite, test_setAndCancelTimer);
    SUITE_ADD_TEST(suite, test_timerPopFiveSeconds);
    SUITE_ADD_TEST(suite, test_timerPopWaitForExitToRun);
    SUITE_ADD_TEST(suite, test_zeroSecondTimer);
    SUITE_ADD_TEST(suite, test_negativeTimeoutValue);
    SUITE_ADD_TEST(suite, test_BBGZ_minWithMaxInt);
    SUITE_ADD_TEST(suite, test_setTimerWithMaxInt);

    return suite;
}


