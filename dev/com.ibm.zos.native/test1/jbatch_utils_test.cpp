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

#include "../include/jbatch_utils.h"
#include "include/CuTest.h"

extern void println(const char *, ...);

/**
 * ...
 */
void test_pad(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char s[9];
    s[8] = 'a';
    strcpy(s, "blah");

    jnu_pad(s, 8, ' ');

    CuAssertMemEquals(tc, "blah    a",s, 9);
}

/**
 * ...
 */
void test_strcpypad(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char s[9];
    s[8] = 'a';
    jnu_strcpypad(s, "blah", ' ', 8);

    CuAssertStrEquals(tc, "blah    ",s);
}


/**
 * ...
 */
void test_strsep(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char * s = strdup("blah\nblahblah\nblah");
    char * s1 = s;
    char * s2 = s + 5;
    char * s3 = s + 14;

    CuAssertStrEquals(tc, "blahblah\nblah", s2);
    CuAssertStrEquals(tc, "blah", s3);

    char * t1 = jnu_strsep(&s,"\n");
    CuAssertStrEquals(tc, "blah", t1);
    CuAssertPtrEquals(tc, s1, t1);

    char * t2 = jnu_strsep(&s,"\n");
    CuAssertStrEquals(tc, "blahblah", t2);
    CuAssertPtrEquals(tc, s2, t2);

    char * t3 = jnu_strsep(&s,"\n");
    CuAssertStrEquals(tc, "blah", t3);
    CuAssertPtrEquals(tc, s3, t3);

    free(s);
}

/**
 * ...
 */
void test_splitBasic(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // basic
    char * s = strdup("--batchManager=a b c");
    char * sp[2] = {NULL, NULL};
    int rc = jnu_split( s, "=", (char **)&sp, 2);

    CuAssertIntEquals(tc, 2, rc);
    CuAssertPtrNotNull(tc, sp[0]);
    CuAssertPtrNotNull(tc, sp[1]);
    CuAssertStrEquals(tc, "--batchManager", sp[0]);
    CuAssertStrEquals(tc, "a b c", sp[1]);

    free(s);
}

/**
 * ...
 */
void test_splitNoValue(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // no value
    char * s = strdup("--view");
    char * sp[2] = {NULL, NULL};
    int rc = jnu_split( s, "=", (char **)&sp, 2);

    CuAssertIntEquals(tc, 1, rc);
    CuAssertPtrNotNull(tc, sp[0]);
    CuAssertPtrIsNull(tc, sp[1]);
    CuAssertStrEquals(tc, "--view", sp[0]);

    free(s);
}

/**
 * ...
 */
void test_splitMax(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char * s = strdup("--name=value=value");
    char * sp[2] = {NULL, NULL};
    int rc = jnu_split( s, "=", (char **)&sp, 2);

    CuAssertIntEquals(tc, 2, rc);
    CuAssertPtrNotNull(tc, sp[0]);
    CuAssertPtrNotNull(tc, sp[1]);
    CuAssertStrEquals(tc, "--name", sp[0]);
    CuAssertStrEquals(tc, "value=value", sp[1]);

    free(s);
}

/**
 * ...
 */
void test_splitMaxIrrelevant(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char * s = strdup("--name=value");
    char * sp[4] = {NULL, NULL, NULL, NULL};
    int rc = jnu_split( s, "=", (char **)&sp, 4);

    CuAssertIntEquals(tc, 2, rc);
    CuAssertPtrNotNull(tc, sp[0]);
    CuAssertPtrNotNull(tc, sp[1]);
    CuAssertPtrIsNull(tc, sp[2]);
    CuAssertPtrIsNull(tc, sp[3]);
    CuAssertStrEquals(tc, "--name", sp[0]);
    CuAssertStrEquals(tc, "value", sp[1]);

    free(s);
}

/**
 * ...
 */
void test_isNumber(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertTrue(tc, jnu_isNumber("10"));
    CuAssertTrue(tc, jnu_isNumber("10:hello"));
    CuAssertFalse(tc, jnu_isNumber("hello:10"));
    CuAssertFalse(tc, jnu_isNumber("hello"));
    CuAssertFalse(tc, jnu_isNumber(""));
    CuAssertFalse(tc, jnu_isNumber(NULL));
}

/**
 * ...
 */
void test_parseInt(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertIntEquals(tc, 10, jnu_parseInt("10", 0));
    CuAssertIntEquals(tc, 11, jnu_parseInt("11:hello", 0));
    CuAssertIntEquals(tc, -1, jnu_parseInt("hello:10", -1));
    CuAssertIntEquals(tc, -1, jnu_parseInt("hello", -1));
    CuAssertIntEquals(tc, -1, jnu_parseInt("", -1));
    CuAssertIntEquals(tc, -1, jnu_parseInt(NULL, -1));
}

/**
 * ...
 */
void test_isallspaces(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertTrue(tc, jnu_isallspaces("") );
    CuAssertTrue(tc, jnu_isallspaces(" \r\n\t") );
    CuAssertFalse(tc, jnu_isallspaces("   x   ") );
    CuAssertFalse(tc, jnu_isallspaces("   x") );
    CuAssertFalse(tc, jnu_isallspaces("x    ") );
    CuAssertFalse(tc, jnu_isallspaces("x") );
}


/**
 * ...
 */
void test_strIsEmpty(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertTrue(tc, jnu_strIsEmpty("") );
    CuAssertTrue(tc, jnu_strIsEmpty(NULL) );
    CuAssertFalse(tc, jnu_strIsEmpty(" \r\n\t") );
    CuAssertFalse(tc, jnu_strIsEmpty("blah") );
}

/**
 * ...
 */
void test_chomp(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    char * dup1 = strdup("blah\n");

    CuAssertStrEquals(tc, "blah\n", dup1);
    CuAssertIntEquals(tc, 5, strlen(dup1));
    dup1 = jnu_chomp(dup1);
    CuAssertStrEquals(tc, "blah", dup1);
    CuAssertIntEquals(tc, 4, strlen(dup1));

    char * dup2 = strdup("blah");
    CuAssertStrEquals(tc, "blah", dup2);
    dup2 = jnu_chomp(dup2);
    CuAssertStrEquals(tc, "blah", dup2);

    char * dup3 = strdup("blah\nblah");
    dup3 = jnu_chomp(dup3);
    CuAssertStrEquals(tc, "blah\nblah", dup3);

    char * dup4 = "";
    dup4 = jnu_chomp(dup4);
    CuAssertStrEquals(tc, "", dup4);

    char * dup5 = strdup("blah\r\n");
    dup5 = jnu_chomp(dup5);
    CuAssertStrEquals(tc, "blah", dup5);
    CuAssertIntEquals(tc, 4, strlen(dup5));
    
    char * dup6 = strdup("\n");
    dup6 = jnu_chomp(dup6);
    CuAssertStrEquals(tc, "", dup6);
    CuAssertIntEquals(tc, 0, strlen(dup6));

    char * dup7 = strdup("  \r\n");
    dup7 = jnu_chomp(dup7);
    CuAssertStrEquals(tc, "  ", dup7);
    CuAssertIntEquals(tc, 2, strlen(dup7));

    CuAssertPtrIsNull(tc, jnu_chomp(NULL) );

    free(dup1);
    free(dup2);
    free(dup3);
    free(dup4);
    free(dup5);
    free(dup6);
    free(dup7);
}

/**
 * ...
 */
void test_strStartsWith(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    CuAssertTrue(tc, jnu_strStartsWith("# comment line", "#") );
    CuAssertTrue(tc, jnu_strStartsWith("blah blah", "blah") );
    CuAssertFalse(tc, jnu_strStartsWith("", "#") );
    CuAssertFalse(tc, jnu_strStartsWith("blah", "foo") );
}

/**
 * ...
 */
void test_mallocStringArray(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    char ** strs = jnu_mallocStringArray(0);
    CuAssertPtrNotNull(tc, strs);
    CuAssertPtrIsNull(tc, strs[0]);
    jnu_freeStringArray(strs);

    strs = jnu_mallocStringArray(1, "hello");
    CuAssertPtrNotNull(tc, strs);
    CuAssertStrEquals(tc, "hello", strs[0]);
    CuAssertPtrIsNull(tc, strs[1]);
    jnu_freeStringArray(strs);

    strs = jnu_mallocStringArray(3, "hello", "goodbye", "hello again");
    CuAssertPtrNotNull(tc, strs);
    CuAssertStrEquals(tc, "hello", strs[0]);
    CuAssertStrEquals(tc, "goodbye", strs[1]);
    CuAssertStrEquals(tc, "hello again", strs[2]);
    CuAssertPtrIsNull(tc, strs[3]);
    jnu_freeStringArray(strs);
}


/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * jbatch_utils_test_suite() {

    CuSuite* suite = CuSuiteNew("jbatch_utils_test");

    SUITE_ADD_TEST(suite, test_strsep);
    SUITE_ADD_TEST(suite, test_pad);
    SUITE_ADD_TEST(suite, test_strcpypad);
    SUITE_ADD_TEST(suite, test_splitBasic);
    SUITE_ADD_TEST(suite, test_splitNoValue);
    SUITE_ADD_TEST(suite, test_splitMax);
    SUITE_ADD_TEST(suite, test_splitMaxIrrelevant);
    SUITE_ADD_TEST(suite, test_isNumber);
    SUITE_ADD_TEST(suite, test_parseInt);
    SUITE_ADD_TEST(suite, test_isallspaces);
    SUITE_ADD_TEST(suite, test_strIsEmpty);
    SUITE_ADD_TEST(suite, test_chomp);
    SUITE_ADD_TEST(suite, test_strStartsWith);
    SUITE_ADD_TEST(suite, test_mallocStringArray);

    return suite;
}




