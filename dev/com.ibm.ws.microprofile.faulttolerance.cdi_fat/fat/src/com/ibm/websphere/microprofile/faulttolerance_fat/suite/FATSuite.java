package com.ibm.websphere.microprofile.faulttolerance_fat.suite;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDIAsyncTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDICircuitBreakerTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDIFallbackTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDIRetryTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDITimeoutTest;

@RunWith(Suite.class)
@SuiteClasses({
                CDIAsyncTest.class,
                //temporarily disabled while api is updated
//                CDIBulkheadTest.class,
                CDICircuitBreakerTest.class,
                CDIFallbackTest.class,
                CDIRetryTest.class,
                CDITimeoutTest.class
})
public class FATSuite {}
