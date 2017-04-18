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
package com.ibm.ws.repository.resolver.internal.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ FeatureResourceTest.class,
               IFixResourceTest.class,
               ProductRequirementTest.class,
               ProductResourceTest.class,
               ResourceImplTest.class,
               LpmResourceTest.class,
               GenericMetadataRequirementTest.class })
public class AllResourceTests {

}
