/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.adaptable.module;

/**
 *
 */
public interface NonPersistentCache {

    /**
     * Stores some data associated with the given container/entry within
     * non persistent in memory cache associated with its overlay instance.
     * 
     * @param owner Class of caller setting data, allows multiple adapters to cache against a given container/entry.
     * @param data Data to store for caller.
     */
    public void addToCache(Class<?> owner, Object data);

    /**
     * Removes some data associated with the given container/entry within
     * non persistent in memory cache associated with its overlay instance.
     * 
     * @param owner Class of caller getting data, allows multiple adapters to cache against a given container/entry.
     */
    public void removeFromCache(Class<?> owner);

    /**
     * Obtains some data associated with the given container/entry within
     * non persistent in memory cache associated with its overlay instance.
     * 
     * @param owner Class of caller getting data, allows multiple adapters to cache against a given container/entry.
     * @returns Cached data if any was held, or null if none was known.
     */
    public Object getFromCache(Class<?> owner);

}
