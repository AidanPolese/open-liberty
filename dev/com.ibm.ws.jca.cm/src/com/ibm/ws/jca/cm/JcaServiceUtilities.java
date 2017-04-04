/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.cm;

import java.security.AccessController;

/**
 *
 */
public class JcaServiceUtilities {
    /**
     * Set context classloader to the one for the resource adapter
     * 
     * @param raClassLoader
     * @return the current classloader
     */
    public ClassLoader beginContextClassLoader(ClassLoader raClassLoader) {
        return raClassLoader == null ? null
                        : AccessController.doPrivileged(new GetAndSetContextClassLoader(raClassLoader));
    }

    /**
     * Restore current context class loader saved when the context class loader was set to the one
     * for the resource adapter.
     * 
     * @param raClassLoader
     * @param previousClassLoader
     */
    public void endContextClassLoader(ClassLoader raClassLoader, ClassLoader previousClassLoader) {
        if (raClassLoader != null) {
            AccessController.doPrivileged(new GetAndSetContextClassLoader(previousClassLoader));
        }
    }

}
