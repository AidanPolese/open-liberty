/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.zos;

/**
 * The {@code NativeMethodManager} is responsible for managing the resolution
 * and linking of native methods to classes.
 */
public interface NativeMethodManager {

    /**
     * Register native methods from the core DLL for the specified class.
     * 
     * @param clazz the class to link native methods for
     * 
     * @throws UnsatisfiedLinkError if an error occurs during resolution or registration
     */
    public void registerNatives(Class<?> clazz);

    /**
     * Register native methods from the core DLL for the specified class. The
     * specified object array will be passed along to registration callback.
     * 
     * @param clazz the class to link native methods for
     * @param extraInfo extra information that will be passed to the registration hook
     * 
     * @throws UnsatisfiedLinkError if an error occurs during resolution or registration
     */
    public void registerNatives(Class<?> clazz, Object[] extraInfo);

}
