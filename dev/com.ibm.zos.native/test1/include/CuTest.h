/**
 * CuTest: http://cutest.sourceforge.net/
 *
 * -----------------------------------------------------------------------------
 * LICENSE
 * -----------------------------------------------------------------------------
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
 * INSTRUCTIONS FOR CREATING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 *
 * 1. Create a new part for your tests.  The naming convention for the new part
 *    should be <base-name-of-part-under-test>_test.mc.  E.g, if you're testing
 *    code in "server_wola_services.mc", name your test part "server_wola_services_test.mc"
 *
 * 2. #include "include/CuTest.h" in your test part.
 *
 * 3. Write your tests.  Each test function should have the signature:
 *
 *      void myTest(CuTest * tc);
 *
 *    Use the assert macros in CuTest.h for verification/validation.
 *
 * 4. Create a test suite "getter" function for your part.  The function shall
 *    return a CuSuite containing all your test functions.  E.g:
 *
 *      CuSuite * server_wola_services_test_suite() {
 *          CuSuite* suite = CuSuiteNew("server_wola_services_test");
 *
 *          SUITE_ADD_TEST(suite, test_buildWolaEnqRname);
 *          SUITE_ADD_TEST(suite, test_get_enq_exclusive_system);
 *
 *          return suite;
 *      }
 *
 *    Note the naming conventions.  The getter function should be named "<test-part-base-name>_suite()".
 *    The CuSuite itself should be named <test-part-base-name>.
 *
 * 5. In unit_test_main.mc, there are 3 things you need to do (search for "LOOK" in the part): 
 *      a) declare your test suite getter function at the top
 *      b) increment the NUM_SUITES
 *      c) add your test suite to loadSuites()
 *
 * 6. Add your part to server/GNUmakefile under UNIT_TEST_OBJECTS.
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR BUILDING, INSTALLING, AND RUNNING THE NATIVE UNIT TEST MODULE:
 * -----------------------------------------------------------------------------
 * 
 * 1. From RTC, invoke the "test" make target from the C/C++ perspective.
 *    This will build server/bbgznut and dist/install-unit-test.sh in your AQ sandbox.
 * 
 * 2. Download dist/install-unit-test.sh from your AQ build to your EZWAS machine
 *
 * 4. Run install-unit-test.sh on your EZWAS machine
 *    Note: the script assumes you've already installed Liberty at ~/wlp
 *
 * 5. Run the tests (see the output from install-unit-test.sh for the run commands). 
 *    E.g, from SDSF:
 *      /S BBGZNUT
 *      /S BBGZNUT,PARMS='server_wola_services_test'
 *
 */

#ifndef CU_TEST_H
#define CU_TEST_H

#include <stdarg.h>

#define CUTEST_VERSION  "CuTest 1.5"

/* CuString */

char* CuStrAlloc(int size);
char* CuStrCopy(const char* old);

#define CU_ALLOC(TYPE)        ((TYPE*) malloc(sizeof(TYPE)))

#define HUGE_STRING_LEN    8192
#define STRING_MAX        256
#define STRING_INC        256

typedef struct
{
    int length;
    int size;
    char* buffer;
} CuString;

void CuStringInit(CuString* str);
CuString* CuStringNew(void);
void CuStringRead(CuString* str, const char* path);
void CuStringAppend(CuString* str, const char* text);
void CuStringAppendChar(CuString* str, char ch);
void CuStringAppendFormat(CuString* str, const char* format, ...);
void CuStringInsert(CuString* str, const char* text, int pos);
void CuStringResize(CuString* str, int newSize);
void CuStringDelete(CuString* str);

/* CuTest */

typedef struct CuTest CuTest;

typedef void (*TestFunction)(CuTest *);

struct CuTest
{
    char* name;
    TestFunction function;
    int failed;
    int ran;
    const char* message;
};

void CuTestInit(CuTest* t, const char* name, TestFunction function);
CuTest* CuTestNew(const char* name, TestFunction function);
void CuTestRun(CuTest* tc);
void CuTestDelete(CuTest *t);

/* Internal versions of assert functions -- use the public versions */
int CuFail_Line(CuTest* tc, const char* file, int line, const char* message2, const char* message);
int CuAssert_Line(CuTest* tc, const char* file, int line, const char* message, int condition);
int CuAssertStrEquals_LineMsg(CuTest* tc, 
    const char* file, int line, const char* message, 
    const char* expected, const char* actual);
int CuAssertMemEquals_LineMsg(CuTest* tc, 
    const char* file, int line, const char* message, 
    void* expected, void* actual, size_t len);
int CuAssertMemBytesEqual_LineMsg(CuTest* tc, 
    const char* file, int line, const char* message, 
    int expected, void* actual, size_t len);
int CuAssertIntEquals_LineMsg(CuTest* tc, 
    const char* file, int line, const char* message, 
    int expected, int actual);
int CuAssertDblEquals_LineMsg(CuTest* tc, 
    const char* file, int line, const char* message, 
    double expected, double actual, double delta);
int CuAssertPtrEquals_LineMsg(CuTest* tc, 
    const char* file, int line, const char* message, 
    void* expected, void* actual);
int CuAssertLongLongEquals_LineMsg(CuTest* tc,
    const char* file, int line, const char* message,
    long long expected, long long actual);


/**
 * Public assert macros.
 *
 * Each macro takes a CuTest* as its first arg (tc).  
 *
 * If an assertion fails, the failure is noted in the CuTest object and the
 * test function returns immediately.
 * 
 * @param tc (CuTest *) - The CuTest object
 * @param ms (char *) - A failure message associated with the assertion
 * @param ex - The expected value (type depends on the assertion)
 * @param ac - The actual value (type depends on the assertion)
 *
 */
#define CuFail(tc, ms)                          if ( CuFail_Line(  (tc), __FILE__, __LINE__, NULL, (ms)) ) return;
#define CuAssert(tc, ms, cond)                  if ( CuAssert_Line((tc), __FILE__, __LINE__, (ms), (cond)) ) return;
#define CuAssertTrue(tc, cond)                  if ( CuAssert_Line((tc), __FILE__, __LINE__, "assert failed", (cond)) ) return;
#define CuAssertFalse(tc, cond)                 CuAssertTrue(tc, !(cond) )
#define CuAssertStrEquals(tc,ex,ac)             if ( CuAssertStrEquals_LineMsg((tc),__FILE__,__LINE__,NULL,(ex),(ac)) ) return;
#define CuAssertStrEquals_Msg(tc,ms,ex,ac)      if ( CuAssertStrEquals_LineMsg((tc),__FILE__,__LINE__,(ms),(ex),(ac)) ) return;
#define CuAssertMemEquals_Msg(tc,ms,ex,ac,len)  if ( CuAssertMemEquals_LineMsg((tc),__FILE__,__LINE__,(ms),(ex),(ac),(len)) ) return;
#define CuAssertMemEquals(tc,ex,ac,len)         CuAssertMemEquals_Msg(tc,"storage should be equal",ex,ac,len)
#define CuAssertMemBytesEqual_Msg(tc,ms,ex,ac,len) if ( CuAssertMemBytesEqual_LineMsg((tc),__FILE__,__LINE__,(ms),(ex),(ac),(len)) ) return;
#define CuAssertMemBytesEqual(tc,ex,ac,len)     CuAssertMemBytesEqual_Msg(tc,"",ex,ac,len)
#define CuAssertIntEquals(tc,ex,ac)             if ( CuAssertIntEquals_LineMsg((tc),__FILE__,__LINE__,NULL,(ex),(ac)) ) return;
#define CuAssertIntEquals_Msg(tc,ms,ex,ac)      if ( CuAssertIntEquals_LineMsg((tc),__FILE__,__LINE__,(ms),(ex),(ac)) ) return;
#define CuAssertDblEquals(tc,ex,ac,dl)          if ( CuAssertDblEquals_LineMsg((tc),__FILE__,__LINE__,NULL,(ex),(ac),(dl)) ) return;
#define CuAssertDblEquals_Msg(tc,ms,ex,ac,dl)   if ( CuAssertDblEquals_LineMsg((tc),__FILE__,__LINE__,(ms),(ex),(ac),(dl)) ) return;
#define CuAssertLongLongEquals_Msg(tc,ms,ex,ac) if ( CuAssertLongLongEquals_LineMsg((tc),__FILE__,__LINE__,(ms),(ex),(ac)) ) return;
#define CuAssertLongLongEquals(tc,ex,ac)        CuAssertLongLongEquals_Msg(tc,NULL,ex,ac)
#define CuAssertPtrEquals(tc,ex,ac)             if ( CuAssertPtrEquals_LineMsg((tc),__FILE__,__LINE__,NULL,(ex),(ac)) ) return;
#define CuAssertPtrEquals_Msg(tc,ms,ex,ac)      if ( CuAssertPtrEquals_LineMsg((tc),__FILE__,__LINE__,(ms),(ex),(ac)) ) return;
#define CuAssertPtrIsNull_Msg(tc,ms,p)          CuAssertPtrEquals_Msg(tc,ms,NULL,p) 
#define CuAssertPtrIsNull(tc,p)                 CuAssertPtrIsNull_Msg(tc,"pointer should be NULL",p)
#define CuAssertPtrNotNull(tc,p)                if ( CuAssert_Line((tc),__FILE__,__LINE__,"pointer should NOT be NULL",(p != NULL)) ) return;
#define CuAssertPtrNotNullMsg(tc,ms,p)          if ( CuAssert_Line((tc),__FILE__,__LINE__,(ms),(p != NULL)) ) return;


/* CuSuite */

/**
 * The max number of tests for 1 suite.
 */
#define MAX_TEST_CASES    1024

/**
 * Add a test to a suite.
 *
 * @param SUITE (CuSuite*) - The suite 
 * @param TEST (TestFunction) - The test function
 *
 */
#define SUITE_ADD_TEST(SUITE,TEST)    CuSuiteAdd(SUITE, CuTestNew(#TEST, TEST))

typedef struct
{
    char * name;                        //!< The suite name.
    int count;                          //!< Number of tests in suite.
    CuTest* list[MAX_TEST_CASES];       //!< The tests
    int failCount;                      //!< Number of tests that failed.

} CuSuite;



/**
 * @param name - The name of the CuSuite (typically the name of the part 
 *               that contains the tests)
 *
 * @return a new CuSuite with the given name.
 */
CuSuite* CuSuiteNew(const char * name);

void CuSuiteInit(CuSuite* testSuite, const char * name);
void CuSuiteDelete(CuSuite *testSuite);
void CuSuiteAdd(CuSuite* testSuite, CuTest *testCase);
void CuSuiteAddSuite(CuSuite* testSuite, CuSuite* testSuite2);
void CuSuiteRun(CuSuite* testSuite);
void CuSuiteSummary(CuSuite* testSuite, CuString* summary);
void CuSuiteDetails(CuSuite* testSuite, CuString* details);

/**
 * Build a junit-style summary of the test suite results.
 *
 * @param testSuite - The suite to summarize
 * @param summary - Output - The string buffer to contain the summary.
 *
 */
void CuSuiteBuildResults(CuSuite* testSuite, CuString* summary);

#endif /* CU_TEST_H */
