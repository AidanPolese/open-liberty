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

#include "../include/server_wola_registration_server.h"
#include "../include/server_wola_shared_memory_anchor.h"

/**
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 */

extern void println(char * messgae_p, ...);

/**
 * Test.  Make sure you didn't break offsets in the WOLA registration.
 */
void test_wolaRgeSize(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    // This is the size you think the RGE is.  Copy the computed size from
    // the comments in server_wola_registration.h here.
    size_t rgeComputedLength = 0x180;
    size_t rgeActualLength = sizeof(WolaRegistration_t);

    CuAssertIntEquals_Msg(tc, "Computed and actual RGE sizes do not match", (int)rgeComputedLength, (int)rgeActualLength);

    // Verify that double-word aligned variables are double-word aligned.
    CuAssertIntEquals_Msg(tc, "Offset of STCK in RGE is not on a double word boundary", 0,
                          ((int)(offsetof(WolaRegistration_t, stckLastStateChange))) % 8);
    CuAssertIntEquals_Msg(tc, "Offset of previous registration in RGE is not on a double word boundary", 0,
                          ((int)(offsetof(WolaRegistration_t, previousRegistration_p))) % 8);
    CuAssertIntEquals_Msg(tc, "Offset of next registration in RGE is not on a double word boundary", 0,
                          ((int)(offsetof(WolaRegistration_t, nextRegistration_p))) % 8);
    CuAssertIntEquals_Msg(tc, "Offset of conn pool PLO counter in RGE is not on a double word boundary", 0,
                          ((int)(offsetof(WolaRegistration_t, connPoolPLOCounter))) % 8);

    println(__FUNCTION__ ": exit");
}

/**
 * Test.
 */
void test_wolaRgeListFunctions(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Dummy-up a BBOASHR and BBOARGE.
    WolaSharedMemoryAnchor_t anchor;
    WolaRegistration_t registrations[3];

    char* regname0 = "REGISTRATION1";
    char* regname1 = "REGISTRATION2";
    char* regname2 = "REGISTRATION3";

    memset(&anchor, 0, sizeof(anchor));
    memset(registrations, 0, sizeof(registrations));

    strcpy((registrations[0]).registrationName, regname0);
    strcpy((registrations[1]).registrationName, regname1);
    strcpy((registrations[2]).registrationName, regname2);

    println(__FUNCTION__ ": Adding registration 0 to list");

    addBboargeToChain(&anchor, &(registrations[0]));

    CuAssertPtrEquals_Msg(tc, "BBOASHR head should point to registration 0", &registrations[0], anchor.firstRge_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 prev should be NULL", NULL, (registrations[0]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 next should be NULL", NULL, (registrations[0]).nextRegistration_p);

    println(__FUNCTION__ ": Adding registration 1 to list");

    addBboargeToChain(&anchor, &(registrations[1]));

    CuAssertPtrEquals_Msg(tc, "BBOASHR head should point to registration 1", &registrations[1], anchor.firstRge_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 prev should be NULL", NULL, (registrations[1]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 next should point to registration 0", &registrations[0], (registrations[1]).nextRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 prev should point to registration 1", &registrations[1], (registrations[0]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 next should be NULL", NULL, (registrations[0]).nextRegistration_p);

    println(__FUNCTION__ ": Adding registration 2 to list");

    addBboargeToChain(&anchor, &(registrations[2]));

    CuAssertPtrEquals_Msg(tc, "BBOASHR head should point to registration 2", &registrations[2], anchor.firstRge_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 prev should be NULL", NULL, (registrations[2]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 next should point to registration 1", &registrations[1], (registrations[2]).nextRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 prev should point to registration 2", &registrations[2], (registrations[1]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 next should point to registration 0", &registrations[0], (registrations[1]).nextRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 prev should point to registration 1", &registrations[1], (registrations[0]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 next should be NULL", NULL, (registrations[0]).nextRegistration_p);

    println(__FUNCTION__ ": Removing registration 2 (head of list)");

    int removeRC = removeBboargeFromChain(&anchor, &(registrations[2]));

    CuAssertIntEquals_Msg(tc, "Return code from removeBboargeFromChain should be 0", 0, removeRC);
    CuAssertPtrEquals_Msg(tc, "BBOASHR head should point to registration 1", &registrations[1], anchor.firstRge_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 prev should be NULL", NULL, (registrations[1]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 next should point to registration 0", &registrations[0], (registrations[1]).nextRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 prev should point to registration 1", &registrations[1], (registrations[0]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 next should be NULL", NULL, (registrations[0]).nextRegistration_p);

    println(__FUNCTION__ ": Adding registration 2 back to list");

    addBboargeToChain(&anchor, &(registrations[2]));

    CuAssertPtrEquals_Msg(tc, "BBOASHR head should point to registration 2", &registrations[2], anchor.firstRge_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 prev should be NULL", NULL, (registrations[2]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 next should point to registration 1", &registrations[1], (registrations[2]).nextRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 prev should point to registration 2", &registrations[2], (registrations[1]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 1 next should point to registration 0", &registrations[0], (registrations[1]).nextRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 prev should point to registration 1", &registrations[1], (registrations[0]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 next should be NULL", NULL, (registrations[0]).nextRegistration_p);

    println(__FUNCTION__ ": Removing registration 1 (middle of list)");

    removeRC = removeBboargeFromChain(&anchor, &(registrations[1]));

    CuAssertIntEquals_Msg(tc, "Return code from removeBboargeFromChain should be 0", 0, removeRC);
    CuAssertPtrEquals_Msg(tc, "BBOASHR head should point to registration 2", &registrations[2], anchor.firstRge_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 prev should be NULL", NULL, (registrations[2]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 next should point to registration 0", &registrations[0], (registrations[2]).nextRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 prev should point to registration 2", &registrations[2], (registrations[0]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 0 next should be NULL", NULL, (registrations[0]).nextRegistration_p);

    println(__FUNCTION__ ": Removing registration 0 (end of list)");

    removeRC = removeBboargeFromChain(&anchor, &(registrations[0]));

    CuAssertIntEquals_Msg(tc, "Return code from removeBboargeFromChain should be 0", 0, removeRC);
    CuAssertPtrEquals_Msg(tc, "BBOASHR head should point to registration 2", &registrations[2], anchor.firstRge_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 prev should be NULL", NULL, (registrations[2]).previousRegistration_p);
    CuAssertPtrEquals_Msg(tc, "BBOASHR registration 2 next should be NULL", NULL, (registrations[2]).nextRegistration_p);

    println(__FUNCTION__ ": Removing registration 2 (only entry)");

    removeRC = removeBboargeFromChain(&anchor, &(registrations[2]));

    CuAssertIntEquals_Msg(tc, "Return code from removeBboargeFromChain should be 0", 0, removeRC);
    CuAssertPtrEquals_Msg(tc, "BBOASHR head should be NULL", NULL, anchor.firstRge_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Find a registration element by a given name.
 */
void test_findClientRegistrationByName(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create registration with name
    unsigned char registrationName[16] = "myRegistration  ";
    struct wolaRegistration reg;
    struct wolaRegistration* regPtr = &reg;
    memset(regPtr, 0, sizeof(struct wolaRegistration)); //clear memory
    memcpy(regPtr->registrationName, registrationName, sizeof(registrationName));

    println(__FUNCTION__ ": created registration: %s", regPtr->registrationName);

    //Create Shared anchor
    struct wolaSharedMemoryAnchor sharedMemAnchor;
    struct wolaSharedMemoryAnchor* sharedMemAnchorPtr = &sharedMemAnchor;
    memset(sharedMemAnchorPtr, 0, sizeof(struct wolaSharedMemoryAnchor)); //clear memory
    sharedMemAnchorPtr->firstRge_p = regPtr;

    struct wolaRegistration* resultPtr = findClientRegistrationByName("myRegistrationxx", sharedMemAnchorPtr);
    CuAssertPtrIsNull(tc, resultPtr);

    resultPtr = findClientRegistrationByName("myRegistration", sharedMemAnchorPtr);
    CuAssertPtrIsNull(tc, resultPtr);

    resultPtr = findClientRegistrationByName("myRegistration  ", sharedMemAnchorPtr);
    CuAssertPtrEquals(tc, regPtr, resultPtr);

    println(__FUNCTION__ ": exit");
}

/**
 * Find a registration element by a false name. This ensures that
 * NULL is correctly returned
 */
void test_findClientRegistrationByNameNotFound(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create registration with name
    unsigned char registrationName[16] = "myRegistration";
    unsigned char registrationNameBad[16] = "notRegistration";
    struct wolaRegistration reg;
    struct wolaRegistration* regPtr = &reg;
    memset(regPtr, 0, sizeof(struct wolaRegistration)); //clear memory
    memcpy(regPtr->registrationName, registrationName, sizeof(registrationName));

    println(__FUNCTION__ ": created registration: %s", regPtr->registrationName);


    //Create Shared anchor
    struct wolaSharedMemoryAnchor sharedMemAnchor;
    struct wolaSharedMemoryAnchor* sharedMemAnchorPtr = &sharedMemAnchor;
    memset(sharedMemAnchorPtr, 0, sizeof(struct wolaSharedMemoryAnchor)); //clear memory
    sharedMemAnchorPtr->firstRge_p = regPtr;

    struct wolaRegistration* resultPtr = findClientRegistrationByName(registrationNameBad, sharedMemAnchorPtr);

    if(resultPtr == NULL){
        println(__FUNCTION__ ": lookup returned null as expected");
    }else{
        println(__FUNCTION__ ": lookup failed. Returned non-null element");
    }

    CuAssertPtrEquals(tc, NULL, resultPtr);

    println(__FUNCTION__ ": exit");
}


/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_wola_registration_test_suite() {

    CuSuite* suite = CuSuiteNew("server_wola_registration_test");

    SUITE_ADD_TEST(suite, test_wolaRgeSize);
    SUITE_ADD_TEST(suite, test_wolaRgeListFunctions);
    SUITE_ADD_TEST(suite, test_findClientRegistrationByName);
    SUITE_ADD_TEST(suite, test_findClientRegistrationByNameNotFound);

    return suite;
}


