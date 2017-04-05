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
package com.ibm.ws.artifact.overlay.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Common Class to hold cached data for Overlay Impls.
 */
public class OverlayCache {
    final Map<String, Map<Class, Object>> cache = new HashMap<String, Map<Class, Object>>();

    public synchronized void addToCache(String path, Class owner, Object data) {
        Map<Class, Object> classStore = cache.get(path);
        if (classStore == null) {
            classStore = new HashMap<Class, Object>();
            cache.put(path, classStore);
        }
        classStore.put(owner, data);
    }

    public synchronized void removeFromCache(String path, Class owner) {
        Map<Class, Object> classStore = cache.get(path);
        if (classStore != null) {
            classStore.remove(owner);
            if (classStore.isEmpty()) {
                cache.remove(path);
            }
        }
    }

    public synchronized Object getFromCache(String path, Class owner) {
        Map<Class, Object> classStore = cache.get(path);
        if (classStore != null) {
            return classStore.get(owner);
        } else {
            return null;
        }
    }

}
