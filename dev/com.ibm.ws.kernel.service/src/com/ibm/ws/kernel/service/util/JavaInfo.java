/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.kernel.service.util;

/**
 * API for reading information related to the JDK
 */
public class JavaInfo {

    public static enum Vendor {
        IBM,
        ORACLE,
        UNKNOWN
    }

    private static JavaInfo instance;

    private final int MAJOR;
    private final int MINOR;
    private final Vendor VENDOR;

    private JavaInfo() {
        String version = PrivHelper.getProperty("java.version");
        String[] versionElements = version.split("\\D"); // split on non-digits

        // Pre-JDK 9 the java.version is 1.MAJOR.MINOR
        // Post-JDK 9 the java.version is MAJOR.MINOR
        int i = Integer.valueOf(versionElements[0]) == 1 ? 1 : 0;
        MAJOR = Integer.valueOf(versionElements[i++]);

        if (i < versionElements.length)
            MINOR = Integer.valueOf(versionElements[i]);
        else
            MINOR = 0;

        String vendor = PrivHelper.getProperty("java.vendor").toLowerCase();
        if (vendor.contains("ibm"))
            VENDOR = Vendor.IBM;
        else if (vendor.contains("oracle"))
            VENDOR = Vendor.ORACLE;
        else
            VENDOR = Vendor.UNKNOWN;
    }

    private static JavaInfo instance() {
        if (instance == null)
            instance = new JavaInfo();
        return instance;
    }

    public static int majorVersion() {
        return instance().MAJOR;
    }

    public static int minorVersion() {
        return instance().MINOR;
    }

    public static Vendor vendor() {
        return instance().VENDOR;
    }
}
