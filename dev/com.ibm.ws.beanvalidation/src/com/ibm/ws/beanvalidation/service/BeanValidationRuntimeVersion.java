/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation.service;

import org.osgi.framework.Version;

public abstract class BeanValidationRuntimeVersion {
    public static final String VERSION = "version";

    public static final Version VERSION_1_0 = new Version(1, 0, 0);
    public static final Version VERSION_1_1 = new Version(1, 1, 0);
}
