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

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerPathTest {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void getDefaultDirs() throws IllegalArgumentException, IOException {
        final String userDir = "/home/Liberty/usr/";
        final String serverName = "myServer";

        assertEquals("/home/Liberty/", ServerPath.INSTALL_DIR.getDefault(userDir, serverName));
        assertEquals("/home/Liberty/usr/", ServerPath.USER_DIR.getDefault(userDir, serverName));
        assertEquals("/home/Liberty/usr/servers/myServer/", ServerPath.OUTPUT_DIR.getDefault(userDir, serverName));
        assertEquals("/home/Liberty/usr/servers/myServer/", ServerPath.CONFIG_DIR.getDefault(userDir, serverName));
        assertEquals("/home/Liberty/usr/shared/apps/", ServerPath.SHARED_APPS_DIR.getDefault(userDir, serverName));
        assertEquals("/home/Liberty/usr/shared/config/", ServerPath.SHARED_CONFIG_DIR.getDefault(userDir, serverName));
        assertEquals("/home/Liberty/usr/shared/resources/", ServerPath.SHARED_RESC_DIR.getDefault(userDir, serverName));
    }
}
