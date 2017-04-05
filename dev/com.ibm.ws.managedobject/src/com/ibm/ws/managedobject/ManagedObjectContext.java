/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.managedobject;

import java.io.Serializable;

/**
 * The context for creating a managed object.
 */
public interface ManagedObjectContext extends Serializable
{
    /**
     * Return context data about an object to be created. The available types
     * vary depending on the factory used to create the ManagedObject.
     *
     * @param klass the data type
     * @return the context data, or null if the data type is unrecognized
     */
    <T> T getContextData(Class<T> klass);

    /**
     * Release any resources associated with this state. This method must not
     * throw exceptions. If cleanup of the state results in an exception, it
     * must be ignored.
     */

    void release();
}
