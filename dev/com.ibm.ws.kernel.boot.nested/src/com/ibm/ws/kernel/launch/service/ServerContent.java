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
package com.ibm.ws.kernel.launch.service;

import java.io.IOException;

import com.ibm.ws.kernel.boot.internal.BootstrapConstants;

public interface ServerContent {

    public final static String REQUEST_SERVER_CONTENT_PROPERTY = BootstrapConstants.REQUEST_SERVER_CONTENT_PROPERTY;

    /**
     * Get an array of platform local absolute paths that represent the entire on-disk content of the app server.
     * osRequest can contain requested platform filtering information
     */
    String[] getServerContentPaths(String osRequest) throws IOException;
}
