/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.fat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ StackTraceFilteringForLoggedExceptionParametersTest.class, StackTraceFilteringForLoggedExceptionWithACauseParametersTest.class,
                StackTraceFilteringForPrintedExceptionTest.class, StackTraceFilteringForPrintedExceptionWithIBMCodeAtTopTest.class,
                StackTraceFilteringForNoClassDefFoundErrorTest.class, StackTraceFilteringForBadlyWrittenThrowableTest.class,
                StackTraceFilteringForUserFeatureExceptionTest.class, StackTraceFilteringForIBMFeatureExceptionTest.class,
                StackTraceFilteringForSpecificationClassesExceptionTest.class, InvalidTraceSpecificationTest.class,
                HealthCenterTest.class, TestHideMessages.class, TestHideMsgDefinedBootstrap.class, IsoDateFormatTest.class })
public class FATSuite {

}
