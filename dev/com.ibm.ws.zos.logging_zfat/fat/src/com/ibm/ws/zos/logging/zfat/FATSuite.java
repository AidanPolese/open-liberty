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
package com.ibm.ws.zos.logging.zfat;

import static org.junit.Assert.assertNotNull;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import componenttest.topology.impl.LibertyServer;

/**
 * Collection of all example tests
 */
@RunWith(Suite.class)
/*
 * The classes specified in the @SuiteClasses annotation
 * below should represent all of the test cases for this FAT.
 */
@SuiteClasses( { ZosLoggingTest.class })
public class FATSuite {
    
    /**
     * Wait for the "smarter planet" message in the log.
     * 
     * Note: this method resets the log marks before searching the log.
     */
    public static void waitForSmarterPlanet(LibertyServer server) {
        
        server.resetLogMarks();
        assertNotNull("The CWWKF0011I smarter planet message not found for server: " + server.getServerRoot(),
                      server.waitForStringInLog("CWWKF0011I"));
    }
    
}
