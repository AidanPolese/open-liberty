/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1999
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.lock;

/**
 * A <code>LockProxy</code> may be used by the <code>LockManager</code>
 * as a placeholder for a lock that is acuired/released by a single
 * <code>Locker</code> at a time. <p>
 */

public interface LockProxy {

    /**
     * Returns true iff this <code>LockProxy</code> instance is actually
     * a full heavy-weight lock instance (as opposed to a placeholder). <p>
     */

    public boolean isLock();

} // LockProxy

