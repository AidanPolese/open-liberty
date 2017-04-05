/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation.config;

import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;

import com.ibm.ws.beanvalidation.service.BeanValidationContext;
import com.ibm.ws.javaee.dd.bval.ValidationConfig;

/**
 * Simple interface to allow implementers to return implementations of {@link ValidationConfigurationInterface}.
 * This can allow the same core config object to be implemented separately for
 * splitting out the Validation API's and CDI implementation and dependencies out
 * of the core container.
 */
public interface ValidationConfigurationFactory {

    /**
     * Create a {@link ValidationConfigurationInterface} object, using the context
     * and the parsed {@link ValidationConfig} to build it.
     * 
     * @param context bean validation context for a module
     * @param config the parsed config from validation.xml if it existed, otherwise null
     * 
     * @return a {@link ValidationConfigurationInterface} object
     */
    ValidationConfigurationInterface createValidationConfiguration(BeanValidationContext context,
                                                                   ValidationConfig config);

    ConstraintValidatorFactory getConstraintValidatorFactoryOverride(Configuration<?> config);
}
