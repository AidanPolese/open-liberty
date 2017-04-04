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
 * A <code>LockReleasedException</code> is thrown whenever a waiting
 * request to acquire a lock managed by the lock manager is interrupted
 * because the lock has been released by another thread. <p>
 */

public class LockReleasedException
                extends LockException
{
    private static final long serialVersionUID = -1316438387962623085L;

    /**
     * Create a new <code>LockReleasedException</code> instance. <p>
     */

    public LockReleasedException() {
        super();
    } // LockReleasedException

} // LockReleasedException
