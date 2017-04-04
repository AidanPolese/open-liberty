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
package com.ibm.ws.classloading.internal.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/** A weak reference that remembers a key */
class WeakKeyedRef<K, V> extends WeakReference<V> implements KeyedRef<K, V> {
    private final K key;

    WeakKeyedRef(K key, V value, ReferenceQueue<V> q) {
        super(value, q);
        this.key = key;
    }

    @Override
    public K getKey() {
        return key;
    }
}
