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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * This subclass of {@link ReferenceQueue} exists solely to cast the return types
 * to a more specific subtype of {@link Reference}.
 * 
 * @param <V> the 'value' or referent type
 * @param <R> the reference type to use
 */

@SuppressWarnings("unchecked")
public class RefQueue<V, R extends Reference<V>> extends ReferenceQueue<V> {
    @Override
    public R poll() {
        return (R) super.poll();
    }

    @Override
    public R remove() throws InterruptedException {
        return (R) super.remove();
    }

    @Override
    public R remove(long timeout) throws InterruptedException {
        return (R) super.remove(timeout);
    }
}
