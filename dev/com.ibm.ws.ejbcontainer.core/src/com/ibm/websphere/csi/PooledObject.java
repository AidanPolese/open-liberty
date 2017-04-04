/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * A PooledObject receives notifications from the Pool when the object is placed into the Pool
 * and when the object is discarded from the Pool.
 * 
 * @see Pool
 * @see PoolManager
 */

public interface PooledObject {

    /**
     * Called after the pool discards the object.
     * This gives the <code>PooledObject</code> an opportunity to perform any
     * required clean up.
     */
    public void discard();

    /**
     * Called prior to the object being placed back in the pool.
     * This gives the <code>PooledObject</code> an opportunity to re-initialize
     * its internal state.
     */
    public void reset();

}
