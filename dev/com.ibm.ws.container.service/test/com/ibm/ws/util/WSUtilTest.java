/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests expected behavior of WSUtil - which currently has one method, resolveURI
 */
public class WSUtilTest {

    /**
     * Negative test - ensures that we receive the proper exception and message when specifying
     * an invalid URL containing more parent directories than actual directories.
     */
    @Test
    public void resolveURI_invalidArgument_tooManyParents() {
        String badURI = "/dir1/dir2/../../../blah"; // note three parents, but only two directories deep
        try {
            WSUtil.resolveURI(badURI);
            fail("Did not throw IllegalArgumentException for invalid URI parameter");
        } catch (IllegalArgumentException ex) {
            assertTrue("Did not contain expected exception text indicating more parents than directory depth",
                       ex.getMessage().contains("is invalid because it contains more references to parent directories (\"..\") than is possible."));
        } catch (Throwable t) {
            fail("Wrong exception thrown.  Expected IllegalArgumentException, caught " + t);
        }
    }

    /**
     * Ensure that parent directories ("..") are correctly resolved.
     */
    @Test
    public void resolveURI_resolveParents() {
        String unresolvedURI = "/usr/servers/../../etc";
        assertEquals("Did not correctly resolve parent directories", "/etc", WSUtil.resolveURI(unresolvedURI));
    }
}
