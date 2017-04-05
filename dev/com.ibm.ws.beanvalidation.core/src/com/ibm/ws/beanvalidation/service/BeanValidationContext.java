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
package com.ibm.ws.beanvalidation.service;

import java.io.InputStream;

import javax.validation.ValidationException;

/**
 * Provides BeanValidation module context data for creating the
 * module specific ValidatorFactory. <p>
 * 
 * This interface also allows different implementations for
 * traditional WebSphere and the Liberty profile. <p>
 */
public interface BeanValidationContext
{

    /**
     * Returns the module (i.e. application) ClassLoader.
     */
    ClassLoader getClassLoader();

    /**
     * Returns the module path.
     */
    String getPath();

    /**
     * Opens an InputStream for the requested file in the module.
     * 
     * @param fileName name of the file within the module
     * 
     * @throws ValidationException if the file cannot be found or accessed.
     */
    InputStream getInputStream(final String fileName) throws ValidationException;

}
