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
package com.ibm.ws.config.utility.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Test;

import com.ibm.ws.config.utility.TestRepository;

/**
 *
 */
public class CommandUtilsTest extends TestRepository {

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     * 
     * @throws Exception
     */
    @Test
    public void testInputConsoleNotAvailable() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("FAIL: did not get back input console not available text", "Input console is not available.",
                     CommandUtils.getMessage("error.inputConsoleNotAvailable"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testMissingIO() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String arg = "ErrorCode";
        String msg = CommandUtils.getMessage("error.missingIO", arg);
        assertNotNull("FAIL: did not get back message text", msg);
        assertTrue("FAIL: message did not contain the expected argument 'ErrorCode'. Got:" + msg, msg.contains(arg));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testErrorMessage() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String arg = "myError";
        String msg = CommandUtils.getMessage("error", arg);
        assertNotNull("FAIL: did not get back message text", msg);
        assertTrue("FAIL: message did not contain the expected argument 'myError'. Got: " + msg,
                   msg.contains(arg));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testFailedToListAllSnippets() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("FAIL: did not get back Failed to list all configuration snippets.",
                     "Failed to list all configuration snippets.",
                     CommandUtils.getMessage("faiedToListAllSnippets"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testInsufficientArgs() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("FAIL: did not get back Insufficient arguments.",
                     "Insufficient arguments.",
                     CommandUtils.getMessage("insufficientArgs"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testGetListOfAllSnippets() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertTrue("FAIL: did not get back input console not available text",
                   CommandUtils.getMessage("getListOfAllSnippets").contains("Retrieving a list of all configuration snippets"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testFindSnippets() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String arg = "myError";
        String msg = CommandUtils.getMessage("findSnippet", arg);
        assertNotNull("FAIL: did not get back message text", msg);
        assertTrue("FAIL: message did not contain the expected argument 'myError'. Got: " + msg,
                   msg.contains(arg));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testSnippetNotFound() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String arg = "myError";
        String msg = CommandUtils.getMessage("snippetNotFound", arg);
        assertNotNull("FAIL: did not get back message text", msg);
        assertTrue("FAIL: message did not contain the expected argument 'myError'. Got: " + msg,
                   msg.contains(arg));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testInvalidArg() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String arg = "myError";
        String msg = CommandUtils.getMessage("invalidArg", arg);
        assertNotNull("FAIL: did not get back message text", msg);
        assertTrue("FAIL: message did not contain the expected argument 'myError'. Got: " + msg,
                   msg.contains(arg));
    }

    @Test
    public void testMissingArg() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String arg = "myError";
        String msg = CommandUtils.getMessage("missingArg", arg);
        assertNotNull("FAIL: did not get back message text", msg);
        assertTrue("FAIL: message did not contain the expected argument 'myError'. Got: " + msg,
                   msg.contains(arg));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testMissingValue() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String arg = "myError";
        String msg = CommandUtils.getMessage("missingValue", arg);
        assertNotNull("FAIL: did not get back message text", msg);
        assertTrue("FAIL: message did not contain the expected argument 'myError'. Got: " + msg,
                   msg.contains(arg));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testMissingConfigSnippetName() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String expectedMsg = "Target configuration snippet was not specified.";
        String msg = CommandUtils.getMessage("missingConfigSnippetName");
        assertNotNull("FAIL: did not get back message text", msg);
        assertEquals("FAIL: message was not Target configuration snippet was not specified. Got: " + msg, expectedMsg, msg);
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testHelpOption() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String expectedMsg = "Target configuration snippet was not specified.";
        String msg = CommandUtils.getMessage("missingConfigSnippetName");
        assertNotNull("FAIL: did not get back message text", msg);
        assertEquals("FAIL: message was not Target configuration snippet was not specified. Got: " + msg, expectedMsg, msg.trim());
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testGetOptionConfigOptions() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertNotNull("FAIL: did not get back message text", CommandUtils.getOption("config.options"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.CommandUtils#getMessage(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void getOptions() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertNotNull("FAIL: did not get back options bundle",
                      CommandUtils.getOptions());
    }

}
