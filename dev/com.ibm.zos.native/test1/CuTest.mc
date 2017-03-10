/**
 * CuTest: http://cutest.sourceforge.net/
 *
 * The license is based on the zlib/libpng license. For more details see
 * http://www.opensource.org/licenses/zlib-license.html. The intent of the
 * license is to: 
 * 
 * - keep the license as simple as possible
 * - encourage the use of CuTest in both free and commercial applications
 *   and libraries
 * - keep the source code together 
 * - give credit to the CuTest contributors for their work
 * 
 * If you ship CuTest in source form with your source distribution, the
 * following license document must be included with it in unaltered form.
 * If you find CuTest useful we would like to hear about it. 
 * 
 * LICENSE
 * 
 * Copyright (c) 2003 Asim Jalis
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software in
 * a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such, and must not
 * be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source
 * distribution.
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#include "include/CuTest.h"

/*-------------------------------------------------------------------------*
 * CuStr
 *-------------------------------------------------------------------------*/

char* CuStrAlloc(int size)
{
    char* newStr = (char*) malloc( sizeof(char) * (size) ); 
    return newStr;
}

char* CuStrCopy(const char* old)
{
    int len = strlen(old);
    char* newStr = CuStrAlloc(len + 1);
    strcpy(newStr, old);
    return newStr;
}

/*-------------------------------------------------------------------------*
 * CuString
 *-------------------------------------------------------------------------*/

void CuStringInit(CuString* str)
{
    str->length = 0;
    str->size = STRING_MAX;
    str->buffer = (char*) malloc(sizeof(char) * str->size);
    str->buffer[0] = '\0';
}

CuString* CuStringNew(void)
{
    CuString* str = (CuString*) malloc(sizeof(CuString));
    str->length = 0;
    str->size = STRING_MAX;
    str->buffer = (char*) malloc(sizeof(char) * str->size);
    str->buffer[0] = '\0';
    return str;
}

void CuStringDelete(CuString *str)
{
        if (!str) return;
        free(str->buffer);
        free(str);
}

void CuStringResize(CuString* str, int newSize)
{
    str->buffer = (char*) realloc(str->buffer, sizeof(char) * newSize); 
    str->size = newSize;
}

void CuStringAppend(CuString* str, const char* text)
{
    int length;

    if (text == NULL) {
        text = "NULL";
    }

    length = strlen(text);
    if (str->length + length + 1 >= str->size)
        CuStringResize(str, str->length + length + 1 + STRING_INC);
    str->length += length;
    strcat(str->buffer, text);
}

void CuStringAppendWithLen(CuString* str, const char* text, size_t length)
{
    if (text == NULL) {
        text = "NULL";
    }

    if (str->length + length + 1 >= str->size)
        CuStringResize(str, str->length + length + 1 + STRING_INC);
    str->length += length;
    strncat(str->buffer, text, length);
}

void CuStringAppendChar(CuString* str, char ch)
{
    char text[2];
    text[0] = ch;
    text[1] = '\0';
    CuStringAppend(str, text);
}

void CuStringAppendFormat(CuString* str, const char* format, ...)
{
    va_list argp;
    char buf[HUGE_STRING_LEN];
    va_start(argp, format);
    vsnprintf(buf, HUGE_STRING_LEN, format, argp);
    va_end(argp);
    CuStringAppend(str, buf);
}

void CuStringInsert(CuString* str, const char* text, int pos)
{
    int length = strlen(text);
    if (pos > str->length)
        pos = str->length;
    if (str->length + length + 1 >= str->size)
        CuStringResize(str, str->length + length + 1 + STRING_INC);
    memmove(str->buffer + pos + length, str->buffer + pos, (str->length - pos) + 1); // TODO: check memmove
    str->length += length;
    memcpy(str->buffer + pos, text, length);
}

/*-------------------------------------------------------------------------*
 * CuTest
 *-------------------------------------------------------------------------*/

void CuTestInit(CuTest* t, const char* name, TestFunction function)
{
    t->name = CuStrCopy(name);
    t->failed = 0;
    t->ran = 0;
    t->message = NULL;
    t->function = function;
}

CuTest* CuTestNew(const char* name, TestFunction function)
{
    CuTest* tc = CU_ALLOC(CuTest);
    CuTestInit(tc, name, function);
    return tc;
}

void CuTestDelete(CuTest *t)
{
    if (!t) return;
    free(t->name);
    free(t);
}

void CuTestRun(CuTest* tc)
{
    tc->ran = 1;
    (tc->function)(tc);
}

static int CuFailInternal(CuTest* tc, const char* file, int line, CuString* string)
{
    char buf[HUGE_STRING_LEN];

    sprintf(buf, "%s:%d: ", file, line);
    CuStringInsert(string, buf, 0);

    tc->failed = 1;
    tc->message = string->buffer;
    return -1;
}

int CuFail_Line(CuTest* tc, const char* file, int line, const char* message2, const char* message)
{
    CuString string;

    CuStringInit(&string);
    if (message2 != NULL) 
    {
        CuStringAppend(&string, message2);
        CuStringAppend(&string, ": ");
    }
    CuStringAppend(&string, message);
    return CuFailInternal(tc, file, line, &string);
}

int CuAssert_Line(CuTest* tc, const char* file, int line, const char* message, int condition)
{
    if (condition) return 0;
    return CuFail_Line(tc, file, line, NULL, message);
}

int CuAssertStrEquals_LineMsg(CuTest* tc, const char* file, int line, const char* message, 
    const char* expected, const char* actual)
{
    CuString string;
    if ((expected == NULL && actual == NULL) ||
        (expected != NULL && actual != NULL &&
         strcmp(expected, actual) == 0))
    {
        return 0;
    }

    CuStringInit(&string);
    if (message != NULL) 
    {
        CuStringAppend(&string, message);
        CuStringAppend(&string, ": ");
    }
    CuStringAppend(&string, "expected <");
    CuStringAppend(&string, expected);
    CuStringAppend(&string, "> but was <");
    CuStringAppend(&string, actual);
    CuStringAppend(&string, ">");
    return CuFailInternal(tc, file, line, &string);
}

/**
 * Assert all bytes starting from actual and going for len bytes are all equal to the
 * expected byte value.
 *
 * This method is useful for testing whether a storage block is cleared to all zeros, e.g.
 */
int CuAssertMemBytesEqual_LineMsg(CuTest* tc, const char* file, int line, const char* message, 
    int expected, void * actual, size_t len)
{
    void * expectedBytes = malloc(len);
    memset(expectedBytes, expected, len);

    int rc = CuAssertMemEquals_LineMsg(tc, file, line, message, expectedBytes, actual, len);

    free(expectedBytes);
    return rc;
}

int CuAssertMemEquals_LineMsg(CuTest* tc, const char* file, int line, const char* message, 
    void * expected, void * actual, size_t len)
{
    CuString string;
    if ((expected == NULL && actual == NULL) ||
        (expected != NULL && actual != NULL &&
         memcmp(expected, actual, len) == 0))
    {
        return 0;
    }

    CuStringInit(&string);
    if (message != NULL) 
    {
        CuStringAppend(&string, message);
        CuStringAppend(&string, ": ");
    }
    CuStringAppend(&string, "expected <");
    CuStringAppendWithLen(&string, expected, len);
    CuStringAppend(&string, "> but was <");
    CuStringAppendWithLen(&string, actual, len);
    CuStringAppend(&string, ">");
    return CuFailInternal(tc, file, line, &string);
}


int CuAssertIntEquals_LineMsg(CuTest* tc, const char* file, int line, const char* message, 
    int expected, int actual)
{
    char buf[STRING_MAX];
    if (expected == actual) return 0;
    sprintf(buf, "expected <%d> but was <%d>", expected, actual);
    return CuFail_Line(tc, file, line, message, buf);
}

#pragma option_override(local_fabs, "OPT(STRICT)")
static double local_fabs(double d) {
    return (d > 0.0) ? d : 0.0 - d;
}

#pragma option_override(CuAssertDblEquals_LineMsg, "OPT(STRICT)")
int CuAssertDblEquals_LineMsg(CuTest* tc, const char* file, int line, const char* message, 
    double expected, double actual, double delta)
{
    char buf[STRING_MAX];
    // if (fabs(expected - actual) <= delta) return;   // No fabs in metal C
    if (local_fabs(expected - actual) <= delta) return 0;
    sprintf(buf, "expected <%f> but was <%f>", expected, actual); 

    return CuFail_Line(tc, file, line, message, buf);
}

int CuAssertPtrEquals_LineMsg(CuTest* tc, const char* file, int line, const char* message, 
    void* expected, void* actual)
{
    char buf[STRING_MAX];
    if (expected == actual) return 0;
    sprintf(buf, "expected pointer <0x%p> but was <0x%p>", expected, actual);
    return CuFail_Line(tc, file, line, message, buf);
}

int CuAssertLongLongEquals_LineMsg(CuTest* tc, const char* file, int line, const char* message,
    long long expected, long long actual)
{
    char buf[STRING_MAX];
    if (expected == actual) return 0;
    sprintf(buf, "expected long long <0x%p> but was <0x%p>", expected, actual);
    return CuFail_Line(tc, file, line, message, buf);
}

/*-------------------------------------------------------------------------*
 * CuSuite
 *-------------------------------------------------------------------------*/

void CuSuiteInit(CuSuite* testSuite, const char * name)
{
    testSuite->name = CuStrCopy(name);
    testSuite->count = 0;
    testSuite->failCount = 0;
    memset(testSuite->list, 0, sizeof(testSuite->list));
}

CuSuite* CuSuiteNew(const char * name)
{
    CuSuite* testSuite = CU_ALLOC(CuSuite);
    CuSuiteInit(testSuite, name);
    return testSuite;
}

void CuSuiteDelete(CuSuite *testSuite)
{
    if (!testSuite) {
        return;
    }

    unsigned int n;
    for (n=0; n < MAX_TEST_CASES; n++)
    {
        if (testSuite->list[n])
        {
            CuTestDelete(testSuite->list[n]);
        }
    }
    free(testSuite->name);
    free(testSuite);
}

void CuSuiteAdd(CuSuite* testSuite, CuTest *testCase)
{
    if (testSuite->count < MAX_TEST_CASES) {
        testSuite->list[testSuite->count] = testCase;
        testSuite->count++;
    } else {
        // TODO
    }
}

void CuSuiteAddSuite(CuSuite* testSuite, CuSuite* testSuite2)
{
    int i;
    for (i = 0 ; i < testSuite2->count ; ++i)
    {
        CuTest* testCase = testSuite2->list[i];
        CuSuiteAdd(testSuite, testCase);
    }
}

void CuSuiteRun(CuSuite* testSuite)
{
    int i;
    for (i = 0 ; i < testSuite->count ; ++i)
    {
        CuTest* testCase = testSuite->list[i];
        CuTestRun(testCase);
        if (testCase->failed) { testSuite->failCount += 1; }
    }
}

/**
 * Build a junit-style summary of the test suite results.
 *
 * @param testSuite - The suite to summarize
 * @param summary - Output - The string buffer to contain the summary.
 *
 */
void CuSuiteBuildResults(CuSuite* testSuite, CuString* summary)
{
    CuStringAppendFormat(summary, "Testsuite: %s\n", testSuite->name);
    CuStringAppendFormat(summary, "Tests run: %d, Failures: %d\n", testSuite->count, testSuite->failCount);

    if (testSuite->failCount > 0) {
        CuStringAppend(summary, " ----- !!! FAILURES !!! -------\n");

        for (int i = 0 ; i < testSuite->count ; ++i)
        {
            CuTest* testCase = testSuite->list[i];
            if (testCase->failed)
            {
                CuStringAppendFormat(summary, "FAILURE: %s: %s\n", testCase->name, testCase->message);
            }
        }

        CuStringAppend(summary, " ----- !!! FAILURES !!! -------\n");
    }
}


void CuSuiteSummary(CuSuite* testSuite, CuString* summary)
{
    int i;
    for (i = 0 ; i < testSuite->count ; ++i)
    {
        CuTest* testCase = testSuite->list[i];
        CuStringAppend(summary, testCase->failed ? "F" : ".");
    }
    CuStringAppend(summary, "\n\n");
}

void CuSuiteDetails(CuSuite* testSuite, CuString* details)
{
    int i;
    int failCount = 0;

    if (testSuite->failCount == 0)
    {
        int passCount = testSuite->count - testSuite->failCount;
        const char* testWord = passCount == 1 ? "test" : "tests";
        CuStringAppendFormat(details, "OK (%d %s)\n", passCount, testWord);
    }
    else
    {
        if (testSuite->failCount == 1)
            CuStringAppend(details, "There was 1 failure:\n");
        else
            CuStringAppendFormat(details, "There were %d failures:\n", testSuite->failCount);

        for (i = 0 ; i < testSuite->count ; ++i)
        {
            CuTest* testCase = testSuite->list[i];
            if (testCase->failed)
            {
                failCount++;
                CuStringAppendFormat(details, "%d) %s: %s\n",
                    failCount, testCase->name, testCase->message);
            }
        }
        CuStringAppend(details, "\n!!!FAILURES!!!\n");

        CuStringAppendFormat(details, "Runs: %d ",   testSuite->count);
        CuStringAppendFormat(details, "Passes: %d ", testSuite->count - testSuite->failCount);
        CuStringAppendFormat(details, "Fails: %d\n",  testSuite->failCount);
    }
}
