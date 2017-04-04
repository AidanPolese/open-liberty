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
package com.ibm.ws.javaee.version;

import org.osgi.framework.Version;

public abstract class JavaEEVersion {
    public static final String VERSION = "version";
    public static final Version DEFAULT_VERSION = new Version(6, 0, 0);

    public static final Version VERSION_6_0 = new Version(6, 0, 0);
    public static final Version VERSION_7_0 = new Version(7, 0, 0);
}
