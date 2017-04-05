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
package com.ibm.ejs.container.lock;

/**
 * A <code>DeadlockException</code> is thrown whenever the container's
 * <code>LockManager</code> determines that waiting to acquire a lock
 * would result in a deadlock. <p>
 */

public class DeadlockException
                extends LockException
{
    private static final long serialVersionUID = 6641400854594164270L;

    /**
     * Create a new <code>DeadlockException</code> instance. <p>
     */

    public DeadlockException() {
        super();
    } // DeadlockException

} // DeadlockException
