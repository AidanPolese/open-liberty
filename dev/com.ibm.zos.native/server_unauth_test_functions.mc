/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
/* This uses regular linkage */
#include "include/ras_tracing.h"
#define RAS_MODULE_CONST  RAS_MODULE_SERVER_UNAUTH_TEST_FUNCTIONS

int test_add_func(int* i1, int* i2)
{
  TraceRecord(trc_level_detailed, TP(1),"entry test_add_func",
              TRACE_DATA_INT(*i1,"first int"),
              TRACE_DATA_INT(*i2,"second int"),
  TRACE_DATA_END_PARMS);
  return *i1 + *i2;
}
