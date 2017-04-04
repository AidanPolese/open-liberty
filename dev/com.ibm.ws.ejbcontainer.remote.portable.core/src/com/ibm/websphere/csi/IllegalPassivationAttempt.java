/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * <code>IllegalPassivationAttempt</code> is thrown by
 * <code>ManagedContainer</code> during a <code>passivate()</code>
 * or <code>passivateAll</code> operation when a bean or beans in the
 * container cache cannot be passivated.
 * <p>
 * 
 * @see ManagedContainer
 * 
 */

public class IllegalPassivationAttempt
                extends CSIException
{
    private static final long serialVersionUID = 2504861767350634798L;
}
