/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.websphere.csi;

/**
 * A <code>StatefulSessionKeyFactory</code> constructs unique identifiers
 * that may be used as the "primary key" for stateful session beans. <p>
 */
public interface StatefulSessionKeyFactory {
    /**
     * Return a new <code>StatefulSessionKey</code> instance. <p>
     */
    public StatefulSessionKey create(); //87918.8(2)

    /**
     * Return a new <code>StatefulSessionKey</code> instance. <p>
     *
     * @param bytes A byte array to construct the key with
     */
    public StatefulSessionKey create(byte[] bytes);
}
