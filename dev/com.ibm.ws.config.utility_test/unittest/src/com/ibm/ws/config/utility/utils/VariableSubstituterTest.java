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
package com.ibm.ws.config.utility.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Test;

/**
 * Tests for com.ibm.ws.config.utility.utils.VariableSubstituter
 */
public class VariableSubstituterTest {
    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.VariableSubstituter.java#substitute(java.lang.StringBuilder, java.util.HashMap)}.
     */
    @Test
    public void testNotNullVariableSubstitute() {
        HashMap<String, String> hm = new HashMap<String, String>();
        StringBuilder text = new StringBuilder("<quickStartSecurity userName=\"${adminUser}\" userPassword=\"${adminPassword}\"/>");
        hm.put("adminUser", "Thomas");
        assertNotNull("FAIL: did not get back message text", VariableSubstituter.substitute(text, hm));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.VariableSubstituter.java#substitute(java.lang.StringBuilder, java.util.HashMap)}.
     */
    @Test
    public void testEmptyVariableSubstitute() {
        HashMap<String, String> hm = new HashMap<String, String>();
        StringBuilder text = new StringBuilder("<quickStartSecurity userName=\"${adminUser}\" userPassword=\"${adminPassword}\"/>");
        hm.put("adminUser", "");
        String expectedSnippet = "<quickStartSecurity userName=\"\" userPassword=\"${adminPassword}\"/>";
        assertEquals("FAIL: Did not replace adminUser with Thomas", expectedSnippet, VariableSubstituter.substitute(text, hm).toString());
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.VariableSubstituter.java#substitute(java.lang.StringBuilder, java.util.HashMap)}.
     */
    @Test
    public void testValidVariableSubstitute() {
        HashMap<String, String> hm = new HashMap<String, String>();
        StringBuilder text = new StringBuilder("<quickStartSecurity userName=\"${adminUser}\" userPassword=\"${adminPassword}\"/>");
        hm.put("adminUser", "Thomas");
        String expectedText = "<quickStartSecurity userName=\"Thomas\" userPassword=\"${adminPassword}\"/>";
        assertEquals("FAIL: Did not replace adminUser with Thomas", expectedText, VariableSubstituter.substitute(text, hm).toString());
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.VariableSubstituter.java#getAllVariables(java.lang.StringBuilder)}.
     */
    @Test
    public void testGetAllVariables() {
        String expectedValues = "[adminUser, adminPassword]";
        String exampleConfigSnippet = "<quickStartSecurity userName=\"${adminUser}\" userPassword=\"${adminPassword}\"/>";
        assertEquals("FAIL: Did not return the list", expectedValues,
                     VariableSubstituter.getAllVariables(new StringBuilder(exampleConfigSnippet)).toString());
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.VariableSubstituter.java#getAllVariables(java.lang.StringBuilder)}.
     */
    @Test
    public void testEmptyGetAllVariables() {
        String expectedValues = "[]";
        String exampleConfigSnippet = "<quickStartSecurity userName=\"\" userPassword=\"\"/>";
        assertEquals("FAIL: Did not return empty list", expectedValues,
                     VariableSubstituter.getAllVariables(new StringBuilder(exampleConfigSnippet)).toString());
    }
}
