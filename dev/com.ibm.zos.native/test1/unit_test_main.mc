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

/**
 * The main module for running native authorized unit tests.
 *
 * Since we're testing authorized code and services, the main must be kicked
 * off in key2.  The only way I know how to do that is via BPXBATA2.
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
 * !! LOOK !! INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 * Search for "LOOK" to find the spots that need to be updated in this part.
 *
 */

#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

#include "../include/mvs_utils.h"
#include "../include/mvs_wto.h"
#include "../include/mvs_user_token_manager.h"
#include "include/CuTest.h"

#include "../include/gen/ihapsa.h"
#include "../include/gen/ihastcb.h"
#include "../include/gen/ikjtcb.h"


// ******************************************************* //
// !! LOOK !!  DECLARE YOUR SUITE'S GETTER METHOD HERE 
// ******************************************************* //
extern CuSuite* CuTest_test_suite(void);
extern CuSuite* CuTest_test_string_suite(void);
extern CuSuite* server_wola_services_test_suite(void);
extern CuSuite* server_wola_registration_test_suite(void);
extern CuSuite* mvs_cell_pool_services_test_suite(void);
extern CuSuite* mvs_plo_test_suite(void);
extern CuSuite* mvs_stimerm_test_suite(void);
extern CuSuite* server_wola_service_queues_test_suite(void);
extern CuSuite* server_wola_shared_memory_anchor_test_suite(void);
extern CuSuite* server_wola_client_test_suite(void);
extern CuSuite* server_wola_message_test_suite(void);
extern CuSuite* server_wola_connection_pool_test_suite(void);
extern CuSuite* server_local_comm_queue_test_suite(void);
extern CuSuite* server_wola_client_get_context_test_suite(void);
extern CuSuite* security_saf_authorization_test_suite(void);
extern CuSuite* security_saf_authentication_test_suite(void);

// ******************************************************* //
// !! LOOK !!  DON'T FORGET TO UPDATE THE COUNT! 
// ******************************************************* //
#define NUM_SUITES 16

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
    suites[2] = server_wola_services_test_suite();
    suites[3] = server_wola_registration_test_suite();
    suites[4] = mvs_cell_pool_services_test_suite();
    suites[5] = mvs_plo_test_suite();
    suites[6] = server_wola_shared_memory_anchor_test_suite();
    suites[7] = server_wola_service_queues_test_suite();
    suites[8] = server_wola_client_test_suite();
    suites[9] = server_wola_message_test_suite();
    suites[10] = mvs_stimerm_test_suite();
    suites[11] = server_wola_connection_pool_test_suite();    
    suites[12] = server_local_comm_queue_test_suite();
    suites[13] = server_wola_client_get_context_test_suite();
    suites[14] = security_saf_authorization_test_suite();
    suites[15] = security_saf_authentication_test_suite();

    // ******************************************************* //
    // !! LOOK !!  ADD YOUR SUITE'S GETTER TO THIS LIST 
    // ******************************************************* //

    return suites;
}

/**
 *
 */
#pragma linkage(BPX4WRT,OS_NOSTACK)
void BPX4WRT(int filedes, void* buffer, int alet, int bufsize, int* retval, int* retcode, int* rsnval);

/**
 * Print a string to the joblog (stdout) via BPX4WRT.
 *
 * @param fd - the output fd (1==stdout, 2==stderr)
 * @param message_p - the null-terminated string to print
 */
static void printString(int fd, const char* message_p) {
    int retval = 0;
    int retcode = 0;
    int rsnval = 0;

    BPX4WRT(fd, &message_p, 0, strlen(message_p), &retval, &retcode, &rsnval);
}

/**
 * Print a formatted string to sysout, followed by a newline.
 */
void println(const char* str, ...) {

    va_list argp;
    char msg[1024];
    va_start(argp, str);
    vsnprintf(msg, 1024, str, argp);
    va_end(argp);

    printString(1, msg);
    printString(1, "\n");
}

/**
 * Print a formatted string to syserr, followed by a newline.
 */
static void perrorln(const char* str, ...) {

    va_list argp;
    char msg[1024];
    va_start(argp, str);
    vsnprintf(msg, 1024, str, argp);
    va_end(argp);

    printString(2, msg);
    printString(2, "\n");
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

    // TODO: setup estae to handle conditions
    
    // Run it.
    CuSuiteRun(suite);

    // Print the output.
    CuString *output = CuStringNew();
    CuSuiteBuildResults(suite, output);
    println(output->buffer);
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
 * @return 0 if all is well; non-zero if we're not in the proper state
 *         (either not in key 2 or not in supervisor state).
 */
int verifyStartingState(void) {
    bbgz_psw current_PSW;
    memset(&current_PSW, 0x00, sizeof(current_PSW));
    extractPSW(&current_PSW);

    if (current_PSW.key != 2) {
        println(__FUNCTION__ "ERROR: this program must be started in key 2!");
        return -1;
    } 

    if (current_PSW.pbm_state == TRUE) {
        println(__FUNCTION__ "ERROR: this program could not switch to supervisor state!");
        return -2;
    }

    return 0;
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
#pragma prolog(main, "SAUTHPRL")
#pragma epilog(main, "SAUTHEPL")
int main(int argc, char** argv) {

    struct __csysenv_s mysysenv;

    // Get into supervisor state.  The unit test process runs in key2 supervisor.
    switchToSupervisorState();

    // Set up our metal C environment.  We are single threaded 
    // so we will use the heap provided by the metal C runtime library.
    initenv(&mysysenv, getAddressSpaceSupervisorStateUserToken(), NULL);

    // Verify the runtime state.
    if ( verifyStartingState() ) {
        return -1;
    }

    CuSuite * suites[NUM_SUITES];
    loadSuites(suites);

    // Parse the args for suite name/test name
    struct UnitTestArgs unitTestArgs;
    parseArgs(argc, argv, &unitTestArgs);

    // Run the tests.
    int rc = runTests(suites, unitTestArgs.suite_name, unitTestArgs.test_name);
    println(__FUNCTION__ ": Exiting. Total Failures: %d", rc);
    perrorln("Test run complete. Total Failures: %d", rc);

    // Terminate the metal C environment.
    termenv();

    return rc;
}

#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")
#pragma insert_asm(" IEANTASM")
