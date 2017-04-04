/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * A PooledObjectMaster is used as the "master copy" for creating instances where creation
 * through the default (no-arg) constructor will not suffice. Typically this is in cases
 * where one needs to copy instance variables from the master copy into each new created copy.
 * 
 * @see PooledObject
 * @see Pool
 * @see PoolManager
 */

public interface PooledObjectMaster extends PooledObject {

    /**
     * Called when the Pool requires a new instance of the PooledObject. Note that
     * a PooledObject type is returned (not a PooledObjectMaster); this is because
     * typically a single PooledObjectMaster is used to create multiple copies of
     * PooledObject. (If necessary, the implementation of this method may of course
     * return a PooledObjectMaster, which the caller can then cast back to
     * PooledObjectMaster as needed.)
     */
    public PooledObject newInstance();

}
