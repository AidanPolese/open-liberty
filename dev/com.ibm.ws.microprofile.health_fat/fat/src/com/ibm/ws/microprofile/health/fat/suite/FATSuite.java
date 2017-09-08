package com.ibm.ws.microprofile.health.fat.suite;

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

import com.ibm.ws.microprofile.health.fat.CDIRetryTest;

@RunWith(Suite.class)
@SuiteClasses({
                CDIRetryTest.class
})

public class FATSuite {}
