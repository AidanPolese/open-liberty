/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.server.rest.helpers;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilsHelperTest {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void getParentDir() {
        assertEquals("C:/temp", FileUtilsHelper.getParentDir("C:/temp/wlp.zip"));
        assertEquals("C:/temp", FileUtilsHelper.getParentDir("C:/temp/wlp"));
        assertEquals("C:/", FileUtilsHelper.getParentDir("C:/temp.zip"));
        assertEquals("C:/", FileUtilsHelper.getParentDir("C:/temp"));
        assertEquals("/home", FileUtilsHelper.getParentDir("/home/myDir.zip"));
        assertEquals("/home", FileUtilsHelper.getParentDir("/home/myDir"));
        assertEquals("/", FileUtilsHelper.getParentDir("/home"));
        assertEquals("/", FileUtilsHelper.getParentDir("/home.zip"));

    }
}
