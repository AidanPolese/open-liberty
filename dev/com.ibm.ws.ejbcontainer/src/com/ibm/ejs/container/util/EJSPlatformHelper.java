/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.util;

/**
 * Override EJSPlatformHelper as provided by tWAS. z/OS on Liberty does not use
 * the split-process architecture.
 */
public class EJSPlatformHelper {
    public static boolean isZOS() {
        return false;
    }

    public static boolean isZOSCRA() {
        return false;
    }
}
