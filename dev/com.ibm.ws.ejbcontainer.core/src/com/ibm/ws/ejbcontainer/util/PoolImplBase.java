/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.util;

/**
 * Abstract base class for Pool implementations
 */
public abstract class PoolImplBase implements Pool {

    /**
     * True iff an attempt to retrieve an instance from this pool
     * hasn't happened recently. <p>
     */
    protected boolean inactive = true;

    /**
     * Pool manager that "owns" this pool.
     */
    protected PoolManagerImpl poolMgr;

    /**
     * Retrieve an object from this pool. If the retrieved object implements the
     * PooledObject interface, the onGetFromPool method will be called.
     * 
     * This method will return null if the pool is empty.
     */
    public abstract Object get();

    /**
     * Return an object instance to this pool. <p>
     * 
     * If there is no room left in the pool the instance will be
     * discarded, and if DiscardStrategy interface is being used,
     * the appropriate callback method will be called.
     */
    public abstract void put(Object o);

    /**
     * Remove some or all of the elements from this pool, down to its
     * minimum value. Discontinue if the pool becomes active while draining.
     * Discarded objects will have appropriate callback method called.
     */
    abstract void periodicDrain();

    /**
     * Remove all of the elements from this pool. Discarded objects will
     * have appropriate callback method called.
     */
    abstract void completeDrain();

    /**
     * Prevents the pool from accepting any further pooled instances.
     */
    // F73236
    abstract void disable();

    /**
     * Destroy this object pool and discard all the objects in it.
     */
    public final void destroy()
    {
        poolMgr.remove(this);
        disable(); // F73236
        completeDrain();
    } // destroy

} // PoolImplBase
