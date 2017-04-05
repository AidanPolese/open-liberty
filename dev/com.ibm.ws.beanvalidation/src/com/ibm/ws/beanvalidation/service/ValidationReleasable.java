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
package com.ibm.ws.beanvalidation.service;

/**
 * Interface to allow any implementer to specify how to release the particular
 * kind of validation releasable.
 * 
 * @param <T> the validation object that is stored to be released
 */
public interface ValidationReleasable<T> {

    /**
     * Release any resources that creating this {@link ValidationReleasable} required.
     */
    public void release();

    /**
     * Get the instance represented by this {@link ValidationReleasable}
     * 
     * @return the instance
     */
    public T getInstance();
}
