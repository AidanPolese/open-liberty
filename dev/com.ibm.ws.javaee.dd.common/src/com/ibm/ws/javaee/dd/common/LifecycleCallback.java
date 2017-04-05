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
 * Represents the lifecycle-callbackType type from the javaee XSD.
 */
public interface LifecycleCallback
                extends InterceptorCallback
{
    /**
     * @return &lt;lifecycle-callback-class>, or null if unspecified
     */
    @Override
    String getClassName();

    /**
     * @return &lt;lifecycle-callback-method>
     */
    @Override
    String getMethodName();
}
