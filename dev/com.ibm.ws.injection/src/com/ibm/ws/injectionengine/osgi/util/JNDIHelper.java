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
package com.ibm.ws.injectionengine.osgi.util;

/**
 *
 */
public class JNDIHelper {
    /**
     * Return true if a JNDI name has a scheme.
     */
    public static boolean hasJNDIScheme(String jndiName) {
        int colonIndex = jndiName.indexOf(':');
        int slashIndex = jndiName.indexOf('/');
        return colonIndex != -1 && (slashIndex == -1 || colonIndex < slashIndex);
    }
}
