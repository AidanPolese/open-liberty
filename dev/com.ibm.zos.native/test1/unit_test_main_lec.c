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

/**
 * The main module for running unit tests in native LE C.
 *
 * Note: this is a copy of unit_test_main.mc, which is used for running unit
 * tests against metal-C code.  metal-C tests run in key 2 whereas these LE
 * C tests run in key 8.
 *
 * By default, this module will run all CuSuites it's aware of.  The CuSuites
 * it's aware of are loaded under loadSuites(). 
 *
 * For running a single suite, specifiy the suite name as the first argument.
 *
 * main() returns 0 if all tests pass. Otherwise it returns the number of tests
 * that failed.
 *
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

#include "include/CuTest.h"


// ************************************************************ //
// !! LOOK (1 of 3) !!  DECLARE YOUR SUITE'S GETTER METHOD HERE 
// ************************************************************ //
extern CuSuite* CuTest_test_suite(void);
extern CuSuite* CuTest_test_string_suite(void);
extern CuSuite* server_util_tiot_test_suite(void);

// ******************************************************* //
// !! LOOK (2 of 3) !!  DON'T FORGET TO UPDATE THE COUNT! 
// ******************************************************* //
#define NUM_SUITES 3

/**
 * Load up all the suites.
 *
 * @param suites - The array of CuSuite pointers to be populated
 * 
 * @return suites
 */
CuSuite ** loadSuites(CuSuite ** suites) {

    suites[0] = CuTest_test_suite();
    suites[1] = CuTest_test_string_suite();
    suites[2] = server_util_tiot_test_suite();

    // *********************************************************** //
    // !! LOOK (3 of 3) !!  ADD YOUR SUITE'S GETTER TO THIS LIST 
    // *********************************************************** //

    return suites;
}

/**
 * Print a formatted string to sysout, followed by a newline.
 */
void println(const char* str, ...) {

    va_list argp;
    va_start(argp, str);
    vfprintf(stdout, str, argp);
    va_end(argp);
    fprintf(stdout,"\n");
}

/**
 * Print a formatted string to syserr, followed by a newline.
 */
static void perrorln(const char* str, ...) {

    va_list argp;
    va_start(argp, str);
    vfprintf(stderr, str, argp);
    va_end(argp);
    fprintf(stderr,"\n");
}

/**
 *
 * @param suites - the array of suites
 * @param name - the name of the suite to find
 *
 * @return The suite with the given name.
 */
CuSuite * findSuite(CuSuite ** suites, const char * name) {
    for (int i = 0 ; i < NUM_SUITES; ++i) {
        CuSuite * suite = suites[i];
        if (!strcmp(name, suite->name)) {
            return suite;
        }
    }
    return NULL;
}

/**
 * Run a single suite and print out its results.
 *
 * @param suite - The suite to run
 *
 * @return the number of tests that failed.
 */
int runSuite(CuSuite * suite) {

    if (suite == NULL) {
        return 1;
    }

    // Run it.
    CuSuiteRun(suite);

    // Print the output.
    CuString *output = CuStringNew();
    CuSuiteBuildResults(suite, output);
    // println(output->buffer);
    perrorln(output->buffer);

    return suite->failCount;
}

/**
 * @param suites - The array of suites
 *
 * @return The total number of failed tests.
 */
int runAllSuites(CuSuite ** suites) {

    int rc = 0;

    for (int i = 0 ; i < NUM_SUITES; ++i) {
        // Run the suite.
        rc += runSuite( suites[i] );
    }

    return rc; 
}

/**
 *
 * @param suite_name - The test suite to run. If null, all tests are run.
 * @param test_name - The individual test within the suite to run. If null, 
 *        all tests in the suite are run.
 *
 * @return the number of tests that failed.
 */
int runTests(CuSuite ** suites, const char * suite_name, const char * test_name) {

    println(__FUNCTION__ ": suite_name: %s, test_name: %s", suite_name, test_name);
    if (suite_name == NULL || strlen(suite_name) == 0) {
        // Run all tests.
        return runAllSuites(suites);
        
    } else if (test_name == NULL || strlen(test_name) == 0) {
        // Run all tests in the given suite.
        CuSuite * suite = findSuite(suites, suite_name);

        if (suite != NULL) {
            return runSuite( suite );
        } else {
            println(__FUNCTION__ ": ERROR: suite not found: %s", suite_name);
        }

    } else {
        // TODO: Run a single test.
        println(__FUNCTION__ ": ERROR: running individual tests is not supported yet: %s", test_name);
    }

    return 1;
}

/**
 * For parsing the args to main().
 */
struct UnitTestArgs {
    char * suite_name;
    char * test_name;
};

/**
 * Parse the args to main().
 */
struct UnitTestArgs * parseArgs(int argc, char ** argv, struct UnitTestArgs * args) {

    // Skip arg 0 (the program name)
    if (argc >= 2) {
        args->suite_name = argv[1];
        println(__FUNCTION__ ": suite_name: #%s#", args->suite_name);
    }
    if (argc >= 3) {
        args->test_name = argv[2];
        println(__FUNCTION__ ": test_name: #%s#", args->test_name);
    }

    return args;
}

/**
 * The unit test main.
 *
 * @param argc The number of arguments as provided by the C runtime.
 * @param argv A pointer to the argument list as provided by the C runtime.
 *
 * @return The number of tests that failed.
 */
int main(int argc, char** argv) {

    CuSuite * suites[NUM_SUITES];
    loadSuites(suites);

    // Parse the args for suite name/test name
    struct UnitTestArgs unitTestArgs;
    parseArgs(argc, argv, &unitTestArgs);

    // Run the tests.
    int rc = runTests(suites, unitTestArgs.suite_name, unitTestArgs.test_name);
    println(__FUNCTION__ ": Exiting. Total Failures: %d", rc);
    perrorln("Test run complete. Total Failures: %d", rc);

    return rc;
}

