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
package com.ibm.ws.javaee.dd.ejb;

/**
 * Represents &lt;remove-method>.
 */
public interface RemoveMethod
{
    /**
     * @return &lt;bean-method>
     */
    NamedMethod getBeanMethod();

    /**
     * @return true if &lt;retain-if-exception> is specified
     * @see #isRetainIfException
     */
    boolean isSetRetainIfException();

    /**
     * @return &lt;retain-if-exception> if specified
     * @see #isSetRetainIfException
     */
    boolean isRetainIfException();
}
