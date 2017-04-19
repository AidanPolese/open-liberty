/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.repository.resolver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.repository.resolver.internal.FixFeatureComparatorTest;
import com.ibm.ws.repository.resolver.internal.resource.AllResourceTests;

/**
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ AllResourceTests.class,
               FixFeatureComparatorTest.class })
public class AllTests {

}
