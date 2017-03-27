/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.org.glassfish.json.fat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.org.glassfish.json.fat.tests.BasicJSONPTest;
import com.ibm.ws.org.glassfish.json.fat.tests.CustomFeatureJSONPTest;
import componenttest.custom.junit.runner.AlwaysPassesTest;

@RunWith(Suite.class)
@SuiteClasses({
               AlwaysPassesTest.class,
               BasicJSONPTest.class,
               CustomFeatureJSONPTest.class
})
public class FATSuite {}
