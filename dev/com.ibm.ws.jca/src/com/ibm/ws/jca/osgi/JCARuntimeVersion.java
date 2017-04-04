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
package com.ibm.ws.jca.osgi;

import org.osgi.framework.Version;

public interface JCARuntimeVersion {

    public static final String VERSION = "version";

    public static final Version VERSION_1_7 = new Version(1, 7, 0);
    public static final Version VERSION_1_6 = new Version(1, 6, 0);

    public Version getVersion();

}
