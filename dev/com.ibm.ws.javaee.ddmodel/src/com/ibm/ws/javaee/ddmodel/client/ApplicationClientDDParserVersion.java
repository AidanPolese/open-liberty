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
package com.ibm.ws.javaee.ddmodel.client;

import com.ibm.ws.javaee.dd.client.ApplicationClient;

public abstract class ApplicationClientDDParserVersion {
    /**
     * Service property name with a value corresponding to {@link ApplicationClient#getVersionID} that
     * indicates the maximum version that the runtime supports.
     */
    public static final String VERSION = "version";
}
