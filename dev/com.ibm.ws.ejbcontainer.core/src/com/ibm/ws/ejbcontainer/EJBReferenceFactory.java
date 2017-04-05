/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

import com.ibm.ws.managedobject.ManagedObjectContext;

/**
 * A factory for creating client references for a specific EJB.
 */
public interface EJBReferenceFactory {
    /**
     * Creates a client reference to an EJB. For stateful EJBs, this will also
     * create a backing instance that must be removed via {@link EJBReference#remote}.
     *
     * @param context the managed object context
     * @return the client reference
     */
    EJBReference create(ManagedObjectContext context);
}
