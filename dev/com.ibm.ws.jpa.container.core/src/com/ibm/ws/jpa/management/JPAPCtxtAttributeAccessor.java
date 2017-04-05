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
package com.ibm.ws.jpa.management;

import javax.persistence.PersistenceContext;

/**
 * Accessor for annotation attributes that were added after JPA 2.0.
 */
public class JPAPCtxtAttributeAccessor {
    /**
     * Returns true if PersistenceContext.serialization() is
     * SynchronizationType.UNSYNCHRONIZED.
     */
    public boolean isUnsynchronized(PersistenceContext pCtxt) {
        return false;
    }
}
