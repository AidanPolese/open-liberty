//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.
// 03/08/05 gilgen      189250.2        return object on put if pool is full.

package com.ibm.wsspi.channelfw.objectpool;

/**
 * Interface for Object Pool usage.
 */
// TODO make this use Generics
public interface ObjectPool {
    /**
     * Get an object from the pool for usage.
     * 
     * @return Object
     */
    Object get();

    /**
     * Put an object into the pool.
     * 
     * @param o
     * @return object that wouldn't fit in the pool (may not be the same as the
     *         one passed in!)
     */
    Object put(Object o);

}
