/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authentication.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.Subject;

/**
 * The cache object contains the subject to be placed on the cache as well as the keys used to cache.
 */
public class CacheObject {

    private final Subject subject;

    private final List<Object> lookupKeys = Collections.synchronizedList(new ArrayList<Object>(8));

    public CacheObject(Subject subject) {
        this.subject = subject;
    }

    public void addLookupKey(Object key) {
        if (key != null) {
            lookupKeys.add(key);
        }
    }

    /**
     * IMPORTANT: It is imperative that the user manually synchronize on the returned list
     * (using the synchronized block) when iterating over it . Failure to follow this
     * advice may result in non-deterministic behavior.
     * 
     * @return the list of lookup keys in the cache object
     */
    public List<Object> getLookupKeys() {
        return lookupKeys;
    }

    public Subject getSubject() {
        return this.subject;
    }
}
