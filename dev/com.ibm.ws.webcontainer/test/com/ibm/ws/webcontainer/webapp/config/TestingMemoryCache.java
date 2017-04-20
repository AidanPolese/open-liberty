/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.webapp.config;

import java.util.HashMap;

import com.ibm.wsspi.adaptable.module.NonPersistentCache;

/**
 *
 */
public class TestingMemoryCache implements NonPersistentCache {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.adaptable.module.NonPersistentCache#addToCache(java.lang.Class, java.lang.Object)
     */

    HashMap<Class<?>, Object> dataStore = new HashMap<Class<?>, Object>();

    @Override
    public void addToCache(Class<?> owner, Object data) {
        dataStore.put(owner, data);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.adaptable.module.NonPersistentCache#removeFromCache(java.lang.Class)
     */
    @Override
    public void removeFromCache(Class<?> owner) {
        dataStore.remove(owner);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.adaptable.module.NonPersistentCache#getFromCache(java.lang.Class)
     */
    @Override
    public Object getFromCache(Class<?> owner) {
        return dataStore.get(owner);
    }

    public void deleteAll() {
        dataStore.clear();
    }

}
