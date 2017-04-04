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
 * A <code>Locker</code> is any object that can obtain locks from the
 * <code>LockManager</code>. <p>
 * 
 * The <code>Locker</code> is the owner (as far as the lock manager is
 * concerened) of any locks acquired from the lock manager. A
 * <code>Locker</code> instance must be provided when acquiring a lock
 * from the <code>LockManager</code> and that same instance must be
 * supplied when releasing the lock or when attempting to convert it
 * from shared to exclusive mode. <p>
 * 
 * The lock manager depends upon being able to determine the identity
 * of <code>Locker</code> instance. For this reason <code>Locker</code>
 * instance must correctly implement the <code>hashCode</code> and
 * <code>equals</code> methods. <p>
 * 
 */

public interface Locker
                extends LockProxy
{
    /**
     * Return the lock mode this locker currently holds the lock
     * identified by lockName in. <p>
     */

    public int getLockMode(Object lockName);

} // Locker

