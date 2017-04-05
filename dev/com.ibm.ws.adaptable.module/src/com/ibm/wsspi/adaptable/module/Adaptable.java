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
package com.ibm.wsspi.adaptable.module;

/**
 * Provides the ability for an object implementing the interface to convert itself to another type of object.
 */
public interface Adaptable {
    /**
     * Adapt this object into an object of the passed type, if possible.
     * 
     * @param <T> The type to adapt to & return.
     * @param adaptTarget The type to adapt to.
     * @return instance of type <T> if successful, null otherwise.
     */
    <T> T adapt(Class<T> adaptTarget) throws UnableToAdaptException;
}
