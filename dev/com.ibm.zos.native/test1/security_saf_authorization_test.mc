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
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "../include/gen/bboapc1p.h"
#include "../include/gen/ihaacee.h"
#include "../include/gen/ihapsa.h"
#include "../include/gen/ikjtcb.h"
#include "../include/common_defines.h"
#include "../include/mvs_utils.h"
#include "../include/security_saf_acee.h"
#include "../include/security_saf_authorization.h"

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


#define TLS_DELETE_THREAD_SEC 2
#define TLS_TASK_ACEE 3

/**
 * Forward declares for functions not already declared in headers.
 */
// -rx- acee * __ptr32 getAceeFromTCB(void) ;
// -rx- int invokeBPX4TLS (int functionCode, SAFServiceResult* serviceResult_p) ;

int populateCommonIRRSIA00Parms_sk(IRRSIA00Parms * __ptr32 irrsia00Parms,
                                   char* username,
                                   int usernameLen,
                                   char* password,
                                   int passwordLen,
                                   char* auditString,
                                   int auditStringLen,
                                   char* applName,
                                   int applNameLen,
                                   RACO* inRaco,
                                   acee* __ptr32 inAcee,
                                   int sourceKey) ;


/**
 * Test.
 */
void test_checkAuthorization(CuTest * tc) {

    saf_results results;
    memset(&results, 0, sizeof(saf_results));
    char * racf_entity_name = "BBG.AUTHMOD.BBGZSAFM.LOCALCOM";
    int rc = checkAuthorization(&results,
                                1,                          // Suppress messages
                                ASIS,                       // Log option
                                NULL,                       // Requestor
                                NULL,                       // ACEE
                                READ,                       // Access level
                                NULL,                       // Application name  
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    println(__FUNCTION__ ": checkAuthorization(%s): %d / x%x / x%x / x%x ", 
            racf_entity_name, 
            rc, 
            results.safReturnCode,
            results.racfReturnCode,
            results.racfReasonCode);

    // MSTONE1 should have read access to this profile.
    CuAssertIntEquals(tc, 0, rc);
    CuAssertIntEquals(tc, 0, results.safReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReasonCode);

    // Test against a profile that doesn't exist.
    memset(&results, 0, sizeof(saf_results));
    racf_entity_name = "bbg.authmod.bbgzsafm.not.exist";
    rc = checkAuthorization(&results,
                                1,                          // Suppress messages
                                ASIS,                       // Log option
                                NULL,                       // Requestor
                                NULL,                       // ACEE
                                READ,                       // Access level
                                NULL,                       // Application name  
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    println(__FUNCTION__ ": checkAuthorization(%s): %d / x%x / x%x / x%x ", 
            racf_entity_name, 
            rc, 
            results.safReturnCode,
            results.racfReturnCode,
            results.racfReasonCode);

    // Expect failures because the profile doesn't exist.
    CuAssertIntEquals(tc, 0, rc);
    CuAssertIntEquals(tc, 8, results.safReturnCode);
    CuAssertIntEquals(tc, 8, results.racfReturnCode);
    CuAssertIntEquals(tc, 512, results.racfReasonCode);
}

/**
 * Test.
 */
void test_checkAuthorizationFast(CuTest * tc) {

    saf_results results;
    memset(&results, 0, sizeof(saf_results));
    char * racf_entity_name = "BBG.AUTHMOD.BBGZSAFM.LOCALCOM";
    int rc = checkAuthorizationFast(&results,
                                1,                          // Suppress messages
                                ASIS,                       // Log option
                                NULL,                       // Requestor
                                NULL,                       // RACO
                                NULL,                       // ACEE
                                READ,                       // Access level
                                NULL,                       // Application name  
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    println(__FUNCTION__ ": checkAuthorizationFast(%s): %d / x%x / x%x / x%x ", 
            racf_entity_name, 
            rc, 
            results.safReturnCode,
            results.racfReturnCode,
            results.racfReasonCode);

    // MSTONE1 should have read access to this profile.
    CuAssertIntEquals(tc, 0, rc);
    CuAssertIntEquals(tc, 0, results.safReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReasonCode);

    // Test against a profile that doesn't exist.
    memset(&results, 0, sizeof(saf_results));
    racf_entity_name = "bbg.authmod.bbgzsafm.not.exist";
    rc = checkAuthorizationFast(&results,
                                1,                          // Suppress messages
                                ASIS,                       // Log option
                                NULL,                       // Requestor
                                NULL,                       // RACO
                                NULL,                       // ACEE
                                READ,                       // Access level
                                NULL,                       // Application name  
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    println(__FUNCTION__ ": checkAuthorizationFast(%s): %d / x%x / x%x / x%x ", 
            racf_entity_name, 
            rc, 
            results.safReturnCode,
            results.racfReturnCode,
            results.racfReasonCode);

    // Expect failures because the profile doesn't exist.
    CuAssertIntEquals(tc, 0, rc);
    CuAssertIntEquals(tc, 8, results.safReturnCode);
    CuAssertIntEquals(tc, 8, results.racfReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReasonCode);
}

/**
 *
 */
void test_checkAuthorizationWithAcee(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();
    int rc = populateCommonIRRSIA00Parms_sk(irrsia00Parms,
                                            "MSTONE1",
                                            strlen("MSTONE1"),
                                            "M00NTEST",
                                            strlen("M00NTEST"),
                                            NULL,
                                            0,
                                            NULL,
                                            0,
                                            NULL,
                                            NULL,
                                            2);     // source key
    CuAssertIntEquals(tc, 0, rc);

    // Setup saf return area
    SAFServiceResult safServiceResult;
    memset(&safServiceResult, 0, sizeof(SAFServiceResult));

    // invoke initACEE
    acee* __ptr32 acee_p = (acee * __ptr32) createACEE(irrsia00Parms, &safServiceResult);
    free(irrsia00Parms);

    CuAssertPtrNotNull(tc, acee_p);

    saf_results results;
    memset(&results, 0, sizeof(saf_results));
    char * racf_entity_name = "BBG.AUTHMOD.BBGZSAFM.LOCALCOM";
    rc = checkAuthorization(&results,
                                1,                          // Suppress messages
                                ASIS,                       // Log option
                                NULL,                       // Requestor
                                acee_p,                     // ACEE
                                READ,                       // Access level
                                NULL,                       // Application name  
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    println(__FUNCTION__ ": checkAuthorization(%s): %d / x%x / x%x / x%x ", 
            racf_entity_name, 
            rc, 
            results.safReturnCode,
            results.racfReturnCode,
            results.racfReasonCode);

    // MSTONE1 should have read access to this profile.
    CuAssertIntEquals(tc, 0, rc);
    CuAssertIntEquals(tc, 0, results.safReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReasonCode);

    println(__FUNCTION__ ": exit");
}


/**
 *
 */
void test_checkAuthorizationFastWithAcee(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();
    int rc = populateCommonIRRSIA00Parms_sk(irrsia00Parms,
                                            "MSTONE1",
                                            strlen("MSTONE1"),
                                            "M00NTEST",
                                            strlen("M00NTEST"),
                                            NULL,
                                            0,
                                            NULL,
                                            0,
                                            NULL,
                                            NULL,
                                            2);     // source key
    CuAssertIntEquals(tc, 0, rc);

    // Setup saf return area
    SAFServiceResult safServiceResult;
    memset(&safServiceResult, 0, sizeof(SAFServiceResult));

    // invoke initACEE
    acee* __ptr32 acee_p = (acee * __ptr32) createACEE(irrsia00Parms, &safServiceResult);
    free(irrsia00Parms);

    CuAssertPtrNotNull(tc, acee_p);

    saf_results results;
    memset(&results, 0, sizeof(saf_results));
    char * racf_entity_name = "BBG.AUTHMOD.BBGZSAFM.LOCALCOM";
    rc = checkAuthorizationFast(&results,
                                1,                          // Suppress messages
                                ASIS,                       // Log option
                                NULL,                       // Requestor
                                NULL,                       // RACO
                                acee_p,                     // ACEE
                                READ,                       // Access level
                                NULL,                       // Application name  
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    println(__FUNCTION__ ": checkAuthorization(%s): %d / x%x / x%x / x%x ", 
            racf_entity_name, 
            rc, 
            results.safReturnCode,
            results.racfReturnCode,
            results.racfReasonCode);

    // MSTONE1 should have read access to this profile.
    CuAssertIntEquals(tc, 0, rc);
    CuAssertIntEquals(tc, 0, results.safReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReturnCode);
    CuAssertIntEquals(tc, 0, results.racfReasonCode);

    println(__FUNCTION__ ": exit");
}



/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * security_saf_authorization_test_suite() {

    CuSuite* suite = CuSuiteNew("security_saf_authorization_test");

    SUITE_ADD_TEST(suite, test_checkAuthorization);
    SUITE_ADD_TEST(suite, test_checkAuthorizationFast);
    SUITE_ADD_TEST(suite, test_checkAuthorizationWithAcee);
    SUITE_ADD_TEST(suite, test_checkAuthorizationFastWithAcee);

    return suite;
}


