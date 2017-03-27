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
package com.ibm.ws.jmx.connector.client.rest.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ObjectIdentityCache {

    private final Map<ObjectReference, Integer> identityMap = new HashMap<ObjectReference, Integer>();
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
    private int nextIdentity = Integer.MIN_VALUE + 1;

    int getObjectIdentity(Object o) {
        // Clean up cleared references
        ObjectReference clearedRef = (ObjectReference) referenceQueue.poll();
        while (clearedRef != null) {
            identityMap.remove(clearedRef);
            clearedRef = (ObjectReference) referenceQueue.poll();
        }

        if (o == null)
            return Integer.MIN_VALUE;

        ObjectReference ref = new ObjectReference(o, referenceQueue);
        if (identityMap.containsKey(ref)) {
            return identityMap.get(ref);
        } else {
            if (nextIdentity == Integer.MAX_VALUE)
                throw new IllegalStateException();
            final int identity = nextIdentity++;
            identityMap.put(ref, identity);
            return identity;
        }
    }

    private static class ObjectReference extends WeakReference<Object> {
        private final int hashCode;

        ObjectReference(Object referent, ReferenceQueue<Object> queue) {
            super(referent, queue);
            hashCode = referent.hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof ObjectReference) {
                ObjectReference other = (ObjectReference) o;
                return other.get() == get();
            } else {
                return false;
            }
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
