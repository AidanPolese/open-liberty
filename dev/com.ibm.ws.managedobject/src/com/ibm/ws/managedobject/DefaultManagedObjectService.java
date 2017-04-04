/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.managedobject;

/**
 * DefaultManagedObjectService is a marker interface that provides a mechanism to obtain
 * a reference to the default implementation of the {@link ManagedObjectService). <p>
 *
 * This is useful for implementations of ManagedObjectService that would like to delegate
 * method calls to the default implementation (for example, when the CDI provided
 * ManagedObjectService does not need to manage a particular object).
 */
public interface DefaultManagedObjectService extends ManagedObjectService {
    // No additional methods are provided
}
