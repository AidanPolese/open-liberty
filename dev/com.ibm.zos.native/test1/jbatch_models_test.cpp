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
#include <errno.h>
#include <stdlib.h>
#include <string.h>

#include "../include/jbatch_json.h"
#include "../include/jbatch_models.h"
#include "../include/jbatch_utils.h"

#include "include/CuTest.h"

extern void println(const char *, ...);


/**
 * ...
 */
extern int jnu_parseCommandLineArgs(int argc, char ** argv, cJSON * args) ;
extern int jnu_getPollingInterval(cJSON * args);
void test_getPollingInterval(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    char *argv[] = { strdup("batchManagerZos"),
                     strdup("submit"),
                     strdup("--batchManager=WOLA1 WOLAAAA2 WOLA3"),
                     strdup("--applicationName=MyApp"),
                     strdup("--moduleName=MyAppModule.war"),
                     strdup("--jobXMLName=SimpleBatchJob"),
                     strdup("--wait"),
                     strdup("--pollingInterval_s=31"),
    };

	cJSON * args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);

    int rc = jnu_parseCommandLineArgs(8, argv, args);
    CuAssertIntEquals(tc, 0, rc);

    CuAssertIntEquals(tc, 31, jnu_getPollingInterval(args));

    cJSON_ReplaceItemInObject(args,"pollingInterval_s", cJSON_CreateString("n-a-n") );
    CuAssertIntEquals(tc, 30, jnu_getPollingInterval(args));
}

/**
 * ... 
 */
extern int jnu_getBatchStatusReturnCode( const char * batchStatus ) ;
void test_getBatchStatusReturnCode(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertIntEquals(tc, 30, jnu_getBatchStatusReturnCode("STARTING") );
    CuAssertIntEquals(tc, 31, jnu_getBatchStatusReturnCode("STARTED") );
    CuAssertIntEquals(tc, 32, jnu_getBatchStatusReturnCode("STOPPING") );
    CuAssertIntEquals(tc, 33, jnu_getBatchStatusReturnCode("STOPPED") );
    CuAssertIntEquals(tc, 34, jnu_getBatchStatusReturnCode("FAILED") );
    CuAssertIntEquals(tc, 35, jnu_getBatchStatusReturnCode("COMPLETED") );
    CuAssertIntEquals(tc, 36, jnu_getBatchStatusReturnCode("ABANDONED") );
    CuAssertIntEquals(tc, -2, jnu_getBatchStatusReturnCode("") );
    CuAssertIntEquals(tc, -2, jnu_getBatchStatusReturnCode("blah") );
    CuAssertIntEquals(tc, -1, jnu_getBatchStatusReturnCode(NULL) );
}

/**
 *
 */
extern int jnu_isDone( const char * batchStatus ) ;
void test_isDone(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    CuAssertIntEquals(tc, 1, jnu_isDone("STOPPED") );
    CuAssertIntEquals(tc, 1, jnu_isDone("FAILED") );
    CuAssertIntEquals(tc, 1, jnu_isDone("COMPLETED") );
    CuAssertIntEquals(tc, 1, jnu_isDone("ABANDONED") );
    CuAssertIntEquals(tc, 0, jnu_isDone("STARTING") );
    CuAssertIntEquals(tc, 0, jnu_isDone("STARTED") );
    CuAssertIntEquals(tc, 0, jnu_isDone("STOPPING") );
    CuAssertIntEquals(tc, 0, jnu_isDone("") );
    CuAssertIntEquals(tc, 0, jnu_isDone(NULL) );
}

/**
 *
 */
extern int jnu_parseExitStatusReturnCode(const char * exitStatus);
void test_parseExitStatusReturnCode(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertIntEquals(tc, 0, jnu_parseExitStatusReturnCode("0") );
    CuAssertIntEquals(tc, 8, jnu_parseExitStatusReturnCode("8") );
    CuAssertIntEquals(tc, 0, jnu_parseExitStatusReturnCode("0: blah blah") );
    CuAssertIntEquals(tc, 11, jnu_parseExitStatusReturnCode("11: blah blah") );
    CuAssertIntEquals(tc, 12, jnu_parseExitStatusReturnCode("12blah blah") );
    CuAssertIntEquals(tc, 30, jnu_parseExitStatusReturnCode("STARTING") );
    CuAssertIntEquals(tc, 31, jnu_parseExitStatusReturnCode("STARTED") );
    CuAssertIntEquals(tc, 32, jnu_parseExitStatusReturnCode("STOPPING") );
    CuAssertIntEquals(tc, 33, jnu_parseExitStatusReturnCode("STOPPED") );
    CuAssertIntEquals(tc, 34, jnu_parseExitStatusReturnCode("FAILED") );
    CuAssertIntEquals(tc, 35, jnu_parseExitStatusReturnCode("COMPLETED") );
    CuAssertIntEquals(tc, 36, jnu_parseExitStatusReturnCode("ABANDONED") );
    CuAssertIntEquals(tc, -2, jnu_parseExitStatusReturnCode("") );
    CuAssertIntEquals(tc, -1, jnu_parseExitStatusReturnCode(NULL) );
}

/**
 * ...
 */
void test_getOrCreateJobParameters(CuTest * tc) {
    println(__FUNCTION__ ": entry");

	cJSON * args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);
    CuAssertPtrIsNull(tc, cJSON_GetObjectItem(args, "jobParameters") );

    cJSON * jobParams = jnu_getOrCreateJobParameters(args);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), jobParams);
    CuAssertPtrNotNull(tc, cJSON_GetObjectItem(args, "jobParameters") );

    cJSON * jobParams2 = jnu_getOrCreateJobParameters(args);
    CuAssertPtrEquals(tc, jobParams, jobParams2);

	cJSON * args2 = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args2);
    cJSON_AddItemToObject(  args2, "jobParameters", cJSON_CreateObject() );
    cJSON * jobParams3 = cJSON_GetObjectItem(args2, "jobParameters") ;
    CuAssertPtrNotNull(tc, jobParams3 );

    cJSON * jobParams4 = jnu_getOrCreateJobParameters(args2);
    CuAssertPtrEquals(tc, jobParams3, jobParams4);

    cJSON_Delete_ns(args);
    cJSON_Delete_ns(args2);
}


/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * jbatch_models_test_suite() {

    CuSuite* suite = CuSuiteNew("jbatch_models_test");

    SUITE_ADD_TEST(suite, test_getPollingInterval);
    SUITE_ADD_TEST(suite, test_getBatchStatusReturnCode);
    SUITE_ADD_TEST(suite, test_isDone);
    SUITE_ADD_TEST(suite, test_parseExitStatusReturnCode);
    SUITE_ADD_TEST(suite, test_getOrCreateJobParameters);

    return suite;
}




