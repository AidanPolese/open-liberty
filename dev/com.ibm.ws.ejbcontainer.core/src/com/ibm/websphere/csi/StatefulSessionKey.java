/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

import java.io.Serializable;

/**
 * The <code>StatefulSessionKey</code> interface serves to mark all
 * instances of a key instance returned by a
 * <code>StatefulSessionKeyFactory</code>. <p>
 */
public interface StatefulSessionKey
                extends Serializable
{
    /**
     * Return a string containing [a-zA-Z0-9_.-] characters only. Typically,
     * the result is a formatted UUID string.
     */
    public String toString();

    // New BeanId support, provide a method to return the bytes representing
    // this key. Used in improving the performance of BeanIds.
    public byte[] getBytes();
}
