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
#include <stdio.h>

#include "../include/jbatch_json.h"
#include "../include/jbatch_native_utility.h"
#include "../include/jbatch_utils.h"

#include "include/CuTest.h"

extern void println(const char *, ...);


/**
 * ...
 */
extern int jnu_parseCommandLineArgs(int argc, char ** argv, cJSON * args) ;
void test_parseCommandLineArgs(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char *argv[] = { strdup("batchManagerZos"),
                     strdup("submit"),
                     strdup("--batchManager=WOLA1 WOLAAAA2 WOLA3"),
                     strdup("--applicationName=MyApp"),
                     strdup("--moduleName=MyAppModule.war"),
                     strdup("--jobXMLName=SimpleBatchJob"),
                     strdup("--wait") };

	cJSON * args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);

    int rc = jnu_parseCommandLineArgs(7, argv, args);
    CuAssertIntEquals(tc, 0, rc);

    cJSON * arg = cJSON_GetObjectItem(args,"command");
    CuAssertStrEquals(tc, "command", arg->string);
    CuAssertStrEquals(tc, "submit", arg->valuestring);

    arg = cJSON_GetObjectItem(args,"batchManager");
    CuAssertStrEquals(tc, "batchManager", arg->string);
    CuAssertStrEquals(tc, "WOLA1 WOLAAAA2 WOLA3", arg->valuestring);

    arg = cJSON_GetObjectItem(args,"applicationName");
    CuAssertStrEquals(tc, "applicationName", arg->string);
    CuAssertStrEquals(tc, "MyApp", arg->valuestring);

    arg = cJSON_GetObjectItem(args,"moduleName");
    CuAssertStrEquals(tc, "moduleName", arg->string);
    CuAssertStrEquals(tc, "MyAppModule.war", arg->valuestring);

    arg = cJSON_GetObjectItem(args,"jobXMLName");
    CuAssertStrEquals(tc, "jobXMLName", arg->string);
    CuAssertStrEquals(tc, "SimpleBatchJob", arg->valuestring);

    arg = cJSON_GetObjectItem(args,"wait");
    CuAssertStrEquals(tc, "wait", arg->string);
    CuAssertStrEquals(tc, "--wait", arg->valuestring);

    cJSON_Delete(args);

    for (int i=0; i < 7; ++i) {
        free(argv[i]);
    }
}

/**
 * ...
 */
void test_parseJobParameters(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char *argv[] = { strdup("batchManagerZos"),
                     strdup("submit"),
                     strdup("--batchManager=WOLA1 WOLAAAA2 WOLA3"),
                     strdup("--jobParameter=hello=goodbye"),
                     strdup("--jobParameter=parm1=parm1value"),
                     strdup("--jobParameter=parm2=parm2value"),
                     strdup("--wait") };

	cJSON * args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);

    int rc = jnu_parseCommandLineArgs(7, argv, args);
    CuAssertIntEquals(tc, 0, rc);

    cJSON * jobParams = cJSON_GetObjectItem(args,"jobParameters");
    CuAssertPtrNotNull(tc, jobParams);

    cJSON * param = cJSON_GetObjectItem(jobParams,"hello");
    CuAssertStrEquals(tc, "hello", param->string);
    CuAssertStrEquals(tc, "goodbye", param->valuestring);

    param = cJSON_GetObjectItem(jobParams,"parm1");
    CuAssertStrEquals(tc, "parm1", param->string);
    CuAssertStrEquals(tc, "parm1value", param->valuestring);

    param = cJSON_GetObjectItem(jobParams,"parm2");
    CuAssertStrEquals(tc, "parm2", param->string);
    CuAssertStrEquals(tc, "parm2value", param->valuestring);

    cJSON_Delete(args);

    for (int i=0; i < 7; ++i) {
        free(argv[i]);
    }
}


/**
 * ...
 */
void test_parseWolaName(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char * s = "WOLA1+WOLA2 WOLAAAA3";

    WolaName wolaName;
    int rc = jnu_parseWolaName(s, &wolaName);

    CuAssertIntEquals(tc, 0, rc);
    CuAssertStrEquals(tc, wolaName.part[0], "WOLA1   ");
    CuAssertStrEquals(tc, wolaName.part[1], "WOLA2   ");
    CuAssertStrEquals(tc, wolaName.part[2], "WOLAAAA3");

}


/**
 * ...
 */
void test_parseWolaNameBad(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char * s = "WOLA1 WOLA2";

    WolaName wolaName;

    int rc = jnu_parseWolaName(s, &wolaName);
    CuAssertIntEquals(tc, 12, rc);

    rc = jnu_parseWolaName(NULL, &wolaName);
    CuAssertIntEquals(tc, -1, rc);

    char * s2 = "WOLA1 WOLAWOLAWOLA2 WOLA3";
    rc = jnu_parseWolaName(s2, &wolaName);
    CuAssertIntEquals(tc, 2 * 100 + 13, rc);
}


/**
 *
 * Requires /u/ralder/props.props
 *
---------------------------------
# this is a test properties file
# for testing jnu_parsePropsFile

hello=goodbye

my.prop.1=value.for.prop.1
blah=blah1=blah2
---------------------------------
 * 
 */
extern int jnu_parsePropsFile( const char * fileName, cJSON * props, int (*parseFunction)(char *,cJSON *) ) ;
extern int jnu_parseProperty(char * lineIn, cJSON * props) ;
extern int jnu_parseArg(char * lineIn, cJSON * props) ;
void test_parsePropsFile(CuTest * tc) {

    println(__FUNCTION__ ": entry");
    const char * fileName = "/u/ralder/props.props";

    cJSON * props = cJSON_CreateObject_ns();
    CuAssertPtrNotNull(tc, props);

    int rc = jnu_parsePropsFile(fileName, props, &jnu_parseProperty);
    CuAssertIntEquals(tc, 0, rc);

    CuAssertIntEquals(tc, 3, cJSON_GetArraySize_ns(props));

    cJSON * prop = cJSON_GetObjectItem(props,"hello");
    CuAssertStrEquals(tc, "hello", prop->string);
    CuAssertStrEquals(tc, "goodbye", prop->valuestring);

    prop = cJSON_GetObjectItem(props,"my.prop.1");
    CuAssertStrEquals(tc, "my.prop.1", prop->string);
    CuAssertStrEquals(tc, "value.for.prop.1", prop->valuestring);

    prop = cJSON_GetObjectItem(props,"blah");
    CuAssertStrEquals(tc, "blah", prop->string);
    CuAssertStrEquals(tc, "blah1=blah2", prop->valuestring);

    cJSON_Delete_ns(props);
}

/**
 * ...
 */
extern int jnu_resolveArgs(cJSON * props) ;
void test_mergeJobParamsFromCommandLineAndPropsFile(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char *argv[] = { strdup("batchManagerZos"),
                     strdup("submit"),
                     strdup("--batchManager=WOLA1 WOLAAAA2 WOLA3"),
                     strdup("--jobParameter=hello=byebye"),     // overrides the file.
                     strdup("--jobParameter=parm1=parm1value"),
                     strdup("--jobParameter=parm2=parm2value"),
                     strdup("--jobParametersFile=/u/ralder/props.props") }; // TODO: write this to /tmp

	cJSON * args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);

    int rc = jnu_parseCommandLineArgs(7, argv, args);
    CuAssertIntEquals(tc, 0, rc);

    rc = jnu_resolveArgs(args);
    CuAssertIntEquals(tc, 0, rc);

    cJSON * jobParams = cJSON_GetObjectItem(args,"jobParameters");
    CuAssertPtrNotNull(tc, jobParams);

    cJSON * param = cJSON_GetObjectItem(jobParams,"hello");
    CuAssertStrEquals(tc, "hello", param->string);
    CuAssertStrEquals(tc, "byebye", param->valuestring);

    param = cJSON_GetObjectItem(jobParams,"parm1");
    CuAssertStrEquals(tc, "parm1", param->string);
    CuAssertStrEquals(tc, "parm1value", param->valuestring);

    param = cJSON_GetObjectItem(jobParams,"parm2");
    CuAssertStrEquals(tc, "parm2", param->string);
    CuAssertStrEquals(tc, "parm2value", param->valuestring);

    param = cJSON_GetObjectItem(jobParams,"my.prop.1");
    CuAssertStrEquals(tc, "my.prop.1", param->string);
    CuAssertStrEquals(tc, "value.for.prop.1", param->valuestring);

    param = cJSON_GetObjectItem(jobParams,"blah");
    CuAssertStrEquals(tc, "blah", param->string);
    CuAssertStrEquals(tc, "blah1=blah2", param->valuestring);

    cJSON_Delete(args);

    for (int i=0; i < 7; ++i) {
        free(argv[i]);
    }
}

/**
 * Note: this test reads from stdin.
 */
extern int jnu_parsePropsStream( FILE * stream, cJSON * props, int (*parseFunction)(char *,cJSON *) ) ;
void test_parsePropsStream(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    cJSON * args = cJSON_CreateObject_ns();
    CuAssertPtrNotNull(tc, args);

    int rc = jnu_parsePropsStream(stdin, args, &jnu_parseArg);
    CuAssertIntEquals(tc, 0, rc);

    CuAssertIntEquals(tc, 0, cJSON_GetArraySize_ns(args));
}

/**
 * ...
 */
void test_normalizeArgs(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char *argv[] = { strdup("batchManagerZos"),
                     strdup("restart"),
                     strdup("--batchManager=WOLA1 WOLAAAA2 WOLA3"),
                     strdup("--jobInstanceId=123"),   
                     strdup("--jobExecutionId=456") }; 

	cJSON * args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);

    int rc = jnu_parseCommandLineArgs(5, argv, args);
    CuAssertIntEquals(tc, 0, rc);

    // jnu_normalizeArgs is called by jnu_resolveArgs.
    rc = jnu_resolveArgs(args);
    CuAssertIntEquals(tc, 0, rc);

    cJSON * param = cJSON_GetObjectItem(args,"jobInstanceId");
    CuAssertPtrNotNull(tc, param);
    CuAssertStrEquals(tc, "jobInstanceId", param->string);
    CuAssertStrEquals(tc, "123", param->valuestring);

    param = cJSON_GetObjectItem(args,"instanceId");
    CuAssertPtrNotNull(tc, param);
    CuAssertStrEquals(tc, "instanceId", param->string);
    CuAssertStrEquals(tc, "123", param->valuestring);

    param = cJSON_GetObjectItem(args,"jobExecutionId");
    CuAssertPtrNotNull(tc, param);
    CuAssertStrEquals(tc, "jobExecutionId", param->string);
    CuAssertStrEquals(tc, "456", param->valuestring);

    param = cJSON_GetObjectItem(args,"executionId");
    CuAssertPtrNotNull(tc, param);
    CuAssertStrEquals(tc, "executionId", param->string);
    CuAssertStrEquals(tc, "456", param->valuestring);

    cJSON_Delete(args);

    for (int i=0; i < 5; ++i) {
        free(argv[i]);
    }
}


/**
 * ...
 */
extern int jnu_printHelp();
void test_printHelp(CuTest * tc) {

    println(__FUNCTION__ ": entry");
    jnu_printHelp();
}

/**
 * ...
 */
extern int jnu_printUsage();
void test_printUsage(CuTest * tc) {

    println(__FUNCTION__ ": entry");
    jnu_printUsage();
}

/**
 * ...
 */
extern int jnu_printTaskHelp(char * command);
void test_printTaskHelp(CuTest * tc) {

    println(__FUNCTION__ ": entry");
    jnu_printTaskHelp("help");
    jnu_printTaskHelp("ping");
    jnu_printTaskHelp("submit");
    jnu_printTaskHelp("stop");
    jnu_printTaskHelp("restart");
    jnu_printTaskHelp("status");
}

/**
 * ...
 */
extern int jnu_helpTask(cJSON * args);
void test_helpTask(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // test null.
    jnu_helpTask(NULL);

    // test help
    char *argv[] = { strdup("batchManagerZos"),
                     strdup("help") };

	cJSON * args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);

    int rc = jnu_parseCommandLineArgs(2, argv, args);
    CuAssertIntEquals(tc, 0, rc);
    println(__FUNCTION__ ": args: %s", cJSON_PrintUnformatted(args));

    cJSON * arg = cJSON_GetObjectItem(args,"command");
    CuAssertStrEquals(tc, "command", arg->string);
    CuAssertStrEquals(tc, "help", arg->valuestring);

    arg = arg->next;
    CuAssertPtrIsNull(tc, arg);

    jnu_helpTask(args);

    cJSON_Delete_ns(args);
    for (int i=0; i < 2; ++i) {
        free(argv[i]);
    }

    // test help <command>
    char *argv2[] = { strdup("batchManagerZos"),
                      strdup("help"),
                      strdup("submit") };

	args = cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), args);

    rc = jnu_parseCommandLineArgs(3, argv2, args);
    CuAssertIntEquals(tc, 0, rc);
    println(__FUNCTION__ ": args: %s", cJSON_PrintUnformatted(args));

    arg = cJSON_GetObjectItem(args,"command");
    CuAssertPtrNotNull(tc, arg);
    CuAssertStrEquals(tc, "command", arg->string);
    CuAssertStrEquals(tc, "help", arg->valuestring);

    arg = arg->next;
    CuAssertPtrNotNull(tc, arg);
    CuAssertStrEquals(tc, "bmit", arg->string);
    CuAssertStrEquals(tc, "submit", arg->valuestring);

    jnu_helpTask(args);

    cJSON_Delete_ns(args);
    for (int i=0; i < 3; ++i) {
        free(argv2[i]);
    }

}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * jbatch_native_utility_test_suite() {

    CuSuite* suite = CuSuiteNew("jbatch_native_utility_test");

    SUITE_ADD_TEST(suite, test_parseCommandLineArgs);
    SUITE_ADD_TEST(suite, test_parseJobParameters);
    SUITE_ADD_TEST(suite, test_parseWolaName);
    SUITE_ADD_TEST(suite, test_parseWolaNameBad);
    SUITE_ADD_TEST(suite, test_normalizeArgs);
    SUITE_ADD_TEST(suite, test_printHelp);
    SUITE_ADD_TEST(suite, test_printUsage);
    SUITE_ADD_TEST(suite, test_printTaskHelp);
    SUITE_ADD_TEST(suite, test_helpTask);

    SUITE_ADD_TEST(suite, test_parsePropsFile);
    // SUITE_ADD_TEST(suite, test_parsePropsStream);
    SUITE_ADD_TEST(suite, test_mergeJobParamsFromCommandLineAndPropsFile);

    return suite;
}




