/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility;

import static org.junit.Assert.assertNotNull;

import org.junit.Assume;
import org.junit.Test;

import com.ibm.ws.config.utility.actions.HelpAction;

/**
 *
 */
public class HelpTaskTest extends TestRepository {
    private static final String SLASH = System.getProperty("file.separator");
    protected final String PATH_TO_FILES = "publish" + SLASH + "files";

    /**
     * Test method for {@link com.ibm.ws.config.utility.actions.HelpAction#getScriptUsage()}.
     * 
     * @throws Exception
     */
    @Test
    public void testNotNullGetScriptUsage() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertNotNull("FAIL: did not get back script usage from configutility script",
                      HelpAction.getScriptUsage());
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.actions.HelpAction#getHelpOptions()}.
     */
    @Test
    public void testNotNullGetHelpOptions() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertNotNull("FAIL: did not get back help from configutility script",
                      HelpAction.getHelpOptions());
    }
}
