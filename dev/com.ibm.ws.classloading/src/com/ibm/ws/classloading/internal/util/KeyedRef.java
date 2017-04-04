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

/**
 * A reference that also remembers a key. This is useful for cleaning up a weak-valued map.
 */
public interface KeyedRef<K, V> extends Ref<V> {
    K getKey();
}
