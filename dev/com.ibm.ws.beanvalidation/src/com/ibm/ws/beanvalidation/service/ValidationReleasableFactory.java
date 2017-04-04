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

import javax.validation.ConstraintValidatorFactory;

import com.ibm.ws.managedobject.ManagedObject;

/**
 * Interface to specify the contract for creating validation releasable objects.
 */
public interface ValidationReleasableFactory {

    /**
     * Create a validation releasable object out of the class type passed in.
     * 
     * @param clazz the type of validation releasable to create
     * @return the releasable object
     */
    public <T> ManagedObject<T> createValidationReleasable(Class<T> clazz);

    /**
     * Create a ConstraintValidatorFactory as a ValidationReleasable.
     * 
     * @return the releasable ConstraintValidatorFactory
     */
    public ValidationReleasable<ConstraintValidatorFactory> createConstraintValidatorFactory();
}
