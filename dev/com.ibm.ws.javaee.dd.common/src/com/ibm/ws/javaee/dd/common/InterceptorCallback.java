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
package com.ibm.ws.javaee.dd.common;

/**
 * Represents elements like lifecycle-callbackType from the javaee XSD or
 * around-timeout or around-invoke as described by the Interceptors spec.
 */
public interface InterceptorCallback
{
    /**
     * @return the callback class, or null if unspecified
     */
    String getClassName();

    /**
     * @return the callback method
     */
    String getMethodName();
}
