/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
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
#include "../include/security_saf_authentication.h"
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


/**
 * Forward declares for functions not already declared in headers.
 */
extern int saf_isNull( const char * p ) ;
extern int saf_isWithinBounds( int x, int lowerBound, int upperBound ) ;
extern int saf_isOutOfBounds( int x, int lowerBound, int upperBound ) ;
extern int safRacrouteExtract_areParmsValid(SafRacrouteExtractParms * parms) ;

/**
 * Test.
 */
void test_saf_isNull(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertTrue(tc, saf_isNull( NULL ));
    CuAssertFalse(tc, saf_isNull( "not null") );
    CuAssertFalse(tc, saf_isNull("") );
}

/**
 * Test.
 */
void test_saf_isWithinBounds(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertTrue(tc, saf_isWithinBounds( 0, 0, 0));
    CuAssertTrue(tc, saf_isWithinBounds( 0, -5, 5));
    CuAssertTrue(tc, saf_isWithinBounds( 0, 0, 5));
    CuAssertTrue(tc, saf_isWithinBounds( 5, 0, 5));
    CuAssertFalse(tc, saf_isWithinBounds( 6, 0, 5));
    CuAssertFalse(tc, saf_isWithinBounds( -1, 0, 5));
    CuAssertFalse(tc, saf_isWithinBounds( -1, 0, 0));
}

/**
 * Test.
 */
void test_saf_isOutOfBounds(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertTrue(tc, saf_isOutOfBounds( 1, 0, 0));
    CuAssertTrue(tc, saf_isOutOfBounds( -1, 0, 0));
    CuAssertTrue(tc, saf_isOutOfBounds( 6, 0, 5));
    CuAssertTrue(tc, saf_isOutOfBounds( -6, 0, 5));
    CuAssertFalse(tc, saf_isOutOfBounds( 0, 0, 5));
    CuAssertFalse(tc, saf_isOutOfBounds( 5, 0, 5));
    CuAssertFalse(tc, saf_isOutOfBounds( 0, 0, 0));
    CuAssertFalse(tc, saf_isOutOfBounds( -1, -5, 0));
}

/**
 * Test.
 */
void test_safRacrouteExtract_areParmsValid(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    SafRacrouteExtractParms goodParms = { 
            .className          = "c",
            .classNameLen       = 1,
            .profileName        = "p",
            .profileNameLen     = 1,
            .fieldName          = "f",
            .fieldNameLen       = 1,
            .racfExtractResults = NULL,
            .safServiceResult   = NULL
         };

    SafRacrouteExtractParms parms ;
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );

    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.className = NULL;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );   
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.classNameLen = 0;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );  
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.classNameLen = SAF_CLASSNAME_LENGTH + 1;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) ); 
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.profileName = NULL;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.profileNameLen = -1;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.profileNameLen = SAF_PROFILENAME_MAX_LENGTH + 1;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );  
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.fieldName = NULL;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );   
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.fieldNameLen = SAF_FIELD_LENGTH + 1;
    CuAssertFalse(tc, safRacrouteExtract_areParmsValid(&parms) );

    // reset
    memcpy(&parms, &goodParms, sizeof(SafRacrouteExtractParms) );   
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );

    parms.classNameLen = SAF_CLASSNAME_LENGTH;
    parms.profileNameLen = SAF_PROFILENAME_MAX_LENGTH ;
    parms.fieldNameLen = SAF_FIELD_LENGTH;
    CuAssertTrue(tc, safRacrouteExtract_areParmsValid(&parms) );
}

/**
 * Test.
 */
void test_safRacrouteExtract_safExtract(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    typedef struct {
        char                    className[SAF_CLASSNAME_LENGTH + 1];                //!< Input - The CLASS of the resource profile.
        char                    fieldName[SAF_FIELD_LENGTH + 1];                    //!< Input - The field to extract
        SAFServiceResult        safServiceResult;                                   //!< Output - SAF RC/RSN
        racf_extract_results    racfExtractResults;                                 //!< Output - extracted data
        char                    profileName[SAF_PROFILENAME_MAX_LENGTH + 1];        //!< Input - The resource profile name (at the bottom cuz it's big)
    } SafRacrouteExtractParmsKey2;

    SafRacrouteExtractParmsKey2 key2Parms;

    // Initialize 
    memset(&key2Parms, 0, sizeof(SafRacrouteExtractParmsKey2));
    key2Parms.racfExtractResults.length = RACF_EXTRACT_RESULTS_MAX_LENGTH;  // init with max capacity of the data buffer.

    // Copy parms from key 8.
    memcpy(key2Parms.className, "REALM", 5);
    memcpy(key2Parms.profileName, "SAFDFLT", 7);
    memcpy(key2Parms.fieldName, "APPLDATA", 8);

    int rc = safExtract(&key2Parms.safServiceResult.safResults,
                        NULL,
                        key2Parms.className,
                        key2Parms.profileName,
                        key2Parms.fieldName,
                        &key2Parms.racfExtractResults);

    CuAssertIntEquals(tc, 0, rc);
    CuAssertIntEquals(tc, 12, key2Parms.racfExtractResults.length);
    CuAssertStrEquals(tc, "WAS.REALMNME", key2Parms.racfExtractResults.data);

    CuAssertIntEquals(tc, 0, key2Parms.safServiceResult.safResults.safReturnCode );
    CuAssertIntEquals(tc, 0, key2Parms.safServiceResult.safResults.racfReturnCode );
    CuAssertIntEquals(tc, 0, key2Parms.safServiceResult.safResults.racfReasonCode );
}


/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * security_saf_authentication_test_suite() {

    CuSuite* suite = CuSuiteNew("security_saf_authentication_test");

    SUITE_ADD_TEST(suite, test_saf_isNull);
    SUITE_ADD_TEST(suite, test_saf_isWithinBounds);
    SUITE_ADD_TEST(suite, test_saf_isOutOfBounds);
    SUITE_ADD_TEST(suite, test_safRacrouteExtract_areParmsValid);
    SUITE_ADD_TEST(suite, test_safRacrouteExtract_safExtract);

    return suite;
}


