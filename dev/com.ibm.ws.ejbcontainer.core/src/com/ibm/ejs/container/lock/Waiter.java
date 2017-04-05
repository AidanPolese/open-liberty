/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2001
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.lock;

/**
 * A <code>Waiter</code> is a helper object used to control threads
 * that are waiting to acquire a <code>Lock</code>. <p>
 * 
 * Multiple threads may be waiting on a <code>Waiter</code>, although
 * since EJBs are inherently single-threaded, this is very unlikely. <p>
 */

class Waiter {

    /*
     * Lock instance this waiter is waiting on.
     */

    final Lock theLock;

    /*
     * Locker instance that is waiting on lock.
     * This defines the identity of this waiter.
     */

    final Locker locker;

    /*
     * The lock mode this waiter is waiting to acquire lock in.
     */

    int mode;

    /**
     * True iff the lock this waiter was waiting for has been released.
     */

    boolean released;

    /**
     * Create a new <code>Waiter</code> instance. <p>
     */

    Waiter(Lock l, Locker locker, int mode)
    {
        this.theLock = l;
        this.locker = locker;
        this.mode = mode;
        this.released = false;
    } // Waiter

} // Waiter

